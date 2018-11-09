#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $DIR

VERSION=3.3.1
NAME=arangodb

if [ ! -d "$DIR/$NAME" ]; then
  # download ArangoDB
  # install dependencies
  sudo apt-get -y install git-core \
    build-essential \
    libssl-dev \
    libjemalloc-dev \
    cmake \
    python2.7 \
    sudo aptitude -y install libldap2-dev
  
  echo "git clone --single-branch --depth 1 git://github.com/arangodb/arangodb.git"
  git clone --single-branch --depth 1 git://github.com/arangodb/arangodb.git

  echo "cmake .."
  mkdir arangodb/build && cd arangodb/build && cmake ..
  echo "compile ArangoDB and create the binary executable in file "
  make
fi

ARCH=$(arch)
PID=$(echo $PPID)
TMP_DIR="/tmp/arangodb.$PID"
PID_FILE="/tmp/arangodb.$PID.pid"
ARANGODB_DIR="$DIR/$NAME"
ARANGOD="${ARANGODB_DIR}/build/bin/arangod"

# create database directory
mkdir ${TMP_DIR}

echo "Starting ArangoDB '${ARANGOD}'"

${ARANGOD} \
    --database.directory ${TMP_DIR} \
    --configuration none \
    --server.endpoint tcp://127.0.0.1:8529 \
    --javascript.app-path ${ARANGODB_DIR}/js/apps \
    --javascript.startup-directory ${ARANGODB_DIR}/js \
    --database.maximal-journal-size 1048576 \
    --server.authentication false &

sleep 2

echo "Check for arangod process"
process=$(ps auxww | grep "bin/arangod" | grep -v grep)

if [ "x$process" == "x" ]; then
  echo "no 'arangod' process found"
  echo "ARCH = $ARCH"
  exit 1
fi

echo "Waiting until ArangoDB is ready on port 8529"

n=0
# timeout value for startup
timeout=60 
while [[ (-z `curl -H 'Authorization: Basic cm9vdDo=' -s 'http://127.0.0.1:8529/_api/version' `) && (n -lt timeout) ]] ; do
  echo -n "."
  sleep 1s
  n=$[$n+1]
done

if [[ n -eq timeout ]];
then
    echo "Could not start ArangoDB. Timeout reached."
    exit 1
fi


echo "ArangoDB is up"
