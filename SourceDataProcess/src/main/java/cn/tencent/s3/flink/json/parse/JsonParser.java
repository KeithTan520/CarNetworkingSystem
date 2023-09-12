package cn.tencent.s3.flink.json.parse;

import cn.tencent.s3.flink.json.bean.VehicleBean;
import org.json.JSONObject;

/**
 * 文件名：JsonParser
 * 项目名：CarNetworkingSystem
 * 描述：
 * 作者：linker
 * 创建时间：2023/9/10
 * 开发步骤：
 * {"batteryAlarm": 0, "carMode": 1,"minVoltageBattery": 3.89, "chargeStatus": 1,"vin":"LS5A3CJC0JF890971"}
 * 将 json 解析成对象
 */
public class JsonParser {
    public static void main(String[] args) {
        String jsonStr = "{\"batteryAlarm\": 0, \"carMode\": 1,\"minVoltageBattery\": 3.89, \"chargeStatus\": 1,\"vin\":\"LS5A3CJC0JF890971\"}";
        //使用 JSON 解析字符串 tool
        JSONObject jsonObject = new JSONObject(jsonStr);
        //解析出来字符串并封装到对象中
        int batteryAlarm = jsonObject.getInt("batteryAlarm");
        int carMode = jsonObject.getInt("carMode");
        Double minVoltageBattery = jsonObject.getDouble("minVoltageBattery");
        int chargeStatus = jsonObject.getInt("chargeStatus");
        String vin = jsonObject.getString("vin");
        //封装对象
        VehicleBean vehicleBean = new VehicleBean(batteryAlarm,
                carMode,
                minVoltageBattery,
                chargeStatus,
                vin);
        String s = vehicleBean.toString();
        System.out.println(s);
    }
}
