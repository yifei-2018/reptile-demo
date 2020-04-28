#!/bin/bash
cd `dirname $0`
cd ../

# kill掉服务
function killService() {
    # 进程id
    pIdList=$1
    for PID in ${pIdList} ; do
        kill ${PID} > /dev/null 2>&1
    done
}

# 校验服务是否kill掉
function checkService(){
    # 进程id
    pIdList=$1
    count=0
    while [ ${count} -lt 1 ]; do
        echo -e "\033[32m.\c\033[0m"
        # 等待1s
        sleep 1
        count=1
        for PID in ${pIdList} ; do
            pIdExist=`ps -f -p ${PID} | grep java`
            if [ -n "${pIdExist}" ]; then
                count=0
                break
            fi
        done
    done
}

# 服务根目录
DEPLOY_DIR=`pwd`
readonly DEPLOY_DIR
# 服务名称（服务根目录名称）
SERVER_NAME=${DEPLOY_DIR##*/}
readonly SERVER_NAME

echo -e "\033[36m ------------ Stopping service start ------------ \033[0m"
# 查找相应服务进程
P_ID_LIST=`ps -ef | grep java | grep -v grep | grep "${DEPLOY_DIR}" |awk '{print $2}'`
readonly P_ID_LIST
if [ -z "${P_ID_LIST}" ]; then
    # 服务未启动
    echo -e "\033[33m WARN: The service of \"${SERVER_NAME}\" does not exist ! \033[0m"
else
    echo -e "\033[32m Stopping the service of \"${SERVER_NAME}\" ...\c\033[0m"
    # 遍历kill掉相应服务进程
    killService ${P_ID_LIST}
    # 遍历校验服务进程是否kill掉
    checkService ${P_ID_LIST}
    echo -e ""
    echo -e "\033[32m The service of \"${SERVER_NAME}\" stops successfully ! \033[0m"
    echo -e "\033[32m P_ID_LIST: ${P_ID_LIST}  \033[0m"
fi
echo -e "\033[36m ------------ Stopping service end ------------ \033[0m"
