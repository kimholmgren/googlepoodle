#!/bin/bash

CLASSPATH=./hamcrest-core-1.3.jar:./hamcrest-library-1.3.jar:./jedis-2.8.0.jar:./jsoup-1.8.3.jar:./junit-4.12.jar:.
javac -cp $CLASSPATH *.java