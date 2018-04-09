#!/bin/bash +vx

VER=`java -version 2>&1 | grep "java version" | awk '{print $3}' | tr -d \" | awk '{split($0, array, ".")} END{print array[2]}'`
if [[ $VER -ge 8 ]]; then
    echo "Required Java version found (1.8 or above)"
else
    echo "In order to build, need Java version 1.8 or above."
fi

mvn clean package 
