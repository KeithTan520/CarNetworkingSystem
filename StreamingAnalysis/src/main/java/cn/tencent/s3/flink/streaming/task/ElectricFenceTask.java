package cn.tencent.s3.flink.streaming.task;

import cn.tencent.s3.flink.streaming.entity.CarDataPartObj;
import cn.tencent.s3.flink.streaming.entity.ElectricFenceModel;
import cn.tencent.s3.flink.streaming.entity.ElectricFenceResultTmp;
import cn.tencent.s3.flink.streaming.flatmap.ElectricFenceModelFunction;
import cn.tencent.s3.flink.streaming.flatmap.ElectricFenceRulesFuntion;
import cn.tencent.s3.flink.streaming.process.ElectricFenceWindowFunction;
import cn.tencent.s3.flink.streaming.sink.ElectricFenceMysqlSink;
import cn.tencent.s3.flink.streaming.source.MysqlElectricFenceResultSource;
import cn.tencent.s3.flink.streaming.source.MysqlElectricFenceSouce;
import cn.tencent.s3.flink.streaming.utils.JsonParsePartUtil;
import cn.tencent.s3.flink.streaming.watermark.ElectricFenceWatermark;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.datanucleus.util.StringUtils;

import java.util.HashMap;

/**
 * 文件名：ElectricFenceTask
 * 项目名：CarNetworkingSystem
 * 描述：
 * 作者：linker
 * 创建时间：2023/10/18
 * 开发步骤：
 * 1.读取流环境
 * 2.读取kafka中json字符串数据
 * 3.将json字符串转换成 ItcastDataPartObj 对象
 * 4.过滤出来正确的数据 errorData 是 empty
 * 5.读取mysql中的数据源，电子围栏的数据流
 * 6.将电子围栏的数据源广播出去
 * 7.将两个数据流进行 connect 连接操作并 flatmap 转换成对象，包含电子围栏的数据和实时上报的车辆数据，拉宽操作
 * 8.分配水印、分组、滚动窗口（90s）操作
 * 9.处理窗口 process，添加是否进入栅栏、出栅栏
 * 10.读取mysql中的配置（当前车辆是否在电子栅栏内存在过，mysql中是否有记录，有的更新时间、没有的话将时间、uuid、inmysql插入数据库）
 * 11.将其广播出去并和窗口数据进行 connect 并 flatmap得到最终需要落库的结果对象
 * 12.执行流环境
 */
public class ElectricFenceTask extends BaseTask {
    public static void main(String[] args) throws Exception {
        //1. 电子围栏分析任务设置、原始数据json解析、过滤异常数据
        StreamExecutionEnvironment env = getEnv(ElectricFenceTask.class.getSimpleName());
        //读取kafka数据源
        FlinkKafkaConsumer<String> consumer = getKafkaConsumer(SimpleStringSchema.class);
        //添加数据源
        DataStreamSource<String> kafkaSource = env.addSource(consumer);
        kafkaSource.printToErr("Kafka源数据>>>");
        //转换成对象，过滤出来正确数据
        SingleOutputStreamOperator<CarDataPartObj> vehicleDataStream = kafkaSource
                .map(JsonParsePartUtil::parseJsonToObject)
                .filter(carDataPartObj -> StringUtils.isEmpty(carDataPartObj.getErrorData()));
        vehicleDataStream.printToErr("车辆数据>>>");
        //5）读取电子围栏规则数据以及电子围栏规则关联的车辆数据并进行广播
        DataStreamSource<HashMap<String, ElectricFenceResultTmp>> electricFenceConfigStream = env.addSource(new MysqlElectricFenceSouce());
        //将读取出来的数据广播出去
        DataStream<HashMap<String, ElectricFenceResultTmp>> broadcastStream = electricFenceConfigStream.broadcast();
        //6）将原始数据（消费的kafka数据）与电子围栏规则数据进行关联操作（Connect）并flatMap为 ElectricFenceRulesFuntion
        SingleOutputStreamOperator<ElectricFenceModel> connectStream = vehicleDataStream
                .connect(broadcastStream)
                .flatMap(new ElectricFenceRulesFuntion());
        connectStream.printToErr("车辆数据关联电子围栏规则后>>>");
        //7）对上步数据分配水印（30s）并根据 vin 分组后应用90s滚动窗口，然后对窗口进行自定义函数的开发（计算出来该窗口的数据属于电子围栏外还是电子围栏内）
        SingleOutputStreamOperator<ElectricFenceModel> windowStream = connectStream
                .assignTimestampsAndWatermarks(new ElectricFenceWatermark())
                .keyBy(ElectricFenceModel::getVin)
                .window(TumblingEventTimeWindows.of(Time.seconds(90)))
                .process(new ElectricFenceWindowFunction());
        windowStream.printToErr("电子围栏结果>>>");
        //8）读取电子围栏分析结果表的数据并进行广播
        DataStream<HashMap<String,Long>> resultStream = env.addSource(new MysqlElectricFenceResultSource()).broadcast();
        resultStream.printToErr("分析结果表>>>"+resultStream);
        //9）对第七步和第八步产生的数据进行关联操作（connect）
        //10）对第九步的结果进行滚动窗口操作，应用自定义窗口函数（实现添加uuid和inMysql属性赋值）
        SingleOutputStreamOperator<ElectricFenceModel> result = windowStream.connect(resultStream).flatMap(new ElectricFenceModelFunction());
        //11）将分析后的电子围栏结果数据实时写入到mysql数据库中
        result.addSink(new ElectricFenceMysqlSink());
        //12）运行作业，等待停止
        env.execute();
    }
}
