package org.example;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.IFill;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;

import java.nio.file.Paths;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: anubis
 * @Date: 2024/7/24 10:58
 */
public class CodeGenerator {

    public static void main(String[] args) {
        //"/Users/anubis/IdeaProjects/MyGitHub/video-platform"
        System.out.println(Paths.get(System.getProperty("user.dir")));
        FastAutoGenerator.create("jdbc:mysql://192.168.1.6:3306/video_db?useUnicode=true&characterEncoding=utf-8&useSSL=false",
                        "root",
                        "1q2w3e4R!@")
                .globalConfig(builder -> builder
                        .author("anubis")
                        //.enableSwagger() // 开启 swagger2 模式
                        .enableSpringdoc() // 开启 swagger3 模式
                        .dateType(DateType.ONLY_DATE)
                        .outputDir(Paths.get(System.getProperty("user.dir")) + "/video-web/src/main/java")
                        .commentDate("yyyy-MM-dd")
                )
                .dataSourceConfig(builder -> builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                    int typeCode = metaInfo.getJdbcType().TYPE_CODE;
                    if (typeCode == Types.SMALLINT) {
                        // 自定义类型转换
                        return DbColumnType.INTEGER;
                    }
                    return typeRegistry.getColumnType(metaInfo);
                }))
                .packageConfig(builder -> builder
                        //.parent("com.example.videoweb")
                        .entity("domain.entity")
                        //.mapper("mapper")
                        //.service("service")
                        //.serviceImpl("service.impl")
                        //.xml("mapper.xml")
                )
                .strategyConfig(builder -> builder
                        .entityBuilder()
                        .enableLombok()
                )
                .strategyConfig(builder -> {
                    List<IFill> list = new ArrayList<>();
                    list.add(new Column("create_date", FieldFill.INSERT));
                    list.add(new Column("update_date", FieldFill.INSERT_UPDATE));
                    builder.addInclude("v_order") // 设置需要生成的表名
                            .addTablePrefix("v_") // 设置过滤表前缀
                            .entityBuilder()
                            .enableLombok()
                            .disableSerialVersionUID()
                            .idType(IdType.ASSIGN_ID) // 分布式id 雪花算法
                            .addTableFills(list); // 默认填充
                })
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}
