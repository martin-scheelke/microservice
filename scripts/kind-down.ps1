#Requires -Version 5.1
<#
Deletes the local kind cluster created by kind-up.ps1.
#>
$ErrorActionPreference = "Stop"
kind delete cluster --name time-microservice
