
1. Set the workdir, at the project home
    ```bash
    
    export WORKDIR=$(pwd)
    cd ${WORKDIR}/api-client-java
    source setup.sh
    ```

1. FOr building the application and creating the jar file
    ```bash
    cd ${WORKDIR}/api-client-java
    export PATH=$PATH:/usr/local/google/home/ganadurai/Developer/maven/apache-maven-3.8.5/bin
    mvn clean install package spring-boot:repackage
    echo "commit and push the jar file"
    ```

1. Build the docker images
    ```bash
    cd ${WORKDIR}/api-client-java
    docker build -t gcr.io/$PROJECT_ID/$GCR_REPO/api-client-java:v1 .
    docker push gcr.io/$PROJECT_ID/$GCR_REPO/api-client-java:v1
    ```

1. Deploy the service account for GCR acces
    ```bash

    export GCP_GCR_SA="gcp-gcr-service-account"
    export TOKEN_LOCATION=/home/admin_/x-project-1/microservices-demo/${GCP_GCR_SA}.json

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
    k1 apply -f api-client-java-deployment.yaml
    k1 apply -f api-client-java-service.yaml
    ```




