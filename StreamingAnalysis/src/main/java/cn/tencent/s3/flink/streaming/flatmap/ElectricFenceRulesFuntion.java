package cn.tencent.s3.flink.streaming.flatmap;

import cn.tencent.s3.flink.streaming.entity.CarDataPartObj;
import cn.tencent.s3.flink.streaming.entity.ElectricFenceModel;
import cn.tencent.s3.flink.streaming.entity.ElectricFenceResultTmp;
import cn.tencent.s3.flink.streaming.utils.DateUtil;
import cn.tencent.s3.flink.streaming.utils.DistanceCaculateUtil;
import org.apache.flink.streaming.api.functions.co.CoFlatMapFunction;
import org.apache.flink.streaming.api.functions.co.RichCoFlatMapFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.HashMap;

/**
 * 文件名：ElectricFenceRulesFuntion
 * 项目名：CarNetworkingSystem
 * 描述：将两个数据流进行合并操作：①实时上报的车辆报文数据 ②当前电子围栏的配置数据流 => 合并成一个数据流 ElectricFenceModel
 * 作者：linker
 * 创建时间：2023/10/19
 * 开发步骤：
 */
public class ElectricFenceRulesFuntion extends RichCoFlatMapFunction<CarDataPartObj, HashMap<String, ElectricFenceResultTmp>, ElectricFenceModel> {
        //日志读取
    private static final Logger logger = LoggerFactory.getLogger(ElectricFenceRulesFuntion.class.getSimpleName());
        //定义变量用于接收配置数据
    HashMap<String,ElectricFenceResultTmp> electricFenceConfig = null;

    /**
     * 读取配置数据吗，将配置和车辆数据封装到 ElectricFenceModel
     * @param carDataPartObj
     * @param collector
     * @throws Exception
     */
    @Override
    public void flatMap1(CarDataPartObj carDataPartObj, Collector<ElectricFenceModel> collector) throws Exception {
        //1.初始化返回的 ElectricFenceModel
        ElectricFenceModel electricFenceModel = new ElectricFenceModel();
        //2.判断如果流数据数据质量（车辆的经纬度不能为0或-999999，车辆GpsTime不能为空）
        if (carDataPartObj.getGpsTime() != null && isValid(carDataPartObj.getLat()) && isValid(carDataPartObj.getLng())) {
            //2.1.获取当前车辆的 vin
            String vin = carDataPartObj.getVin();
            //2.2.通过vin获取电子围栏的配置信息
            ElectricFenceResultTmp electricFenceResultTmp = electricFenceConfig.getOrDefault(vin, null);
            //2.3.如果电子围栏配置信息不为空
            if (electricFenceResultTmp != null) {
                //2.3.1.说明当前车辆关联了电子围栏规则，需要判断当前上报的数据是否在电子围栏规则的生效时间内，
                // 先获取上报地理位置时间gpsTimestamp
                // 将时间字符串类型转换成时间戳类型
                long getGpsTime = DateUtil.convertStringToDateTime(carDataPartObj.getGpsTime()).getTime();
                //2.3.2.如果当前gpsTimestamp>=开始时间戳并且gpsTimestamp<=结束时间戳，以下内容存入到 ElectricFenceModel
                if (getGpsTime >= electricFenceResultTmp.getStartTime().getTime() &&
                        getGpsTime < electricFenceResultTmp.getEndTime().getTime()) {
                    //2.3.2.1.实时车辆上报数据在电子围栏生效期内 vin gpstime lng lat 终端时间和终端时间戳
                    electricFenceModel.setVin(vin);
                    electricFenceModel.setGpsTime(carDataPartObj.getGpsTime());
                    electricFenceModel.setLng(carDataPartObj.getLng());
                    electricFenceModel.setLat(carDataPartObj.getLat());
                    electricFenceModel.setTerminalTime(carDataPartObj.getTerminalTime());
                    electricFenceModel.setTerminalTimestamp(carDataPartObj.getTerminalTimeStamp());
                    //2.3.2.2.电子围栏id，电子围栏名称，地址，半径
                    electricFenceModel.setEleId(electricFenceResultTmp.getId());
                    electricFenceModel.setEleName(electricFenceResultTmp.getName());
                    electricFenceModel.setAddress(electricFenceResultTmp.getAddress());
                    electricFenceModel.setRadius(electricFenceResultTmp.getRadius());
                    //2.3.2.3.电子围栏经纬度
                    electricFenceModel.setLongitude(electricFenceResultTmp.getLongitude());
                    electricFenceModel.setLatitude(electricFenceResultTmp.getLatitude());
                    //2.3.2.4.计算车辆的经纬度和电子围栏中心点的经纬度计算球面距离，
                    //如果两点之间大于半径（单位是千米）的距离，就是存在于圆外，否则反之
                    //DISTANCE 结果是 double ，单位是 m 米
                    Double distance = DistanceCaculateUtil.getDistance(
                            carDataPartObj.getLat(),
                            carDataPartObj.getLng(),
                            electricFenceResultTmp.getLatitude(),
                            electricFenceResultTmp.getLongitude()
                    );
                    //封装，是否在当前的电子围栏的圈内 0 内 1 外
                    if (electricFenceResultTmp.getRadius() >= distance/1000D) {
                        electricFenceModel.setNowStatus(0);
                    }else {
                        electricFenceModel.setNowStatus(1);
                    }
                    //2.3.2.5.收集结果数据
                    collector.collect(electricFenceModel);
                }else {
                    logger.warn("当前电子围栏的配置时间已过期~");
//                    System.out.println("当前电子围栏的配置时间已过期~");
                }
            }else {
                logger.warn("当前的上报车辆的电子围栏的配置信息为空，请检查~");
//                System.out.println("当前的上报车辆的电子围栏的配置信息为空，请检查~");
            }
        }else {
            logger.error("当前上报的数据中gps时间为空或经度纬度无效");
//            System.out.println("当前上报的数据中gps时间为空或经度纬度无效");
        }
    }

    /**
     * 判断当前坐标 经度和维度是否有效
     * 当前数据不能为 0 或者 -999999
     * @param location
     * @return
     */
    private boolean isValid(Double location) {
        if (location != 0 && location != -999999) {
            return true;
        } else {
        return false;
        }
    }

    /**
     *
     * @param value
     * @param out
     * @throws Exception
     */
    @Override
    public void flatMap2(HashMap<String, ElectricFenceResultTmp> value, Collector<ElectricFenceModel> out) throws Exception {
        electricFenceConfig = value;
        System.out.println(electricFenceConfig.toString());
    }
}
