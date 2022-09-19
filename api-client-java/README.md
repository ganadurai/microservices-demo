
1. Set the workdir, at the project home
    ```bash
    
    export WORKDIR=$(pwd)
    cd ${WORKDIR}/api-client-java
    source setup.sh
    ```

1. Set the GCP credentials
    ```bash
    GKE_PROJECT_ID=<project_id hosting gke cluster> 
    gcloud config set project $GKE_PROJECT_ID
    gcloud auth application-default login --no-launch-browser

    export TOKEN=$(gcloud auth print-access-token)
    ```

1. Enable for GCP Repository auth
    ```bash
    VERSION=2.1.5
    OS=linux  # or "darwin" for OSX, "windows" for Windows.
    ARCH=amd64  # or "386" for 32-bit OSs, "arm64" for ARM 64.

    curl -fsSL "https://github.com/GoogleCloudPlatform/docker-credential-gcr/releases/download/v${VERSION}/docker-credential-gcr_${OS}_${ARCH}-${VERSION}.tar.gz" \
    | tar xz docker-credential-gcr \
    && chmod +x docker-credential-gcr && sudo mv docker-credential-gcr /usr/bin/
    ```
    
1. FOr building the application and creating the jar file use the cloudtop box
    ```bash
    cd ${WORKDIR}/api-client-java
    export PATH=$PATH:/usr/local/google/home/ganadurai/Developer/maven/apache-maven-3.8.5/bin
    git pull
    mvn clean install package spring-boot:repackage
    echo "commit and push the jar file"
    ```

1. Build the docker images
    ```bash
    cd ${WORKDIR}/api-client-java
    git pull
    docker build -t gcr.io/$PROJECT_ID/$GCR_REPO/api-client-java:v1 .
    docker push gcr.io/$PROJECT_ID/$GCR_REPO/api-client-java:v1
    ```

1. Deploy the service account for GCR acces
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

    export TOKEN_LOCATION=${WORKDIR}/kubernetes-manifests/api-client/$GCP_GCR_SA.json
    
    gcloud iam service-accounts keys create ${TOKEN_LOCATION} \
        --iam-account=$GCP_GCR_SA@$PROJECT_ID.iam.gserviceaccount.com \
        --project=$PROJECT_ID

    k1 create secret docker-registry $GCP_GCR_SA-key \
        --docker-server=gcr.io --docker-username=_json_key \
        --docker-password="$(cat ${TOKEN_LOCATION})" \
        --docker-email=$GCP_GCR_SA@$PROJECT_ID.iam.gserviceaccount.com \
        -n online-boutique

    k2 create secret docker-registry $GCP_GCR_SA-key \
        --docker-server=gcr.io --docker-username=_json_key \
        --docker-password="$(cat ${TOKEN_LOCATION})" \
        --docker-email=$GCP_GCR_SA@$PROJECT_ID.iam.gserviceaccount.com \
        -n online-boutique

    k1 patch serviceaccount default -p '{"imagePullSecrets": [{"name": "gcp-gcr-service-account-key"}]}' -n online-boutique

    k2 patch serviceaccount default -p '{"imagePullSecrets": [{"name": "gcp-gcr-service-account-key"}]}' -n online-boutique
    ```

1. Deploy the kubernetes objects
    ```bash
    cd ${WORKDIR}/kubernetes-manifests/api-client
    k1 delete deployment api-client-java
    k1 apply -f api-client-java-deployment.yaml
    k1 apply -f api-client-java-service.yaml
    export API_ENDPOINT=$(k1 get svc api-client-java \
     -o jsonpath={.status.loadBalancer.ingress..ip})
    ```

1. Create Postman collection with the api endpoints created OnlineBoutique.postman_collection.

1. Convert the postman collection into OpenAPI spec 3.0. For more details on the conversin library refer to https://joolfe.github.io/postman-to-openapi/
    ```bash
    p2o ~/Downloads/OnlineBoutique.postman_collection.json -f OnlineBoutique-OpneAPI.yml 
    ```



