#!/bin/bash

VERSION=$1

#if [[ $VERSION=="" ]]
#then
#	echo "MISSING VERSION PARAM"
#	exit 1
#fi

mvn clean install resources:resources compiler:compile javafx:jlink



