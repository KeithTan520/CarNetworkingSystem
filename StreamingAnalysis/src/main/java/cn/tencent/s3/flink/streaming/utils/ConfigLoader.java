package cn.tencent.s3.flink.streaming.utils;

import org.apache.commons.lang3.StringUtils;
import org.mortbay.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 文件名：ConfigLoader
 * 项目名：CarNetworkingSystem
 * 描述：主要用于读取配置文件中的 key / value 键值对
 * 作者：linker
 * 创建时间：2023/9/13
 * 开发步骤：
 *
 * 1）使用classLoader（类加载器），加载conf.properties
 * 2）使用Properties的load方法加载inputStream
 * 3）编写方法获取配置项的key对应的值
 * 4）编写方法获取int的key值
 */
public class ConfigLoader {
    //todo 1）使用classLoader（类加载器），加载conf.properties
    static InputStream is = ConfigLoader.class.getClassLoader().getResourceAsStream("conf.properties");
    //todo 2）使用Properties的load方法加载inputStream
    static Properties props = new Properties();
    //todo 静态代码块，动态代码块，构造器
    /**
     * static{
     *     编写静态代码块
     * }
     */
    static {
        try {
            props.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //todo 3）编写方法获取配置项的key对应的值
    public static String get(String key){
        String value = props.getProperty(key);
        return value;
    }
    //todo 4）编写方法获取int的key值
    public static Integer getInt(String key){
        String valueStr = props.getProperty(key);
    //判空
        if (StringUtils.isNotEmpty(valueStr)) {
        int value = Integer.parseInt(valueStr);
        return value;
        }else {
            return null;
        }
    }
}
