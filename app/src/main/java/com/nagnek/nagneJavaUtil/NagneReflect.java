package com.nagnek.nagneJavaUtil;

import java.lang.reflect.Field;

/**
 * Created by yongtakpc on 2016. 7. 10..
 */
public class NagneReflect {
    public static Object getFieldValue(Object object, String fieldName) {
        Class aClass = object.getClass();
        Field field = null;
        Object fieldValue = null;
        try {
            field = aClass.getField(fieldName);
            try {
                fieldValue = field.get(object); // ojbect에서 field의 값 가져오기
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return fieldValue;
    }
}
