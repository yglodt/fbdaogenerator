#!/bin/sh

cd bin
ln -sf ../fbdaogenerator.ini
export CLASSPATH=.:../lib/jaybird-full-2.1.5.jar:../lib/ant.jar:/usr/lib/jvm/java-6-sun/lib/tools.jar

java Main

cd ..
