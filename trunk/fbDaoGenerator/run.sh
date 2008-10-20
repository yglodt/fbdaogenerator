#!/bin/sh

cd bin
ln -sf ../$1
export CLASSPATH=.:../lib/jaybird-full-2.1.6.jar:../lib/ant.jar:/usr/lib/jvm/java-6-sun/lib/tools.jar

java Main $1

cd ..
