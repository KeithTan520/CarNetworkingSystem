package cn.tencent.s3.flink.streaming.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Date 2021/11/15 14:52
 * 此类用于封装车辆的所有采集并解析后的数据
 * 包括不仅限于 整车数据、位置数据、极值数据、告警数据等，200+字段
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarDataObj {
    // 档位驱动力状态	0：无驱动力 1：有驱动力
    private int gearDriveForce = -999999;
    // 电池单体一致性差报警	0：正常 1：异常
    private int batteryConsistencyDifferenceAlarm = -999999;
    // SOC,单位：%
    private int soc = -999999;
    // SOC跳变报警	0：正常 1：异常
    private int socJumpAlarm = -999999;
    // 蠕行功能状态	启动/关闭
    private int caterpillaringFunction= -999999;
    // 有效卫星数
    private int satNum = -999999;
    // SOC低报警	0：正常 1：异常
    private int socLowAlarm = -999999;
    // 充电枪连接状态	0:解锁1:锁定2:失败
    private int chargingGunConnectionState = -999999;
    //最低温度子系统号	有效值范围：1~250，0xFE表示异常，0xFF表示无效
    private int minTemperatureSubSystemNum = -999999;
    // 充电电子锁状态	0：解锁 1：锁止
    private int chargedElectronicLockStatus = -999999;
    //最高电压电池单体代号	有效值范围：1~250，0xFE表示异常，1xFF表示无效
    private int maxVoltageBatteryNum = -999999;
    // 终端时间
    private String terminalTime = "";
    //单体电池过压报警	0：正常 1：异常
    private int singleBatteryOverVoltageAlarm = -999999;
    // 其他故障总数 N4	有效值范围：0~252，0xFE表示异常，0xFF表示无效
    private int otherFaultCount = -999999;
    // 车载储能装置过压报警	0：正常 1：异常
    private int vehicleStorageDeviceOvervoltageAlarm = -999999;
    // 制动系统报警	0：正常 1：异常
    private int brakeSystemAlarm = -999999;
    //服务器时间
    private String serverTime = "";
    //车辆唯一编号
    private String vin = "";
    //可充电储能装置故障总数 N1	有效值范围：0~252，0xFE表示异常，0xFF表示无效
    private int rechargeableStorageDevicesFaultCount = -999999;
    // 驱动电机温度报警	0：正常 1：异常
    private int driveMotorTemperatureAlarm = -999999;
    // 档位制动力状态	0：无制动力 1：有制动力
    private int gearBrakeForce = -999999;
    //DC-DC状态报警	0：正常 1：异常
    private int dcdcStatusAlarm = -999999;
    //位置纬度
    private Double lat = -999999D;
    //驱动电机故障代码列表	每4个字节代表一个故障码，由厂家进行自定义
    private String driveMotorFaultCodes = "";
    // 驱动电机控制器温度报警	0：正常 1：异常
    private int driveMotorFaultCount = -999999;
    // 终端类型
    private String deviceType = "";
    //车速
    private Double vehicleSpeed = -999999D;
    //位置经度
    private Double lng = -999999D;
    //充电时间延长原因
    private int chargingTimeExtensionReason= -999999;
    //可充电储能子系统电压信息列表
    private String nevChargeSystemVoltageDtoList = "";
    //nevChargeSystemVoltageDtoList里的信息 本帧电池起始序号
    private int currentBatteryStartNum = -999999;
    // 单体电池电压列表	Array格式
    private String batteryVoltage = "";
    //可充电储能装置电压	V
    private Double chargeSystemVoltage = -999999D;
    //本帧电池单体总数
    private int currentBatteryCount = -999999;
    //电池单体总数
    private int batteryCount = -999999;
    //电池子系统号
    private int childSystemNum = -999999;
    //可充电储能装置电流	A
    private Double chargeSystemCurrent = -999999D;

    //位置时间
    private String gpsTime = "";
    //发动机故障总数 N3	有效值范围：0~252，0xFE表示异常，0xFF表示无效
    private int engineFaultCount = -999999;
    //车辆ID（可忽略）
    private String carId = "";
    //当前电量	单位：%，如传输值为900 为90%
    private Double currentElectricity = -999999D;
    //单体电池欠压报警	0：正常 1：异常
    private int singleBatteryUnderVoltageAlarm = -999999;
    //最高电压电池子系统号	有效值范围：1~250，0xFE表示异常，0xFF表示无效
    private int maxVoltageBatterySubSystemNum = -999999;
    //最低温度探针序号	有效值范围：1~250，0xFE表示异常，0xFF表示无效
    private int minTemperatureProbe = -999999;
    //驱动电机数量
    private int driveMotorNum = -999999;
    //总电压	单位：V，实际取值0.1~100V
    private Double totalVoltage = -999999D;
    //温度差异报警	0：正常 1：异常
    private int temperatureDifferenceAlarm = -999999;
    //最高报警等级	"有效值：0~3。0：无故障，1：1级故障 2：2级故障，3：3级故障。0xFE表示异常，0xFF表示无效"
    private int maxAlarmLevel = -999999;
    //车辆状态（可忽略）
    private int status = 0;
    //挡位位置
    private int geerPosition = -999999;
    //平均能耗（历史累积平均能耗）	单位：kWh/百公里
    private Double averageEnergyConsumption = -999999D;
    //电池单体电压最低值	单位：V，实际值=传输值*0.001，即实际为0~15V。0xFF,0xFE表示异常，0xFF,0xFF表示无效
    private Double minVoltageBattery = -999999D;
    //挡位状态
    private int geerStatus = -999999;
    //驱动电机数据
    private String driveMotorData = "";
    //driveMotorData里的信息 电机控制器输入电压	V
    private Double controllerInputVoltage = -999999D;
    //电机控制器温度	℃
    private Double controllerTemperature = -999999D;
    //电机转速
    private Double revolutionSpeed = -999999D;
    //电机数量
    private int num = -999999;
    //电机控制器直流母线电流	A
    private Double controllerDcBusCurrent = -999999D;
    //电机温度	℃
    private Double temperature = -999999D;
    //电机扭矩	Nm
    private Double torque = -999999D;
    //电机状态
    private int state = -999999;
    //最低电压电池单体代号	有效值范围：1~250，0xFE表示异常，0xFF表示无效
    private int minVoltageBatteryNum = -999999;
    //GPS是否有效（可忽略）
    private String validGps = "";
    //发动机故障列表	每4个字节代表一个故障码，由厂家进行自定义
    private String engineFaultCodes = "";
    //最低温度值	单位：℃。实际值：-40~210。0xFE表示异常，1xFF表示无效
    private Double minTemperatureValue = -999999D;
    //"0x01: 停车充电 0x02: 行车充电 0x03: 未充电  0x04:充电完成 0xFE: 异常 0xFF:无效"
    private int chargeStatus = -999999;
    //行程开始时间
    private String ignitionTime = "";
    //累计里程	单位：km
    private Double totalOdometer = -999999D;
    //位置海拔
    private Double alti = -999999D;
    //车速（可忽略）	单位：km/h
    private Double speed = -999999D;
    //SOC过高报警	0：正常 1：异常
    private int socHighAlarm = -999999;
    //车载储能装置欠压报警	0：正常 1：异常
    private int vehicleStorageDeviceUndervoltageAlarm = -999999;
    //总电流	单位：A，实际值 = 传输值 * 0.1-1000，即实际取值为-1000~1000A,
    private Double totalCurrent = -999999D;
    //电池高温报警	0：正常 1：异常
    private int batteryAlarm = -999999;
    //可充电储能系统不匹配报警	0：正常 1：异常
    private int rechargeableStorageDeviceMismatchAlarm = -999999;
    //是否历史轨迹点（可忽略）
    private int isHistoryPoi = -999999;
    //车载储能装置类型过充	0：正常 1：异常
    private int vehiclePureDeviceTypeOvercharge = -999999;
    //电池单体电压最高值	单位：V，实际值=传输值*0.001，即实际为0~15V。0xFF,0xFE表示异常，0xFF,0xFF表示无效
    private Double maxVoltageBattery = -999999D;
    //DC-DC温度报警	0：正常 1：异常
    private int dcdcTemperatureAlarm = -999999;
    //同validGps（可忽略）
    private String isValidGps = "";
    //最后回传时间
    private String lastUpdatedTime = "";
    //驱动电机控制器温度报警	0：正常 1：异常
    private int driveMotorControllerTemperatureAlarm = -999999;
    //可充电储能子系统温度信息列表	列表中包含序号85-86字段内容
    private String nevChargeSystemTemperatureDtoList = "" ;
    //nevChargeSystemTemperatureDtoList里的信息 电池模块温度列表
    private String probeTemperatures = "";
    //温度探针个数
    private int chargeTemperatureProbeNum = -999999;
    //行程开始的累计里程	单位：km
    private Double igniteCumulativeMileage = -999999D;
    //DCDC状态	0x01 工作 0x02断开 0xFE: 异常 0xFF:无效
    private int dcStatus = -999999;
    //是否补发	TRUE:补发数据 ； FALSE:实时数据
    private String repay = "";
    //最高温度子系统号	有效值范围：1~250，0xFE表示异常，0xFF表示无效
    private int maxTemperatureSubSystemNum = -999999;
    //车辆状态	0x01: 车辆启动状态，0x02：熄火状态 0x03：其他状态，0xFE：异常，0xFF：无效
    private int carStatus = -999999;
    //最低电压电池子系统代号	有效值范围：1~250，0xFE表示异常，0xFF表示无效
    private int minVoltageBatterySubSystemNum = -999999;
    //方位角度值
    private Double heading = -999999D;
    //前电机故障代码 json数据中，ecuErrCodeDataList数组中ecuType=4
    private String IpuFaultCodes = "";
    //TUID
    private String tuid = "";
    //能量回收状态	高/低
    private int energyRecoveryStatus = -999999;
    //点火状态	0：未点火 ；2：已点火
    private int fireStatus = -999999;
    //可忽略
    private String targetType = "";
    //最高温度探针序号	有效值范围：1~250，0xFE表示异常，0xFF表示无效
    private int maxTemperatureProbe = -999999;
    //可充电储能装置故障代码列表	每4个字节代表一个故障码，由厂家进行自定义
    private String rechargeableStorageDevicesFaultCodes = "";
    //运行模式	0x01: 纯电 0x02 混动 0x03 燃油 0xFE: 异常 0xFF: 无效
    private int carMode = -999999;
    //高压互锁状态报警	0：正常 1：异常
    private int highVoltageInterlockStateAlarm = -999999;
    //绝缘报警	0：正常 1：异常
    private int insulationAlarm = -999999;
    //续航里程信息	单位：km
    private int mileageInformation = -999999;
    //最高温度值	单位：℃。实际值：-40~210。0xFE表示异常，1xFF表示无效
    private Double maxTemperatureValue = -999999D;
    //其他故障代码列表	每4个字节代表一个故障码，由厂家进行自定义
    private String otherFaultCodes = "";
    //当前电量	%
    private Double remainPower = -999999D;
    //绝缘电阻	kΩ
    private int insulateResistance = -999999;
    //电池低温加热器状态	"0：停止加热 1：正在加热"
    private int batteryLowTemperatureHeater = -999999;
    //百公里油耗
    private String fuelConsumption100km = "";
    //百毫秒油耗
    private String fuelConsumption = "";
    //发动机速度
    private String engineSpeed = "";
    //发动机状态	0：stop  1：crank   2：running
    private String engineStatus = "";
    //行李箱门状态	"0x0:Close 0x1:Open"
    private int trunk = -999999;
    //近光灯工作状态 "0x0:OFF 0x1:ON 0x2:Not used 0x3:Reserved"
    private int lowBeam = -999999;
    //锁电机过热保护 "0x0:OFF 0x1:ON"
    private String triggerLatchOverheatProtect = "";
    //右转向灯信号 "0x0:OFF 0x1:ON 0x2:Not used 0x3:Error"
    private int turnLndicatorRight = -999999;
    //远光灯工作状态	"0x0:OFF 0x1:ON 0x2:Not used 0x3:Reserved"
    private int highBeam = -999999;
    //左转向灯信号	"0x0:OFF 0x1:ON 0x2:Not used 0x3:Error"
    private int turnLndicatorLeft = -999999;
    //BCU软件版本
    private int bcuSwVers = -999999;
    //BCU硬件版本
    private int bcuHwVers = -999999;
    //"0x0:Initializing 0x1:Standby 0x2:PreCharge 0x3:HVActive 0x4:Powerdown 0x5:Failure 0x7:ReadytoSleep"
    private int bcuOperMod = -999999;
    /**
     * 0x1:拔枪退出充电;0x2:交流充电CP导致充电结束;0x3:交直流充电开始VCU充电允许信号致充电结束;0x4:充电机交流断电压小于90V导致充电结束;
     * 0x5:CC信号导致充电结束;0x6:OBC充电状态小于1导致充电结束;0x7:交直流充电过程中模式异常导致充电结束;0x8:充电过程中VCU充电允许信号导致充电结束;
     * 0x9:VCU要求高压下电导致充电结束;0xA:充电结束阶段OBC状态不为1导致充电结束;0xB:充电模式7导致充电结束;0xC:进行直流充电流程前报文超时导致充电结束;
     * 0xD:充电开启前VCU充电允许信号导致充电结束;0xE:加热故障导致充电结束;0xF:OBCCAN报文超时导致充电结束;0x10:BCU内部故障导致充电结束;
     * 0x11:CC信号1min内累积3次错误导致充电结束;0x12:直流报文超时导致充电结束;0x13:CC2信号导致充电结束;0x14:CCS充电暂停导致充电结束;
     * 0x15:温度不在范围导致充电结束;0x16:充电桩CST导致充电结束;0x17:充电桩参数不合适导致充电结束"
     */
    private int chrgEndReason = -999999;
    //单次能量回收能量存储值	Kwh
    private String BCURegenEngDisp = "";
    //单次能量回收容量存储值	Ah
    private int BCURegenCpDisp = -999999;
    /**
     * 电池充电模式 0x0:nochargemode 0x1:10AhomeACcharge 0x2:10Acablecontrolcharge 0x3:16AACchargestake/cablecontrolcharge
     * 0x4:32AACchargestake 0x5:63AACchargestake 0x6:DCcharge 0x7:AC/DCmodevalid 0x8:ACsemi-connection
     * 0x9:ACchargeunconventionality 0xA:DCchargeunconventionality
     */
    private int bcuChrgMod = -999999;
    //电池充电状态 "0x0:uncharged 0x1:charging 0x2:fullofcharge 0x3:chargeend"
    private int batteryChargeStatus = -999999;
    //电池故障代码
    private int bcuFltRnk = -999999;
    //电池极注高温报警 "0x0:Noerror 0x1:error"
    private String battPoleTOver = "";
    //电池健康状态 %
    private Double bcuSOH = -999999D;
    //电池内部加热激活 0x0:no active 0x1:heat active 0x2:cool active 0x3:加热中止
    private int battIntrHeatActive = -999999;
    //电池热管理模式请求 "0x0:not request 0x1: heat without HVactive 0x2: heat with HVactive 0x3:cool"
    private int battIntrHeatReq = -999999;
    //电池热管理目标温度 ℃
    private String BCUBattTarT = "";
    //电池外部加热请求"0x0:notrequest 0x1:request"
    private int battExtHeatReq = -999999;
    //电池最大充电功率(长时)  kW
    private String BCUMaxChrgPwrLongT = "";
    //电池最大放电功率(长时)  kW
    private String BCUMaxDchaPwrLongT = "";
    //累计能量回收能量存储值  Kwh
    private String BCUTotalRegenEngDisp = "";
    //累计能量回收容量存储值  Ah
    private String BCUTotalRegenCpDisp = "";
    //DCDC故障等级 "0x0:无故障 0x1:性能限制 0x2:暂时停止工作 0x3:立即停止工作"
    private int dcdcFltRnk = -999999;
    //DCDC故障码 json数据中，ecuErrCodeDataList数组中ecuType=3
    private String DcdcFaultCode = "";
    //DCDC输出电流 A
    private Double dcdcOutpCrrt = -999999D;
    //DCDC输出电压 V
    private Double dcdcOutpU = -999999D;
    //当前可输出的功率 W
    private int dcdcAvlOutpPwr = -999999;
    //ABS工作状态 "0x0:NotActive 0x1:Active"
    private String absActiveStatus = "";
    //ABS故障 "0x0:No error 0x1:Error"
    private String absStatus = "";
    //EPB故障状态 "0x0:no error 0x1:not defined 0x2:not defined 0x3:error"
    private String VcuBrkErr = "";
    //EPB夹紧力 kN
    private String EPB_AchievedClampForce = "";
    //EPB开关位置 "0x0:no request 0x1:Release request 0x2:Apply request 0x3:Error"
    private String epbSwitchPosition = "";
    //EPB状态 "0x0:both brakes released 0x1:both brakes applied 0x2:both brakes in operation 0x3:unknown"
    private String epbStatus = "";
    //ESP工作状态 "0x0:NotActive 0x1:Active"
    private String espActiveStatus = "";
    //ESP功能开启状态 "0x0:OFF 0x1:ON"
    private String espFunctionStatus = "";
    //ESP故障 "0x0:No error 0x1:Error"
    private String ESP_TCSFailStatus = "";
    //HHC功能激活 "0x0:function is not in active 0x1:function is in active"
    private String hhcActive = "";
    //TCS激活 "0x0:Not Active 0x1:Active"
    private String tcsActive = "";
    //制动主缸压力信号 Bar
    private String espMasterCylinderBrakePressure = "";
    //制动主缸压力信号有效 "0x0:Valid 0x1:Invalid"
    private String ESP_MasterCylinderBrakePressureValid = "";
    //力矩传感器状态 "0x0:Normal 0x1:Abnormal"
    private String espTorqSensorStatus = "";
    //助力转向故障 "0x0:No Failed 0x1:Failed"
    private String EPS_EPSFailed = "";
    //转角传感器失效 "0x0:Valid 0x1:Invalid"
    private String sasFailure = "";
    //转角速度 deg/s
    private String sasSteeringAngleSpeed = "";
    //转向角度 degree
    private String sasSteeringAngle = "";
    //转向角度失效信号 "0x0:Valid 0x1:Invalid"
    private String sasSteeringAngleValid = "";
    //转向力矩 Nm
    private String espSteeringTorque = "";
    //AC请求信号 "0x0:OFF 0x1:ON"
    private int acReq = -999999;
    //AC系统故障 "0x0:NotFailure 0x1:Failure"
    private int acSystemFailure = -999999;
    //PTC实际消耗功率 kW
    private Double ptcPwrAct = -999999D;
    //等离子发生器状态 "0x0:inactive 0x1:active"
    private int plasmaStatus = -999999;
    //电池包进水口温度 ℃
    private int battInTemperature = -999999;
    //电池加热回路状态 "0x0:off 0x1:Normal 0x2:warning 0x3:Failure"
    private String battWarmLoopSts = "";
    //电池冷却回路状态 "0x0:off 0x1:Normal 0x2:warning 0x3:Failure"
    private String battCoolngLoopSts = "";
    //电池冷却器工作标志位 "0x0:notactive 0x1:active"
    private String battCoolActv = "";
    //电机出水口温度 ℃
    private int motorOutTemperature = -999999;
    //电源状态反馈 "0x0:OFF 0x1:ACC 0x2:ON 0x3:Start"
    private String powerStatusFeedBack = "";
    //后除霜开关  "0x0:OFF 0x1:ON"
    private int AC_RearDefrosterSwitch = -999999;
    //后雾灯工作状态  "0x0:OFF 0x1:ON 0x2:Not used 0x3:Error"
    private int rearFoglamp = -999999;
    //驾驶侧门锁状态信号  "0x0:Lock 0x1:Unlock 0x2:Not used 0x3:Error"
    private int driverDoorLock = -999999;
    //驾驶员温度调节自动	℃
    private Double acDriverReqTemp = -999999D;
    //警戒状态信息  "0x0:Disarmed 0x1:Prearmed 0x2:Armed 0x3:Activated"
    private int keyAlarm = -999999;
    //空气净化状态提醒  "0x0:inactive 0x1:active"
    private int airCleanStsRemind = -999999;
    //内外循环模式  "0x0:recycle 0x1:fresh 0x2:auto recycle 0x3:error"
    private int recycleType = -999999;
    //启动控制信号  "0x0:NoRequest 0x1:StartupStart 0x2:StartupStop 0x3:Invalid"
    private String startControlsignal = "";
    //气囊系统报警灯状态  "0x0:Lamp off-no failure 0x1:Lamp on-no failure 0x2:Lamp flashing-no failure 0x3:Failure-failure present"
    private int airBagWarningLamp = -999999;
    //前除霜信号  "0x0:inactive 0x1:active"
    private int frontDefrosterSwitch = -999999;
    //前吹风模式  "0x0:blow face 0x1:blow face/blow feet 0x2:blow feet 0x3:blow fee/defroster 0x4:defroster 0x7:error"
    private String frontBlowType = "";
    //前排风量调节  "0x0:OFF 0x1:1档 0x2:2档 0x3:3档 0x4:4档 0x5:5档 0x6:6档 0x7:7档 0x8:8档"
    private int frontReqWindLevel = -999999;
    //前雨刮工作状态  "0x0:OFF 0x1:Low 0x2:High 0x3:Error"
    private String bcmFrontWiperStatus = "";
    //热管理系统实际消耗功率 kW
    private String tmsPwrAct = "";
    //未检测到钥匙报警信号 "0x0:nactive 0x1:ON"
    private int keyUndetectedAlarmSign = -999999;
    //位置灯工作状态 "0x0:OFF 0x1:ON 0x2:Not used 0x3:Reserved"
    private String positionLamp = "";
    /**
     * 温度调节电动 "0x0:Level 1 0x1:Level 2 0x2:Level 3 0x3:Level 4 0x4:Level 5 0x5:Level 6 0x6:Level 7 0x7:Level 8 0x8:Level 9
     * 0x9:Level 10 0xA:Level 11 0xB:Level 12 0xC:Level 13 0xD:Level 14 0xE:Level 15 0xF:Level 16"
     */
    private int driverReqTempModel = -999999;
    //转向灯开关状态信号 "0x0:Not Actived 0x1:Left Actived 0x2:Right Actived 0x3:Invalid"
    private int turnLightSwitchSts = -999999;
    //自动大灯状态 "0x0:Not Actived 0x1:Actived"
    private int autoHeadlightStatus = -999999;
    //左前门状态 "0x0:Close 0x1:Open"
    private int driverDoor = -999999;
    //前电机控制器故障等级 "0x0:Noerrors 0x1:reserved 0x2:alarmintosafemode 0x3:stop. 0x4:emergecestop."
    private int frntIpuFltRnk = -999999;
    //前电机控制器软件版本号
    private String frontIpuSwVers = "";
    //前电机控制器硬件版本号
    private int frontIpuHwVers = -999999;
    //前电机长时最大扭矩 Nm
    private int frntMotTqLongTermMax = -999999;
    //前电机长时最小扭矩 Nm
    private int frntMotTqLongTermMin = -999999;
    //CP信号占空比 %
    private int cpvValue = -999999;
    //充电机工作状态 "0x0:Init 0x1:Standby 0x2:working 0x3:reserved 0x4:Failure 0x5:reserved 0x6:Sleep"
    private int obcChrgSts = -999999;
    //充电机故障等级 "0x0:无故障 0x1:性能限制 0x2:暂时停止工作 0x3:立即停止工作"
    private String obcFltRnk = "";
    //电池故障代码 json数据中，ecuErrCodeDataList数组中ecuType=2
    private String BcuFaultCodes = "";
    //充电机交流端实时输入电流	A
    private Double obcChrgInpAcI = -999999D;
    //充电机交流端实时输入电压	V
    private int obcChrgInpAcU = -999999;
    //充电机实时充电电流	A
    private Double obcChrgDcI = -999999D;
    //充电机实时充电电压	V
    private Double obcChrgDcU = -999999D;
    //充电机温度	℃
    private int obcTemperature = -999999;
    //充电机最大允许输出功率	w
    private int obcMaxChrgOutpPwrAvl = -999999;
    //副驶安全带扣状态 "0x0:Buckled 0x1:Unbuckle 0x2:Not Used 0x3:Not Used"
    private int passengerBuckleSwitch = -999999;
    //碰撞输出信号 "0x0:No Crash 0x1:crash"
    private String crashlfo = "";
    //主驶安全带扣状态 "0x0:Buckled 0x1:Unbuckle 0x2:Not Used 0x3:Not Used"
    private int driverBuckleSwitch = -999999;
    //禁止发动机启动 "0x0:No Inhibit 0x1:Inhibit Engine Start 0x2:Reserved 0x3:Invalid"
    private String engineStartHibit = "";
    //远程解闭锁请求 "0x0 : No Request 0x1 : Remote Lock 0x2 : Remote Unlock 0x3 : Invalid."
    private String lockCommand = "";
    //远程寻车 "0x0 : No Request 0x1:Light 0x2 :Horn 0x3 :Light And Horn"
    private String searchCarReq = "";
    //主驾温度请求信号
    private String acTempValueReq = "";
    //VCU故障代码 json数据中，ecuErrCodeDataList数组中ecuType=1
    private String VcuFaultCode = "";
    //VCU故障数量
    private String vcuErrAmnt = "";
    //VCU软件版本号
    private int vcuSwVers = -999999;
    //VCU硬件版本号
    private int vcuHwVers = -999999;
    //低速报警状态 "0x0:opened 0x1:closed 0x2:Reserved"
    private String lowSpdWarnStatus = "";
    //低压电瓶充电请求 "0x0:无请求 0x1:低压电瓶充电"
    private int lowBattChrgRqe = -999999;
    //低压电瓶充电状态 "0x0:补电成功 0x1:补电失败"
    private String lowBattChrgSts = "";
    //低压电瓶电压 V
    private Double lowBattU = -999999D;
    //电子手刹状态 "0x0:放下 0x1:拉上"
    private int handlebrakeStatus = -999999;
    //换挡器位置有效 "0x0:Valid 0x1:Invalid"
    private String shiftPositionValid = "";
    //加速踏板位置有效 "0x0:valid 0x1:invalid"
    private String accPedalValid = "";
    //驾驶模式 "0x0:Reserved 0x1:Normal 0x2:Sport 0x3:ECO"
    private int driveMode = -999999;
    //驾驶模式按键状态 "0x0:未按下 0x1:按下"
    private int driveModeButtonStatus = -999999;
    //碰撞信号状态 "0x0:NoCrash 0x1:DeploymentCrash"
    private int VCUSRSCrashOutpSts = -999999;
    /**
     * 文字提醒 "0x0：初始值或无效值 0x1：无法启动，请拔充电枪 0x2：换挡时请按解锁按钮 0x3：P挡未锁止，请维修（预留）
     * 0x4：P挡未解锁，请维修（预留） 0x5：高压系统过温 0x6：电网波动，请更换充电地点 0x7：制动助力不足，请谨慎驾驶
     * 0x8：请在P档下进行充电（预留） 0x9：请刷卡或连接电源 0xA：请选择一种充电方式 0xB：无法充电，请拉起手刹
     * 0xC：动力系统故障，请立即停车 0xD：电网波动，充电时间延长 0xE：换挡器故障，请维修 0xF：请手动解锁充电电子锁
     * 0x10：电子锁锁止失败，充电时间延长 0x11：电池温度低 0x12：请踩刹车退出P档（预留） 0x13：电子锁锁止失败，充电停止
     * 0x14：12V蓄电池电压过低，请靠边停车 0x15：动力电池电量低，请及时充电 0x16：功率限制，请减速慢行
     * 0x17：换挡时请踩下制动踏板 0x18：请将钥匙拧到Start档后再换挡（低配仪表）/请踩制动踏板并按Start按钮后再换挡（高配仪表）"
     */
    private int textDispEna = -999999;
    //巡航控制状态 "0x0:Off 0x1:Active 0x2:Standby 0x3:Error"
    private int crsCtrlStatus = -999999;
    //巡航目标车速 Km/h
    private int crsTarSpd = -999999;
    /**
     * 巡航信息提示 "0x0:无提示 0x1:系统故障，巡航禁止 0x2:当前车速过低不满足巡航条件 0x3:当前车速过高不满足巡航条件
     * 0x4:非前进挡，巡航禁止 0x5:车身稳定系统激活，巡航禁止。 0x6:动力系统超速，巡航禁止。 0x7:制动踏板踩下，巡航禁止
     * 0x8:无有效目标车速，请按set键重新设定 0x9:请按启动键，进入可行驶模式 0xA:巡航已关闭 0xB:巡航已退出"
     */
    private int crsTextDisp = -999999;
    //钥匙ON档信号 "0x0:非ON 0x1:ON"
    private int keyOn = -999999;
    //整车功率限制 "0x0:NoError 0x1:Error"
    private int vehPwrlim = -999999;
    //整车配置信息 "0x0:Level1 0x1:Level2 0x2:Level3 0x3:Invalid"
    private String vehCfgInfo = "";
    //制动真空压力信号 kPa
    private int vacBrkPRmu = -999999;
    /**
     *充电机故障码  "0x0:当前无故障 0x1:12V电池电压过高 0x2:12V电池电压过低 0x3:CP内部6V电压异常 0x4:CP内部9V电压异常
     * 0x5:CP内部频率异常 0x6:CP内部占空比异常 0x7:CAN收发器异常 0x8:内部SCI通信失败 0x9:内部SCICRC错误 0xA:输出过压关机
     * 0xB:输出低压关机 0xC:交流输入低压关机 0xD:输入过压关机 0xE:环境温度过低关机 0xF:环境温度过高关机 0x10:充电机PFC电压欠压
     * 0x11:输入过载 0x12:输出过载 0x13:自检故障 0x14:外部CANbusoff 0x15:内部CANbusoff 0x16:外部CAN通信超时
     * 0x17:外部CAN使能超时 0x18:外部CAN通信错误 0x19:输出短路 0x1A:充电参数错误 0x1B:充电机PFC电压过压
     * 0x1C:内部SCI通信失败 0x1D:过功率 0x1E:PFC电感过温 0x1F:LLC变压器过温 0x20:M1功率板过温 0x21:PFC温度降额
     * 0x22:LLC温度降额 0x23:M1板温度降额 0x24:Air环境温度降额" json数据中，ecuErrCodeDataList数组中ecuType=5
     */
    private String ObcFaultCode = "";

    // 扩展字段 终端时间
    private Long terminalTimeStamp = -999999L;
    // 扩展字段，用于存储异常数据
    private String errorData = "";
}