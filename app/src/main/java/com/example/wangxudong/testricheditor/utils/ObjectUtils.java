package com.example.wangxudong.testricheditor.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Map;

public class ObjectUtils {
    /**
     * 计算一个对象所占字节数
     *
     * @param o 该对象必须继承Serializable接口即可序列化
     * @return
     * @throws IOException
     */
    public static int size(final Object o) throws IOException {
        if (o == null) {
            return 0;
        }
        ByteArrayOutputStream buf = new ByteArrayOutputStream(4096);
        ObjectOutputStream out = new ObjectOutputStream(buf);
        out.writeObject(o);
        out.flush();
        buf.close();
        out.close();
        return buf.size();
    }

    /**
     * 赋值对象，返回对象的引用，如果参数o为符合对象，则内部每一个对象必须可序列化
     *
     * @param o 该对象必须继承Serializable接口即可序列化
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object copy(final Object o) throws IOException, ClassNotFoundException {
        if (o == null) {
            return null;
        }

        ByteArrayOutputStream outbuf = new ByteArrayOutputStream(4096);
        ObjectOutput out = new ObjectOutputStream(outbuf);
        out.writeObject(o);
        out.flush();
        outbuf.close();

        ByteArrayInputStream inbuf = new ByteArrayInputStream(outbuf.toByteArray());
        ObjectInput in = new ObjectInputStream(inbuf);
        return in.readObject();
    }

    /**
     * 序列化对象
     *
     * @param data data实现序列化
     * @return
     * @throws IOException
     */
    public static <T> String serialize(T data) throws IOException {
        String serStr = null;
        if (data != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                    byteArrayOutputStream);
            objectOutputStream.writeObject(data);
            serStr = byteArrayOutputStream.toString("ISO-8859-1");
            serStr = java.net.URLEncoder.encode(serStr, "UTF-8");
            objectOutputStream.close();
            byteArrayOutputStream.close();
        }
        return serStr;
    }

    /**
     * 反序列化对象
     *
     * @param str
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static <T> T deSerialization(String str) throws IOException,
            ClassNotFoundException {
        T data = null;
        try {
            if (str != null) {
                String redStr = java.net.URLDecoder.decode(str, "UTF-8");
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                        redStr.getBytes("ISO-8859-1"));
                ObjectInputStream objectInputStream = new ObjectInputStream(
                        byteArrayInputStream);
                data = (T) objectInputStream.readObject();
                objectInputStream.close();
                byteArrayInputStream.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }
    /**
     * 判断对象是否为空
     *
     * @param object 当前对象
     * @return true:为空 false:不为空
     */
    public static boolean isEmpty(Object object) {
        if (object == null)
            return true;
        if (object instanceof CharSequence)
            return ((CharSequence) object).length() == 0;
        if (object instanceof Collection)
            return ((Collection<?>) object).isEmpty();
        if (object instanceof Map)
            return ((Map<?,?>) object).isEmpty();
        if (object.getClass().isArray())
            return java.lang.reflect.Array.getLength(object) == 0;
        return false;
    }

    /**
     * 判断对象是否不为空
     *
     * @param object 当前对象
     * @return true:不为空 false:为空
     */
    public static boolean isNotEmpty(Object object) {
        return !isEmpty(object);
    }
}