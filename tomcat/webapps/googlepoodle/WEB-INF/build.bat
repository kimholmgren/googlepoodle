#!/bin/bash

CLASSPATH=./classes/hamcrest-core-1.3.jar:./classes/hamcrest-library-1.3.jar:./classes/jedis-2.8.0.jar:./classes/jsoup-1.8.3.jar:./classes/junit-4.12.jar:.:/Applications/tomcat/lib/servlet-api.jar
javac -cp $CLASSPATH classes/*.java