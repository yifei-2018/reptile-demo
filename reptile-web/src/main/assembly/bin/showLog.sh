#!/bin/bash
cd `dirname $0`
cd ../

#参数1-显示行数
showLineCount=$1
# 服务根目录
DEPLOY_DIR=`pwd`
# 解压后的目录名
STD_OUT_LOG_FILE=${DEPLOY_DIR}/logs/stdout.log
readonly STD_OUT_LOG_FILE

# 判断参数1是否为数值
expr ${showLineCount} "+" 10 &> /dev/null
if [ $? -eq 0 ];then
  showLineCount=$1
else
  # 显示行数（默认2000行）
  showLineCount=2000
fi

# 查看日志
echo -e "\033[36m ------------ service log board------------ \033[0m"
tail -${showLineCount}f ${STD_OUT_LOG_FILE}
