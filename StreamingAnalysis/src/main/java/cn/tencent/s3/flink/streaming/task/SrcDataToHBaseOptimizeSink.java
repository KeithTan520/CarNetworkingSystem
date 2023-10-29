package cn.tencent.s3.flink.streaming.task;


import cn.tencent.s3.flink.streaming.entity.CarDataObj;
import cn.tencent.s3.flink.streaming.utils.DateUtil;
import cn.tencent.s3.flink.streaming.utils.StringUtil;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author itcast
 * Date 2021/11/16 14:57
 * Desc TODO
 */
public class SrcDataToHBaseOptimizeSink extends RichSinkFunction<CarDataObj> {
    //设置logger
    private static final Logger logger = LoggerFactory.getLogger(SrcDataToHBaseOptimizeSink.class);
    //成员变量 表名
    private String tablename;
    //创建连接对象
    Connection conn = null;
    //创建表对象
    //Table table = null;
    //创建缓存对象
    BufferedMutator bufferedMutator = null;

    public SrcDataToHBaseOptimizeSink(String tableName) {
        this.tablename = tableName;
    }

    /**
     * 设置hbase连接，开启连接，创建 statement，创建hbase 表
     *
     * @param parameters
     * @throws Exception
     */
    @Override
    public void open(Configuration parameters) throws Exception {
        //3.1 从上下文获取到全局的参数
        ParameterTool parameterTool = (ParameterTool) getRuntimeContext()
                .getExecutionConfig()
                .getGlobalJobParameters();
        //3.2 设置hbase的配置，Zookeeper Quorum集群和端口和TableInputFormat的输入表
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        //设置 Zookeeper Quorum 集群和端口
        conf.set(HConstants.ZOOKEEPER_QUORUM, parameterTool.get("zookeeper.quorum"));
        conf.set(HConstants.ZOOKEEPER_CLIENT_PORT, parameterTool.get("zookeeper.clientPort"));
        //处理落库的表
        conf.set(TableInputFormat.INPUT_TABLE, tablename);

        org.apache.hadoop.conf.Configuration hConf = HBaseConfiguration.create(conf);
        //3.3 通过连接工厂创建连接
        conn = ConnectionFactory.createConnection(hConf);
        //3.4 通过连接获取表对象
        //table = conn.getTable(TableName.valueOf(tablename));
        //设置缓存对象的参数
        BufferedMutatorParams params = new BufferedMutatorParams(TableName.valueOf(tablename));
        //设置多久或多大缓存刷写到hbase中
        params.writeBufferSize(10*1024*1024);
        params.setWriteBufferPeriodicFlushTimeoutMs(10*1000);

        bufferedMutator = conn.getBufferedMutator(params);
    }

    /**
     * 关闭表和连接
     *
     * @throws Exception
     */
    @Override
    public void close() throws Exception {
        //强制将不满足条件的数据刷写进来
        bufferedMutator.flush();
        //4.1 关闭hbase 表和连接资源
        if (bufferedMutator != null) bufferedMutator.close();
        if (conn != null) conn.close();
    }

    /**
     * 设置将 CarDataObj 对象写入到 HBase 中
     * @param carDataObj
     * @param context
     * @throws Exception
     */
    @Override
    public void invoke(CarDataObj carDataObj, Context context) throws Exception {
        //5.1 setDataSourcePut输入参数value，返回put对象
        Put put = setDataSourcePut(carDataObj);
        bufferedMutator.mutate(put);
    }

