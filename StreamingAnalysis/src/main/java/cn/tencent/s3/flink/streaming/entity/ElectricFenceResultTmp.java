package cn.tencent.s3.flink.streaming.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 文件名：ElectricFenceResultTmp
 * 项目名：CarNetworkingSystem
 * 描述：
 * 作者：linker
 * 创建时间：2023/10/18
 * 开发步骤：
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElectricFenceResultTmp {
    //电子围栏id
    private int id;
    //电子围栏名称
    private String name;
    //电子围栏中心地址
    private String address;
    //电子围栏半径
    private float radius;
    //电子围栏中心点的经度
    private double longitude;
    //电子围栏中心点的维度
    private double latitude;
    //电子围栏的开始时间
    private Date startTime;
    //电子围栏的结束时间
    private Date endTime;

    @Override
    public String toString() {
        return "ElectricFenceResultTmp{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", radius=" + radius +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
