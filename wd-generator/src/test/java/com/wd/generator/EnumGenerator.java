package com.wd.generator;

/**
 *
 *
 * @author huangwenda
 * @date 2025/10/11 14:49
 **/
public class EnumGenerator {

    public static void main(String[] args) {
        String tableName = "sys_global_config";
        String tableNamePrefix = "sys_";
        String line = "`type` varchar(100) NOT NULL DEFAULT '' COMMENT '类型(SYSTEM:系统内置,NORMAL:普通)',";

        // 去掉前缀并转换为驼峰命名，首字母大写
        String processedName = removePrefixAndToCamelCase(tableName, tableNamePrefix);

        // 使用正则表达式提取三个数据
        String fieldPattern = "`([^`]+)`";  // 匹配反引号之间的字段名
        String commentPattern = "COMMENT '\\s*([^($]+)\\((.+)\\)'";  // 匹配注释中的描述和枚举值

        String fieldName = "";
        String fieldDesc = "";
        String enumValues = "";

        // 提取字段名
        java.util.regex.Pattern fieldPat = java.util.regex.Pattern.compile(fieldPattern);
        java.util.regex.Matcher fieldMatcher = fieldPat.matcher(line);
        if (fieldMatcher.find()) {
            fieldName = fieldMatcher.group(1);
        }

        // 提取字段描述和枚举值
        java.util.regex.Pattern commentPat = java.util.regex.Pattern.compile(commentPattern);
        java.util.regex.Matcher commentMatcher = commentPat.matcher(line);
        if (commentMatcher.find()) {
            fieldDesc = commentMatcher.group(1).trim();  // 字段描述
            enumValues = commentMatcher.group(2).trim(); // 枚举值映射
        }

        String enumName = processedName + toCamelCase(fieldName) + "Enum";

        StringBuilder sb = new StringBuilder(500);
        sb.append("================类名==================").append("\n");
        sb.append(enumName).append("\n");
        sb.append("================类名==================").append("\n\n");

        sb.append("================类实现================").append("\n");
        sb.append("import com.wd.common.core.model.CodeDescEnum;\n" +
                "import lombok.AllArgsConstructor;\n" +
                "import lombok.Getter;\n" +
                "\n" +
                "/**\n" +
                " * " + fieldDesc + "枚举\n" +
                " **/\n" +
                "@Getter\n" +
                "@AllArgsConstructor\n" +
                "public enum "+ enumName + " implements CodeDescEnum {\n");

        String[] enumValueSplit = enumValues.split(",");
        for (String enumValue : enumValueSplit) {
            String[] splitField = enumValue.split(":");
            sb.append("    ").append(splitField[0]).append("(\"").append(splitField[0]).append("\", \"").append(splitField[1]).append("\"),\n");
        }

        sb.append(
                "    ;\n" +
                "\n" +
                "    private final String code;\n" +
                "\n" +
                "    private final String desc;\n" +
                "}\n");
        sb.append("================类实现================").append("\n");

        System.out.println(sb);
    }

    /**
     * 去掉表名前缀并转换为驼峰命名（首字母大写）
     *
     * @param tableName 表名
     * @param prefix 前缀
     * @return 转换后的名称
     */
    public static String removePrefixAndToCamelCase(String tableName, String prefix) {
        // 去掉前缀
        if (tableName.startsWith(prefix)) {
            tableName = tableName.substring(prefix.length());
        }

        return toCamelCase(tableName);
    }

    /**
     * 转换为驼峰命名（首字母大写）
     *
     * @param tableName 表名
     * @return 转换后的名称
     */
    public static String toCamelCase(String tableName) {

        // 转换为驼峰命名，首字母大写
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;

        for (char c : tableName.toCharArray()) {
            if (c == '_') {
                capitalizeNext = true;
            } else {
                if (capitalizeNext) {
                    result.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else {
                    result.append(Character.toLowerCase(c));
                }
            }
        }

        return result.toString();
    }
}
