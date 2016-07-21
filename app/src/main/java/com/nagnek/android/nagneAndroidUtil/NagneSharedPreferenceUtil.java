package com.nagnek.android.nagneAndroidUtil;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.nagnek.android.debugLog.Dlog;
import com.nagnek.nagneJavaUtil.NagneReflect;

import java.lang.reflect.Field;

/**
 * Created by yongtakpc on 2016. 7. 9..
 * 사용방법 :
 * NagneSharedPreferenceUtil.appendValue(this, 파일이름, 키값 값1 );
 * NagneSharedPreferenceUtil.appendValue(this, 파일이름 키값, 값2);
 */

public class NagneSharedPreferenceUtil {
    public static final int SUCCESS = 0;
    public static final int FAIL_NONE_EXIST_ITEM = 1;

    /*
    특정 클래스의 정보들을 클래스의 특정 필드(field) 값을 키로 하여 object의 전체 필드 정보(public 필드만)를 저장한다.
    가령 연락처 Contractor 클래스에서 키 필드가 phoneNumber 라면 keyFiledName을 phoneNumer로 넘겨준다.
    각 필드별로 String.valueOf 형태를 통해 필드들의 String 값을 , 형태로 묶어서 저장한다
    */
    public static int saveObjectToSharedPreferenceUsingKeyFieldName(Activity activity, String fileName, Object object, String keyFieldName) {
        Class aClass = object.getClass();
        Field[] fields = aClass.getDeclaredFields();
        Object key = NagneReflect.getFieldValue(object, keyFieldName);
        String keyString = String.valueOf(key);
        NagneSharedPreferenceUtil.removeKey(activity, fileName, keyString);

        for (Field field : fields) {
            if(field.getName().contentEquals("CREATOR")  || field.getName().contentEquals("$change")) {
                continue;
            }
            Object fieldValue = NagneReflect.getFieldValue(object, field.getName());
            String fieldStringValue = String.valueOf(fieldValue);
            Dlog.i(fieldStringValue);
            NagneSharedPreferenceUtil.appendValue(activity, fileName, keyString, fieldStringValue);
        }

        return SUCCESS;
    }

    public static int saveObjectToSharedPreferenceUsingKeyFieldName(Activity activity, String fileName, Object object, int keyFieldName) {
        return saveObjectToSharedPreferenceUsingKeyFieldName(activity, fileName, object, String.valueOf(keyFieldName));
    }

    public static int saveObjectToSharedPreferenceUsingKey(Activity activity, String fileName, Object object, int key) {
        return saveObjectToSharedPreferenceUsingKey(activity, fileName, object, String.valueOf(key));
    }

    public static int saveObjectToSharedPreferenceUsingKey(Activity activity, String fileName, Object object, String key) {
        Class aClass = object.getClass();
        Field[] fields = aClass.getDeclaredFields();
        Dlog.i("key : " + key);
        NagneSharedPreferenceUtil.removeKey(activity, fileName, key);

        for (Field field : fields) {
            if(field.getName().contentEquals("CREATOR") || field.getName().contentEquals("$change")) {
                continue;
            }
            Object fieldValue = NagneReflect.getFieldValue(object, field.getName());
            String fieldStringValue = String.valueOf(fieldValue);
            Dlog.i(fieldStringValue);
            NagneSharedPreferenceUtil.appendValue(activity, fileName, key, fieldStringValue);
        }

        return SUCCESS;
    }

    public static int saveValueToSharedPreferenceUsingKey(Activity activity, String fileName, int value, String key) {
        return saveValueToSharedPreferenceUsingKey(activity, fileName, String.valueOf(value), key);
    }

    public static int saveValueToSharedPreferenceUsingKey(Activity activity, String fileName, int value, int key) {
        return saveValueToSharedPreferenceUsingKey(activity, fileName, String.valueOf(value), String.valueOf(key));
    }

    public static int saveValueToSharedPreferenceUsingKey(Activity activity, String fileName, String value, int key) {
        return saveValueToSharedPreferenceUsingKey(activity, fileName, value, String.valueOf(key));
    }

