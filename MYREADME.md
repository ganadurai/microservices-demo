
1. Set the workdir, at the project home
    ```bash
    export WORKDIR=$(pwd)
    ```

1. Create the namespace
    ```bash
    kubectl apply -f ${WORKDIR}/online-boutique-namespace.yaml 
    ```

2. Create the images
    ```bash
    
    cd ${WORKDIR}/src/adservice/
    docker build -t local.docker/online-boutique/adservice:v1 . 

    cd ${WORKDIR}/src/frontend/
    docker build -t local.docker/online-boutique/frontend:v1 . 
    
    cd ${WORKDIR}/src/cartservice/src/
    docker build -t local.docker/online-boutique/cartservice:v1 . 
    
    cd ${WORKDIR}/src/loadgenerator/
    docker build -t local.docker/online-boutique/loadgenerator:v1 . 
    
    cd ${WORKDIR}/src/productcatalogservice/
    docker build -t local.docker/online-boutique/productcatalogservice:v1 . 
    
    cd ${WORKDIR}/src/paymentservice/
    docker build -t local.docker/online-boutique/paymentservice:v1 . 
    
    cd ${WORKDIR}/src/checkoutservice/
    docker build -t local.docker/online-boutique/checkoutservice:v1 . 
    
    cd ${WORKDIR}/src/emailservice/
    docker build -t local.docker/online-boutique/emailservice:v1 . 
    
    cd ${WORKDIR}/src/currencyservice/
    docker build -t local.docker/online-boutique/currencyservice:v1 . 
    
    cd ${WORKDIR}/src/shippingservice/
    docker build -t local.docker/online-boutique/shippingservice:v1 . 
    
    cd ${WORKDIR}/src/recommendationservice/
    docker build -t local.docker/online-boutique/recommendationservice:v1 .

    docker images
    ```

1. Update the kubernetes for local deployment
    ```bash
    cp -R ${WORKDIR}/kubernetes-manifests ${WORKDIR}/local-kubernetes-manifests

    echo "Setting the git ignore for the local kubernetes manifests"
    cat "local-kubernetes-manifests/" > .gitignore

    cd ${WORKDIR}/local-kubernetes-manifests

    k1 patch serviceaccount default -p '{"imagePullSecrets": [{"name": "gcp-gcr-service-account-key"}]}' -n online-boutique



