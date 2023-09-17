package cn.tencent.s3.flink.streaming.producer;

import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.io.TextInputFormat;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.FileProcessingMode;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.annotation.Nullable;
import java.util.Properties;

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
    public static void main(String[] args) throws Exception {
        //todo 1.flink创建流执行环境，设置并行度
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        //todo 2.设置开启checkpoint
        env.enableCheckpointing(5000);
        //todo 3.设置重启策略 不重启
        env.setRestartStrategy(RestartStrategies.noRestart());
        //todo 4.读取File数据源
        DataStreamSource<String> source = env.readFile(
                new TextInputFormat(null),
                "/Users/tankeith/bigdata_analysis/CarNetworkingSystem/data/sourcedata.txt",
                FileProcessingMode.PROCESS_CONTINUOUSLY,
                60 * 1000
        );
        //source.printToErr();
        //todo 5.初始化 FlinkKafkaProducer及必须配置
        Properties props = new Properties();
        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.88.161:9092,192.168.88.162:9092");
        props.setProperty(ProducerConfig.BATCH_SIZE_CONFIG,5+"");

        FlinkKafkaProducer<String> producer = new FlinkKafkaProducer<>(
                "vehicledata",
                new KafkaSerializationSchema<String>() {
                    @Override
                    public ProducerRecord<byte[], byte[]> serialize(String s, @Nullable Long aLong) {
                        return new ProducerRecord<byte[], byte[]>(
                                "vehicledata",
                                s.getBytes()
                        );
                    }
                },
                props,
                FlinkKafkaProducer.Semantic.NONE
        );
        //todo 6.写入到kafka集群
        source.addSink(producer);
        //todo 7.执行流环境
        env.execute();
    }
}
