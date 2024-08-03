#!/bin/bash

# 定义 mysqld_exporter 的路径
BASE_DIR="/home/prometheus/mysqld_exporter-0.15.1.linux-arm64"
MYSQLD_EXPORTER_PATH="$BASE_DIR/mysqld_exporter"
CONFIG_FILE="$BASE_DIR/mysqldExporter.cnf"
LOG_FILE="$BASE_DIR/mysqld_exporter.log"

# 启动 mysqld_exporter
start_mysqld_exporter() {
    nohup $MYSQLD_EXPORTER_PATH --config.my-cnf=$CONFIG_FILE --web.listen-address=:9104 --collect.slave_status --collect.binlog_size --collect.info_schema.processlist --collect.info_schema.innodb_metrics --collect.engine_innodb_status --collect.perf_schema.file_events --collect.perf_schema.replication_group_member_stats > $LOG_FILE 2>&1 &
    echo "mysqld_exporter started."
}

# 停止 mysqld_exporter
stop_mysqld_exporter() {
    # 查找并杀死 mysqld_exporter 进程
    MYSQLD_EXPORTER_PID=$(pgrep mysqld_exporter)
    if [ -n "$MYSQLD_EXPORTER_PID" ]; then
        kill $MYSQLD_EXPORTER_PID
        echo "mysqld_exporter stopped."
    else
        echo "mysqld_exporter is not running."
    fi
}

# 重启 mysqld_exporter
restart_mysqld_exporter() {
    stop_mysqld_exporter
    start_mysqld_exporter
}

# 查看 mysqld_exporter 日志
view_logs() {
    tail -f $LOG_FILE
}

# 根据传入的参数执行相应的操作
case "$1" in
    start)
        start_mysqld_exporter
        ;;
    stop)
        stop_mysqld_exporter
        ;;
    restart)
        restart_mysqld_exporter
        ;;
    logs)
        view_logs
        ;;
    *)
        echo "Usage: $0 {start|stop|restart|logs}"
        exit 1
        ;;
esac
