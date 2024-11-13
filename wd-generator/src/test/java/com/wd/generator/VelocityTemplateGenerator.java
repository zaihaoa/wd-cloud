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
    private static final String[] TO_GENERATE_TABLE_NAME = {"sys_user"};
    // 表前缀
    static String TABLE_PREFIX = "sys_";

    // 模块名
    static String MODULE_NAME = "";

    // 输出目录
//    static String OUTPUT_DIR_FILE = System.getProperty("user.dir") + MODULE_NAME + "/src/main/java";
//    static String OUTPUT_DIR_XML = System.getProperty("user.dir") + MODULE_NAME + "/src/main/resources/mapper";
    static String OUTPUT_DIR_FILE = "/Users/huangwenda/Downloads" + MODULE_NAME + "/src/main/java";
    static String OUTPUT_DIR_XML = "/Users/huangwenda/Downloads" + MODULE_NAME + "/src/main/resources/mapper";

    // 父包名
    static String PARENT_PACKAGE = "com.gerp";
    // 作者
    static String AUTHOR = "huangwenda";

    public static void main(String[] args) {
        GeneratorParam generatorParam = GeneratorParam
                .builder()
                .build();
        generate(generatorParam);
//        System.out.println(System.getProperty("user.dir"));
    }

    public static void generate(GeneratorParam generatorParam) {
        FastAutoGenerator
                .create(new DataSourceConfig.Builder(
                        "jdbc:mysql://localhost:3306/celebrity?autoReconnect=true&useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false&serverTimezone=Asia/Shanghai",
                        "root",
                        "huang123456")
                        .typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                            // tinyint转换成Integer
                            if (JdbcType.TINYINT == metaInfo.getJdbcType()) {
                                return DbColumnType.INTEGER;
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
                                .entity("entity") // 实体类包名
                                .service("service") // service包名
                                .serviceImpl("service.impl") // serviceImpl包名
                                .mapper("mapper") // mapper包名
                                .controller("controller") // controller包名
                                .pathInfo(Collections.singletonMap(OutputFile.xml, OUTPUT_DIR_XML)) // xml位置
                )
                .templateConfig(builder -> builder
                        .entity("/templates/vm/entity.java")
                        .service("/templates/vm/service.java")
                        .serviceImpl("/templates/vm/serviceImpl.java")
                        .mapper("/templates/vm/mapper.java")
                        .xml("/templates/vm/mapper.xml")
                        .controller("/templates/vm/controller.java")
                )
                .injectionConfig(builder -> {
                    builder.beforeOutputFile((tableInfo, objectMap) -> {
                                String comment = tableInfo.getComment();
                                System.out.println(comment);

                                // 自定义属性
//                                objectMap.put("generatePage", generatorParam.getGeneratePage());
//                                objectMap.put("generatePageAndExportPage", generatorParam.getGeneratePageAndExportPage());
//                                objectMap.put("generateImport", generatorParam.getGenerateImport());
//                                objectMap.put("generateAdd", generatorParam.getGenerateAdd());
//                                objectMap.put("generateUpdate", generatorParam.getGenerateUpdate());
//                                objectMap.put("generateDelete", generatorParam.getGenerateDelete());
                            })
                            .customFile(new CustomFile.Builder()
                                    .fileName("DTO.java")
                                    .filePath(OUTPUT_DIR_FILE)
                                    .templatePath("templates/vm/dto.java.vm")
                                    .packageName(PARENT_PACKAGE + ".dto")
                                    .build())
                            .customFile(new CustomFile.Builder()
                                    .fileName("VO.java")
                                    .filePath(OUTPUT_DIR_FILE)
                                    .templatePath("templates/vm/vo.java.vm")
                                    .packageName(PARENT_PACKAGE + ".vo")
                                    .build());
//                    if (generatorParam.getGeneratePage() || generatorParam.getGeneratePageAndExportPage()) {
//                        builder.customFile(new CustomFile.Builder()
//                                .fileName("PageParam.java")
//                                .filePath(OUTPUT_DIR_FILE)
//                                .templatePath("templates/vm/pageParam.java.vm")
//                                .packageName(PARENT_PACKAGE + ".dto")
//                                .build());
//                    }
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
//                              .logicDeleteColumnName("is_deleted") // 逻辑删除数据库中字段名
//                              .logicDeletePropertyName("deleted") // 逻辑删除实体类中的字段名
                                .naming(NamingStrategy.underline_to_camel) // 表名 下划线 -》 驼峰命名
                                .columnNaming(NamingStrategy.underline_to_camel) // 字段名 下划线 -》 驼峰命名
                                .idType(IdType.ASSIGN_ID) // 主键生成策略 雪花算法生成id
                                .formatFileName("%s") // Entity 文件名称
//                              .addTableFills(new Column("create_time", FieldFill.INSERT)) // 表字段填充
//                              .addTableFills(new Column("update_time", FieldFill.INSERT_UPDATE)) // 表字段填充
                                .superClass("com.wd.common.core.BaseUserTimeEntity") // 父类
                                .addSuperEntityColumns("create_user_id", "update_user_id", "create_time", "update_time") // 父类中的列


                                .mapperBuilder() // Mapper 策略配置
                                .mapperAnnotation(Mapper.class) // 开启@Mapper注解
                                .enableBaseColumnList() // 启用 columnList (通用查询结果列)
                                .enableBaseResultMap() // 启动resultMap
                                .formatMapperFileName("%sMapper") // Mapper 文件名称
                                .formatXmlFileName("%sMapper") // Xml 文件名称

                                .serviceBuilder() // Service 策略配置
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
}
