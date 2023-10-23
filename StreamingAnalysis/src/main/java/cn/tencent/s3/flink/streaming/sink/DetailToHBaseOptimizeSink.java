package cn.tencent.s3.flink.streaming.sink;


import cn.tencent.s3.flink.streaming.entity.CarDataObj;
import cn.tencent.s3.flink.streaming.utils.StringUtil;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.util.Bytes;
import org.datanucleus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 文件名：DetailToHBaseOptimizeSink
 * 项目名：CarNetworkingSystem
 * 描述：
 * 作者：linker
 * 创建时间：2023/9/19
 * 开发步骤：
 */
public class DetailToHBaseOptimizeSink extends RichSinkFunction<CarDataObj> {
    //设置logger
    private static final Logger logger = LoggerFactory.getLogger(DetailToHBaseOptimizeSink.class);
    //成员变量 表名
    private String tablename;
    //创建缓存对象
    private BufferedMutator bufferedMutator = null;
    //创建连接对象
    Connection conn = null;

    public DetailToHBaseOptimizeSink(String tablename) {
        this.tablename = tablename;
    }

    /**
     * 设置hbase连接，开启连接，创建 statement，创建hbase 表
     * @param parameters
     * @throws Exception
     */
    @Override
    public void open(Configuration parameters) throws Exception {
        //3.1 从上下文获取到全局的参数
        ParameterTool parameterTool = (ParameterTool) getRuntimeContext()
                .getExecutionConfig()
                .getGlobalJobParameters();
        //3.2 设置hbase的配置，Zookeeper Quorum集群和端口和TableInputFormat的输入表
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        //设置 Zookeeper Quorum 集群和端口
        conf.set(HConstants.ZOOKEEPER_QUORUM,parameterTool.get("zookeeper.quorum"));
        conf.set(HConstants.ZOOKEEPER_CLIENT_PORT,parameterTool.get("zookeeper.clientPort"));
        //处理落库的表
        conf.set(TableInputFormat.INPUT_TABLE,tablename);

        org.apache.hadoop.conf.Configuration hConf = HBaseConfiguration.create(conf);
        //3.3 通过连接工厂创建连接
        conn = ConnectionFactory.createConnection(conf);
        //3.4 通过连接获取表对象
        //table = conn.getTable(TableName.valueOf(tablename));
        //设置缓存对象的参数
        BufferedMutatorParams params = new BufferedMutatorParams(TableName.valueOf(tablename));
        //设置多久或多大缓存刷写到hbase中
        params.writeBufferSize(10*1024*1024);
        params.setWriteBufferPeriodicFlushTimeoutMs(10*1000);

         bufferedMutator = conn.getBufferedMutator(params);
    }

    /**
     * 关闭表和连接
     * @throws Exception
     */
    @Override
    public void close() throws Exception {
        //强制将不满足条件的数据刷写进来
        bufferedMutator.flush();
        //4.1 关闭hbase 表和连接资源
        if (bufferedMutator != null) bufferedMutator.close();
        if (conn != null) conn.close();
    }

    /**
     * 设置将 CarDataObj 对象写入到 HBase 中
     * @param value
     * @param context
     * @throws Exception
     */
    @Override
    public void invoke(CarDataObj value, Context context) throws Exception {
        //5.1 setDataSourcePut输入参数value，返回put对象
        Put put = setDataSource(value);
        bufferedMutator.mutate(put);
    }

    /**
     * //6. 实现 setDataSourcePut 方法
     * @param carDataObj
     * @return
     */
    private Put setDataSource(CarDataObj carDataObj){
        //准备表名、rowkey设计、列簇cf、列名=列值
        //6.1 如何设计rowkey VIN+时间戳翻转
        String rowkey = carDataObj.getVin() + StringUtil.reverse(carDataObj.getTerminalTime() + "");
        //6.2 定义列簇的名称
        String cf = "cf";
        Put put = null;
        try {
            //6.3 通过 rowkey 实例化 put
            put = new Put(Bytes.toBytes(rowkey));
            //6.4 将所有的字段添加到put的字段中
            //设置需要写入的列有那些
            //这两个列一定不为空，如果为空就不是正常数据了
            put.addColumn(Bytes.toBytes(cf),Bytes.toBytes("vin"),Bytes.toBytes(carDataObj.getVin()));
            put.addColumn(Bytes.toBytes(cf),Bytes.toBytes("terminalTime"),Bytes.toBytes(carDataObj.getTerminalTime()));
            //电量百分比(currentElectricity)、当前电量(remainPower)、百公里油耗(fuelConsumption100km)、
            // 发动机速度(engineSpeed)、车辆速度(vehicleSpeed)
            if (carDataObj.getCurrentElectricity() != -999999D) {
                put.addColumn(Bytes.toBytes(cf),Bytes.toBytes("currentElectricity"),Bytes.toBytes(carDataObj.getCurrentElectricity()));
            }
            if (carDataObj.getRemainPower() != -999999D) {
                put.addColumn(Bytes.toBytes(cf),Bytes.toBytes("remainPower"),Bytes.toBytes(carDataObj.getRemainPower()));
            }
            if (StringUtils.isEmpty(carDataObj.getFuelConsumption100km())) {
                put.addColumn(Bytes.toBytes(cf),Bytes.toBytes("fuelConsumption100km"),Bytes.toBytes(carDataObj.getFuelConsumption100km()));
            }
            if (StringUtils.isEmpty(carDataObj.getEngineSpeed())) {
                put.addColumn(Bytes.toBytes(cf),Bytes.toBytes("engineSpeed"),Bytes.toBytes(carDataObj.getEngineSpeed()));
            }
            if (carDataObj.getVehicleSpeed() != -999999D) {
                put.addColumn(Bytes.toBytes(cf),Bytes.toBytes("vehicleSpeed"),Bytes.toBytes(carDataObj.getVehicleSpeed()));
            }
            System.out.println("成功put");
        } catch (Exception e) {
            logger.error("封装车辆常见字段对象失败，异常信息"+e.getMessage());
        }
        //返回put对象
        return put;
    }
}
