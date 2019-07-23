#!/bin/sh

PORT=9010
BASEDIR=../repository

nohup java -Dserver.port=$PORT -Dbasedir=$BASEDIR -jar sfp.jar >/dev/null 2>&1 &

echo "started!"
