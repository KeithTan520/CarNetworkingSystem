package cn.tencent.s3.flink.producer;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * 文件名：FlinkKafkaWriter
 * 项目名：CarNetworkingSystem
 * 描述：主要用于将解析后的报文json字符串写入到 kafka 集群
 * 作者：linker
 * 创建时间：2023/9/12
 * 开发步骤：
 * todo 1.flink创建流执行环境，设置并行度
 * todo 2.设置开启checkpoint
 * todo 3.设置重启策略 不重启
 * todo 4.读取File数据源，初始化 FlinkKafkaProducer及必须配置
 * todo 5.添加数据源
 * todo 6.写入到kafka集群
 * todo 7.执行流环境
 */
public class FlinkKafkaWriter {
    public static void main(String[] args) {
        //todo 1.flink创建流执行环境，设置并行度
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        //todo 2.设置开启checkpoint
        env.enableCheckpointing(5);
        //todo 3.设置重启策略 不重启

        //todo 4.读取File数据源，初始化 FlinkKafkaProducer及必须配置
        //todo 5.添加数据源
        //todo 6.写入到kafka集群
        //todo 7.执行流环境
    }
}
