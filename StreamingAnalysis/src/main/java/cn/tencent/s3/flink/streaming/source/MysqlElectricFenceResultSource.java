package cn.tencent.s3.flink.streaming.source;

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
 * 文件名：MysqlElectricFenceResultSource
 * 项目名：CarNetworkingSystem
 * 描述：读取mysql中的数据，返回 Map<String,Long>
 * 作者：linker
 * 创建时间：2023/10/22
 * 开发步骤：
 * String : Vin
 * Long : 数据库中电子围栏id
 */
public class MysqlElectricFenceResultSource extends RichSourceFunction<HashMap<String,Long>> {
    Connection conn = null;
    Statement statement = null;
    //定义读取电子栅栏规则的休眠时间（多久去数据库中读取一次电子栅栏的配置）
    Long electricFenceSleep = 0L;
    //用于标记当前是否被循环读取
    boolean flag = true;
    //1.重写 open 方法
    @Override
    public void open(Configuration parameters) throws Exception {
        //1.1 获取上下文中的 parameterTool
        ParameterTool parameterTool = (ParameterTool)getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
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

    @Override
    public void run(SourceContext<HashMap<String,Long>> sourceContext) throws Exception {
        //sql
        String sql = "select vin, min(id) id from vehicle_networking.electric_fence " +
                "where inTime is not null and outTime is null " +
                "GROUP BY vin";
        //执行返回受影响的行数
        ResultSet rs = statement.executeQuery(sql);
        //封装Hashmap
        HashMap<String, Long> result = new HashMap<>();
        //读取数据
        while (rs.next()) {
            String vin = rs.getString("vin");
            long id = rs.getLong("id");
            result.put(vin,id);
        }
        sourceContext.collect(result);
        TimeUnit.SECONDS.sleep(1);
    }

    @Override
    public void cancel() {
        flag = false;
    }

    //2.重写 close 方法
    @Override
    public void close() throws Exception {
        //2.1 关闭 statement 和 conn
        if (conn.isClosed()) {
            conn.close();
        }
        if (statement.isClosed()) {
            statement.close();
        }
    }
}
