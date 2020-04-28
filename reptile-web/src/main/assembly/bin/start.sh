#!/bin/bash
cd `dirname $0`
cd ../

# 服务根目录
DEPLOY_DIR=`pwd`
readonly DEPLOY_DIR
# conf目录
CONF_DIR=${DEPLOY_DIR}/conf
readonly CONF_DIR
# lib目录
LIB_DIR=${DEPLOY_DIR}/lib
readonly LIB_DIR
# 服务名称（服务根目录名称）
SERVER_NAME=${DEPLOY_DIR##*/}
readonly SERVER_NAME
# log目录
LOGS_DIR=${DEPLOY_DIR}/logs
readonly LOGS_DIR
# log文件
STD_OUT_FILE=${LOGS_DIR}/stdout.log
readonly STD_OUT_FILE
# 启动类
START_CLASS="com.yifei.reptile.web.ReptileWebApplication"
readonly START_CLASS

# 判断服务是否已启动
P_ID_LIST=`ps -ef | grep java | grep -v grep | grep "${DEPLOY_DIR}" |awk '{print $2}'`
if [ -n "${P_ID_LIST}" ]; then
    echo -e "\033[31m ERROR: The service of \"${SERVER_NAME}\" already started! \033[0m"
    echo -e "\033[31m P_ID_LIST: ${P_ID_LIST}  \033[0m"
    exit 1
fi

# 若日志目录不存在，则新建
if [ ! -d ${LOGS_DIR} ]; then
    mkdir ${LOGS_DIR}
fi

# lib目录下的jar包
LIB_JARS=`ls ${LIB_DIR} | grep .jar | awk '{print "'${LIB_DIR}'/"$0}'| tr "\n" ":"`

echo -e "\033[36m ------------ Starting service start ------------ \033[0m"
# java基本配置
JAVA_OPTS=" -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true "
readonly JAVA_OPTS

# debug
JAVA_DEBUG_OPTS=""
if [ "$1" = "debug" ]; then
    JAVA_DEBUG_OPTS=" -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n "
fi

# jmx
JAVA_JMX_OPTS=""
if [ "$1" = "jmx" ]; then
    JAVA_JMX_OPTS=" -Dcom.sun.management.jmxremote.port=1099 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false "
fi

# java内存配置
JAVA_MEM_OPTS=""
BITS=`java -version 2>&1 | grep -i 64-bit`
if [ -n "${BITS}" ]; then
        JAVA_MEM_OPTS=" -server -Xms2g -Xmx2g -Xmn256m -XX:PermSize=128m -Xss256k -XX:+HeapDumpOnOutOfMemoryError -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 "
    else
        JAVA_MEM_OPTS=" -server -Xms1g -Xmx1g -XX:PermSize=128m -XX:+HeapDumpOnOutOfMemoryError -XX:SurvivorRatio=2 -XX:+UseParallelGC "
fi

# 运行启动类（若stdout.log日志已存在，则先备份）
if [ -f "${STD_OUT_FILE}" ];then
     # 系统当前时间
    currentTime=$(date "+%Y%m%d%H%M%S")
    mv ${STD_OUT_FILE} ${LOGS_DIR}/stdout_${currentTime}_backup.log
fi
nohup java ${JAVA_OPTS} ${JAVA_MEM_OPTS} ${JAVA_DEBUG_OPTS} ${JAVA_JMX_OPTS} -classpath ${CONF_DIR}:${LIB_JARS} ${START_CLASS} > ${STD_OUT_FILE} 2>&1 &

# 判断服务是否启动成功
COUNT=0
echo -e "\033[32m Starting the service of \"${SERVER_NAME}\" ...\c\033[0m"
while [ ${COUNT} -lt 1 ]; do
    echo -e "\033[32m.\c\033[0m"
    # 等待1s
    sleep 1
    COUNT=`ps -f | grep java | grep -v grep | grep "$DEPLOY_DIR" | awk '{print $2}' | wc -l`
    if [ ${COUNT} -gt 0 ]; then
        break
    fi
done
echo -e ""
P_ID_LIST=`ps -ef | grep java | grep -v grep | grep "${DEPLOY_DIR}" |awk '{print $2}'`
echo -e "\033[32m The service of \"${SERVER_NAME}\" starts successfully ! \033[0m"
echo -e "\033[32m P_ID_LIST: ${P_ID_LIST}  \033[0m"
echo -e "\033[32m STD_OUT_FILE: ${STD_OUT_FILE} \033[0m"
echo -e "\033[36m ------------ Starting service end ------------ \033[0m"

# 是否查看启动日志
while true
do
    echo -e "\033[32m Are you sure to check the service log?[y/n]:\c\033[0m"
	read -r inputValue
	case ${inputValue} in
	    [yY][eE][sS]|[yY])
			# 查看日志
            ${DEPLOY_DIR}/bin/showLog.sh
            exit 1;;
	    [nN][oO]|[nN])
			exit 1;;
	    *)
			echo -e "\033[33m Invalid input... \033[0m";;
	esac
done
