package com.wd.generator;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.builder.CustomFile;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.type.JdbcType;

import java.util.Collections;

/**
 * @author huangwenda
 * @date 2023/8/24 14:49
 **/
public class VelocityTemplateGenerator {

    // 当前要生成的表
    private static final String[] TO_GENERATE_TABLE_NAME = {"sys_dictionary"};
    // 表前缀
    static String TABLE_PREFIX = "sys_";

    // 模块名
    static String MODULE_NAME = "/wd-system/wd-system-server";

    static String MODULE_PACKAGE_NAME = "basic";

    // 输出目录
    static String OUTPUT_DIR_FILE = System.getProperty("user.dir") + MODULE_NAME + "/src/main/java";
    static String OUTPUT_DIR_XML = System.getProperty("user.dir") + MODULE_NAME + "/src/main/resources/mapper/" + MODULE_PACKAGE_NAME;

    // 父包名
    static String PARENT_PACKAGE = "com.wd.system." + MODULE_PACKAGE_NAME;
    // 作者
    static String AUTHOR = "huangwenda";

    public static void main(String[] args) {
        generate();
    }

    public static void generate() {
        FastAutoGenerator
                .create(new DataSourceConfig.Builder(
                        "jdbc:mysql://localhost:3306/wd_cloud3?autoReconnect=true&useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false&serverTimezone=Asia/Shanghai",
                        "root",
                        "huang123456")
                        .typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                            // tinyint转换成Boolean
                            if (JdbcType.TINYINT == metaInfo.getJdbcType()) {
                                return DbColumnType.BOOLEAN;
                            }
                            return typeRegistry.getColumnType(metaInfo);
                        }))
                .globalConfig(builder -> builder
                        .outputDir(OUTPUT_DIR_FILE) // 输出目录
                        .author(AUTHOR) // 作者名称
                        .commentDate("yyyy-MM-dd") // 注释日期
                        .enableSpringdoc() // 开启springdoc
                )
                .packageConfig(builder -> builder
//                        .moduleName(MODULE_NAME) // 模块包名
                                .parent(PARENT_PACKAGE) // 父包名
                                .entity("domain.entity") // 实体类包名
                                .service("service") // service包名
                                .serviceImpl("service.impl") // serviceImpl包名
                                .mapper("mapper") // mapper包名
                                .controller("controller") // controller包名
                                .pathInfo(Collections.singletonMap(OutputFile.xml, OUTPUT_DIR_XML)) // xml位置
                )
                .templateConfig(builder -> builder
                        .entity("/templates/entity.java")
                        .service("/templates/service.java")
                        .serviceImpl("/templates/serviceImpl.java")
                        .mapper("/templates/mapper.java")
                        .xml("/templates/mapper.xml")
                        .controller("/templates/controller.java")
                )
                .injectionConfig(builder -> {
                    builder.beforeOutputFile((tableInfo, objectMap) -> {
                        String entity = (String) objectMap.get("entity");
                        // 首字母缩写
                        objectMap.put("acronymEntity", acronym(entity));

                        // 自定义属性
                                objectMap.put("generatePageAndExportPage", true);
                                objectMap.put("generateImport", true);
                                objectMap.put("generateAdd", true);
                                objectMap.put("generateUpdate", true);
                                objectMap.put("generateDelete", true);
                    })
                            .customFile(new CustomFile.Builder()
                                    .fileName("PageParamDTO.java")
                                    .filePath(OUTPUT_DIR_FILE)
                                    .templatePath("templates/pageParam.java.vm")
                                    .packageName(PARENT_PACKAGE + ".domain.dto")
                                    .build())
                            .customFile(new CustomFile.Builder()
                                    .fileName("PageDTO.java")
                                    .filePath(OUTPUT_DIR_FILE)
                                    .templatePath("templates/pageDTO.java.vm")
                                    .packageName(PARENT_PACKAGE + ".domain.dto")
                                    .build())
                            .customFile(new CustomFile.Builder()
                                    .fileName("PageVO.java")
                                    .filePath(OUTPUT_DIR_FILE)
                                    .templatePath("templates/pageVO.java.vm")
                                    .packageName(PARENT_PACKAGE + ".domain.vo")
                                    .build())
                            .customFile(new CustomFile.Builder()
                                    .fileName("Converter.java")
                                    .filePath(OUTPUT_DIR_FILE)
                                    .templatePath("templates/convert.java.vm")
                                    .packageName(PARENT_PACKAGE + ".convert")
                                    .build())
                            .customFile(new CustomFile.Builder()
                                    .fileName("AddDTO.java")
                                    .filePath(OUTPUT_DIR_FILE)
                                    .templatePath("templates/add.java.vm")
                                    .packageName(PARENT_PACKAGE + ".domain.dto")
                                    .build())
                            .customFile(new CustomFile.Builder()
                                    .fileName("UpdateDTO.java")
                                    .filePath(OUTPUT_DIR_FILE)
                                    .templatePath("templates/update.java.vm")
                                    .packageName(PARENT_PACKAGE + ".domain.dto")
                                    .build())
                    ;
                })
                .strategyConfig(builder -> builder
                                .addInclude(TO_GENERATE_TABLE_NAME) // 表匹配
                                .addTablePrefix(TABLE_PREFIX) // 增加过滤表前缀
//                              .addTableSuffix("_db") // 增加过滤表后缀
//                              .addFieldPrefix("t_") // 增加过滤字段前缀
//                              .addFieldSuffix("_field")// 增加过滤字段后缀


                                .entityBuilder() // Entity 策略配置
                                .enableLombok() // 开启lombok
                                .enableRemoveIsPrefix() // 开启boolean类型字段移除is前缀
                                .enableTableFieldAnnotation() //开启生成实体时生成的字段注解
//                              .versionColumnName("version") // 乐观锁数据库字段
//                              .versionPropertyName("version") // 乐观锁实体类名称
//                              .logicDeleteColumnName("delete_flag") // 逻辑删除数据库中字段名
//                              .logicDeletePropertyName("deleteFlag") // 逻辑删除实体类中的字段名
                                .naming(NamingStrategy.underline_to_camel) // 表名 下划线 -》 驼峰命名
                                .columnNaming(NamingStrategy.underline_to_camel) // 字段名 下划线 -》 驼峰命名
                                .idType(IdType.ASSIGN_ID) // 主键生成策略 雪花算法生成id
                                .formatFileName("%s") // Entity 文件名称
//                              .addTableFills(new Column("create_time", FieldFill.INSERT)) // 表字段填充
//                              .addTableFills(new Column("update_time", FieldFill.INSERT_UPDATE)) // 表字段填充
                                .superClass("com.wd.common.core.model.BaseUserTimeEntity") // 父类
                                .addSuperEntityColumns("create_user_id", "update_user_id", "create_time", "update_time") // 父类中的列


                                .mapperBuilder() // Mapper 策略配置
                                .superClass("com.wd.common.mybatisplus.SuperMapper")
                                .mapperAnnotation(Mapper.class) // 开启@Mapper注解
//                                .enableBaseColumnList() // 启用 columnList (通用查询结果列)
//                                .enableBaseResultMap() // 启动resultMap
                                .formatMapperFileName("%sMapper") // Mapper 文件名称
                                .formatXmlFileName("%sMapper") // Xml 文件名称
//                                .mapperXmlTemplate("/templates/mapper.xml")

                                .serviceBuilder() // Service 策略配置
                                .superServiceClass("com.wd.common.mybatisplus.SuperService")
                                .formatServiceFileName("%sService") // Service 文件名称
                                .formatServiceImplFileName("%sServiceImpl") // ServiceImpl 文件名称

                                .controllerBuilder() // Controller 策略配置
                                .enableRestStyle() // 开启@RestController
                                .enableHyphenStyle() // 开启连字符
                                .formatFileName("%sController") // Controller 文件名称

                                .enableFileOverride() // 开启文件覆盖
                )
                .templateEngine(new VelocityTemplateEngine())
                .execute();

    }

    public static String acronym(String str) {

        String first = str.substring(0, 1);

        String after = str.substring(1); //substring(1),获取索引位置1后面所有剩余的字符串

        first = first.toLowerCase();

        return first + after;
    }
}
