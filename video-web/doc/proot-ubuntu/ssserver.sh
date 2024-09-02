#!/bin/bash

# 定义配置文件路径
config_file="/root/.nvm/versions/node/v18.19.0/lib/node_modules/shadowsocks/config.json"

# 定义进程名称
process_name="ssserver"

# 日志文件路径
log_file="/root/ssserver.log"

# 函数：获取主机当前 wlan0 ip
get_wlan0_ip() {
    wlan0_ip=$(ifconfig | grep -A 2 'wlan0' | grep -oP '(\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3})' | head -n 1)
    if [ -z "$wlan0_ip" ]; then
        echo "无法获取 wlan0 接口的 IP 地址，请检查网络配置。"
        exit 1
    fi
    echo "当前 wlan0 接口的 IP 地址为: $wlan0_ip"
    echo "$wlan0_ip"
}

# 函数：修改配置文件内的 IP 地址
update_config() {
    local new_ip=$1
    if [ ! -f "$config_file" ]; then
        echo "配置文件 $config_file 不存在，请检查路径。"
        exit 1
    fi

    # 使用 sed 替换配置文件中的 IP 地址
    sed -i "s/\"server\":\"[^\"]*\"/\"server\":\"$new_ip\"/" "$config_file"

    # 检查替换是否成功
    if ! grep -q "\"server\":\"$new_ip\"" "$config_file"; then
        echo "替换配置文件中的 IP 地址失败。"
        exit 1
    fi
}

# 函数：启动 Shadowsocks 服务
start_service() {
    echo "启动 Shadowsocks 服务..."
    ssserver -c "$config_file" >> "$log_file" 2>&1 &
}

# 函数：停止 Shadowsocks 服务
stop_service() {
    echo "停止 Shadowsocks 服务..."
    pkill -f "$process_name"
}

# 函数：重启 Shadowsocks 服务
restart_service() {
    stop_service
    start_service
}

# 函数：检查服务状态
check_status() {
    if pgrep -f 'ssserver -c' > /dev/null; then
        echo "Shadowsocks 服务正在运行。"
    else
        echo "Shadowsocks 服务未运行。"
    fi
}

# 函数：查看日志
view_logs() {
    echo "查看 Shadowsocks 日志："
    cat "$log_file"
}

# 参数解析
case "$1" in
    start)
        get_wlan0_ip
        update_config "$wlan0_ip"
        start_service
        ;;
    stop)
        stop_service
        ;;
    restart)
        get_wlan0_ip
        update_config "$wlan0_ip"
        restart_service
        ;;
    status)
        check_status
        ;;
    logs)
        view_logs
        ;;
    *)
        echo "Usage: $0 {start|stop|restart|status|logs}"
        exit 1
        ;;
esac
