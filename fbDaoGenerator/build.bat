cd bin

set CLASSPATH=.;..\lib\jaybird-full-2.1.6.jar;..\lib\ant.jar;C:\Progra~1\Java\jdk1.6.0_06\lib\tools.jar
javac ..\src\*.java

copy ..\src\*.class .

cd ..
