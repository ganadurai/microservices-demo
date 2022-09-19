#!/bin/bash

if [[ $WORKDIR ]]; then
    export PROJECT_ID=asm-project-346001
    export VPC="vpc"
    export GKE1="gke1"
    export GKE2="gke2"
    export REGION="us-central1"
    export GKE1_LOCATION="${REGION}-a"
    export GKE2_LOCATION="${REGION}-b"
    export GKE1_CTX="gke_${PROJECT_ID}_${GKE1_LOCATION}_${GKE1}"
    export GKE2_CTX="gke_${PROJECT_ID}_${GKE2_LOCATION}_${GKE2}"
    export GKE1_KUBECONFIG="${WORKDIR}/gke1_kubeconfig"
    export GKE2_KUBECONFIG="${WORKDIR}/gke2_kubeconfig"
    export GCR_REPO="online-boutique-api-extn"
    export GKE_CHANNEL="REGULAR"
    export ASM_CHANNEL="regular"
    export CNI_ENABLED="true"
    export ASM_GATEWAYS_NAMESPACE="asm-gateways"
    export ONLINE_BOUTIQUE_NS="online-boutique"
    
    alias k=kubectl
    alias k1="kubectl --context=${GKE1_CTX} -n online-boutique"
    alias k2="kubectl --context=${GKE2_CTX} -n online-boutique"

    alias terraform=/usr/bin/terraform

    gcloud auth application-default login --no-launch-browser

else
  echo "Set the variable WORKDIR of the base dir of this application tutorial folder and rerun!"
fi