    //6. 实现 setDataSourcePut 方法
    private Put setDataSourcePut(CarDataObj carDataObj) {
        //准备表名、rowkey设计、列簇cf、列名=列值
        //6.1 如何设计rowkey VIN+时间戳翻转
        String rowkey = carDataObj.getVin()+ StringUtil.reverse(carDataObj.getTerminalTimeStamp()+"");
        //6.2 定义列簇的名称
        String cf = "cf";
        Put put = null;
        try {
            //6.3 通过 rowkey 实例化 put
             put = new Put(Bytes.toBytes(rowkey));
            //6.4 将所有的字段添加到put的字段中
            put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("vin"), Bytes.toBytes(carDataObj.getVin()));
            put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("terminalTime"), Bytes.toBytes(carDataObj.getTerminalTime()));
            if (carDataObj.getSoc() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("soc"), Bytes.toBytes(String.valueOf(carDataObj.getSoc())));
            if (carDataObj.getLat() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("lat"), Bytes.toBytes(String.valueOf(carDataObj.getLat())));
            if (carDataObj.getLng() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("lng"), Bytes.toBytes(String.valueOf(carDataObj.getLng())));
            if (carDataObj.getGearDriveForce() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("gearDriveForce"), Bytes.toBytes(String.valueOf(carDataObj.getGearDriveForce())));
            if (carDataObj.getBatteryConsistencyDifferenceAlarm() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("batteryConsistencyDifferenceAlarm"), Bytes.toBytes(String.valueOf(carDataObj.getBatteryConsistencyDifferenceAlarm())));
            if (carDataObj.getSocJumpAlarm() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("socJumpAlarm"), Bytes.toBytes(String.valueOf(carDataObj.getSocJumpAlarm())));
            if (carDataObj.getCaterpillaringFunction() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("caterpillaringFunction"), Bytes.toBytes(String.valueOf(carDataObj.getCaterpillaringFunction())));
            if (carDataObj.getSatNum() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("satNum"), Bytes.toBytes(String.valueOf(carDataObj.getSatNum())));
            if (carDataObj.getSocLowAlarm() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("socLowAlarm"), Bytes.toBytes(String.valueOf(carDataObj.getSocLowAlarm())));
            if (carDataObj.getChargingGunConnectionState() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("chargingGunConnectionState"), Bytes.toBytes(String.valueOf(carDataObj.getChargingGunConnectionState())));
            if (carDataObj.getMinTemperatureSubSystemNum() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("minTemperatureSubSystemNum"), Bytes.toBytes(String.valueOf(carDataObj.getMinTemperatureSubSystemNum())));
            if (carDataObj.getChargedElectronicLockStatus() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("chargedElectronicLockStatus"), Bytes.toBytes(String.valueOf(carDataObj.getChargedElectronicLockStatus())));
            if (carDataObj.getMaxVoltageBatteryNum() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("maxVoltageBatteryNum"), Bytes.toBytes(String.valueOf(carDataObj.getMaxVoltageBatteryNum())));
            if (carDataObj.getSingleBatteryOverVoltageAlarm() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("singleBatteryOverVoltageAlarm"), Bytes.toBytes(String.valueOf(carDataObj.getSingleBatteryOverVoltageAlarm())));
            if (carDataObj.getOtherFaultCount() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("otherFaultCount"), Bytes.toBytes(String.valueOf(carDataObj.getOtherFaultCount())));
            if (carDataObj.getVehicleStorageDeviceOvervoltageAlarm() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("vehicleStorageDeviceOvervoltageAlarm"), Bytes.toBytes(String.valueOf(carDataObj.getVehicleStorageDeviceOvervoltageAlarm())));
            if (carDataObj.getBrakeSystemAlarm() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("brakeSystemAlarm"), Bytes.toBytes(String.valueOf(carDataObj.getBrakeSystemAlarm())));
            if (!carDataObj.getServerTime().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("serverTime"), Bytes.toBytes(carDataObj.getServerTime()));
            if (carDataObj.getRechargeableStorageDevicesFaultCount() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("rechargeableStorageDevicesFaultCount"), Bytes.toBytes(String.valueOf(carDataObj.getRechargeableStorageDevicesFaultCount())));
            if (carDataObj.getDriveMotorTemperatureAlarm() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("driveMotorTemperatureAlarm"), Bytes.toBytes(String.valueOf(carDataObj.getDriveMotorTemperatureAlarm())));
            if (carDataObj.getGearBrakeForce() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("gearBrakeForce"), Bytes.toBytes(String.valueOf(carDataObj.getGearBrakeForce())));
            if (carDataObj.getDcdcStatusAlarm() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("dcdcStatusAlarm"), Bytes.toBytes(String.valueOf(carDataObj.getDcdcStatusAlarm())));
            if (!carDataObj.getDriveMotorFaultCodes().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("driveMotorFaultCodes"), Bytes.toBytes(carDataObj.getDriveMotorFaultCodes()));
            if (!carDataObj.getDeviceType().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("deviceType"), Bytes.toBytes(carDataObj.getDeviceType()));
            if (carDataObj.getVehicleSpeed() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("vehicleSpeed"), Bytes.toBytes(String.valueOf(carDataObj.getVehicleSpeed())));
            if (carDataObj.getChargingTimeExtensionReason() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("chargingTimeExtensionReason"), Bytes.toBytes(String.valueOf(carDataObj.getChargingTimeExtensionReason())));
            if (carDataObj.getCurrentBatteryStartNum() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("currentBatteryStartNum"), Bytes.toBytes(String.valueOf(carDataObj.getCurrentBatteryStartNum())));
            if (!carDataObj.getBatteryVoltage().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("batteryVoltage"), Bytes.toBytes(carDataObj.getBatteryVoltage()));
            if (carDataObj.getChargeSystemVoltage() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("chargeSystemVoltage"), Bytes.toBytes(String.valueOf(carDataObj.getChargeSystemVoltage())));
            if (carDataObj.getCurrentBatteryCount() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("currentBatteryCount"), Bytes.toBytes(String.valueOf(carDataObj.getCurrentBatteryCount())));
            if (carDataObj.getBatteryCount() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("batteryCount"), Bytes.toBytes(String.valueOf(carDataObj.getBatteryCount())));
            if (carDataObj.getChildSystemNum() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("childSystemNum"), Bytes.toBytes(String.valueOf(carDataObj.getChildSystemNum())));
            if (carDataObj.getChargeSystemCurrent() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("chargeSystemCurrent"), Bytes.toBytes(String.valueOf(carDataObj.getChargeSystemCurrent())));
            if (!carDataObj.getGpsTime().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("gpsTime"), Bytes.toBytes(carDataObj.getGpsTime()));
            if (carDataObj.getEngineFaultCount() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("engineFaultCount"), Bytes.toBytes(String.valueOf(carDataObj.getEngineFaultCount())));
            if (!carDataObj.getCarId().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("carId"), Bytes.toBytes(carDataObj.getCarId()));
            if (carDataObj.getCurrentElectricity() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("currentElectricity"), Bytes.toBytes(String.valueOf(carDataObj.getCurrentElectricity())));
            if (carDataObj.getSingleBatteryUnderVoltageAlarm() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("singleBatteryUnderVoltageAlarm"), Bytes.toBytes(String.valueOf(carDataObj.getSingleBatteryUnderVoltageAlarm())));
            if (carDataObj.getMaxVoltageBatterySubSystemNum() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("maxVoltageBatterySubSystemNum"), Bytes.toBytes(String.valueOf(carDataObj.getMaxVoltageBatterySubSystemNum())));
            if (carDataObj.getMinTemperatureProbe() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("minTemperatureProbe"), Bytes.toBytes(String.valueOf(carDataObj.getMinTemperatureProbe())));
            if (carDataObj.getDriveMotorNum() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("driveMotorNum"), Bytes.toBytes(String.valueOf(carDataObj.getDriveMotorNum())));
            if (carDataObj.getTotalVoltage() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("totalVoltage"), Bytes.toBytes(String.valueOf(carDataObj.getTotalVoltage())));
            if (carDataObj.getTemperatureDifferenceAlarm() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("temperatureDifferenceAlarm"), Bytes.toBytes(String.valueOf(carDataObj.getTemperatureDifferenceAlarm())));
            if (carDataObj.getMaxAlarmLevel() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("maxAlarmLevel"), Bytes.toBytes(String.valueOf(carDataObj.getMaxAlarmLevel())));
            if (carDataObj.getStatus() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("status"), Bytes.toBytes(String.valueOf(carDataObj.getStatus())));
            if (carDataObj.getGeerPosition() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("geerPosition"), Bytes.toBytes(String.valueOf(carDataObj.getGeerPosition())));
            if (carDataObj.getAverageEnergyConsumption() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("averageEnergyConsumption"), Bytes.toBytes(String.valueOf(carDataObj.getAverageEnergyConsumption())));
            if (carDataObj.getMinVoltageBattery() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("minVoltageBattery"), Bytes.toBytes(String.valueOf(carDataObj.getMinVoltageBattery())));
            if (carDataObj.getGeerStatus() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("geerStatus"), Bytes.toBytes(String.valueOf(carDataObj.getGeerStatus())));
            if (carDataObj.getControllerInputVoltage() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("controllerInputVoltage"), Bytes.toBytes(String.valueOf(carDataObj.getControllerInputVoltage())));
            if (carDataObj.getControllerTemperature() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("controllerTemperature"), Bytes.toBytes(String.valueOf(carDataObj.getControllerTemperature())));
            if (carDataObj.getRevolutionSpeed() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("revolutionSpeed"), Bytes.toBytes(String.valueOf(carDataObj.getRevolutionSpeed())));
            if (carDataObj.getNum() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("num"), Bytes.toBytes(String.valueOf(carDataObj.getNum())));
            if (carDataObj.getControllerDcBusCurrent() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("controllerDcBusCurrent"), Bytes.toBytes(String.valueOf(carDataObj.getControllerDcBusCurrent())));
            if (carDataObj.getTemperature() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("temperature"), Bytes.toBytes(String.valueOf(carDataObj.getTemperature())));
            if (carDataObj.getTorque() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("torque"), Bytes.toBytes(String.valueOf(carDataObj.getTorque())));
            if (carDataObj.getState() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("state"), Bytes.toBytes(String.valueOf(carDataObj.getState())));
            if (carDataObj.getMinVoltageBatteryNum() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("minVoltageBatteryNum"), Bytes.toBytes(String.valueOf(carDataObj.getMinVoltageBatteryNum())));
            if (!carDataObj.getValidGps().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("validGps"), Bytes.toBytes(carDataObj.getValidGps()));
            if (!carDataObj.getEngineFaultCodes().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("engineFaultCodes"), Bytes.toBytes(carDataObj.getEngineFaultCodes()));
            if (carDataObj.getMinTemperatureValue() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("minTemperatureValue"), Bytes.toBytes(String.valueOf(carDataObj.getMinTemperatureValue())));
            if (carDataObj.getChargeStatus() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("chargeStatus"), Bytes.toBytes(String.valueOf(carDataObj.getChargeStatus())));
            if (!carDataObj.getIgnitionTime().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("ignitionTime"), Bytes.toBytes(carDataObj.getIgnitionTime()));
            if (carDataObj.getTotalOdometer() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("totalOdometer"), Bytes.toBytes(String.valueOf(carDataObj.getTotalOdometer())));
            if (carDataObj.getAlti() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("alti"), Bytes.toBytes(String.valueOf(carDataObj.getAlti())));
            if (carDataObj.getSpeed() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("speed"), Bytes.toBytes(String.valueOf(carDataObj.getSpeed())));
            if (carDataObj.getSocHighAlarm() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("socHighAlarm"), Bytes.toBytes(String.valueOf(carDataObj.getSocHighAlarm())));
            if (carDataObj.getVehicleStorageDeviceUndervoltageAlarm() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("vehicleStorageDeviceUndervoltageAlarm"), Bytes.toBytes(String.valueOf(carDataObj.getVehicleStorageDeviceUndervoltageAlarm())));
            if (carDataObj.getTotalCurrent() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("totalCurrent"), Bytes.toBytes(String.valueOf(carDataObj.getTotalCurrent())));
            if (carDataObj.getBatteryAlarm() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("batteryAlarm"), Bytes.toBytes(String.valueOf(carDataObj.getBatteryAlarm())));
            if (carDataObj.getRechargeableStorageDeviceMismatchAlarm() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("rechargeableStorageDeviceMismatchAlarm"), Bytes.toBytes(String.valueOf(carDataObj.getRechargeableStorageDeviceMismatchAlarm())));
            if (carDataObj.getIsHistoryPoi() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("isHistoryPoi"), Bytes.toBytes(String.valueOf(carDataObj.getIsHistoryPoi())));
            if (carDataObj.getVehiclePureDeviceTypeOvercharge() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("vehiclePureDeviceTypeOvercharge"), Bytes.toBytes(String.valueOf(carDataObj.getVehiclePureDeviceTypeOvercharge())));
            if (carDataObj.getMaxVoltageBattery() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("maxVoltageBattery"), Bytes.toBytes(String.valueOf(carDataObj.getMaxVoltageBattery())));
            if (carDataObj.getDcdcTemperatureAlarm() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("dcdcTemperatureAlarm"), Bytes.toBytes(String.valueOf(carDataObj.getDcdcTemperatureAlarm())));
            if (!carDataObj.getIsValidGps().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("isValidGps"), Bytes.toBytes(carDataObj.getIsValidGps()));
            if (!carDataObj.getLastUpdatedTime().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("lastUpdatedTime"), Bytes.toBytes(carDataObj.getLastUpdatedTime()));
            if (carDataObj.getDriveMotorControllerTemperatureAlarm() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("driveMotorControllerTemperatureAlarm"), Bytes.toBytes(String.valueOf(carDataObj.getDriveMotorControllerTemperatureAlarm())));
            if (!carDataObj.getProbeTemperatures().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("probeTemperatures"), Bytes.toBytes(carDataObj.getProbeTemperatures()));
            if (carDataObj.getChargeTemperatureProbeNum() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("chargeTemperatureProbeNum"), Bytes.toBytes(String.valueOf(carDataObj.getChargeTemperatureProbeNum())));
            if (carDataObj.getIgniteCumulativeMileage() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("igniteCumulativeMileage"), Bytes.toBytes(String.valueOf(carDataObj.getIgniteCumulativeMileage())));
            if (carDataObj.getDcStatus() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("dcStatus"), Bytes.toBytes(String.valueOf(carDataObj.getDcStatus())));
            if (!carDataObj.getRepay().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("repay"), Bytes.toBytes(carDataObj.getRepay()));
            if (carDataObj.getMaxTemperatureSubSystemNum() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("maxTemperatureSubSystemNum"), Bytes.toBytes(String.valueOf(carDataObj.getMaxTemperatureSubSystemNum())));
            if (carDataObj.getCarStatus() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("carStatus"), Bytes.toBytes(String.valueOf(carDataObj.getCarStatus())));
            if (carDataObj.getMinVoltageBatterySubSystemNum() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("minVoltageBatterySubSystemNum"), Bytes.toBytes(String.valueOf(carDataObj.getMinVoltageBatterySubSystemNum())));
            if (carDataObj.getHeading() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("heading"), Bytes.toBytes(String.valueOf(carDataObj.getHeading())));
            if (carDataObj.getDriveMotorFaultCount() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("driveMotorFaultCount"), Bytes.toBytes(String.valueOf(carDataObj.getDriveMotorFaultCount())));
            if (!carDataObj.getTuid().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("tuid"), Bytes.toBytes(carDataObj.getTuid()));
            if (carDataObj.getEnergyRecoveryStatus() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("energyRecoveryStatus"), Bytes.toBytes(String.valueOf(carDataObj.getEnergyRecoveryStatus())));
            if (carDataObj.getFireStatus() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("fireStatus"), Bytes.toBytes(String.valueOf(carDataObj.getFireStatus())));
            if (!carDataObj.getTargetType().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("targetType"), Bytes.toBytes(carDataObj.getTargetType()));
            if (carDataObj.getMaxTemperatureProbe() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("maxTemperatureProbe"), Bytes.toBytes(String.valueOf(carDataObj.getMaxTemperatureProbe())));
            if (!carDataObj.getRechargeableStorageDevicesFaultCodes().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("rechargeableStorageDevicesFaultCodes"), Bytes.toBytes(carDataObj.getRechargeableStorageDevicesFaultCodes()));
            if (carDataObj.getCarMode() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("carMode"), Bytes.toBytes(String.valueOf(carDataObj.getCarMode())));
            if (carDataObj.getHighVoltageInterlockStateAlarm() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("highVoltageInterlockStateAlarm"), Bytes.toBytes(String.valueOf(carDataObj.getHighVoltageInterlockStateAlarm())));
            if (carDataObj.getInsulationAlarm() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("insulationAlarm"), Bytes.toBytes(String.valueOf(carDataObj.getInsulationAlarm())));
            if (carDataObj.getMileageInformation() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("mileageInformation"), Bytes.toBytes(String.valueOf(carDataObj.getMileageInformation())));
            if (carDataObj.getMaxTemperatureValue() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("maxTemperatureValue"), Bytes.toBytes(String.valueOf(carDataObj.getMaxTemperatureValue())));
            if (carDataObj.getOtherFaultCodes().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("otherFaultCodes"), Bytes.toBytes(carDataObj.getOtherFaultCodes()));
            if (carDataObj.getRemainPower() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("remainPower"), Bytes.toBytes(String.valueOf(carDataObj.getRemainPower())));
            if (carDataObj.getInsulateResistance() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("insulateResistance"), Bytes.toBytes(String.valueOf(carDataObj.getInsulateResistance())));
            if (carDataObj.getBatteryLowTemperatureHeater() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("batteryLowTemperatureHeater"), Bytes.toBytes(String.valueOf(carDataObj.getBatteryLowTemperatureHeater())));
            if (!carDataObj.getFuelConsumption100km().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("fuelConsumption100km"), Bytes.toBytes(carDataObj.getFuelConsumption100km()));
            if (!carDataObj.getFuelConsumption().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("fuelConsumption"), Bytes.toBytes(carDataObj.getFuelConsumption()));
            if (!carDataObj.getEngineSpeed().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("engineSpeed"), Bytes.toBytes(carDataObj.getEngineSpeed()));
            if (!carDataObj.getEngineStatus().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("engineStatus"), Bytes.toBytes(carDataObj.getEngineStatus()));
            if (carDataObj.getTrunk() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("trunk"), Bytes.toBytes(String.valueOf(carDataObj.getTrunk())));
            if (carDataObj.getLowBeam() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("lowBeam"), Bytes.toBytes(String.valueOf(carDataObj.getLowBeam())));
            if (!carDataObj.getTriggerLatchOverheatProtect().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("triggerLatchOverheatProtect"), Bytes.toBytes(carDataObj.getTriggerLatchOverheatProtect()));
            if (carDataObj.getTurnLndicatorRight() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("turnLndicatorRight"), Bytes.toBytes(String.valueOf(carDataObj.getTurnLndicatorRight())));
            if (carDataObj.getHighBeam() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("highBeam"), Bytes.toBytes(String.valueOf(carDataObj.getHighBeam())));
            if (carDataObj.getTurnLndicatorLeft() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("turnLndicatorLeft"), Bytes.toBytes(String.valueOf(carDataObj.getTurnLndicatorLeft())));
            if (carDataObj.getBcuSwVers() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("bcuSwVers"), Bytes.toBytes(String.valueOf(carDataObj.getBcuSwVers())));
            if (carDataObj.getBcuHwVers() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("bcuHwVers"), Bytes.toBytes(String.valueOf(carDataObj.getBcuHwVers())));
            if (carDataObj.getBcuOperMod() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("bcuOperMod"), Bytes.toBytes(String.valueOf(carDataObj.getBcuOperMod())));
            if (carDataObj.getChrgEndReason() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("chrgEndReason"), Bytes.toBytes(String.valueOf(carDataObj.getChrgEndReason())));
            if (!carDataObj.getBCURegenEngDisp().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("BCURegenEngDisp"), Bytes.toBytes(carDataObj.getBCURegenEngDisp()));
            if (carDataObj.getBCURegenCpDisp() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("BCURegenCpDisp"), Bytes.toBytes(String.valueOf(carDataObj.getBCURegenCpDisp())));
            if (carDataObj.getBcuChrgMod() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("bcuChrgMod"), Bytes.toBytes(String.valueOf(carDataObj.getBcuChrgMod())));
            if (carDataObj.getBatteryChargeStatus() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("batteryChargeStatus"), Bytes.toBytes(String.valueOf(carDataObj.getBatteryChargeStatus())));
            if (!carDataObj.getBcuFaultCodes().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("BcuFaultCodes"), Bytes.toBytes(carDataObj.getBcuFaultCodes()));
            if (carDataObj.getBcuFltRnk() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("bcuFltRnk"), Bytes.toBytes(String.valueOf(carDataObj.getBcuFltRnk())));
            if (!carDataObj.getBattPoleTOver().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("battPoleTOver"), Bytes.toBytes(carDataObj.getBattPoleTOver()));
            if (carDataObj.getBcuSOH() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("bcuSOH"), Bytes.toBytes(String.valueOf(carDataObj.getBcuSOH())));
            if (carDataObj.getBattIntrHeatActive() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("battIntrHeatActive"), Bytes.toBytes(String.valueOf(carDataObj.getBattIntrHeatActive())));
            if (carDataObj.getBattIntrHeatReq() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("battIntrHeatReq"), Bytes.toBytes(String.valueOf(carDataObj.getBattIntrHeatReq())));
            if (!carDataObj.getBCUBattTarT().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("BCUBattTarT"), Bytes.toBytes(carDataObj.getBCUBattTarT()));
            if (carDataObj.getBattExtHeatReq() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("battExtHeatReq"), Bytes.toBytes(String.valueOf(carDataObj.getBattExtHeatReq())));
            if (!carDataObj.getBCUMaxChrgPwrLongT().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("BCUMaxChrgPwrLongT"), Bytes.toBytes(carDataObj.getBCUMaxChrgPwrLongT()));
            if (!carDataObj.getBCUMaxDchaPwrLongT().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("BCUMaxDchaPwrLongT"), Bytes.toBytes(carDataObj.getBCUMaxDchaPwrLongT()));
            if (!carDataObj.getBCUTotalRegenEngDisp().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("BCUTotalRegenEngDisp"), Bytes.toBytes(carDataObj.getBCUTotalRegenEngDisp()));
            if (!carDataObj.getBCUTotalRegenCpDisp().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("BCUTotalRegenCpDisp"), Bytes.toBytes(carDataObj.getBCUTotalRegenCpDisp()));
            if (carDataObj.getDcdcFltRnk() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("dcdcFltRnk"), Bytes.toBytes(String.valueOf(carDataObj.getDcdcFltRnk())));
            if (!carDataObj.getDcdcFaultCode().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("DcdcFaultCode"), Bytes.toBytes(carDataObj.getDcdcFaultCode()));
            if (carDataObj.getDcdcOutpCrrt() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("dcdcOutpCrrt"), Bytes.toBytes(String.valueOf(carDataObj.getDcdcOutpCrrt())));
            if (carDataObj.getDcdcOutpU() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("dcdcOutpU"), Bytes.toBytes(String.valueOf(carDataObj.getDcdcOutpU())));
            if (carDataObj.getDcdcAvlOutpPwr() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("dcdcAvlOutpPwr"), Bytes.toBytes(String.valueOf(carDataObj.getDcdcAvlOutpPwr())));
            if (!carDataObj.getAbsActiveStatus().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("absActiveStatus"), Bytes.toBytes(carDataObj.getAbsActiveStatus()));
            if (!carDataObj.getAbsStatus().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("absStatus"), Bytes.toBytes(carDataObj.getAbsStatus()));
            if (!carDataObj.getVcuBrkErr().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("VcuBrkErr"), Bytes.toBytes(carDataObj.getVcuBrkErr()));
            if (!carDataObj.getEPB_AchievedClampForce().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("EPB_AchievedClampForce"), Bytes.toBytes(carDataObj.getEPB_AchievedClampForce()));
            if (!carDataObj.getEpbSwitchPosition().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("epbSwitchPosition"), Bytes.toBytes(carDataObj.getEpbSwitchPosition()));
            if (!carDataObj.getEpbStatus().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("epbStatus"), Bytes.toBytes(carDataObj.getEpbStatus()));
            if (!carDataObj.getEspActiveStatus().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("espActiveStatus"), Bytes.toBytes(carDataObj.getEspActiveStatus()));
            if (!carDataObj.getEspFunctionStatus().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("espFunctionStatus"), Bytes.toBytes(carDataObj.getEspFunctionStatus()));
            if (!carDataObj.getESP_TCSFailStatus().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("ESP_TCSFailStatus"), Bytes.toBytes(carDataObj.getESP_TCSFailStatus()));
            if (!carDataObj.getHhcActive().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("hhcActive"), Bytes.toBytes(carDataObj.getHhcActive()));
            if (!carDataObj.getTcsActive().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("tcsActive"), Bytes.toBytes(carDataObj.getTcsActive()));
            if (!carDataObj.getEspMasterCylinderBrakePressure().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("espMasterCylinderBrakePressure"), Bytes.toBytes(carDataObj.getEspMasterCylinderBrakePressure()));
            if (!carDataObj.getESP_MasterCylinderBrakePressureValid().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("ESP_MasterCylinderBrakePressureValid"), Bytes.toBytes(carDataObj.getESP_MasterCylinderBrakePressureValid()));
            if (!carDataObj.getEspTorqSensorStatus().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("espTorqSensorStatus"), Bytes.toBytes(carDataObj.getEspTorqSensorStatus()));
            if (!carDataObj.getEPS_EPSFailed().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("EPS_EPSFailed"), Bytes.toBytes(carDataObj.getEPS_EPSFailed()));
            if (!carDataObj.getSasFailure().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("sasFailure"), Bytes.toBytes(carDataObj.getSasFailure()));
            if (!carDataObj.getSasSteeringAngleSpeed().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("sasSteeringAngleSpeed"), Bytes.toBytes(carDataObj.getSasSteeringAngleSpeed()));
            if (!carDataObj.getSasSteeringAngle().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("sasSteeringAngle"), Bytes.toBytes(carDataObj.getSasSteeringAngle()));
            if (!carDataObj.getSasSteeringAngleValid().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("sasSteeringAngleValid"), Bytes.toBytes(carDataObj.getSasSteeringAngleValid()));
            if (!carDataObj.getEspSteeringTorque().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("espSteeringTorque"), Bytes.toBytes(carDataObj.getEspSteeringTorque()));
            if (carDataObj.getAcReq() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("acReq"), Bytes.toBytes(String.valueOf(carDataObj.getAcReq())));
            if (carDataObj.getAcSystemFailure() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("acSystemFailure"), Bytes.toBytes(String.valueOf(carDataObj.getAcSystemFailure())));
            if (carDataObj.getPtcPwrAct() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("ptcPwrAct"), Bytes.toBytes(String.valueOf(carDataObj.getPtcPwrAct())));
            if (carDataObj.getPlasmaStatus() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("plasmaStatus"), Bytes.toBytes(String.valueOf(carDataObj.getPlasmaStatus())));
            if (carDataObj.getBattInTemperature() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("battInTemperature"), Bytes.toBytes(String.valueOf(carDataObj.getBattInTemperature())));
            if (!carDataObj.getBattWarmLoopSts().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("battWarmLoopSts"), Bytes.toBytes(carDataObj.getBattWarmLoopSts()));
            if (!carDataObj.getBattCoolngLoopSts().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("battCoolngLoopSts"), Bytes.toBytes(carDataObj.getBattCoolngLoopSts()));
            if (!carDataObj.getBattCoolActv().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("battCoolActv"), Bytes.toBytes(carDataObj.getBattCoolActv()));
            if (carDataObj.getMotorOutTemperature() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("motorOutTemperature"), Bytes.toBytes(String.valueOf(carDataObj.getMotorOutTemperature())));
            if (!carDataObj.getPowerStatusFeedBack().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("powerStatusFeedBack"), Bytes.toBytes(String.valueOf(carDataObj.getPowerStatusFeedBack())));
            if (carDataObj.getAC_RearDefrosterSwitch() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("AC_RearDefrosterSwitch"), Bytes.toBytes(String.valueOf(carDataObj.getAC_RearDefrosterSwitch())));
            if (carDataObj.getRearFoglamp() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("rearFoglamp"), Bytes.toBytes(String.valueOf(carDataObj.getRearFoglamp())));
            if (carDataObj.getDriverDoorLock() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("driverDoorLock"), Bytes.toBytes(String.valueOf(carDataObj.getDriverDoorLock())));
            if (carDataObj.getAcDriverReqTemp() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("acDriverReqTemp"), Bytes.toBytes(String.valueOf(carDataObj.getAcDriverReqTemp())));
            if (carDataObj.getKeyAlarm() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("keyAlarm"), Bytes.toBytes(String.valueOf(carDataObj.getKeyAlarm())));
            if (carDataObj.getAirCleanStsRemind() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("airCleanStsRemind"), Bytes.toBytes(String.valueOf(carDataObj.getAirCleanStsRemind())));
            if (carDataObj.getRecycleType() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("recycleType"), Bytes.toBytes(String.valueOf(carDataObj.getRecycleType())));
            if (!carDataObj.getStartControlsignal().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("startControlsignal"), Bytes.toBytes(carDataObj.getStartControlsignal()));
            if (carDataObj.getAirBagWarningLamp() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("airBagWarningLamp"), Bytes.toBytes(String.valueOf(carDataObj.getAirBagWarningLamp())));
            if (carDataObj.getFrontDefrosterSwitch() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("frontDefrosterSwitch"), Bytes.toBytes(String.valueOf(carDataObj.getFrontDefrosterSwitch())));
            if (!carDataObj.getFrontBlowType().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("frontBlowType"), Bytes.toBytes(carDataObj.getFrontBlowType()));
            if (carDataObj.getFrontReqWindLevel() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("frontReqWindLevel"), Bytes.toBytes(String.valueOf(carDataObj.getFrontReqWindLevel())));
            if (!carDataObj.getBcmFrontWiperStatus().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("bcmFrontWiperStatus"), Bytes.toBytes(carDataObj.getBcmFrontWiperStatus()));
            if (!carDataObj.getTmsPwrAct().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("tmsPwrAct"), Bytes.toBytes(carDataObj.getTmsPwrAct()));
            if (carDataObj.getKeyUndetectedAlarmSign() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("keyUndetectedAlarmSign"), Bytes.toBytes(String.valueOf(carDataObj.getKeyUndetectedAlarmSign())));
            if (!carDataObj.getPositionLamp().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("positionLamp"), Bytes.toBytes(carDataObj.getPositionLamp()));
            if (carDataObj.getDriverReqTempModel() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("driverReqTempModel"), Bytes.toBytes(String.valueOf(carDataObj.getDriverReqTempModel())));
            if (carDataObj.getTurnLightSwitchSts() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("turnLightSwitchSts"), Bytes.toBytes(String.valueOf(carDataObj.getTurnLightSwitchSts())));
            if (carDataObj.getAutoHeadlightStatus() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("autoHeadlightStatus"), Bytes.toBytes(String.valueOf(carDataObj.getAutoHeadlightStatus())));
            if (carDataObj.getDriverDoor() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("driverDoor"), Bytes.toBytes(String.valueOf(carDataObj.getDriverDoor())));
            if (!carDataObj.getIpuFaultCodes().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("IpuFaultCodes"), Bytes.toBytes(carDataObj.getIpuFaultCodes()));
            if (carDataObj.getFrntIpuFltRnk() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("frntIpuFltRnk"), Bytes.toBytes(String.valueOf(carDataObj.getFrntIpuFltRnk())));
            if (!carDataObj.getFrontIpuSwVers().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("frontIpuSwVers"), Bytes.toBytes(carDataObj.getFrontIpuSwVers()));
            if (carDataObj.getFrontIpuHwVers() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("frontIpuHwVers"), Bytes.toBytes(String.valueOf(carDataObj.getFrontIpuHwVers())));
            if (carDataObj.getFrntMotTqLongTermMax() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("frntMotTqLongTermMax"), Bytes.toBytes(String.valueOf(carDataObj.getFrntMotTqLongTermMax())));
            if (carDataObj.getFrntMotTqLongTermMin() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("frntMotTqLongTermMin"), Bytes.toBytes(String.valueOf(carDataObj.getFrntMotTqLongTermMin())));
            if (carDataObj.getCpvValue() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("cpvValue"), Bytes.toBytes(String.valueOf(carDataObj.getCpvValue())));
            if (carDataObj.getObcChrgSts() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("obcChrgSts"), Bytes.toBytes(String.valueOf(carDataObj.getObcChrgSts())));
            if (!carDataObj.getObcFltRnk().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("obcFltRnk"), Bytes.toBytes(carDataObj.getObcFltRnk()));
            if (carDataObj.getObcChrgInpAcI() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("obcChrgInpAcI"), Bytes.toBytes(String.valueOf(carDataObj.getObcChrgInpAcI())));
            if (carDataObj.getObcChrgInpAcU() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("obcChrgInpAcU"), Bytes.toBytes(String.valueOf(carDataObj.getObcChrgInpAcU())));
            if (carDataObj.getObcChrgDcI() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("obcChrgDcI"), Bytes.toBytes(String.valueOf(carDataObj.getObcChrgDcI())));
            if (carDataObj.getObcChrgDcU() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("obcChrgDcU"), Bytes.toBytes(String.valueOf(carDataObj.getObcChrgDcU())));
            if (carDataObj.getObcTemperature() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("obcTemperature"), Bytes.toBytes(String.valueOf(carDataObj.getObcTemperature())));
            if (carDataObj.getObcMaxChrgOutpPwrAvl() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("obcMaxChrgOutpPwrAvl"), Bytes.toBytes(String.valueOf(carDataObj.getObcMaxChrgOutpPwrAvl())));
            if (carDataObj.getPassengerBuckleSwitch() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("passengerBuckleSwitch"), Bytes.toBytes(String.valueOf(carDataObj.getPassengerBuckleSwitch())));
            if (!carDataObj.getCrashlfo().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("crashlfo"), Bytes.toBytes(carDataObj.getCrashlfo()));
            if (carDataObj.getDriverBuckleSwitch() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("driverBuckleSwitch"), Bytes.toBytes(String.valueOf(carDataObj.getDriverBuckleSwitch())));
            if (!carDataObj.getEngineStartHibit().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("engineStartHibit"), Bytes.toBytes(carDataObj.getEngineStartHibit()));
            if (!carDataObj.getLockCommand().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("lockCommand"), Bytes.toBytes(carDataObj.getLockCommand()));
            if (!carDataObj.getSearchCarReq().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("searchCarReq"), Bytes.toBytes(carDataObj.getSearchCarReq()));
            if (!carDataObj.getAcTempValueReq().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("acTempValueReq"), Bytes.toBytes(carDataObj.getAcTempValueReq()));
            if (!carDataObj.getVcuFaultCode().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("VcuFaultCode"), Bytes.toBytes(carDataObj.getVcuFaultCode()));
            if (!carDataObj.getVcuErrAmnt().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("vcuErrAmnt"), Bytes.toBytes(carDataObj.getVcuErrAmnt()));
            if (carDataObj.getVcuSwVers() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("vcuSwVers"), Bytes.toBytes(String.valueOf(carDataObj.getVcuSwVers())));
            if (carDataObj.getVcuHwVers() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("vcuHwVers"), Bytes.toBytes(String.valueOf(carDataObj.getVcuHwVers())));
            if (!carDataObj.getLowSpdWarnStatus().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("lowSpdWarnStatus"), Bytes.toBytes(carDataObj.getLowSpdWarnStatus()));
            if (carDataObj.getLowBattChrgRqe() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("lowBattChrgRqe"), Bytes.toBytes(String.valueOf(carDataObj.getLowBattChrgRqe())));
            if (!carDataObj.getLowBattChrgSts().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("lowBattChrgSts"), Bytes.toBytes(carDataObj.getLowBattChrgSts()));
            if (carDataObj.getLowBattU() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("lowBattU"), Bytes.toBytes(String.valueOf(carDataObj.getLowBattU())));
            if (carDataObj.getHandlebrakeStatus() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("handlebrakeStatus"), Bytes.toBytes(String.valueOf(carDataObj.getHandlebrakeStatus())));
            if (!carDataObj.getShiftPositionValid().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("shiftPositionValid"), Bytes.toBytes(carDataObj.getShiftPositionValid()));
            if (!carDataObj.getAccPedalValid().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("accPedalValid"), Bytes.toBytes(carDataObj.getAccPedalValid()));
            if (carDataObj.getDriveMode() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("driveMode"), Bytes.toBytes(String.valueOf(carDataObj.getDriveMode())));
            if (carDataObj.getDriveModeButtonStatus() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("driveModeButtonStatus"), Bytes.toBytes(String.valueOf(carDataObj.getDriveModeButtonStatus())));
            if (carDataObj.getVCUSRSCrashOutpSts() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("VCUSRSCrashOutpSts"), Bytes.toBytes(String.valueOf(carDataObj.getVCUSRSCrashOutpSts())));
            if (carDataObj.getTextDispEna() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("textDispEna"), Bytes.toBytes(String.valueOf(carDataObj.getTextDispEna())));
            if (carDataObj.getCrsCtrlStatus() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("crsCtrlStatus"), Bytes.toBytes(String.valueOf(carDataObj.getCrsCtrlStatus())));
            if (carDataObj.getCrsTarSpd() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("crsTarSpd"), Bytes.toBytes(String.valueOf(carDataObj.getCrsTarSpd())));
            if (carDataObj.getCrsTextDisp() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("crsTextDisp"), Bytes.toBytes(String.valueOf(carDataObj.getCrsTextDisp())));
            if (carDataObj.getKeyOn() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("keyOn"), Bytes.toBytes(String.valueOf(carDataObj.getKeyOn())));
            if (carDataObj.getVehPwrlim() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("vehPwrlim"), Bytes.toBytes(String.valueOf(carDataObj.getVehPwrlim())));
            if (!carDataObj.getVehCfgInfo().isEmpty())
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("vehCfgInfo"), Bytes.toBytes(carDataObj.getVehCfgInfo()));
            if (carDataObj.getVacBrkPRmu() != -999999)
                put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("vacBrkPRmu"), Bytes.toBytes(carDataObj.getVacBrkPRmu()));
            put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("processTime"), Bytes.toBytes(DateUtil.getCurrentDateTime()));
        }catch (Exception ex){
            logger.error("封装 put 对象失败，异常信息"+ex.getMessage());
        }
        return put;
    }
}
