package cn.tencent.s3.flink.streaming.json.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 文件名：ComplexVehicleBean
 * 项目名：CarNetworkingSystem
 * 描述：
 * 作者：linker
 * 创建时间：2023/9/10
 * 开发步骤：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComplexVehicleBean {
    private int batteryAlarm;
    private int carMode;
    private double minVoltageBattery;
    private int chargeStatus;
    private String vin;
    private List<Integer> probeTemperatures;
    private int chargeTemperatureProbeNum;
    private int childSystemNum;
}
