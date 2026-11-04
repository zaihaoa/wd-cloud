package com.wd.common.server.config;

import com.wd.common.core.annotions.FieldPermission;
import com.wd.common.core.model.FieldPermissionChecker;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.SerializationConfig;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.BeanPropertyWriter;
import tools.jackson.databind.ser.ValueSerializerModifier;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author huangwenda
 * @date 2026/11/4 15:19
 **/
public class FieldPermissionModifier extends ValueSerializerModifier {

    private final FieldPermissionChecker checker;

    public FieldPermissionModifier(FieldPermissionChecker checker) {
        this.checker = checker;
    }

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
                                                     BeanDescription.Supplier beanDesc,
                                                     List<BeanPropertyWriter> properties) {

        List<BeanPropertyWriter> result = new ArrayList<>(properties.size());

        for (BeanPropertyWriter property : properties) {
            FieldPermission permission = property.getAnnotation(FieldPermission.class);

            if (permission == null) {
                result.add(property);
                continue;
            }

            result.add(new BeanPropertyWriter(property) {
                @Override
                public void serializeAsProperty(Object bean, JsonGenerator gen, SerializationContext context) throws Exception {

                    // 每次输出时检查当前用户权限
                    if (!checker.hasPermission(permission.value())) {
                        // 显式写出 null，即使配置 NON_NULL 也保留字段
                        gen.writeNullProperty(getName());
                        return;
                    }

                    super.serializeAsProperty(bean, gen, context);
                }
            });
        }

        return result;
    }
}
