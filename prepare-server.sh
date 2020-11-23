#!/bin/bash

####################################################
# DO NOT CHANGE THE GRADLE OPTIONS IN THE BLOCK    #
# BELOW, IT WILL HAVE IMPACT ON THE PERFORMANCE    #
# OF YOUR APPLICATION                              #
####################################################
GRADLE_OPTS="-Dgradle.user.home=~/gradle_cache"    #
####################################################



./gradlew clean bootrun &

while ! netstat -tna | grep 'LISTEN\>' | grep -q ':8081\>'; do
  echo "waiting for spring application to start"
  sleep 6 # time in seconds, tune it as needed
done

# mongoimport --uri mongodb+srv://anchal:anchal123@buildout-qa.1zpxa.mongodb.net/quiz --jsonArray --collection qnaset --type json --file ./initial_data_load.json
# If you have any script to load the data make sure that its part of this bash script.

