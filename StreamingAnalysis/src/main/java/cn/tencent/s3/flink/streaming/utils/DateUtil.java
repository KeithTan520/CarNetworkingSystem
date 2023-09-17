package cn.tencent.s3.flink.streaming.utils;



import org.apache.commons.lang3.time.FastDateFormat;

import java.text.ParseException;
import java.util.Date;

/**
 * 文件名：DateUtil
 * 项目名：CarNetworkingSystem
 * 描述：实现常见的日期、时间的转换，将时间转字符串，将字符串转时间，将日期转字符串，将字符串转日期，日期和时间之间的转换
 * 将日期转换成时间戳，将时间戳再转换成日期
 * 作者：linker
 * 创建时间：2023/9/13
 * 开发步骤：
 */
public class DateUtil {
    /**
     * TODO 1、直接获得当前日期，格式：“yyyy-MM-dd HH:mm:ss”
     * @return
     */
    public static String getCurrentDateTime(){
        return FastDateFormat.getInstance(DateFormatDefine.DATE_TIME_FORMAT.getFormat()).format(new Date());
    }

    /**
     * TODO 2、直接获得当前日期，格式：”yyyyMMdd”
     * @return
     */
    public static String getCurrentDate(){
        return FastDateFormat.getInstance(DateFormatDefine.DATE_FORMAT.getFormat()).format(new Date());
    }
    /**
     * TODO 3、字符串日期格式转换，传入参数格式：“yyyy-MM-dd”，转成Date类型
     * @param str
     * @return
     */
    public static Date convertStringToDate(String str) {
        Date date = null;
        try {
            //注意FastDateFormat是线程非安全的，因此使用的时候必须要每次创建一个新的实例才可以
            FastDateFormat formatter = FastDateFormat.getInstance(DateFormatDefine.DATE_FORMAT.getFormat());
            date = formatter.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * TODO 4、字符串日期格式转换，传入参数格式：“yyyy-MM-dd”，转成Date类型
     * @param str
     * @return
     */
    public static Date convertDateStrToDate(String str){
        Date date = null;
        try {
            //注意：FastDateFormat是线程非安全的，因此使用的时候每次都必须要创建一个实例
            FastDateFormat format =FastDateFormat.getInstance(DateFormatDefine.DATE2_FORMAT.getFormat());
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * TODO 5、字符串日期格式转换，传入参数格式：“yyyy-MM-dd HH:mm:ss”，转成Date类型
     * @param str
     * @return
     */
    public static Date convertStringToDateTime(String str){
        Date date = null;
        try {
            //注意FastDateFormat是线程非安全的，因此使用的时候必须要每次创建一个新的实例才可以
            date = FastDateFormat.getInstance(DateFormatDefine.DATE_TIME_FORMAT.getFormat()).parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * TODO 6、字符串日期格式转换，传入参数格式：”yyyy-MM-dd HH:mm:ss“，转成”yyyyMMdd”格式
     * @param str
     * @return
     */
    public static String convertStringToDateString(String str){
        String dateStr = null;
        //第一步：先将日期字符串转换成日期对象
        Date date = convertStringToDateTime(str);
        //第二步：再将日期对象转换成指定的日期字符串
        dateStr = FastDateFormat.getInstance(DateFormatDefine.DATE_FORMAT.getFormat()).format(date);
        return dateStr;
    }


}
