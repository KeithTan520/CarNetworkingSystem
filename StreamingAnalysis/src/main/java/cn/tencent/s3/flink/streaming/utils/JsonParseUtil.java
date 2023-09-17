package cn.tencent.s3.flink.streaming.utils;


import cn.tencent.s3.flink.streaming.entity.CarDataObj;
import org.apache.commons.lang3.time.FastDateFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class JsonParseUtil {
    private static Logger logger = LoggerFactory.getLogger("JsonParseUtil");
    private static FastDateFormat sdf =FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    /**
     * @desc:解析json成为carDataObj对象
     * @param jsonString
     * @return 转换后的carDataObj对象
     */
    public static CarDataObj parseJsonToObject(String jsonString) {
        CarDataObj carDataObj = new CarDataObj();
        try {
            HashMap vehicleMap = jsonToMap(jsonString);
            carDataObj.setGearDriveForce(convertIntType("gearDriveForce", vehicleMap));
            carDataObj.setBatteryConsistencyDifferenceAlarm(convertIntType("batteryConsistencyDifferenceAlarm", vehicleMap));
            carDataObj.setSoc(convertIntType("soc", vehicleMap));
            carDataObj.setSocJumpAlarm(convertIntType("socJumpAlarm", vehicleMap));
            carDataObj.setCaterpillaringFunction(convertIntType("caterpillaringFunction", vehicleMap));
            carDataObj.setSatNum(convertIntType("satNum", vehicleMap));
            carDataObj.setSocLowAlarm(convertIntType("socLowAlarm", vehicleMap));
            carDataObj.setChargingGunConnectionState(convertIntType("chargingGunConnectionState", vehicleMap));
            carDataObj.setMinTemperatureSubSystemNum(convertIntType("minTemperatureSubSystemNum", vehicleMap));
            carDataObj.setChargedElectronicLockStatus(convertIntType("chargedElectronicLockStatus", vehicleMap));
            carDataObj.setMaxVoltageBatteryNum(convertIntType("maxVoltageBatteryNum", vehicleMap));
            carDataObj.setTerminalTime(convertStringType("terminalTime", vehicleMap));
            carDataObj.setSingleBatteryOverVoltageAlarm(convertIntType("singleBatteryOverVoltageAlarm", vehicleMap));
            carDataObj.setOtherFaultCount(convertIntType("otherFaultCount", vehicleMap));
            carDataObj.setVehicleStorageDeviceOvervoltageAlarm(convertIntType("vehicleStorageDeviceOvervoltageAlarm", vehicleMap));
            carDataObj.setBrakeSystemAlarm(convertIntType("brakeSystemAlarm", vehicleMap));
            carDataObj.setServerTime(convertStringType("serverTime", vehicleMap));
            carDataObj.setVin(convertStringType("vin", vehicleMap).toUpperCase());
            carDataObj.setRechargeableStorageDevicesFaultCount(convertIntType("rechargeableStorageDevicesFaultCount", vehicleMap));
            carDataObj.setDriveMotorTemperatureAlarm(convertIntType("driveMotorTemperatureAlarm", vehicleMap));
            carDataObj.setGearBrakeForce(convertIntType("gearBrakeForce", vehicleMap));
            carDataObj.setDcdcStatusAlarm(convertIntType("dcdcStatusAlarm", vehicleMap));
            carDataObj.setLat(convertDoubleType("lat", vehicleMap));
            carDataObj.setDriveMotorFaultCodes(convertStringType("driveMotorFaultCodes", vehicleMap));
            carDataObj.setDeviceType(convertStringType("deviceType", vehicleMap));
            carDataObj.setVehicleSpeed(convertDoubleType("vehicleSpeed", vehicleMap));
            carDataObj.setLng(convertDoubleType("lng", vehicleMap));
            carDataObj.setChargingTimeExtensionReason(convertIntType("chargingTimeExtensionReason", vehicleMap));
            carDataObj.setGpsTime(convertStringType("gpsTime", vehicleMap));
            carDataObj.setEngineFaultCount(convertIntType("engineFaultCount", vehicleMap));
            carDataObj.setCarId(convertStringType("carId", vehicleMap));
            carDataObj.setCurrentElectricity(convertDoubleType("vehicleSpeed", vehicleMap));
            carDataObj.setSingleBatteryUnderVoltageAlarm(convertIntType("singleBatteryUnderVoltageAlarm", vehicleMap));
            carDataObj.setMaxVoltageBatterySubSystemNum(convertIntType("maxVoltageBatterySubSystemNum", vehicleMap));
            carDataObj.setMinTemperatureProbe(convertIntType("minTemperatureProbe", vehicleMap));
            carDataObj.setDriveMotorNum(convertIntType("driveMotorNum", vehicleMap));
            carDataObj.setTotalVoltage(convertDoubleType("totalVoltage", vehicleMap));
            carDataObj.setTemperatureDifferenceAlarm(convertIntType("temperatureDifferenceAlarm", vehicleMap));
            carDataObj.setMaxAlarmLevel(convertIntType("maxAlarmLevel", vehicleMap));
            carDataObj.setStatus(convertIntType("status", vehicleMap));
            carDataObj.setGeerPosition(convertIntType("geerPosition", vehicleMap));
            carDataObj.setAverageEnergyConsumption(convertDoubleType("averageEnergyConsumption", vehicleMap));
            carDataObj.setMinVoltageBattery(convertDoubleType("minVoltageBattery", vehicleMap));
            carDataObj.setGeerStatus(convertIntType("geerStatus", vehicleMap));
            carDataObj.setMinVoltageBatteryNum(convertIntType("minVoltageBatteryNum", vehicleMap));
            carDataObj.setValidGps(convertStringType("validGps", vehicleMap));
            carDataObj.setEngineFaultCodes(convertStringType("engineFaultCodes", vehicleMap));
            carDataObj.setMinTemperatureValue(convertDoubleType("minTemperatureValue", vehicleMap));
            carDataObj.setChargeStatus(convertIntType("chargeStatus", vehicleMap));
            carDataObj.setIgnitionTime(convertStringType("ignitionTime", vehicleMap));
            carDataObj.setTotalOdometer(convertDoubleType("totalOdometer", vehicleMap));
            carDataObj.setAlti(convertDoubleType("alti", vehicleMap));
            carDataObj.setSpeed(convertDoubleType("speed", vehicleMap));
            carDataObj.setSocHighAlarm(convertIntType("socHighAlarm", vehicleMap));
            carDataObj.setVehicleStorageDeviceUndervoltageAlarm(convertIntType("vehicleStorageDeviceUndervoltageAlarm", vehicleMap));
            carDataObj.setTotalCurrent(convertDoubleType("totalCurrent", vehicleMap));
            carDataObj.setBatteryAlarm(convertIntType("batteryAlarm", vehicleMap));
            carDataObj.setRechargeableStorageDeviceMismatchAlarm(convertIntType("rechargeableStorageDeviceMismatchAlarm", vehicleMap));
            carDataObj.setIsHistoryPoi(convertIntType("isHistoryPoi", vehicleMap));
            carDataObj.setVehiclePureDeviceTypeOvercharge(convertIntType("vehiclePureDeviceTypeOvercharge", vehicleMap));
            carDataObj.setMaxVoltageBattery(convertDoubleType("maxVoltageBattery", vehicleMap));
            carDataObj.setDcdcTemperatureAlarm(convertIntType("dcdcTemperatureAlarm", vehicleMap));
            carDataObj.setIsValidGps(convertStringType("isValidGps", vehicleMap));
            carDataObj.setLastUpdatedTime(convertStringType("lastUpdatedTime", vehicleMap));
            carDataObj.setDriveMotorControllerTemperatureAlarm(convertIntType("driveMotorControllerTemperatureAlarm", vehicleMap));
            carDataObj.setIgniteCumulativeMileage(convertDoubleType("igniteCumulativeMileage", vehicleMap));
            carDataObj.setDcStatus(convertIntType("dcStatus", vehicleMap));
            carDataObj.setRepay(convertStringType("repay", vehicleMap));
            carDataObj.setMaxTemperatureSubSystemNum(convertIntType("maxTemperatureSubSystemNum", vehicleMap));
            carDataObj.setMinVoltageBatterySubSystemNum(convertIntType("minVoltageBatterySubSystemNum", vehicleMap));
            carDataObj.setHeading(convertDoubleType("heading", vehicleMap));
            carDataObj.setTuid(convertStringType("tuid", vehicleMap));
            carDataObj.setEnergyRecoveryStatus(convertIntType("energyRecoveryStatus", vehicleMap));
            carDataObj.setFireStatus(convertIntType("fireStatus", vehicleMap));
            carDataObj.setTargetType(convertStringType("targetType", vehicleMap));
            carDataObj.setMaxTemperatureProbe(convertIntType("maxTemperatureProbe", vehicleMap));
            carDataObj.setRechargeableStorageDevicesFaultCodes(convertStringType("rechargeableStorageDevicesFaultCodes", vehicleMap));
            carDataObj.setCarMode(convertIntType("carMode", vehicleMap));
            carDataObj.setHighVoltageInterlockStateAlarm(convertIntType("highVoltageInterlockStateAlarm", vehicleMap));
            carDataObj.setInsulationAlarm(convertIntType("insulationAlarm", vehicleMap));
            carDataObj.setMileageInformation(convertIntType("mileageInformation", vehicleMap));
            carDataObj.setMaxTemperatureValue(convertDoubleType("maxTemperatureValue", vehicleMap));
            carDataObj.setOtherFaultCodes(convertStringType("otherFaultCodes", vehicleMap));
            carDataObj.setRemainPower(convertDoubleType("remainPower", vehicleMap));
            carDataObj.setInsulateResistance(convertIntType("insulateResistance", vehicleMap));
            carDataObj.setBatteryLowTemperatureHeater(convertIntType("batteryLowTemperatureHeater", vehicleMap));
            carDataObj.setFuelConsumption100km(convertStringType("fuelConsumption100km", vehicleMap));
            carDataObj.setFuelConsumption(convertStringType("fuelConsumption", vehicleMap));
            carDataObj.setEngineSpeed(convertStringType("engineSpeed", vehicleMap));
            carDataObj.setEngineStatus(convertStringType("engineStatus", vehicleMap));
            carDataObj.setTrunk(convertIntType("trunk", vehicleMap));
            carDataObj.setLowBeam(convertIntType("lowBeam", vehicleMap));
            carDataObj.setTriggerLatchOverheatProtect(convertStringType("triggerLatchOverheatProtect", vehicleMap));
            carDataObj.setTurnLndicatorRight(convertIntType("turnLndicatorRight", vehicleMap));
            carDataObj.setHighBeam(convertIntType("highBeam", vehicleMap));
            carDataObj.setTurnLndicatorLeft(convertIntType("turnLndicatorLeft", vehicleMap));
            carDataObj.setBcuSwVers(convertIntType("bcuSwVers", vehicleMap));
            carDataObj.setBcuHwVers(convertIntType("bcuHwVers", vehicleMap));
            carDataObj.setBcuOperMod(convertIntType("bcuOperMod", vehicleMap));
            carDataObj.setChrgEndReason(convertIntType("chrgEndReason", vehicleMap));
            carDataObj.setBCURegenEngDisp(convertStringType("BCURegenEngDisp", vehicleMap));
            carDataObj.setBCURegenCpDisp(convertIntType("BCURegenCpDisp", vehicleMap));
            carDataObj.setBcuChrgMod(convertIntType("bcuChrgMod", vehicleMap));
            carDataObj.setBatteryChargeStatus(convertIntType("batteryChargeStatus", vehicleMap));
            carDataObj.setBcuFltRnk(convertIntType("bcuFltRnk", vehicleMap));
            carDataObj.setBattPoleTOver(convertStringType("battPoleTOver", vehicleMap));
            carDataObj.setBcuSOH(convertDoubleType("bcuSOH", vehicleMap));
            carDataObj.setBattIntrHeatActive(convertIntType("battIntrHeatActive", vehicleMap));
            carDataObj.setBattIntrHeatReq(convertIntType("battIntrHeatReq", vehicleMap));
            carDataObj.setBCUBattTarT(convertStringType("BCUBattTarT", vehicleMap));
            carDataObj.setBattExtHeatReq(convertIntType("battExtHeatReq", vehicleMap));
            carDataObj.setBCUMaxChrgPwrLongT(convertStringType("BCUMaxChrgPwrLongT", vehicleMap));
            carDataObj.setBCUMaxDchaPwrLongT(convertStringType("BCUMaxDchaPwrLongT", vehicleMap));
            carDataObj.setBCUTotalRegenEngDisp(convertStringType("BCUTotalRegenEngDisp", vehicleMap));
            carDataObj.setBCUTotalRegenCpDisp(convertStringType("BCUTotalRegenCpDisp ", vehicleMap));
            carDataObj.setDcdcFltRnk(convertIntType("dcdcFltRnk", vehicleMap));
            carDataObj.setDcdcOutpCrrt(convertDoubleType("dcdcOutpCrrt", vehicleMap));
            carDataObj.setDcdcOutpU(convertDoubleType("dcdcOutpU", vehicleMap));
            carDataObj.setDcdcAvlOutpPwr(convertIntType("dcdcAvlOutpPwr", vehicleMap));
            carDataObj.setAbsActiveStatus(convertStringType("absActiveStatus", vehicleMap));
            carDataObj.setAbsStatus(convertStringType("absStatus", vehicleMap));
            carDataObj.setVcuBrkErr(convertStringType("VcuBrkErr", vehicleMap));
            carDataObj.setEPB_AchievedClampForce(convertStringType("EPB_AchievedClampForce", vehicleMap));
            carDataObj.setEpbSwitchPosition(convertStringType("epbSwitchPosition", vehicleMap));
            carDataObj.setEpbStatus(convertStringType("epbStatus", vehicleMap));
            carDataObj.setEspActiveStatus(convertStringType("espActiveStatus", vehicleMap));
            carDataObj.setEspFunctionStatus(convertStringType("espFunctionStatus", vehicleMap));
            carDataObj.setESP_TCSFailStatus(convertStringType("ESP_TCSFailStatus", vehicleMap));
            carDataObj.setHhcActive(convertStringType("hhcActive", vehicleMap));
            carDataObj.setTcsActive(convertStringType("tcsActive", vehicleMap));
            carDataObj.setEspMasterCylinderBrakePressure(convertStringType("espMasterCylinderBrakePressure", vehicleMap));
            carDataObj.setESP_MasterCylinderBrakePressureValid(convertStringType("ESP_MasterCylinderBrakePressureValid", vehicleMap));
            carDataObj.setEspTorqSensorStatus(convertStringType("espTorqSensorStatus", vehicleMap));
            carDataObj.setEPS_EPSFailed(convertStringType("EPS_EPSFailed", vehicleMap));
            carDataObj.setSasFailure(convertStringType("sasFailure", vehicleMap));
            carDataObj.setSasSteeringAngleSpeed(convertStringType("sasSteeringAngleSpeed", vehicleMap));
            carDataObj.setSasSteeringAngle(convertStringType("sasSteeringAngle", vehicleMap));
            carDataObj.setSasSteeringAngleValid(convertStringType("sasSteeringAngleValid", vehicleMap));
            carDataObj.setEspSteeringTorque(convertStringType("espSteeringTorque", vehicleMap));
            carDataObj.setAcReq(convertIntType("acReq", vehicleMap));
            carDataObj.setAcSystemFailure(convertIntType("acSystemFailure", vehicleMap));
            carDataObj.setPtcPwrAct(convertDoubleType("ptcPwrAct", vehicleMap));
            carDataObj.setPlasmaStatus(convertIntType("plasmaStatus", vehicleMap));
            carDataObj.setBattInTemperature(convertIntType("battInTemperature", vehicleMap));
            carDataObj.setBattWarmLoopSts(convertStringType("battWarmLoopSts", vehicleMap));
            carDataObj.setBattCoolngLoopSts(convertStringType("battCoolngLoopSts", vehicleMap));
            carDataObj.setBattCoolActv(convertStringType("battCoolActv", vehicleMap));
            carDataObj.setMotorOutTemperature(convertIntType("motorOutTemperature", vehicleMap));
            carDataObj.setPowerStatusFeedBack(convertStringType("powerStatusFeedBack", vehicleMap));
            carDataObj.setAC_RearDefrosterSwitch(convertIntType("AC_RearDefrosterSwitch", vehicleMap));
            carDataObj.setRearFoglamp(convertIntType("rearFoglamp", vehicleMap));
            carDataObj.setDriverDoorLock(convertIntType("driverDoorLock", vehicleMap));
            carDataObj.setAcDriverReqTemp(convertDoubleType("acDriverReqTemp", vehicleMap));
            carDataObj.setKeyAlarm(convertIntType("keyAlarm", vehicleMap));
            carDataObj.setAirCleanStsRemind(convertIntType("airCleanStsRemind", vehicleMap));
            carDataObj.setRecycleType(convertIntType("recycleType", vehicleMap));
            carDataObj.setStartControlsignal(convertStringType("startControlsignal", vehicleMap));
            carDataObj.setAirBagWarningLamp(convertIntType("airBagWarningLamp", vehicleMap));
            carDataObj.setFrontDefrosterSwitch(convertIntType("frontDefrosterSwitch", vehicleMap));
            carDataObj.setFrontBlowType(convertStringType("frontBlowType", vehicleMap));
            carDataObj.setFrontReqWindLevel(convertIntType("frontReqWindLevel", vehicleMap));
            carDataObj.setBcmFrontWiperStatus(convertStringType("bcmFrontWiperStatus", vehicleMap));
            carDataObj.setTmsPwrAct(convertStringType("tmsPwrAct", vehicleMap));
            carDataObj.setKeyUndetectedAlarmSign(convertIntType("keyUndetectedAlarmSign", vehicleMap));
            carDataObj.setPositionLamp(convertStringType("positionLamp", vehicleMap));
            carDataObj.setDriverReqTempModel(convertIntType("driverReqTempModel", vehicleMap));
            carDataObj.setTurnLightSwitchSts(convertIntType("turnLightSwitchSts", vehicleMap));
            carDataObj.setAutoHeadlightStatus(convertIntType("autoHeadlightStatus", vehicleMap));
            carDataObj.setDriverDoor(convertIntType("driverDoor", vehicleMap));
            carDataObj.setFrntIpuFltRnk(convertIntType("frntIpuFltRnk", vehicleMap));
            carDataObj.setFrontIpuSwVers(convertStringType("frontIpuSwVers", vehicleMap));
            carDataObj.setFrontIpuHwVers(convertIntType("frontIpuHwVers", vehicleMap));
            carDataObj.setFrntMotTqLongTermMax(convertIntType("frntMotTqLongTermMax", vehicleMap));
            carDataObj.setFrntMotTqLongTermMin(convertIntType("frntMotTqLongTermMin", vehicleMap));
            carDataObj.setCpvValue(convertIntType("cpvValue", vehicleMap));
            carDataObj.setObcChrgSts(convertIntType("obcChrgSts", vehicleMap));
            carDataObj.setObcFltRnk(convertStringType("obcFltRnk", vehicleMap));
            carDataObj.setObcChrgInpAcI(convertDoubleType("obcChrgInpAcI", vehicleMap));
            carDataObj.setObcChrgInpAcU(convertIntType("obcChrgInpAcU", vehicleMap));
            carDataObj.setObcChrgDcI(convertDoubleType("obcChrgDcI", vehicleMap));
            carDataObj.setObcChrgDcU(convertDoubleType("obcChrgDcU", vehicleMap));
            carDataObj.setObcTemperature(convertIntType("obcTemperature", vehicleMap));
            carDataObj.setObcMaxChrgOutpPwrAvl(convertIntType("obcMaxChrgOutpPwrAvl", vehicleMap));
            carDataObj.setPassengerBuckleSwitch(convertIntType("passengerBuckleSwitch", vehicleMap));
            carDataObj.setCrashlfo(convertStringType("crashlfo", vehicleMap));
            carDataObj.setDriverBuckleSwitch(convertIntType("driverBuckleSwitch", vehicleMap));
            carDataObj.setEngineStartHibit(convertStringType("engineStartHibit", vehicleMap));
            carDataObj.setLockCommand(convertStringType("lockCommand", vehicleMap));
            carDataObj.setSearchCarReq(convertStringType("searchCarReq", vehicleMap));
            carDataObj.setAcTempValueReq(convertStringType("acTempValueReq", vehicleMap));
            carDataObj.setVcuErrAmnt(convertStringType("vcuErrAmnt", vehicleMap));
            carDataObj.setVcuSwVers(convertIntType("vcuSwVers", vehicleMap));
            carDataObj.setVcuHwVers(convertIntType("vcuHwVers", vehicleMap));
            carDataObj.setLowSpdWarnStatus(convertStringType("lowSpdWarnStatus", vehicleMap));
            carDataObj.setLowBattChrgRqe(convertIntType("lowBattChrgRqe", vehicleMap));
            carDataObj.setLowBattChrgSts(convertStringType("lowBattChrgSts", vehicleMap));
            carDataObj.setLowBattU(convertDoubleType("lowBattU", vehicleMap));
            carDataObj.setHandlebrakeStatus(convertIntType("handlebrakeStatus", vehicleMap));
            carDataObj.setShiftPositionValid(convertStringType("shiftPositionValid", vehicleMap));
            carDataObj.setAccPedalValid(convertStringType("accPedalValid", vehicleMap));
            carDataObj.setDriveMode(convertIntType("driveMode", vehicleMap));
            carDataObj.setDriveModeButtonStatus(convertIntType("driveModeButtonStatus", vehicleMap));
            carDataObj.setVCUSRSCrashOutpSts(convertIntType("VCUSRSCrashOutpSts", vehicleMap));
            carDataObj.setTextDispEna(convertIntType("textDispEna", vehicleMap));
            carDataObj.setCrsCtrlStatus(convertIntType("crsCtrlStatus", vehicleMap));
            carDataObj.setCrsTarSpd(convertIntType("crsTarSpd", vehicleMap));
            carDataObj.setCrsTextDisp(convertIntType("crsTextDisp",vehicleMap ));
            carDataObj.setKeyOn(convertIntType("keyOn", vehicleMap));
            carDataObj.setVehPwrlim(convertIntType("vehPwrlim", vehicleMap));
            carDataObj.setVehCfgInfo(convertStringType("vehCfgInfo", vehicleMap));
            carDataObj.setVacBrkPRmu(convertIntType("vacBrkPRmu", vehicleMap));

            /* ------------------------------------------nevChargeSystemVoltageDtoList 可充电储能子系统电压信息列表-------------------------------------- */
            List<Map<String, Object>> nevChargeSystemVoltageDtoList = jsonToList(vehicleMap.getOrDefault("nevChargeSystemVoltageDtoList", new ArrayList<Object>()).toString());
            if (!nevChargeSystemVoltageDtoList.isEmpty()) {
                // 只取list中的第一个map集合,集合中的第一条数据为有效数据
                Map<String, Object> nevChargeSystemVoltageDtoMap = nevChargeSystemVoltageDtoList.get(0);
                carDataObj.setCurrentBatteryStartNum(convertIntType("currentBatteryStartNum",nevChargeSystemVoltageDtoMap));
                carDataObj.setBatteryVoltage(convertJoinStringType("batteryVoltage", nevChargeSystemVoltageDtoMap));
                carDataObj.setChargeSystemVoltage(convertDoubleType("chargeSystemVoltage",nevChargeSystemVoltageDtoMap));
                carDataObj.setCurrentBatteryCount(convertIntType("currentBatteryCount", nevChargeSystemVoltageDtoMap));
                carDataObj.setBatteryCount(convertIntType("batteryCount", nevChargeSystemVoltageDtoMap));
                carDataObj.setChildSystemNum(convertIntType("childSystemNum", nevChargeSystemVoltageDtoMap));
                carDataObj.setChargeSystemCurrent(convertDoubleType("chargeSystemCurrent", nevChargeSystemVoltageDtoMap));
            }
            /* -------------------------------------------------------------------------------------------------------------- */

            /* ----------------------------------------------driveMotorData-------------------------------------------------- */
            List<Map<String, Object>> driveMotorData = jsonToList(vehicleMap.getOrDefault("driveMotorData", new ArrayList()).toString());                                    //驱动电机数据
            if (!driveMotorData.isEmpty()) {
                Map<String, Object> driveMotorMap = driveMotorData.get(0);
                carDataObj.setControllerInputVoltage(convertDoubleType("controllerInputVoltage", driveMotorMap));
                carDataObj.setControllerTemperature(convertDoubleType("controllerTemperature", driveMotorMap));
                carDataObj.setRevolutionSpeed(convertDoubleType("revolutionSpeed", driveMotorMap));
                carDataObj.setNum(convertIntType("num", driveMotorMap));
                carDataObj.setControllerDcBusCurrent(convertDoubleType("controllerDcBusCurrent", driveMotorMap));
                carDataObj.setTemperature(convertDoubleType("temperature", driveMotorMap));
                carDataObj.setTorque(convertDoubleType("torque", driveMotorMap));
                carDataObj.setState(convertIntType("state", driveMotorMap));
            }
            /* -------------------------------------------------------------------------------------------------------------- */

            /* -----------------------------------------nevChargeSystemTemperatureDtoList------------------------------------ */
            List<Map<String, Object>> nevChargeSystemTemperatureDtoList = jsonToList(vehicleMap.getOrDefault("nevChargeSystemTemperatureDtoList", new ArrayList()).toString());
            if (!nevChargeSystemTemperatureDtoList.isEmpty()) {
                Map<String, Object> nevChargeSystemTemperatureMap = nevChargeSystemTemperatureDtoList.get(0);
                carDataObj.setProbeTemperatures(convertJoinStringType("probeTemperatures", nevChargeSystemTemperatureMap));
                carDataObj.setChargeTemperatureProbeNum(convertIntType("chargeTemperatureProbeNum", nevChargeSystemTemperatureMap));
            }
            /* -------------------------------------------------------------------------------------------------------------- */

            /* --------------------------------------------ecuErrCodeDataList------------------------------------------------ */
            Map<String, Object> xcuerrinfoMap = jsonToMap(vehicleMap.getOrDefault("xcuerrinfo", new HashMap<String, Object>()).toString());
            if (!xcuerrinfoMap.isEmpty()) {
                List<Map<String, Object>> ecuErrCodeDataList = jsonToList(xcuerrinfoMap.getOrDefault("ecuErrCodeDataList", new ArrayList()).toString()) ;
                if (ecuErrCodeDataList.size() > 4) {
                    carDataObj.setVcuFaultCode(convertJoinStringType("errCodes", ecuErrCodeDataList.get(0)));
                    carDataObj.setBcuFaultCodes(convertJoinStringType("errCodes", ecuErrCodeDataList.get(1)));
                    carDataObj.setDcdcFaultCode(convertJoinStringType("errCodes", ecuErrCodeDataList.get(2)));
                    carDataObj.setIpuFaultCodes(convertJoinStringType("errCodes", ecuErrCodeDataList.get(3)));
                    carDataObj.setObcFaultCode(convertJoinStringType("errCodes", ecuErrCodeDataList.get(4)));
                }
            }
            /* -------------------------------------------------------------------------------------------------------------- */

            // carStatus不在有效范围，设置值为255
            if (convertStringType("carStatus", vehicleMap).length() > 3) {
                carDataObj.setCarStatus(255);
            } else {
                carDataObj.setCarStatus(convertIntType("carStatus", vehicleMap));
            }
            // terminalTime字段不为空，设置标记时间为terminalTime时间
            if(!carDataObj.getTerminalTime().isEmpty()){
                carDataObj.setTerminalTimeStamp(sdf.parse(carDataObj.getTerminalTime()).getTime());
            }
        } catch (Exception e){
            carDataObj.setErrorData(jsonString);
            logger.error("json 数据格式错误...", e);
        }
        // 如果没有VIN号和终端时间，则为无效数据
        if(carDataObj.getVin().isEmpty() || carDataObj.getTerminalTime().isEmpty()
                || carDataObj.getTerminalTimeStamp() < 1){
            if(carDataObj.getVin().isEmpty()){
                logger.error("vin.isEmpty");
            }
            if(carDataObj.getTerminalTime().isEmpty()){
                logger.error("terminalTime.isEmpty");
            }
            carDataObj.setErrorData(jsonString);
        }
        return carDataObj;
    }

    /**
     * @desc:将Json对象转换成Map
     * @param jsonString
     * @return Map对象
     * @throws JSONException
     */
    public static HashMap jsonToMap(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        HashMap result = new HashMap();
        Iterator iterator = jsonObject.keys();
        String key = null;
        Object value = null;
        while (iterator.hasNext()) {
            key = (String) iterator.next();
            value = jsonObject.get(key);
            result.put(key, value);
        }
        return result;
    }

    /**
     * @desc:将数组转换为List，数组内部的json字符串转换为map
     * @param jsonString
     * @return List<Map<String, Object>>
     * @throws JSONException
     */
    public static List<Map<String, Object>> jsonToList(String jsonString) throws JSONException {
        List<Map<String, Object>> resultList = new ArrayList();
        JSONArray jsonArray = new JSONArray(jsonString);
        for (int i = 0; i < jsonArray.length(); i++) {
            HashMap map =jsonToMap(jsonArray.get(i).toString());
            resultList.add(map);
        }
        return resultList;
    }

    /**
     * 提取类型重复转换代码
     * @param fieldName
     * @param map
     * @return 对应数据类型的值
     */
    private static int convertIntType(String fieldName, Map<String, Object> map) {
        return Integer.parseInt(map.getOrDefault(fieldName, -999999).toString());
    }
    private static String convertStringType(String fieldName, Map<String, Object> map) {
        return map.getOrDefault(fieldName, "").toString();
    }
    private static double convertDoubleType(String fieldName, Map<String, Object> map) {
        return Double.parseDouble(map.getOrDefault(fieldName, -999999).toString());
    }
    private static String convertJoinStringType(String fieldName, Map<String, Object> map) {
        return String.join("~", convertStringToArray(map.getOrDefault(fieldName, new ArrayList()).toString()));
    }

    /**
     * 解决类型转换异常：string转数组,字符串以","分割
     * @param str
     * @return list
     */
    private static List convertStringToArray(String str) {
        return Arrays.asList(str.split(","));
    }
}