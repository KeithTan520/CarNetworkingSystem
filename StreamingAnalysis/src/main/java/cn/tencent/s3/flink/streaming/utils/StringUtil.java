package cn.tencent.s3.flink.streaming.utils;

import lombok.Data;
import org.junit.Test;

/**
 * 文件名：StringUtil
 * 项目名：CarNetworkingSystem
 * 描述：字符串处理工具类
 * 作者：linker
 * 创建时间：2023/9/13
 * 开发步骤：
 */
public class StringUtil {
    /**
     * 字符串倒序：有递归法（不推荐）、数组倒序拼接、冒泡对调、使用StringBuffer的reverse方法等。
     * 冒泡对调（推荐）
     * @param s
     * @return
     */
    public static String reverse(String s){
        char[] chars = s.toCharArray();
        int n = chars.length - 1;
        int halfLength = n /2;
        for (int i = 0; i <= halfLength; i++) {
            char tmp = chars[i];
            chars[i] = chars[n - i];
            chars[n - i] = tmp;
        }
        return new String(chars);
    }
}
