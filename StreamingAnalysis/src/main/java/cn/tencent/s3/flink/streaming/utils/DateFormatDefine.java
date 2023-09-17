package cn.tencent.s3.flink.streaming.utils;

/**
 * 文件名：DateFormatDefine
 * 项目名：CarNetworkingSystem
 * 描述：枚举就是一组常量
 * 作者：linker
 * 创建时间：2023/9/13
 * 开发步骤：
 */
public enum DateFormatDefine {
    //时间格式， yyyy-MM-dd HH:mm:ss
    DATE_TIME_FORMAT("yyyy-MM-dd HH:mm:ss"),
    DATE_FORMAT("yyyy-MM-dd"),
    DATE2_FORMAT("yyyyMMdd");

    private String format;
    //构造方法
    DateFormatDefine(String _format){
        this.format = _format;
    }
    //获取当前的 format
    public String getFormat(){
        return this.format;
    }
}
