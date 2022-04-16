
1. Set the workdir, at the project home
    ```bash
    
    export WORKDIR=$(pwd)
    cd ${WORKDIR}/api-client-java
    source setup.sh
    ```

1. FOr building the application and creating the jar file
    ```bash
    export PATH=$PATH:/usr/local/google/home/ganadurai/Developer/maven/apache-maven-3.8.5/bin
    mvn clean install package spring-boot:repackage
    echo "commit and push the jar file"
    ```

1. Build the docker images
    ```bash
    docker build -t gcr.io/$PROJECT_ID/$GCR_REPO/api-client-java:v1 .
    docker push gcr.io/$PROJECT_ID/$GCR_REPO/api-client-java:v1
    ```

1. Deploy the kubernetes objects
    ```bash
    cd ${WORKDIR}/kubernetes-manifests/api-client
    k1 apply -f api-client-java-deployment.yaml
    k1 apply -f api-client-java-service.yaml
    ```



1. Update the kubernetes for local deployment
    ```bash
    cp -R ${WORKDIR}/kubernetes-manifests ${WORKDIR}/local-kubernetes-manifests

    echo "Setting the git ignore for the local kubernetes manifests"
    cat "local-kubernetes-manifests/" > .gitignore

    cd ${WORKDIR}/local-kubernetes-manifests

    k1 patch serviceaccount default -p '{"imagePullSecrets": [{"name": "gcp-gcr-service-account-key"}]}' -n online-boutique



