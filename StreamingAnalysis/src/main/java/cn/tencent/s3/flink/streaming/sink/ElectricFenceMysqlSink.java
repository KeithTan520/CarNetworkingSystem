package cn.tencent.s3.flink.streaming.sink;

import cn.tencent.s3.flink.streaming.entity.ElectricFenceModel;
import cn.tencent.s3.flink.streaming.utils.DateUtil;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


/**
 * 文件名：ElectricFenceMysqlSink
 * 项目名：CarNetworkingSystem
 * 描述：
 * 作者：linker
 * 创建时间：2023/10/22
 * 开发步骤：
 */
public class ElectricFenceMysqlSink extends RichSinkFunction<ElectricFenceModel> {
    //logger日志
    Logger logger = LoggerFactory.getLogger(ElectricFenceMysqlSink.class);
    //创建休眠时间临时对象
    Long electricFenceSleep = 0L;
    //创建连接临时对象
    Connection conn = null;
    //
    PreparedStatement ps = null;

    //重写open方法
    @Override
    public void open(Configuration parameters) throws Exception {
        //通过上下文获取配置文件
        ParameterTool parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        //获取驱动包名
        String driver = parameterTool.getRequired("jdbc.driver");
        //设置驱动类
        Class.forName(driver);
        //读取连接的参数
        String user = parameterTool.getRequired("jdbc.user");
        String url = parameterTool.getRequired("jdbc.url");
        String password = parameterTool.getRequired("jdbc.password");
        //读取配置中休眠时间
        electricFenceSleep = Long.parseLong(parameterTool.getRequired("elerules.millionseconds"));
        //获取连接
        conn = DriverManager.getConnection(
                url,
                user,
                password
        );
    }

    @Override
    public void invoke(ElectricFenceModel value, Context context){
        try {
            //出围栏(且能获取到进围栏状态的)则修改进围栏的状态
            if (value.getStatusAlarm() == 0 && value.getInMysql()) {
               String executeSql = "update vehicle_networking.electric_fence set outTime=?,gpsTime=?," +
                       "lat=?,lng=?,terminalTime=?,processTime=? where id=?";
                ps = conn.prepareStatement(executeSql);
                ps.setObject(1,value.getOutEleTime());
                ps.setObject(2,value.getGpsTime());
                ps.setObject(3,value.getLat());
                ps.setObject(4,value.getLng());
                ps.setObject(5,value.getTerminalTime());
                ps.setObject(6, DateUtil.getCurrentDateTime());
                ps.setObject(7,value.getUuid());
            }else {
                // 进入围栏，转换ElectricFenceModel对象，插入结构数据到电子围栏结果表
                //noinspection SqlResolve
                String sql = "insert into vehicle_networking.electric_fence(vin,inTime,outTime,gpsTime,lat,lng,eleId,eleName," +
                        "address,latitude,longitude,radius,terminalTime,processTime) " +
                        "                       values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                    ps = conn.prepareStatement(sql);
                    ps.setString(1, value.getVin());
                    ps.setObject(2, value.getInEleTime());
                    ps.setObject(3, value.getOutEleTime());
                    ps.setObject(4, value.getGpsTime());
                    ps.setDouble(5, value.getLat());
                    ps.setDouble(6, value.getLng());
                    ps.setInt(7, value.getEleId());
                    ps.setString(8, value.getEleName());
                    ps.setString(9, value.getAddress());
                    ps.setDouble(10, value.getLatitude());
                    ps.setDouble(11, value.getLongitude());
                    ps.setFloat(12, value.getRadius());
                    ps.setObject(13, value.getTerminalTime());
                    ps.setObject(14, DateUtil.getCurrentDate());
            }
            ps.execute();
            logger.warn("MysqlSink，批量插入数据成功，插入{}条数据",ps.getMetaData());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void close() throws Exception {
        if (conn.isClosed()) {
            conn.close();
        }
        if (ps.isClosed()){
            ps.close();
        }
    }
}
