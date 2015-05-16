#!/usr/bin/env bash

MY_BASE="`dirname "$0"`"
MY_BASE="`cd "$MY_BASE"; pwd`"

PATH=$JAVA_HOME/bin:$PATH
JVM="java"

JAVA_OPTS="-Xmx2g"
CLASS_PATH="$MY_BASE/target/*:$MY_BASE/target/dependency/*"
LOG4J="-Dlog4j.configuration=file:$MY_BASE/home/conf/log4j.properties"

DEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"

$JVM $JAVA_OPTS $LOG4J -cp $CLASS_PATH -Desapp.server.home=$MY_BASE/home org.elasticsearch.esapp.server.Main


