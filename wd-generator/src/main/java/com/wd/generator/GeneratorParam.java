package com.wd.generator;

import lombok.*;

/**
 * @author huangwenda
 * @date 2023/8/24 18:39
 **/
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeneratorParam {

    private String dbUrl;

    private String dbUsername;

    private String dbPassword;

    /**
     * 是否生成分页方法
     */
    private Boolean generatePage = true;

    /**
     * 是否生成分页和导出方法
     */
    private Boolean generatePageAndExportPage = true;

    /**
     * 是否生成导入方法
     */
    private Boolean generateImport = true;

    /**
     * 是否生成新增方法
     */
    private Boolean generateAdd = true;

    /**
     * 是否生成更新方法
     */
    private Boolean generateUpdate = true;

    /**
     * 是否生成删除方法
     */
    private Boolean generateDelete = true;

}
