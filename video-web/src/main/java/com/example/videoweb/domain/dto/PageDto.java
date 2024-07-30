package com.example.videoweb.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: chailei
 * @Date: 2024/7/30 16:00
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageDto {

    @Schema(description = "当前页")
    @NotNull(message = "当前页不能为空")
    private Integer current;

    @Schema(description = "分页参数")
    @NotNull(message = "分页参数不能为空")
    private Integer size;

    @Schema(description = "排序字段")
    private String sortBy;

    @Schema(description = "是否升序")
    @NotNull(message = "是否升序不能为空")
    private Boolean asc;

}