    public static int saveValueToSharedPreferenceUsingKey(Activity activity, String fileName, String value, String key) {
        NagneSharedPreferenceUtil.setValue(activity, fileName, key, value);
        return SUCCESS;
    }

    public static String[] loadObjectFromSharedPreferenceUsingKeyFieldName(Activity activity, String fileName, Object object, String keyFieldName) {
        Class aClass = object.getClass();
        Object key = NagneReflect.getFieldValue(object, keyFieldName);
        String keyString = String.valueOf(key);
        String[] valueList = NagneSharedPreferenceUtil.getValueList(activity, fileName, keyString);
        return valueList;
    }

    public static int isExistKeyItem(Activity activity, String fileName, String key) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(fileName, Activity.MODE_PRIVATE);
        String temp = sharedPreferences.getString(key, null);
        if (temp == null) {
            return FAIL_NONE_EXIST_ITEM;
        }
        return SUCCESS;
    }

    public static boolean appendValue(Activity activity, String fileName, String key, String value) {
        //Get previous value items
        String valueList = getStringFromPreferences(activity, fileName, null, key);
        if(value == null || value.equals("")) {
            value = "null";
        }
        Dlog.i("key : " + key);
        Dlog.i("value : " + value);
        // Append new Value item
        if (valueList != null) {
            valueList = valueList + "," + value;
        } else {
            valueList = value;
        }
        Dlog.i("valueList : " + valueList);
        // Save in Shared Preferences
        return putStringInPreferences(activity, fileName, key, valueList);
    }


    public static boolean setValue(Activity activity, String fileName, String key, int value) {
        // Save in Shared Preferences
        return setValue(activity, fileName, key,  String.valueOf(value));
    }

    public static boolean setValue(Activity activity, String fileName, String key, String value) {
        // Save in Shared Preferences
        return putStringInPreferences(activity, fileName, key, value);
    }

    public static int removeKey(Activity activity, String fileName, String key) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(fileName, Activity.MODE_PRIVATE);
        String temp = sharedPreferences.getString(key, null);
        if (temp == null) {
            Dlog.i("key" + key + "가 없음");
            return FAIL_NONE_EXIST_ITEM;
        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(key);
            editor.apply();
            Dlog.i("key" + key + "제거");
        }
        return SUCCESS;
    }

    public static int removeKey(Activity activity, String fileName, int key) {
        return removeKey(activity, fileName, String.valueOf(key));
    }

    public static String[] getValueList(Activity activity, String fileName, String key) {
        String valueList = getStringFromPreferences(activity, fileName, null, key);
        Dlog.i("valueListFinal : " + valueList);
        if(valueList == null) {
            return null;
        }
        return convertStringToArray(valueList);
    }

    public static String[] getValueList(Activity activity, String fileName, int key) {
        return getValueList(activity, fileName, String.valueOf(key));
    }

    public static String getValue(Activity activity, String fileName, String key) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(fileName, Activity.MODE_PRIVATE);
        String temp = sharedPreferences.getString(key, null);
        return temp;
    }

    public static String getValue(Activity activity, String fileName, int key) {
        return getValue(activity, fileName, String.valueOf(key));
    }

    private static boolean putStringInPreferences(Activity activity, String fileName, String key, String value) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(fileName, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
        return true;
    }

    private static String getStringFromPreferences(Activity activity, String fileName, String defaultValue, String key) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(fileName, Activity.MODE_PRIVATE);
        String temp = sharedPreferences.getString(key, defaultValue);
        return temp;
    }

     private static String[] convertStringToArray(String str) {
        String[] arr = str.split(",");
        return arr;
    }

    public static boolean clearSpecificSharedPreference(Activity activity, String fileName) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(fileName, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        return true;
    }

    public static boolean clearAllSharedPreferences(Activity activity) {
        activity.getSharedPreferences("YOUR_PREFS", 0).edit().clear().commit();
        return true;
    }
}
