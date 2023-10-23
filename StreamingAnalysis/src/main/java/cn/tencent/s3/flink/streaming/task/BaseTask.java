package cn.tencent.s3.flink.streaming.task;

import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.core.fs.Path;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.sink.filesystem.OutputFileConfig;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import org.apache.flink.streaming.api.functions.sink.filesystem.bucketassigners.DateTimeBucketAssigner;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

import static org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumerBase.KEY_PARTITION_DISCOVERY_INTERVAL_MILLIS;

/**
 * 文件名：BaseTask
 * 项目名：CarNetworkingSystem
 * 描述：抽象工具类
 * 作者：linker
 * 创建时间：2023/9/13
 * 开发步骤：
 * 此抽象类主要用于实现以下三个功能
 * 1.获取可执行的流环境，根据参数配置
 * 2.读取kafka数据源，根据参数配置及用户输入
 * 3.封装写入到 HDFS 上的方法
 */
public class BaseTask {
     private static Logger logger = LoggerFactory.getLogger("BaseTask");

    /**
     * 通过flink自带的参数工具 ParameterTool 获取传入的参数及配置中的参数
     */
    private static ParameterTool parameterTool  = null;

    /*
      使用 static 静态代码块来实现
     */
    static {
        try {
            parameterTool = ParameterTool.fromPropertiesFile(
                    BaseTask.class.getClassLoader().getResourceAsStream("conf.properties")
            );
        } catch (IOException e) {
            logger.error("当前读取配置文件出错 "+e.getMessage());
        }
    }
    public static StreamExecutionEnvironment getEnv(String jobName) {
        //todo 1.创建流执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //todo 2.设置并行度 ①配置文件并行度设置 ②客户端设置 flink run -p 2 ③在程序中 env.setParallel(2) ④算子上并行度（级别最高）
        env.setParallelism(1);
        //todo 3.开启checkpoint及相应的配置，最大容忍次数，最大并行checkpoint个数，checkpoint间最短间隔时间，checkpoint的最大
        env.enableCheckpointing(5000);
        env.getCheckpointConfig().setCheckpointTimeout(60 * 1000);
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
        env.getCheckpointConfig().setTolerableCheckpointFailureNumber(10);
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(500);
        //todo 让 job 所有的算子都能拿到这些配置
        env.getConfig().setGlobalJobParameters(parameterTool);
        //todo 3.1容忍的超时时间，checkpoint如果取消是否删除checkpoint 等
        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.
                ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        //todo 3.2将其保存到 状态后端 statebackend
        env.setStateBackend(new FsStateBackend(parameterTool.get("hdfsUri")+"/flink-checkpoint"+jobName));
        //todo 4.开启重启策略
        env.setRestartStrategy(RestartStrategies.noRestart());
        return env;
    }

        public static <T>FlinkKafkaConsumer<T> getKafkaConsumer(Class<? extends DeserializationSchema> clazz) {
        //todo 5. 读取kafka中的数据
        //todo 5.2 配置参数
        Properties props = new Properties();
        props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,parameterTool.get("bootstrap.servers"));
        props.setProperty(ConsumerConfig.GROUP_ID_CONFIG,parameterTool.get("group.id"));
        props.setProperty(KEY_PARTITION_DISCOVERY_INTERVAL_MILLIS,10*60*1000 + "");
        //todo 5.1 设置 FlinkKafkaConsumer
            FlinkKafkaConsumer<T> consumer = null;
            try {
                consumer = new FlinkKafkaConsumer<T>(
                        parameterTool.get("kafka.topic"),
                        clazz.newInstance(),
                        props
                );
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            //todo 5.3 消费 kafka 的offset 提交给 flink 来管理
        consumer.setCommitOffsetsOnCheckpoints(true);
        consumer.setStartFromLatest();
        return consumer;
    }

    public static SinkFunction<String> sinkHDFS(String prefix,String suffix,String path) {
        StreamingFileSink fileSink = StreamingFileSink.forRowFormat(
                        new Path(parameterTool.getRequired("hdfsUri") + parameterTool.getRequired(path)),
                        new SimpleStringEncoder()
                )
                //todo 设置桶策略 yyyyMMdd
                .withBucketAssigner(new DateTimeBucketAssigner<String>("yyyyMMdd"))
                //todo 滚筒策略 多长时间、多大分成文件 pending
                .withRollingPolicy(DefaultRollingPolicy.builder()
                        .withMaxPartSize(100 * 1024 * 1024)
                        .withInactivityInterval(20000)
                        //最大的写入时间
                        .withRolloverInterval(10000)
                        .build()
                )
                //todo 文件的前缀后缀
                .withOutputFileConfig(
                        new OutputFileConfig(
                                prefix, suffix
                        )
                ).build();
        return fileSink;
    }

}
