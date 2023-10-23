package cn.tencent.s3.flink.streaming.source;

import cn.tencent.s3.flink.streaming.entity.ElectricFenceResultTmp;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * 文件名：MysqlElectricFenceSouce
 * 项目名：CarNetworkingSystem
 * 描述：Desc 这个类主要用于读取 mysql 中的电子栅栏的配置信息
 * 作者：linker
 * 创建时间：2023/10/18
 * 开发步骤：
 * 泛型 HashMap<String, ElectricFenceResultTmp>
 * String: vin ElectricFenceResultTmp: 电子栅栏的配置数据
 * 电子栅栏的配置数据都有啥：
 * 1.vin
 * 2.中心点 经度和维度
 * 3.栅栏半径
 * 4.有效的开始时间
 * 5.有效的结束时间
 */
public class MysqlElectricFenceSouce extends RichSourceFunction<HashMap<String, ElectricFenceResultTmp>> {
    Connection conn = null;
    Statement statement = null;
    //定义读取电子栅栏规则的休眠时间（多久去数据库中读取一次电子栅栏的配置）
    long electricFenceSleep = 0L;
    //用于标记当前是否被循环读取
    boolean flag = true;
    //1.重写 open 方法
    @Override
    public void open(Configuration parameters) throws Exception {
        //1.1 获取上下文中的 parameterTool
        ParameterTool parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        String driver = parameterTool.getRequired("jdbc.driver");
        //设置驱动类
        Class.forName(driver);
        //读取连接的参数
        String url = parameterTool.getRequired("jdbc.url");
        String user = parameterTool.getRequired("jdbc.user");
        String password = parameterTool.getRequired("jdbc.password");
        //读取配置中的休眠时间
        electricFenceSleep = Long.parseLong(parameterTool.getRequired("elerules.millionseconds"));
        //1.2 读取配置文件中，注册驱动 url user password,获取连接
        conn = DriverManager.getConnection(
                url,
                user,
                password
        );
        //1.3 实例化statement
        statement = conn.createStatement();
    }
    //2.重写 close 方法
    @SuppressWarnings("AliControlFlowStatementWithoutBraces")
    @Override
    public void close() throws Exception {
        //2.1 关闭 statement 和 conn
        if(!conn.isClosed()) conn.close();
        if(!statement.isClosed()) statement.close();
    }
    //3.重写 run 方法
    @Override
    public void run(SourceContext<HashMap<String, ElectricFenceResultTmp>> sourceContext) throws Exception {
        //3.1 每指定时间循环读取 mysql 中的电子围栏规则
        while (flag) {
            HashMap<String, ElectricFenceResultTmp> result = new HashMap<>();
            //两个表关联
            //noinspection SqlResolve
            String sql = "select vins.vin,setting.id,setting.name,setting.address,setting.radius,setting.longitude,setting.latitude,setting.start_time,setting.end_time \n" +
                    "from vehicle_networking.electronic_fence_setting setting \n" +
                    "inner join vehicle_networking.electronic_fence_vins vins on setting.id=vins.setting_id \n" +
                    "where setting.status=1";
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                String vin = rs.getString("vin");
                result.put(vin,
                        new ElectricFenceResultTmp(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getString("address"),
                                rs.getFloat("radius"),
                                rs.getDouble("longitude"),
                                rs.getDouble("latitude"),
                                rs.getDate("start_time"),
                                rs.getDate("end_time")
                        )
                );
            }
            //3.2 收集 electricFenceResult 指定休眠时间
            sourceContext.collect(result);
            TimeUnit.MILLISECONDS.sleep(electricFenceSleep);
        }

    }
    //4.重写 cancel 方法
    @Override
    public void cancel() {
        flag = false;
    }
}
