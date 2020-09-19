#!/bin/bash

VERSION=`cat pom.xml |grep version | head -1 | sed -e 's/<[^>]*>//g' | xargs`

if [ $VERSION == "" ]
then
	echo "MISSING VERSION PARAM"
	exit 1
fi

rm -rf ./target
mvn clean install resources:resources compiler:compile javafx:jlink
ln -s bin/notepack target/notepack/notepack

currentDir=`pwd`

mkdir -p release/$VERSION

cd target
zip -r $currentDir/release/$VERSION/notepack-linux-runtime-$VERSION.zip notepack

cd $currentDir

../jdk/bin/jpackage -n notepack --runtime-image target/notepack/ -m notepack/notepack.Main --app-version $VERSION -d $currentDir/release/$VERSION/





