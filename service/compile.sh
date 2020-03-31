#!/bin/bash

# Download maven into tmp
cd /tmp
curl https://apache.dattatec.com/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz -o maven.tar.gz
# Extract mvn and set PATH
tar -xzf maven.tar.gz
