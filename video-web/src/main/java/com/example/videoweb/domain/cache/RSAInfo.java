package com.example.videoweb.domain.cache;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: chailei
 * @Date: 2024/8/16 13:57
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RSAInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = -8572759230082725133L;

    private String publicKey;
    private String privateKey;
}
