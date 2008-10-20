@echo off
cd bin

copy ..\fbdaogenerator.ini .

rem set CLASSPATH=.;..\lib\jaybird-full-2.1.6.jar
rem java Main

java -classpath .;..\lib\jaybird-full-2.1.6.jar Main

cd ..

pause
