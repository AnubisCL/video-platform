#!/bin/bash

# 基本信息
PID_FILE="/home/webui/video-platform/pid.txt"
JAR_FILE=/home/webui/video-platform/video-web.jar
CONFIG_LOCATION=/home/webui/video-platform/conf/

# 日志
TODAY=$(date +%Y-%m-%d)  # 获取今天的日期
LOG_FILE_NAME="video-web.log.$TODAY.log"  # 构建日志文件名
LOG_PATH="/home/webui/video-platform/logs/$LOG_FILE_NAME"  # 构建完整的日志文件路径

# 远程JVM 远程调试端口
REMOTE_JVM="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:7078"

case "$1" in
    start)
        echo "Starting the application..."
        nohup java -Xms256m -Xmx256m -Xmn128m -jar -Dspring.config.location=$CONFIG_LOCATION $JAR_FILE > /dev/null 2>&1 &
        echo $! > $PID_FILE
        ;;
    stop)
        echo "Stopping the application..."
        if [ -f $PID_FILE ]; then
            kill -15 $(cat $PID_FILE)
            rm $PID_FILE
        else
            echo "PID file not found. The application may not be running."
        fi
        ;;
    log)
        echo "Showing today's log file. Press CTRL+C to exit..."
        # 检查日志文件是否存在，如果存在则显示，否则提示用户
        if [ -f "$LOG_PATH" ]; then
            tail -10f "$LOG_PATH"
        else
            echo "Today's log file does not exist yet."
        fi
        ;;
    restart)
        echo "Restarting the application..."
        $0 stop
        sleep 5
        $0 start
        ;;
    *)
        echo "Usage: $0 {start|stop|log|restart}"
        exit 1
        ;;
esac

exit 0