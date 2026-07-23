#!/bin/bash
# Build the Phoenix API middleware and copy the executable JAR to dist/.
# Run from the project root.

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
cd "${PROJECT_DIR}"

echo "==> Building phoenix-api-middleware..."
mvn clean package

JAR_NAME="phoenix-api-middleware-1.0.0.jar"
JAR_PATH="target/${JAR_NAME}"

echo "==> Copying ${JAR_NAME} to dist/..."
mkdir -p dist
cp "${JAR_PATH}" "dist/${JAR_NAME}"

echo "==> Release ready: dist/${JAR_NAME}"
echo "==> Run with: java -jar dist/${JAR_NAME}"
