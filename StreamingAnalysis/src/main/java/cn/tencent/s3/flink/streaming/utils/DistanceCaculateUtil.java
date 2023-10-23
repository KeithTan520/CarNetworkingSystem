package cn.tencent.s3.flink.streaming.utils;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;

import java.util.Objects;

/**
 * 文件名：DistanceCaculateUtil
 * 项目名：CarNetworkingSystem
 * 描述：TODO 球面距离计算工具类;根据两个点的经纬度，计算出距离
 * 作者：linker
 * 创建时间：2023/10/19
 * 开发步骤：
 */
public class DistanceCaculateUtil {
    /**
     * @desc 计算地址位置方法，坐标系、经纬度用于计算距离(直线距离)
     * @param gpsFrom
     * @param gpsTo
     * @param ellipsoid
     * @return 计算距离
     */
    private static Double getDistanceMeter(GlobalCoordinates gpsFrom, GlobalCoordinates gpsTo, Ellipsoid ellipsoid){
        GeodeticCurve geodeticCurve = new GeodeticCalculator().calculateGeodeticCurve(ellipsoid, gpsFrom, gpsTo);
        return geodeticCurve.getEllipsoidalDistance();
    }

    /**
     * @desc 使用传入的ellipsoidsphere方法计算距离
     * @param latitude  位置1经度
     * @param longitude 位置1维度
     * @param latitude2 位置2经度
     * @param longitude2 位置2维度
     * @param ellipsoid 椭圆计算算法
     * @return
     */
    private static Double ellipsoidMethodDistance(Double latitude, Double longitude, Double latitude2, Double longitude2, Ellipsoid ellipsoid){
        //todo 位置点经度、维度不为空 位置点2经度、维度不为空 椭圆算法
        Objects.requireNonNull(latitude, "latitude is not null");
        Objects.requireNonNull(longitude, "longitude is not null");
        Objects.requireNonNull(latitude2, "latitude2 is not null");
        Objects.requireNonNull(longitude2, "longitude2 is not null");
        Objects.requireNonNull(ellipsoid, "ellipsoid method is not null");
        //todo 地球坐标对象：封装经度维度坐标对象
        GlobalCoordinates source = new GlobalCoordinates(latitude, longitude);
        GlobalCoordinates target = new GlobalCoordinates(latitude2, longitude2);
        //todo 椭圆范围计算方法
        return getDistanceMeter(source,target,ellipsoid);
    }

    /**
     * @desc 使用ellipsoidsphere方法计算距离
     * @param latitude
     * @param longitude
     * @param latitude2
     * @param longitude2
     * @return distance 单位：m
     */
    public static Double getDistance(Double latitude, Double longitude, Double latitude2, Double longitude2){
        // 椭圆范围计算方法：Ellipsoid.Sphere
        return ellipsoidMethodDistance(latitude, longitude, latitude2, longitude2, Ellipsoid.Sphere);
    }
}
