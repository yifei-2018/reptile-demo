@echo off & setlocal enabledelayedexpansion

cd ..\lib
set LIB_JARS=""
for %%i in (*) do (
    set LIB_JARS=!LIB_JARS!;..\lib\%%i
)

cd ..\bin

rem 判断参数1
if ""%1"" == ""debug"" goto debug
if ""%1"" == ""jmx"" goto jmx

rem 启动类
set START_CLASS="com.yifei.reptile.web.ReptileWebApplication"

java -Xms64m -Xmx1024m -XX:MaxPermSize=64M -XX:+HeapDumpOnOutOfMemoryError -classpath ..\conf;%LIB_JARS% %START_CLASS%
goto end

:debug
java -Xms64m -Xmx1024m -XX:MaxPermSize=64M -XX:+HeapDumpOnOutOfMemoryError -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n -classpath ..\conf;%LIB_JARS% %START_CLASS%
goto end

:jmx
java -Xms64m -Xmx1024m -XX:MaxPermSize=64M -XX:+HeapDumpOnOutOfMemoryError -Dcom.sun.management.jmxremote.port=1099 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -classpath ..\conf;%LIB_JARS% %START_CLASS%

:end
pause