package com.fy.baselibrary.utils.os;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * 判断手机系统类型 核心类
 * Created by fangs on 2017/7/4.
 */
public class BuildProperties {

    private final Properties properties;

    private BuildProperties() throws IOException {
        properties = new Properties();
        try {
            properties.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean containsKey(final Object key) {
        return properties.containsKey(key);
    }

    public boolean containsValue(final Object value) {
        return properties.containsValue(value);
    }

    public Set<Map.Entry<Object, Object>> entrySet() {
        return properties.entrySet();
    }

    public String getProperty(final String name) {
        return properties.getProperty(name);
    }

    public String getProperty(final String name, final String defaultValue) {
        return properties.getProperty(name, defaultValue);
    }

    public boolean isEmpty() {
        return properties.isEmpty();
    }

    public Enumeration<Object> keys() {
        return properties.keys();
    }

    public Set<Object> keySet() {
        return properties.keySet();
    }

    public int size() {
        return properties.size();
    }

    public Collection<Object> values() {
        return properties.values();
    }


    public static BuildProperties newInstance() throws IOException {
        return new BuildProperties();
    }
}
