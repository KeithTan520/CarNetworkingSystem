package cn.tencent.s3.flink.streaming.sink;

import cn.tencent.s3.flink.streaming.entity.CarDataObj;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Author itcast
 * Date 2021/9/22 10:02
 * Desc 将每条车辆的数据直接写入到 Hive 中
 */
public class SaveErrorDataHiveSink extends RichSinkFunction<CarDataObj> {
    //定义 logger
    private static final Logger logger = LoggerFactory.getLogger(SaveErrorDataHiveSink.class);
    //2.创建有参构造方法，参数包括数据库名和表名
    //定义变量
    private String dbName;
    private String tableName;
    //定义连接对象和statement对象
    private Connection conn = null;
    private Statement statement = null;
    //构造方法
    public SaveErrorDataHiveSink(String _dbName,String _tableName){
        this.dbName = _dbName;
        this.tableName = _tableName;
    }

    //3.重写open方法进行Hive连接的初始化
    @Override
    public void open(Configuration parameters) throws Exception {
        //3.1 将JDBC驱动 org.apache.hive.jdbc.HiveDriver 加载进来
        //获取全局参数
        ParameterTool parameterTool = (ParameterTool) getRuntimeContext()
                .getExecutionConfig()
                .getGlobalJobParameters();
        //获取当前上下文中 hive 的驱动
        Class.forName(parameterTool.getRequired("hive.driver"));
        //3.2 设置JDBC连接Hive的连接器，端口为10000
        conn = DriverManager.getConnection(
                parameterTool.getRequired("hive.url"),
                parameterTool.getRequired("hive.user"),
                parameterTool.get("hive.password")
        );
        //3.3 创建Statement
        statement = conn.createStatement();
        //3.4 定义 schemaAndTableExists 实现库不存在创建库，表不存在创建表
        Boolean flag = schemaAndTableExists(dbName,tableName,statement);
        if(flag){
            logger.info("当前数据库和表初始化成功！");
        }else{
            logger.warn("请检查数据库和表！");
        }
    }

    //5.重写cloese方法 关闭连接
    @Override
    public void close() throws Exception {
        if(!statement.isClosed())statement.close();
        if(!conn.isClosed())conn.close();
    }

    //4.重写invoke将每条数据
    @Override
    public void invoke(CarDataObj value, Context context) throws Exception {
        //4.1 编写SQL将数据插入到表中
        // insert into itcast_error values('11111');
        StringBuffer buffer = new StringBuffer();
        buffer.append("INSERT INTO "+tableName);
        buffer.append(" VALUES('");
        buffer.append(value.getErrorData()+"'");
        //4.2 执行statement.executeUpdate 将数据直接落地到Hive表
        statement.executeUpdate(buffer.toString());
    }

    //6.定义 schemaAndTableExists 方法 create database if not exists库或表, execute，选择数据库

    /**
     * 初始化数据库和数据表，如果初始化成功返回 true，否则 false
     * @param dbName
     * @param tableName
     * @param statement
     * @return
     */
    private Boolean schemaAndTableExists(String dbName, String tableName, Statement statement) {
        //数据库是否存在
        Boolean flag = true;
        try{
            //初始化数据库
            String createDBSQL="create database if not exists "+dbName;
            boolean executeDB = statement.execute(createDBSQL);
            if(executeDB){
                logger.info("当前数据库创建成功");
                flag = true;
            }else{
                logger.info("当前数据库已经存在");
                flag = true;
            }
            //初始化数据表
            String createTableSQL = "use "+tableName+";create table if not exists "+tableName+" (json string) partition by dt" +
                    " row formatted delimited field terminate by '\t' location '/apps/hive/warehouse/ods.db/itcast_error'";
            boolean executeTable = statement.execute(createTableSQL);
            if(executeTable){
                logger.info("当前数据库表创建成功");
                flag = true;
            }else{
                logger.info("当前数据表已经存在");
                flag = true;
            }
        }catch (Exception ex){
            logger.warn("初始化失败！");
            flag = false;
        }
        return flag;
    }
}