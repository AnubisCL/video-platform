#!/bin/bash

# 定义 Nginx Exporter 的路径
BASE_DIR="/home/prometheus/nginx-prometheus-exporter-1.3.0.linux-arm64"
NGINX_EXPORTER_PATH="$BASE_DIR/nginx-prometheus-exporter"
LOG_FILE="$BASE_DIR/nginx_exporter.log"

# 启动 Nginx Exporter
start_nginx_exporter() {
    nohup $NGINX_EXPORTER_PATH --nginx.scrape-uri=http://127.0.0.1:8083/stub_status --web.listen-address=:9113 > $LOG_FILE 2>&1 &
    echo "Nginx Exporter started."
}

# 停止 Nginx Exporter
stop_nginx_exporter() {
    # 查找并杀死 Nginx Exporter 进程
    NGINX_EXPORTER_PID=$(pgrep -f nginx-prometheus-exporter)
    if [ -n "$NGINX_EXPORTER_PID" ]; then
        kill $NGINX_EXPORTER_PID
        echo "Nginx Exporter stopped."
    else
        echo "Nginx Exporter is not running."
    fi
}

# 重启 Nginx Exporter
restart_nginx_exporter() {
    stop_nginx_exporter
    start_nginx_exporter
}

# 查看 Nginx Exporter 日志
view_logs() {
    tail -f $LOG_FILE
}

# 根据传入的参数执行相应的操作
case "$1" in
    start)
        start_nginx_exporter
        ;;
    stop)
        stop_nginx_exporter
        ;;
    restart)
        restart_nginx_exporter
        ;;
    logs)
        view_logs
        ;;
    *)
        echo "Usage: $0 {start|stop|restart|logs}"
        exit 1
        ;;
esac
