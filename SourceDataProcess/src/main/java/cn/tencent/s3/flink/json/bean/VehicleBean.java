package cn.tencent.s3.flink.json.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件名：VehicleBean
 * 项目名：CarNetworkingSystem
 * 描述：
 * 作者：linker
 * 创建时间：2023/9/10
 * 开发步骤：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleBean {
    private int batteryAlarm;
    private int carMode;
    private double minVoltageBattery;
    private int chargeStatus;
    private String vin;
}
