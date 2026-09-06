#Requires -Version 5.1

#Builds both application images, creates (or reuses) the local kind cluster,
#loads the images into it, and applies the local Kustomize overlay.

$ErrorActionPreference = "Stop"
$repoRoot = Split-Path -Parent $PSScriptRoot

Write-Host "Building microservice:local image..."
docker build -t microservice:local -f "$repoRoot/microservice/Dockerfile" $repoRoot
if ($LASTEXITCODE -ne 0) { throw "docker build (microservice) failed" }

Write-Host "Building time-client:local image..."
docker build -t time-client:local -f "$repoRoot/client/Dockerfile" $repoRoot
if ($LASTEXITCODE -ne 0) { throw "docker build (time-client) failed" }

$clusterName = "time-microservice"
try {
    $existing = kind get clusters 2>$null
} catch {
    $existing = @()
}
if ($existing -contains $clusterName) {
    Write-Host "kind cluster '$clusterName' already exists, reusing it."
} else {
    Write-Host "Creating kind cluster '$clusterName'..."
    kind create cluster --config "$repoRoot/k8s/kind-config.yaml"
    if ($LASTEXITCODE -ne 0) { throw "kind create cluster failed" }
}

Write-Host "Loading images into kind..."
kind load docker-image microservice:local --name $clusterName
kind load docker-image time-client:local --name $clusterName

Write-Host "Applying k8s/overlays/local via kustomize..."
kubectl apply -k "$repoRoot/k8s/overlays/local"
if ($LASTEXITCODE -ne 0) { throw "kubectl apply failed" }

kubectl -n time-microservice-local rollout status deployment/postgres
kubectl -n time-microservice-local rollout status deployment/microservice
kubectl -n time-microservice-local rollout status deployment/time-client

Write-Host ""
Write-Host "Ready:"
Write-Host "  microservice -> http://localhost:30080/api/v1/time"
Write-Host "  time-client  -> http://localhost:30081/client/time"
