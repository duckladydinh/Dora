#!/usr/bin/env bash

if [[ "$#" != "2"  ]]; then
	echo -e "Please follow syntax\n>>>>>\tice-mvn install/generate \$ICE_HOME"
	exit 1
fi

ICE_HOME="$2"

if [[ -z "$2" ]]; then
	echo ${ICE_HOME} is not a valid \$ICE_HOME?
fi

if [[ ! -d "$ICE_HOME" ]]; then
	echo Where is ICE_HOME?
	exit 1
fi

if [[ "$1" == "install" ]]; then
	echo =============================================================
	echo = Installing dependencies...
	echo =============================================================
	find ${ICE_HOME} -name '*.jar' | xargs -I path sh -c 'name=$(basename path .jar); mvn install:install-file -Dfile=path -DartifactId=$name -DgroupId=com.ritsumei.ice -Dversion=1.0.0 -Dpackaging=jar'
    exit 0
fi

if [[ "$1" == "generate" ]]; then
    echo =============================================================
    echo = Generating dependencies...
    echo =============================================================
    find ${ICE_HOME} -name '*.jar' | xargs -I path sh -c 'name=$(basename path .jar); echo -e "\n"\<dependency\> "\n\t"\<groupId\>com.ritsumei.ice\<\/groupId\> "\n\t"\<artifactId\>$name\<\/artifactId\>  "\n\t"\<version\>1.0.0\<\/version\>  "\n"\<\/dependency\>'
    exit 0
fi

echo -e "The provided option is not supported! Please follow syntax\n>>>>>\tice-mvn install/generate \$ICE_HOME"
