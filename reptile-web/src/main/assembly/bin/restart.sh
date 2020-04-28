#!/bin/bash
cd `dirname $0`

# 停止服务
./stop.sh

# 等待1s
sleep 1

# 启动服务
./start.sh
