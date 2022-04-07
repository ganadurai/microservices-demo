# Canary setup of frontend , 

1. This is an extesntion to ASM setup and online-boutique setup, details at
https://gitlab.com/ganadurai/asm-terraform.git
Checkout tutorial/README.md

1. Environment variables to set
```bash

```

1. Modify the html of home page
/src/frontend/templates/home.html
```bash
<span class="platform-flag" style="width: 220px;">
    {{$.platform_name}} - v2
</span>
```

1. Containrize and push frontend as version v2 and deploy as canary

export GCR_REPO=microservices-demo

cd /src/frontend/
docker build -t gcr.io/$PROJECT_ID/$GCR_REPO/frontend:v2 .
docker push gcr.io/$PROJECT_ID/$GCR_REPO/frontend:v2

1. Construct the descriptor files as in kubernetes-manifests/frontend-canary
More details at https://www.youtube.com/watch?v=7cINRP0BFY8

1. Create service account to enable kubernets to download images from GCR
```bash

cd kubernetes-manifests/frontend-canary

export GCP_GCR_SA="gcp-gcr-service-account"

gcloud iam service-accounts create $GCP_GCR_SA \
    --project=$PROJECT_ID

gcloud projects add-iam-policy-binding $PROJECT_ID \
    --member "serviceAccount:$GCP_GCR_SA@$PROJECT_ID.iam.gserviceaccount.com" \
    --role "roles/containerregistry.ServiceAgent"

gcloud projects get-iam-policy $PROJECT_ID \
    --flatten="bindings[].members" \
    --format='table(bindings.role)' \
    --filter="bindings.members:$GCP_GCR_SA@$PROJECT_ID.iam.gserviceaccount.com"

gcloud iam service-accounts keys create ${PROJECT_HOME}/$GCP_GCR_SA.json \
    --iam-account=$GCP_GCR_SA@$PROJECT_ID.iam.gserviceaccount.com \
    --project=$PROJECT_ID

alias k1='kubectl --context=gke_asm-project-3_us-central1-a_gke1 -n online-boutique'
alias k2='kubectl --context=gke_asm-project-3_us-central1-b_gke2 -n online-boutique'

k1 create secret docker-registry $GCP_GCR_SA-key \
    --docker-server=gcr.io --docker-username=_json_key \
    --docker-password="$(cat ${PROJECT_HOME}/$GCP_GCR_SA.json)" \
    --docker-email=$GCP_GCR_SA@$PROJECT_ID.iam.gserviceaccount.com \
    -n online-boutique

k2 create secret docker-registry $GCP_GCR_SA-key \
    --docker-server=gcr.io --docker-username=_json_key \
    --docker-password="$(cat ${PROJECT_HOME}/$GCP_GCR_SA.json)" \
    --docker-email=$GCP_GCR_SA@$PROJECT_ID.iam.gserviceaccount.com \
    -n online-boutique

k1 patch serviceaccount default -p '{"imagePullSecrets": [{"name": "gcp-gcr-service-account-key"}]}' -n online-boutique

k2 patch serviceaccount default -p '{"imagePullSecrets": [{"name": "gcp-gcr-service-account-key"}]}' -n online-boutique

```

1. Deploy frontend-v2 and the Virtual services to support canary traffic routings

Changes on frontend-v2.yaml

apiVersion: apps/v1
kind: Deployment
metadata:
  name: frontend-v2
spec:
  selector:
    matchLabels:
      app: frontend
  template:
    metadata:
      labels:
        app: frontend
        version: v2

    spec:
      # serviceAccountName: default
      containers:
        - name: server
          image: gcr.io/asm-project-3/microservices-demo/frontend:v2


```bash
    cd ${PROJECT_HOME}/online-boutique/kubernetes-manifests/frontend-canary

    kubectl --context=${GKE1_CTX} apply -f frontend-v2.yaml -n online-boutique
    kubectl --context=${GKE2_CTX} apply -f frontend-v2.yaml -n online-boutique

```

1. Update the frontend Deployment, adding the version

```bash
kubectl --context=${GKE1_CTX} edit deployment v1
```
  template:
    metadata:
      labels:
        app: frontend
        version: v1


1. Deploy the DestinationRules and VirtualServices for traffic splitting
```bash
   cd ${PROJECT_HOME}/online-boutique/kubernetes-manifests/frontend-canary

   kubectl --context=${GKE1_CTX} apply -f frontend-destination-rules.yaml -n online-boutique
   kubectl --context=${GKE2_CTX} apply -f frontend-destination-rules.yaml -n online-boutique

   kubectl --context=${GKE1_CTX} apply -f frontend-canary-ingress-service.yaml \
   -n online-boutique
   kubectl --context=${GKE2_CTX} apply -f frontend-canary-ingress-service.yaml \
   -n online-boutique

   kubectl --context=${GKE1_CTX} apply -f frontend-canary-service.yaml \
   -n online-boutique
   kubectl --context=${GKE2_CTX} apply -f frontend-canary-service.yaml \
   -n online-boutique
```

1. To rollback the canary changes for the external ingress (from browser)
```bash
   
   kubectl --context=${GKE1_CTX} delete -f frontend-destination-rules.yaml -n online-boutique
   kubectl --context=${GKE2_CTX} delete -f frontend-destination-rules.yaml -n online-boutique


   kubectl --context=${GKE1_CTX} delete -f frontend-canary-ingress-service.yaml \
   -n online-boutique
   kubectl --context=${GKE2_CTX} delete -f frontend-canary-ingress-service.yaml \
   -n online-boutique

```

1. To rollback the canary changes for the internal svc ingress (from loadgen)
```bash
   
   kubectl --context=${GKE1_CTX} delete -f frontend-canary-service.yaml \
   -n online-boutique
   kubectl --context=${GKE2_CTX} delete -f frontend-canary-service.yaml \
   -n online-boutique
```





