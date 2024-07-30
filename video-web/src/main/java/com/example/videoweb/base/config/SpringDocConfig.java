package com.example.videoweb.base.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: anubis
 * @Date: 2024/7/24 12:46
 */
@Configuration
public class SpringDocConfig {

        private License license() {
            return new License()
                    .name("Apache-2.0 license")
                    .url("http://www.apache.org/licenses/");
        }

        private Info info(){
            return new Info()
                    .title("Video-Web API")
                    .description("video-web project for develop.")
                    .version("v1.0.0")
                    .license(license());
        }
        private ExternalDocumentation externalDocumentation() {
            return new ExternalDocumentation()
                    .description("Github Repositories")
                    .url("https://github.com/AnubisCL/video-plaform");
        }

        @Bean
        public OpenAPI springShopOpenAPI() {
            return new OpenAPI()
                    .info(info())
                    .externalDocs(externalDocumentation());
        }

}
