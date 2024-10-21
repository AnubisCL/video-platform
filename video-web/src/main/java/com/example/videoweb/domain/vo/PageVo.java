package com.example.videoweb.domain.vo;

import com.example.videoweb.base.annotation.ReplaceIpEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * @Author: chailei
 * @Date: 2024/7/30 16:20
 */
@Data
@Builder
@ReplaceIpEntity
public class PageVo<T> {

    @Schema(description = "总条数")
    private Long total;
    @Schema(description = "总页数")
    private Long pages;
    @Schema(description = "数据")
    @ReplaceIpEntity
    private T records;


}
