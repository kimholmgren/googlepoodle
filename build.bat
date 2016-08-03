#!/bin/bash

CLASSPATH=./com/hamcrest-core-1.3.jar:./com/hamcrest-library-1.3.jar:./com/jedis-2.8.0.jar:./com/jsoup-1.8.3.jar:./com/junit-4.12.jar:.
javac -cp $CLASSPATH com/*.java