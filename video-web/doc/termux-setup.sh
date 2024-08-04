termux-wake-lock # 后台运行
termux-setup-storage # 文件访问

# pkg
pkg clean
pkg upgrade -y
pkg update -y
pkg install vim git wget curl -y

# 启动
vim $PREFIX/etc/motd
 _____                              
|_   _|__ _ __ _ __ ___  _   ___  __
  | |/ _ \ '__| '_ ` _ \| | | \ \/ /
  | |  __/ |  | | | | | | |_| |>  < 
  |_|\___|_|  |_| |_| |_|\___</_/\_\
  
  Put wings on your dreams!

# root
pkg install tsu
termux-chroot

# 自启动
pkg install termux-services -y
# 更新
apt update

# proot-distro
vim ~/.bashrc
echo "用户: "$(whoami)
echo "正在登录ubuntu"
proot-distro login ubuntu

# ipv6 (su)
ip -6 addr show

# [Process completed (signal 9) - press Enter]
# 原因：Android 12及以上的PhantomProcesskiller限制了应用的子进程，最大允许应用有32个子进程。
# https://blog.csdn.net/a18845594188/article/details/131296936
pkg install android-tools
adb pair 192.168.1.6:35742
adb connect 192.168.1.6:46513
# 设置最大子进程是65536
adb shell device_config set_sync_disabled_for_tests persistent
adb shell device_config put activity_manager max_phantom_processes 65536

# ffmpeg
ps -ef | grep -v grep | grep ffmpeg


