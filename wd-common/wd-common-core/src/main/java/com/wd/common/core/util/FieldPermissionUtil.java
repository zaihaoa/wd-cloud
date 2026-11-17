package com.wd.common.core.util;

import com.wd.common.core.annotions.FieldPermission;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 *
 * @author huangwenda
 * @date 2026/11/17 16:10
 **/
public class FieldPermissionUtil {


    /**
     * 获取{{cls}}中没有字段权限的对应字段名
     * @param cls
     * @param authFieldPermissionList
     * @return
     */
    public static List<String> notFieldPermissionNames(Class<?> cls, List<String> authFieldPermissionList) {

        if (cls == null || authFieldPermissionList == null || authFieldPermissionList.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> result = new ArrayList<>();

        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            FieldPermission annotation = field.getAnnotation(FieldPermission.class);
            if (Objects.isNull(annotation)) {
                continue;
            }
            String fieldPermissionCode = annotation.value();
            if (!authFieldPermissionList.contains(fieldPermissionCode)) {
                // 没有权限
                result.add(field.getName());
            }
        }
        return result;
    }
}
