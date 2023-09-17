package cn.tencent.s3.flink.streaming.json.parse;

import cn.tencent.s3.flink.streaming.json.bean.ComplexVehicleBean;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

/**
 * 文件名：ComplexJsonParserOptimizer
 * 项目名：CarNetworkingSystem
 * 描述：
 * 作者：linker
 * 创建时间：2023/9/12
 * 开发步骤：
 */
public class ComplexJsonParserOptimizer {
    public static void main(String[] args) {
        String jsonStr = "{\"batteryAlarm\": 0,\"carMode\": 1,\"minVoltageBattery\": 3.89,\"chargeStatus\": 1,\"vin\": \"LS5A3CJC0JF890971\",\"nevChargeSystemTemperatureDtoList\": [{\"probeTemperatures\": [25, 23, 24, 21, 24, 21, 23, 21, 23, 21, 24, 21, 24, 21, 25, 21],\"chargeTemperatureProbeNum\": 16,\"childSystemNum\": 1}]}";
        //使用 JSON 解析字符串 tool
        //此处生成优化 JSON 字符串解析的逻辑
        Map<String,Object> map = toMap(jsonStr);
        //解析出来字符串并封装到对象中
        int batteryAlarm = Integer.parseInt(map.getOrDefault("batteryAlarm", -99999).toString());
        int carMode = Integer.parseInt(map.getOrDefault("carMode", -99999).toString());
        double minVoltageBattery = Double.parseDouble(map.getOrDefault("minVoltageBattery", -99999.9).toString());
        int chargeStatus = Integer.parseInt(map.getOrDefault("chargeStatus", -99999).toString());
        String vin = map.getOrDefault("vin", -99999).toString();
        //定义一个List用于接收每个探针温度的值
        ArrayList<Integer> probeTemperatuerList = new ArrayList<>();
        //解析 jsonArray
        String nevChargeSystemTemperatureDtoListString = map.getOrDefault("nevChargeSystemTemperatureDtoList", -99999).toString();
        List<Map<String,Object>> mapList =  toList(nevChargeSystemTemperatureDtoListString);
        //只需要第一个元素的值
        Map<String, Object> stringObjectMap = mapList.get(0);
        //转换成 JSON 对象
        int chargeTemperatureProbeNum = Integer.parseInt(stringObjectMap.getOrDefault("chargeTemperatureProbeNum", -99999).toString());
        int childSystemNum = Integer.parseInt(stringObjectMap.getOrDefault("childSystemNum", -99999).toString());
        //获取每个探针的温度列表
        String probeTemperatureArr = stringObjectMap.get("probeTemperatures").toString();
        String s = probeTemperatureArr.substring(1, probeTemperatureArr.length() - 1);
        String[] strings = s.split(",");
        for (String string : strings) {
            probeTemperatuerList.add(Integer.parseInt(string));
        }
        //封装对象I
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
        //输出对象
        String vehicleString = vehicleBean.toString();
        System.out.println(vehicleString);
    }

    /**
     * 输入一个 json 数据字符串，返回一个 List 列表，列表中是每个元素的键值对
     * @param arrString  nevChargeSystemTemperatureDtoList
     * @return
     */
    private static List<Map<String, Object>> toList(String arrString) {
        List<Map<String, Object>> lists = new ArrayList<>();
        //解析出来对应的 key 所有的元素列表
        JSONArray jsonArray = new JSONArray(arrString);
        //遍历数组
        for (int i = 0; i < jsonArray.length(); i++) {
            Map<String, Object> map = toMap(jsonArray.get(i).toString());
            lists.add(map);
        }
        return lists;
    }

    /**
     * 此方法用于将 jsonObject 转换成 Map<String,Object>
     * @param jsonString
     * @return
     */
    private static Map<String, Object> toMap(String jsonString) {
        //生成对象
        JSONObject jsonObject = new JSONObject(jsonString);
        Map<String, Object> result = new HashMap<>();
        //解析出来对象
        Set<String> keys = jsonObject.keySet();
        //遍历当前所有的key
        for (String key : keys) {
            Object value = jsonObject.get(key);
            result.put(key, value);
        }
        //返回
        return result;

    }
}
