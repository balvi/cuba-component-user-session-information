#!/usr/bin/env bash

if [[ -z "$1" || -z "$2" || -z "$3" ]]
  then
    echo "Define Repository URL, Username and Password as parameters. Usage: ./upload-to-repository.sh https://api.bintray.com/maven/balvi/... myUser myPassword"

  else
    ./gradlew -DuploadRepositoryRelease=$1 -DuploadRepositoryUsername=$2 -DuploadRepositoryPassword=$3 uploadArchives

fi
