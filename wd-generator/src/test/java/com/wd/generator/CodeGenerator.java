package com.wd.generator;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.type.JdbcType;

import java.util.Collections;

/**
 *
 *
 * @author huangwenda
 * @date 2026/11/4 17:17
 **/
public class CodeGenerator {

    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/wd_cloud3?autoReconnect=true&useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false&serverTimezone=Asia/Shanghai";
        String username = "root";
        String password = "huang123456";

        // 当前要生成的表
        String[] generateTableName = {"sys_role_permission_relevance"};
        // 表前缀
        String tablePrefix = "sys_";

        // 模块名
        String moduleName = "/wd-system/wd-system-server";
        // 模块中的包名称
        String modulePackageName = "basic";

        // 输出目录
        String outputDirFile = System.getProperty("user.dir") + moduleName + "/src/main/java";
        String outputDirXml = System.getProperty("user.dir") + moduleName + "/src/main/resources/mapper/" + modulePackageName;

        // 父包名
        String parentPackage = "com.wd.system." + modulePackageName;
        // 作者
        String author = "huangwenda";

        FastAutoGenerator
                .create(new DataSourceConfig.Builder(url, username, password)
                        .typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                            // tinyint转换成Boolean
                            if (JdbcType.TINYINT == metaInfo.getJdbcType()) {
                                return DbColumnType.BOOLEAN;
                            }
                            return typeRegistry.getColumnType(metaInfo);
                        }))
                .globalConfig(builder -> builder
                        .outputDir(outputDirFile) // 输出目录
                        .author(author) // 作者名称
                        .commentDate("yyyy-MM-dd") // 注释日期
                        .enableSpringdoc() // 开启springdoc
                )
                .packageConfig(builder -> builder
                        .parent(parentPackage) // 父包名
                        .entity("domain.entity") // 实体类包名
                        .service("service") // service包名
                        .serviceImpl("service.impl") // serviceImpl包名
                        .mapper("mapper") // mapper包名
                        .controller("controller") // controller包名
                        .pathInfo(Collections.singletonMap(OutputFile.xml, outputDirXml)) // xml位置
                )
                .strategyConfig(builder -> builder
                                .addInclude(generateTableName) // 表匹配
                                .addTablePrefix(tablePrefix) // 增加过滤表前缀
//                              .addTableSuffix("_db") // 增加过滤表后缀
//                              .addFieldPrefix("t_") // 增加过滤字段前缀
//                              .addFieldSuffix("_field")// 增加过滤字段后缀

                                .entityBuilder() // Entity 策略配置
                                .fieldUseJavaDoc(false) // 不生成字段文档注释，保留字段注解
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
//                              .enableBaseColumnList() // 启用 columnList (通用查询结果列)
//                              .enableBaseResultMap() // 启动resultMap
                                .formatMapperFileName("%sMapper") // Mapper 文件名称
                                .formatXmlFileName("%sMapper") // Xml 文件名称
//                              .mapperXmlTemplate("/templates/mapper.xml")

                                .serviceBuilder() // Service 策略配置
                                .superServiceClass("com.wd.common.mybatisplus.SuperService")
                                .formatServiceFileName("%sService") // Service 文件名称
                                .formatServiceImplFileName("%sServiceImpl") // ServiceImpl 文件名称

                                .controllerBuilder() // Controller 策略配置
                                .enableRestStyle() // 开启@RestController
                                .enableHyphenStyle() // 开启连字符
                                .formatFileName("%sController") // Controller 文件名称
//                              .enableFileOverride() // 开启文件覆盖
                )
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}
