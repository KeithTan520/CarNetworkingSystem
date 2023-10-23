package cn.tencent.s3.flink.streaming.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 文件名：ElectricFenceModel
 * 项目名：CarNetworkingSystem
 * 描述：电子围栏规则计算模型
 * 作者：linker
 * 创建时间：2023/10/19
 * 开发步骤：
 * Comparable 接口是 Java 中的一个泛型接口，它规定了一个类的实例如何与其他同类型的实例进行比较。
 * 通常，它要求类实现 compareTo 方法，该方法会返回一个整数值，表示当前对象与另一个对象的大小关系。
 * 在你的 ElectricFenceModel 类中，你已经实现了 compareTo 方法。
 * 通过实现 Comparable 接口，你的 ElectricFenceModel 类可以利用 Java 的排序算法进行排序，
 * 或者在需要排序的数据结构中使用，比如集合（Collections）。这对于需要按照时间戳（terminalTimestamp）比较和排序 ElectricFenceModel 对象的情况非常有用，
 * 因为你已经在 compareTo 方法中定义了比较的逻辑。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ElectricFenceModel implements Comparable<ElectricFenceModel> {
    //车架号
    private String vin = "";
    //电子围栏结果表UUID
    private Long uuid = -999999L;
    //上次状态 0 里面 1 外面
    private int lastStatus = -999999;
    //当前状态 0  里面 1 外面
    private int nowStatus = -999999;
    //位置时间 yyyy-MM-dd HH:mm:ss
    private String gpsTime = "";
    //位置纬度--
    private Double lat = -999999D;
    //位置经度--
    private Double lng = -999999D;
    //电子围栏ID
    private int eleId = -999999;
    //电子围栏名称
    private String eleName = "";
    //中心点地址
    private String address = "";
    //中心点纬度
    private Double latitude;
    //中心点经度
    private Double longitude = -999999D;
    //电子围栏半径
    private Float radius = -999999F;
    //出围栏时间
    private String outEleTime = null;
    //进围栏时间
    private String inEleTime = null;
    //是否在mysql结果表中
    private Boolean inMysql = false;
    //状态报警 0：出围栏 1：进围栏
    private int statusAlarm = -999999;
    //报警信息
    private String statusAlarmMsg = "";
    //终端时间
    private String terminalTime = "";
    // 扩展字段 终端时间
    private Long terminalTimestamp = -999999L;

    @Override
    public int compareTo(ElectricFenceModel o) {
        if(this.getTerminalTimestamp() > o.getTerminalTimestamp()){
            return  1;
        }
        else if(this.getTerminalTimestamp() < o.getTerminalTimestamp()){
            return  -1;
        }else{
            return 0;
        }
    }
}
