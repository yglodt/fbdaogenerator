#!/bin/sh

cd bin

CLASSPATH=.:../lib/jaybird-full-2.1.6.jar:../lib/ant.jar:/usr/lib/jvm/java-6-sun/lib/tools.jar javac -verbose ../src/*.java 
mv ../src/*.class .

cd ..
