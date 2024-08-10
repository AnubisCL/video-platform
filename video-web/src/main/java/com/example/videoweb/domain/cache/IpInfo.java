package com.example.videoweb.domain.cache;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: chailei
 * @Date: 2024/8/10 12:08
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IpInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 5082267035123057999L;

    private String ipv4;
    private Boolean isIpv4;
    private String ipv6;
    private Boolean isIpv6;
}
