#!/bin/bash

JAR_FILE="/home/webui/video-plaform/video-web.jar"
LOG_FILE="/home/webui/video-plaform/1.log"
PID_FILE="/home/webui/video-plaform/pid.txt"
CONFIG_LOCATION="/home/webui/video-plaform/conf" # 替换为你的配置文件路径
# 远程JVM 远程调试端口
REMOTE_JVM="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=7078"

case "$1" in
    start)
        echo "Starting the application..."
        nohup java -Xms128m -Xmx128m -Xmn64m -Dspring.config.location=$CONFIG_LOCATION -jar $JAR_FILE > $LOG_FILE 2>&1 &
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
        echo "Showing the log file. Press CTRL+C to exit..."
        tail -f $LOG_FILE
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
