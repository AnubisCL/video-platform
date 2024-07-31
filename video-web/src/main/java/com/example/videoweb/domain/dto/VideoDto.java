package com.example.videoweb.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

/**
 * @Author: chailei
 * @Date: 2024/7/30 15:49
 */
@Data
@Builder
public class VideoDto {

    @Valid
    private PageDto page;
    @Schema(description = "搜索词")
    private String keyword;

}
