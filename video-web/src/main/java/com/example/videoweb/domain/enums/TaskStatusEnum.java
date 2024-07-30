package com.example.videoweb.domain.enums;

/**
 * @Author: chailei
 * @Date: 2024/7/23 17:54
 */
public enum TaskStatusEnum {

    // 0:未开始,1:正在执行[下载],2:执行完成[下载],3:执行失败[下载],4:正在执行[推流],5:执行完成[推流],6:执行失败[推流]
    UN_START(0),
    DOWNLOADING(1),
    DOWNLOAD_COMPLETE(2),
    DOWNLOAD_FAIL(3),
    PUSHING(4),
    PUSH_COMPLETE(5),
    PUSH_FAIL(6);

    private Integer code;

    TaskStatusEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
