# ~/.bashrc: executed by bash(1) for non-login shells.
# see /usr/share/doc/bash/examples/startup-files (in the package bash-doc)
# for examples

# If not running interactively, don't do anything
[ -z "$PS1" ] && return

# don't put duplicate lines in the history. See bash(1) for more options
# ... or force ignoredups and ignorespace
HISTCONTROL=ignoredups:ignorespace

# append to the history file, don't overwrite it
shopt -s histappend

# for setting history length see HISTSIZE and HISTFILESIZE in bash(1)
HISTSIZE=1000
HISTFILESIZE=2000

# check the window size after each command and, if necessary,
# update the values of LINES and COLUMNS.
shopt -s checkwinsize

# make less more friendly for non-text input files, see lesspipe(1)
[ -x /usr/bin/lesspipe ] && eval "$(SHELL=/bin/sh lesspipe)"

# set variable identifying the chroot you work in (used in the prompt below)
if [ -z "$debian_chroot" ] && [ -r /etc/debian_chroot ]; then
    debian_chroot=$(cat /etc/debian_chroot)
fi

# set a fancy prompt (non-color, unless we know we "want" color)
case "$TERM" in
    xterm-color) color_prompt=yes;;
esac

# uncomment for a colored prompt, if the terminal has the capability; turned
# off by default to not distract the user: the focus in a terminal window
# should be on the output of commands, not on the prompt
#force_color_prompt=yes

if [ -n "$force_color_prompt" ]; then
    if [ -x /usr/bin/tput ] && tput setaf 1 >&/dev/null; then
	# We have color support; assume it's compliant with Ecma-48
	# (ISO/IEC-6429). (Lack of such support is extremely rare, and such
	# a case would tend to support setf rather than setaf.)
	color_prompt=yes
    else
	color_prompt=
    fi
fi

if [ "$color_prompt" = yes ]; then
    PS1='${debian_chroot:+($debian_chroot)}\[\033[01;32m\]\u@\h\[\033[00m\]:\[\033[01;34m\]\w\[\033[00m\]\$ '
else
    PS1='${debian_chroot:+($debian_chroot)}\u@\h:\w\$ '
fi
unset color_prompt force_color_prompt

# If this is an xterm set the title to user@host:dir
case "$TERM" in
xterm*|rxvt*)
    PS1="\[\e]0;${debian_chroot:+($debian_chroot)}\u@\h: \w\a\]$PS1"
    ;;
*)
    ;;
esac

# enable color support of ls and also add handy aliases
if [ -x /usr/bin/dircolors ]; then
    test -r ~/.dircolors && eval "$(dircolors -b ~/.dircolors)" || eval "$(dircolors -b)"
    alias ls='ls --color=auto'
    #alias dir='dir --color=auto'
    #alias vdir='vdir --color=auto'

    alias grep='grep --color=auto'
    alias fgrep='fgrep --color=auto'
    alias egrep='egrep --color=auto'
fi

# some more ls aliases
alias ll='ls -alF'
alias la='ls -A'
alias l='ls -CF'

# Alias definitions.
# You may want to put all your additions into a separate file like
# ~/.bash_aliases, instead of adding them here directly.
# See /usr/share/doc/bash-doc/examples in the bash-doc package.

if [ -f ~/.bash_aliases ]; then
    . ~/.bash_aliases
fi

# enable programmable completion features (you don't need to enable
# this, if it's already enabled in /etc/bash.bashrc and /etc/profile
# sources /etc/bash.bashrc).
#if [ -f /etc/bash_completion ] && ! shopt -oq posix; then
#    . /etc/bash_completion
#fi

#if [ ! -f /proc/$(cat /var/run/supervisord.pid)/status ]; then
#    service supervisor start
#fi

