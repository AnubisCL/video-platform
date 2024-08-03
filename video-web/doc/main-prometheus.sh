#!/bin/bash

# 定义 Prometheus 的路径
BASE_DIR="/home/prometheus/prometheus-2.54.0-rc.0.linux-arm64"
PROMETHEUS_PATH="$BASE_DIR/prometheus"
CONFIG_FILE="$BASE_DIR/prometheus.yml"
LOG_FILE="$BASE_DIR/prometheus.log"

# 启动 Prometheus
start_prometheus() {
#  ./prometheus --config.file=/home/prometheus/prometheus-2.54.0-rc.0.linux-arm64/prometheus.yml
    nohup $PROMETHEUS_PATH --config.file=$CONFIG_FILE > $LOG_FILE 2>&1 &
    echo "Prometheus started."
}

# 停止 Prometheus
stop_prometheus() {
    # 查找并杀死 Prometheus 进程
    PROMETHEUS_PID=$(pgrep -f prometheus-2.54.0-rc.0.linux-arm64)
    if [ -n "$PROMETHEUS_PID" ]; then
        kill $PROMETHEUS_PID
        echo "Prometheus stopped."
    else
        echo "Prometheus is not running."
    fi
}

# 重启 Prometheus
restart_prometheus() {
    stop_prometheus
    start_prometheus
}

# 查看 Prometheus 日志
view_logs() {
    tail -10f $LOG_FILE
}

# 根据传入的参数执行相应的操作
case "$1" in
    start)
        start_prometheus
        ;;
    stop)
        stop_prometheus
        ;;
    restart)
        restart_prometheus
        ;;
    logs)
        view_logs
        ;;
    *)
        echo "Usage: $0 {start|stop|restart|logs}"
        exit 1
        ;;
esac
