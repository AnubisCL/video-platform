#!/bin/bash

# 定义 Node Exporter 的路径
BASE_DIR="/home/prometheus/node_exporter-1.8.2.linux-arm64"
NODE_EXPORTER_PATH="$BASE_DIR/node_exporter"
LOG_FILE="$BASE_DIR/node_exporter.log"
node_exporter --web.listen-address=:9100
# 启动 Node Exporter
start_node_exporter() {
    $NODE_EXPORTER_PATH --web.listen-address=:9100
    echo "Node Exporter started."
}

# 停止 Node Exporter
stop_node_exporter() {
    # 查找并杀死 Node Exporter 进程
    NODE_EXPORTER_PID=$(pgrep node_exporter)
    if [ -n "$NODE_EXPORTER_PID" ]; then
        kill $NODE_EXPORTER_PID
        echo "Node Exporter stopped."
    else
        echo "Node Exporter is not running."
    fi
}

# 重启 Node Exporter
restart_node_exporter() {
    stop_node_exporter
    start_node_exporter
}

# 查看 Node Exporter 日志
view_logs() {
    tail -f $LOG_FILE
}

# 根据传入的参数执行相应的操作
case "$1" in
    start)
        start_node_exporter
        ;;
    stop)
        stop_node_exporter
        ;;
    restart)
        restart_node_exporter
        ;;
    logs)
        view_logs
        ;;
    *)
        echo "Usage: $0 {start|stop|restart|logs}"
        exit 1
        ;;
esac
