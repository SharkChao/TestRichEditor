package com.example.wangxudong.testricheditor.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.wangxudong.testricheditor.utils.ObjectUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * SharedPreferences管理类
 */
public class SharedPreferencesMgr {

    private static SharedPreferences sPrefs;

    public static void init(Context context, String fileName) {
        if (sPrefs == null) {
            sPrefs = context.getSharedPreferences(fileName, Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
        }
    }

    public static int getInt(String key, int defaultValue) {
        return sPrefs.getInt(key, defaultValue);
    }

    public static void setInt(String key, int value) {
        sPrefs.edit().putInt(key, value).commit();
    }

    public static long getLong(String key, long defaultValue) {
        return sPrefs.getLong(key, defaultValue);
    }

    public static void setLong(String key, long value) {
        sPrefs.edit().putLong(key, value).commit();
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return sPrefs.getBoolean(key, defaultValue);
    }

    public static void setBoolean(String key, boolean value) {
        sPrefs.edit().putBoolean(key, value).commit();
    }

    public static String getString(String key, String defaultValue) {
        return sPrefs.getString(key, defaultValue);
    }

    public static void setString(String key, String value) {
        sPrefs.edit().putString(key, value).commit();
    }

    public static ArrayList<String> getStringSet(String key) {
        Set set = sPrefs.getStringSet(key, null);
        if(set != null && set.size() > 0){
            return  new ArrayList<>(set);
        }
        return null;
    }

    public static void setStringSet(String key, ArrayList<String> value) {
        sPrefs.edit().putStringSet(key, new HashSet<String>(value)).commit();
    }

    public static boolean contains(String key) {
        return sPrefs.contains(key);
    }

    public static <T> void setSerialzableObject(String key, T value) throws IOException {
        String strData = ObjectUtils.serialize(value);
        if (strData != null)//防止删除数据
            setString(key, strData);
    }

    public static <T> T getSerialzableObject(String key, String defaultValue) throws IOException, ClassNotFoundException {
        T data = ObjectUtils.deSerialization(getString(key, defaultValue));
        return data;
    }

    public static void clearAll() {
        sPrefs.edit().clear().commit();
    }

    public static void removeOneData(String key) {
        sPrefs.edit().remove(key).commit();
    }
}
