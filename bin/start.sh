#!/bin/sh

while read line;do
    eval "$line"
done < config

nohup java -Dserver.port=$port -Drepository=$repository -jar sfp.jar >/dev/null 2>&1 &

echo "started!"
