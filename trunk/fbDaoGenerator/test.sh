#!/bin/sh

#./build.sh

cd bin
ln -sf ../fbdaogenerator.ini
export CLASSPATH=.:../lib/jaybird-full-2.1.6.jar:/tmp/dao.jar

java Test

cd ..
