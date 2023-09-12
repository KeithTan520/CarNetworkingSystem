package cn.tencent.s3.flink.json.parse;

import cn.tencent.s3.flink.json.bean.ComplexVehicleBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件名：ComplexJsonParser
 * 项目名：CarNetworkingSystem
 * 描述：
 * 作者：linker
 * 创建时间：2023/9/10
 * 开发步骤：
 */
public class ComplexJsonParser {
    public static void main(String[] args) {
        String jsonStr = "{\"batteryAlarm\": 0,\"carMode\": 1,\"minVoltageBattery\": 3.89,\"chargeStatus\": 1,\"vin\": \"LS5A3CJC0JF890971\",\"nevChargeSystemTemperatureDtoList\": [{\"probeTemperatures\": [25, 23, 24, 21, 24, 21, 23, 21, 23, 21, 24, 21, 24, 21, 25, 21],\"chargeTemperatureProbeNum\": 16,\"childSystemNum\": 1}]}";
        JSONObject jsonObject = new JSONObject(jsonStr);
        //解析出来字符串并封装到对象中
        int batteryAlarm = jsonObject.getInt("batteryAlarm");
        int carMode = jsonObject.getInt("carMode");
        Double minVoltageBattery = jsonObject.getDouble("minVoltageBattery");
        int chargeStatus = jsonObject.getInt("chargeStatus");
        String vin = jsonObject.getString("vin");

        ////定义一个List用于接收每个探针温度的值
        List<Integer> probeTemperatuerList = new ArrayList<>();

        JSONArray nevChargeSystemTemperatureDtoList = jsonObject.getJSONArray("nevChargeSystemTemperatureDtoList");
        String jsonTemp = nevChargeSystemTemperatureDtoList.get(0).toString();
        JSONObject jsonObject1 = new JSONObject(jsonTemp);

        JSONArray probeTemperatures = jsonObject1.getJSONArray("probeTemperatures");
        for (int i = 0; i < probeTemperatures.length(); i++) {
            probeTemperatuerList.add(probeTemperatures.getInt(i));
        }
        int chargeTemperatureProbeNum = jsonObject1.getInt("chargeTemperatureProbeNum");
        int childSystemNum = jsonObject1.getInt("childSystemNum");
        //封装对象
        ComplexVehicleBean vehicleBean = new ComplexVehicleBean(
                batteryAlarm,
                carMode,
                minVoltageBattery,
                chargeStatus,
                vin,
                probeTemperatuerList,
                chargeTemperatureProbeNum,
                childSystemNum
                );
        String s = vehicleBean.toString();
        System.out.println(s);
    }
}
