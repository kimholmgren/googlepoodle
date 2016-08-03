#!/bin/bash

CLASSPATH=./googlepoodle/com/hamcrest-core-1.3.jar:./googlepoodle/com/hamcrest-library-1.3.jar:./googlepoodle/com/jedis-2.8.0.jar:./googlepoodle/com/jsoup-1.8.3.jar:./googlepoodle/com/junit-4.12.jar:.:/googlepoodle/com/SearchEngine
cd ..
java -cp $CLASSPATH googlepoodle.com.SearchEngine