#================================ my path ======================================
# nvm配置
export NVM_NODEJS_ORG_MIRROR=https://registry.npmmirror.com/mirrors/node/
export NVM_IOJS_ORG_MIRROR=https://registry.npmmirror.com/mirrors/node/
export NVM_DIR="/root/.nvm"
[ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh"  # This loads nvm
[ -s "$NVM_DIR/bash_completion" ] && \. "$NVM_DIR/bash_completion"  # This loads nvm bash_completion
#================================ my path ======================================

#================================ my shell ======================================
# 使用 supervisor 管理自启动的服务
supervisor_status=$(service supervisor status)
if [[ $supervisor_status != *"supervisord is running"* ]]; then
    echo "Supervisor starting."
    service supervisor start
fi

# 定义JAR文件的名称
JAR_NAME="video-web.jar"
# 检查输出中是否包含JAR_NAME
if ! echo "$(jps -l)" | grep -q "$JAR_NAME"; then
    echo "The $JAR_NAME starting."
    # 切换目录再执行shell，避免日志输出到当前目录
    (cd /home/webui/video-platform && ./main.sh restart) &> /dev/null &
fi
#================================ my shell ======================================

# 检查nginx服务状态
#nginx_status=$(service nginx status)
#if [[ $nginx_status != *"nginx is running"* ]]; then
#    echo "Nginx is not running. Restarting..."
#    service nginx restart
#fi

# 检查mysql服务状态
#mysql_status=$(service mysql status)
#if ! echo "$mysql_status" | grep -q "Server version"; then
#    echo "MySQL is not running. Restarting..."
#    service mysql restart
#fi

# 检查jenkins服务状态
#jenkins_status=$(service jenkins status)
#if [[ $jenkins_status != *"jenkins is running"* ]]; then
#    echo "Jenkins is not running. Restarting..."
#    service jenkins restart
#fi

# 检查Grafana服务状态
#GRAFANA_PID=$(pgrep grafana)
#if [ -n "$GRAFANA_PID" ]; then
#    echo "Grafana is running. PID: $GRAFANA_PID"
#else
#    service grafana-server restart
#fi
# Prometheus相关
#PROMETHEUS_GREP=$(ps -ef | grep -v grep | grep prometheu)
#PROMETHEUS="prometheus-2.54.0-rc.0.linux-arm64"
#NODE_EXPORTER="node_exporter-1.8.2.linux-arm64"
#MYSQLD_EXPORTER="mysqld_exporter-0.15.1.linux-arm64"
#NGINX_EXPORTER="nginx-prometheus-exporter-1.3.0.linux-arm64"
# 检查Prometheus服务状态
#if ! echo "$PROMETHEUS_GREP" | grep -q "$PROMETHEUS"; then
#    echo "The $PROMETHEUS process is not running. Starting it now..."
#    # 切换目录再执行shell，避免日志输出到当前目录
#    (cd /home/prometheus/prometheus-2.54.0-rc.0.linux-arm64 && ./main-prometheus.sh restart) &> /dev/null &
#fi
# 检查Node Exporter服务状态
#if ! echo "$PROMETHEUS_GREP" | grep -q "$NODE_EXPORTER"; then
#    echo "The $NODE_EXPORTER process is not running. Starting it now..."
#     切换目录再执行shell，避免日志输出到当前目录
#    (cd /home/prometheus/node_exporter-1.8.2.linux-arm64 && ./main-nodeExporter.sh restart) &> /dev/null &
#fi
# 检查Mysqld Exporter服务状态
#if ! echo "$PROMETHEUS_GREP" | grep -q "$MYSQLD_EXPORTER"; then
#    echo "The $MYSQLD_EXPORTER process is not running. Starting it now..."
#    # 切换目录再执行shell，避免日志输出到当前目录
#    (cd /home/prometheus/mysqld_exporter-0.15.1.linux-arm64 && ./main-mysqldExporter.sh restart) &> /dev/null &
#fi
# 检查Nginx Exporter服务状态
#if ! echo "$PROMETHEUS_GREP" | grep -q "$NGINX_EXPORTER"; then
#    echo "The $NGINX_EXPORTER process is not running. Starting it now..."
#    # 切换目录再执行shell，避免日志输出到当前目录
#    (cd /home/prometheus/nginx-prometheus-exporter-1.3.0.linux-arm64 && ./main-nginxExporter.sh restart) &> /dev/null &
#fi