#!/usr/bin/env bash
#
# Register an EKS cluster with a locally running Argo CD instance.
#
# Logs in to Argo CD running locally (e.g. port-forwarded from Minikube),
# then registers the given EKS cluster as a deployment destination using
# a static ServiceAccount bearer token (no AWS exec-plugin dependency).
#
# Usage:
#   ./scripts/register-eks-cluster-with-argoocd.sh
#   EKS_CONTEXT=arn:aws:eks:... ARGOCD_SERVER=localhost:8095 ./scripts/register-eks-cluster-with-argoocd.sh

set -euo pipefail

EKS_CONTEXT="${EKS_CONTEXT:-arn:aws:eks:ap-southeast-2:665206375138:cluster/peculiar-funk-crow}"
ARGOCD_SERVER="${ARGOCD_SERVER:-localhost:8095}"
MINIKUBE_CONTEXT="${MINIKUBE_CONTEXT:-minikube}"
ARGOCD_NAMESPACE="${ARGOCD_NAMESPACE:-argocd}"

for tool in kubectl argocd base64; do
  command -v "$tool" >/dev/null 2>&1 || { echo "ERROR: $tool not found in PATH" >&2; exit 1; }
done

echo "==> Fetching Argo CD admin password from ${MINIKUBE_CONTEXT}/${ARGOCD_NAMESPACE}"
encoded="$(kubectl --context "$MINIKUBE_CONTEXT" -n "$ARGOCD_NAMESPACE" \
  get secret argocd-initial-admin-secret -o jsonpath='{.data.password}' || true)"
if [[ -z "$encoded" ]]; then
  echo "ERROR: argocd-initial-admin-secret not found." >&2
  echo "If the password was rotated, log in manually:" >&2
  echo "  argocd login ${ARGOCD_SERVER} --username admin --insecure" >&2
  exit 1
fi
password="$(printf '%s' "$encoded" | base64 -d)"

echo "==> Logging in to Argo CD at ${ARGOCD_SERVER}"
argocd login "$ARGOCD_SERVER" \
  --username admin \
  --password "$password" \
  --insecure \
  --grpc-web

echo "==> Verifying kubeconfig context '${EKS_CONTEXT}' exists"
if ! kubectl config get-contexts -o name | grep -Fxq "$EKS_CONTEXT"; then
  echo "ERROR: kubeconfig context '${EKS_CONTEXT}' not found." >&2
  echo "Run: aws eks update-kubeconfig --name <cluster> --region <region>" >&2
  exit 1
fi

echo "==> Registering EKS cluster with Argo CD (--upsert makes this idempotent)"
argocd cluster add "$EKS_CONTEXT" --cluster-resources --upsert --yes

echo "==> Done. Registered clusters:"
argocd cluster list
