#!/bin/bash

# When you create a cluster on mongo cloud you will get a command to register like below
# mongo "mongodb+srv://cluster0.ufsyo.mongodb.net/test" --username admin
# You need to frame the url from there for connection without prompt. The url above becomes
# mongo "mongodb+srv://user:encoded_password@cluster0.your_cluster_id.mongodb.net/database_name"
# for my case above, It looked like
# mongo "mongodb+srv://admin:p%4055w0rd@cluster0.ufsyo.mongodb.net/test"
# where my password is 'p@55w0rd', I encoded it on www.url-encode-online.rocks/urlencode
# post you are able to successfully replace the statement below to point to your cluster.

export MONGO_URL="mongodb+srv://anchal:anchal123@buildout-qa.1zpxa.mongodb.net/quiz"
