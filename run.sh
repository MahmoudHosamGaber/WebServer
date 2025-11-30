#!/bin/sh
set -e

(
  cd "$(dirname "$0")" 
  mvn -B package -Ddir=./target/
)
exec java -jar ./target/codecrafters-http-server.jar "$@"
