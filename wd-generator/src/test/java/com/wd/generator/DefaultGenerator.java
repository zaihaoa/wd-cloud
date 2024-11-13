package com.wd.generator;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author huangwenda
 * @date 2023/3/6 17:38
 **/
public class DefaultGenerator {
    public static void main(String[] args) {
        generate();
    }

    /**
     * 代码生成
     */
    public static void generate() {
        // 作者
        String author = "huangwenda";
        // 输出目录
        String outputDir = "/Users/huangwenda/Downloads/test2/";
        // 父包名
        String parentPackage = "com.wd.system.file";
        // 模块包名
        String modulePackage = "";

        FastAutoGenerator
                .create("jdbc:mysql://localhost:3306/wd_cloud?autoReconnect=true&useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false&serverTimezone=Asia/Shanghai",
                        "root",
                        "huang123456")
                // 全局配置
                .globalConfig(builder ->
                        builder
                                // 作者名称
                                .author(author)
                                // 注释日期
                                .commentDate("yyyy-MM-dd")
                                // 输出目录
                                .outputDir(outputDir)
                )
                // 包配置
                .packageConfig(builder -> {
                    builder
                            .parent(parentPackage) // 父包名
                            .moduleName(modulePackage) // 模块包名
                            .entity("entity") // 实体类包名
                            .service("service") // service包名
                            .serviceImpl("service.impl") // serviceImpl包名
                            .mapper("mapper") // mapper包名
                            .controller("controller") // controller包名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, outputDir)); // xml位置
                })

                // 策略配置
                .strategyConfig(builder ->
                                builder
                                        // 表匹配
                                        .addInclude("sys_file_download")
                                        // 增加过滤表前缀
                                        .addTablePrefix("sys_")
                                        // 增加过滤表后缀
//                                      .addTableSuffix("_db")
                                        // 增加过滤字段前缀
//                                      .addFieldPrefix("t_")
                                        // 增加过滤字段后缀
//                                      .addFieldSuffix("_field")


                                        // Entity 策略配置
                                        .entityBuilder()
                                        .enableLombok() // 开启lombok
                                        .enableRemoveIsPrefix() // 开启boolean类型字段移除is前缀
                                        .enableTableFieldAnnotation() //开启生成实体时生成的字段注解
                                        // 乐观锁数据库字段
//                                      .versionColumnName("version")
                                        // 乐观锁实体类名称
//                                      .versionPropertyName("version")
                                        // 逻辑删除数据库中字段名
//                                      .logicDeleteColumnName("is_deleted")
                                        // 逻辑删除实体类中的字段名
//                                      .logicDeletePropertyName("deleted")
                                        // 表名 下划线 -》 驼峰命名
                                        .naming(NamingStrategy.underline_to_camel)
                                        // 字段名 下划线 -》 驼峰命名
                                        .columnNaming(NamingStrategy.underline_to_camel)
                                        // 主键生成策略 雪花算法生成id
                                        .idType(IdType.ASSIGN_ID)
                                        // Entity 文件名称
                                        .formatFileName("%s")
                                        // 表字段填充
//                                      .addTableFills(new Column("create_time", FieldFill.INSERT))
//                                      .addTableFills(new Column("update_time", FieldFill.INSERT_UPDATE))

                                        .superClass("com.wd.common.core.BaseUserTimeEntity")
                                        .addSuperEntityColumns(
                                                "create_user_id",
                                                "update_user_id",
                                                "create_time",
                                                "update_time")

                                        // Controller 策略配置
                                        .controllerBuilder()
                                        // 开启@RestController
                                        .enableRestStyle()
                                        .enableHyphenStyle()
                                        // Controller 文件名称
                                        .formatFileName("%sController")

                                        // Service 策略配置
                                        .serviceBuilder()
                                        // Service 文件名称
                                        .formatServiceFileName("%sService")
                                        // ServiceImpl 文件名称
                                        .formatServiceImplFileName("%sServiceImpl")

                                        // Mapper 策略配置
                                        .mapperBuilder()
                                        // 开启@Mapper
                                        .mapperAnnotation(org.apache.ibatis.annotations.Mapper.class)
                                        // 启用 columnList (通用查询结果列)
                                        .enableBaseColumnList()
                                        // 启动resultMap
                                        .enableBaseResultMap()
                                        // Mapper 文件名称
                                        .formatMapperFileName("%sMapper")
                                        // Xml 文件名称
                                        .formatXmlFileName("%sMapper")
                                        .enableFileOverride()
                )

                // 注入配置
                .injectionConfig(builder -> {
                    Map<String, String> customFile = new HashMap<>();

                    // 自定义模板
                    builder.customFile(customFile);
                })
                .execute();
    }
}
