#!/bin/sh

cd bin

CLASSPATH=.:../lib/jaybird-full-2.1.3.jar:../lib/mailapi.jar:../lib/pop3.jar  javac ../src/*.java 
mv ../src/*.class .

cd ..
