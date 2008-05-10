#!/bin/sh

cd bin
ln -sf ../fbdaogenerator.ini
export CLASSPATH=.:../lib/jaybird-full-2.1.3.jar

java Main

cd ..
