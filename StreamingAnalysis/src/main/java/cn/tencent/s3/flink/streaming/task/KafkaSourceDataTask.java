package cn.tencent.s3.flink.streaming.task;

import cn.tencent.s3.flink.streaming.entity.CarDataObj;
import cn.tencent.s3.flink.streaming.sink.DetailToHBaseOptimizeSink;
import cn.tencent.s3.flink.streaming.utils.JsonParseUtil;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.commons.lang3.StringUtils;

import static cn.tencent.s3.flink.streaming.task.BaseTask.getKafkaConsumer;
import static cn.tencent.s3.flink.streaming.task.BaseTask.sinkHDFS;

/**
 * 文件名：KafkaSourceDataTask
 * 项目名：CarNetworkingSystem
 * 描述：
 * 作者：linker
 * 创建时间：2023/9/13
 * 开发步骤：
 * //todo 1.创建流执行环境
 * //todo 2.设置并行度 ①配置文件并行度设置 ②客户端设置 flink run -p 2 ③在程序中 env.setParallel(2) ④算子上并行度（级别最高）
 * //todo 3.开启checkpoint及相应的配置，最大容忍次数，最大并行checkpoint个数，checkpoint间最短间隔时间，checkpoint的最大
 * //todo 容忍的超时时间，checkpoint如果取消是否删除checkpoint 等
 * //todo 4.开启重启策略
 * //todo 5. 读取kafka中的数据
 * //todo 5.1 设置 FlinkKafkaConsumer
 * //todo 5.2 配置参数
 * //todo 5.3 消费 kafka 的offset 提交给 flink 来管理
 * //todo 6 env.addSource
 * //todo 7 打印输出
 * //todo 8 将读取出来的 json 字符串转换成 ItcastDataObj
 * //todo 9 将数据拆分成正确的数据和异常的数据
 * //todo 10 将正确的数据保存到 hdfs
 * //todo 11 将错误的数据保存到 hdfs 上
 * //todo 12 将正确的数据写入到 hbase 中
 * //todo 8 执行流环境
 */
public class KafkaSourceDataTask {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = BaseTask.getEnv(KafkaSourceDataTask.class.getSimpleName());
        FlinkKafkaConsumer<String> consumer = getKafkaConsumer(SimpleStringSchema.class);
        //todo 6 env.addSource
        DataStreamSource<String> source = env.addSource(consumer);
        //todo 7 打印输出
        source.printToErr();
        //todo 8 将读取出来的 json 字符串转换成 CarDataObj
        SingleOutputStreamOperator<CarDataObj> vehicleData = source.map(json -> JsonParseUtil.parseJsonToObject(json));
        //todo 9 将数据拆分成正确的数据和异常的数据
        SingleOutputStreamOperator<CarDataObj> sourceDataStream = vehicleData.filter(obj -> StringUtils.isEmpty(obj.getErrorData()));
        //todo 9.1 如果当前不为空，说明是错误的数据
        SingleOutputStreamOperator<CarDataObj> errorDataStream = vehicleData.filter(obj -> StringUtils.isNotEmpty(obj.getErrorData()));
        //todo 10 将正确的数据保存到 hdfs
        //sourceDataStream.map(t->t.toHiveString()).addSink(sinkHDFS("src_",".txt","srcData"));
        //todo 11 将错误的数据保存到 hdfs 上
        //errorDataStream.map(t->t.toHiveString()).addSink(sinkHDFS("error_",".txt","errorData"));
        //todo 12 将正确的数据写入到 hbase 中
        sourceDataStream.addSink(new DetailToHBaseOptimizeSink("src_car"));
        //todo 13 执行流环境
        env.execute();
    }




}
