#!/bin/bash

export JAVA_HOME=${JAVA_HOME_1_6}
echo $JAVA_HOME
export PATH=${JAVA_HOME}/bin:${PATH}
echo $PATH
bash gradle.sh clean genOnline buildon war buildTar

