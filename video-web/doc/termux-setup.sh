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
