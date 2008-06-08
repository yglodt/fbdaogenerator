#!/bin/sh

cd bin
ln -sf ../fbdaogenerator.ini
export CLASSPATH=.:../lib/jaybird-full-2.1.3.jar:../lib/ant.jar:/usr/lib/jvm/java-6-sun-1.6.0.06/lib/tools.jar

java Main

cd ..
