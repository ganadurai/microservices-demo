# Canary setup of frontend 

1. Modify the html of home page
/src/frontend/templates/home.html
```bash
<span class="platform-flag" style="width: 220px;">
    {{$.platform_name}} - v2
</span>
```

1. Containrize and push frontend as version v2 and deploy as canary
cd /src/frontend/
docker build -t gcr.io/$PROJECT_ID/$GCR_REPO/frontend:v2 .
docker push gcr.io/$PROJECT_ID/$GCR_REPO/frontend:v2

1. Construct the descriptor files as in kubernetes-manifests/frontend-canary
More details at https://www.youtube.com/watch?v=7cINRP0BFY8

1. Create service account to enable kubernets to download images from GCR
```bash
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

gcloud iam service-accounts keys create $GCP_GCR_SA.json \
    --iam-account=$GCP_GCR_SA@$PROJECT_ID.iam.gserviceaccount.com \
    --project=$PROJECT_ID

kubectl create secret docker-registry $GCP_GCR_SA-key \
    --docker-server=gcr.io --docker-username=_json_key \
    --docker-password="$(cat $WORK_DIR/$GCP_GCR_SA.json)" \
    --docker-email=$GCP_GCR_SA@$PROJECT_ID.iam.gserviceaccount.com \
    -n online-boutique

kubectl patch serviceaccount default -p '{"imagePullSecrets": [{"name": "gcp-gcr-service-account-key"}]}' -n online-boutique
```