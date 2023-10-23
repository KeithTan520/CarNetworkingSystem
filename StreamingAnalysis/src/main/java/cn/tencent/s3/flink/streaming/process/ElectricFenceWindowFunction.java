package cn.tencent.s3.flink.streaming.process;

import cn.tencent.s3.flink.streaming.entity.ElectricFenceModel;
import cn.tencent.s3.flink.streaming.flatmap.ElectricFenceRulesFuntion;
import com.google.common.collect.Lists;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Beans;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * 文件名：ElectricFenceWindowFunction
 * 项目名：CarNetworkingSystem
 * 描述：当前类主要用于将每个窗口内的数据进行封装
 * 作者：linker
 * 创建时间：2023/10/20
 * 开发步骤：
 * ElectricFenceModel -> ElectricFenceModel
 * 添加几个字段
 * 进栅栏时间、出栅栏时间、告警信息（出栅栏、进栅栏），当前报文是在栅栏内还是在栅栏外
 * 判断在窗口内，栅栏内的个数和栅栏外的个数谁多
 * 栅栏外 > 栅栏内个数 : 在栅栏外
 * 栅栏内 >= 栅栏外个数 : 在栅栏内
 * <p>
 * 用到的新特性
 * BeanProperties 对象的拷贝
 * <IN, OUT, KEY, W extends Window>
 */
public class ElectricFenceWindowFunction extends ProcessWindowFunction<ElectricFenceModel, ElectricFenceModel, String, TimeWindow> {
    //logger
    private static final Logger logger = LoggerFactory.getLogger(ElectricFenceRulesFuntion.class.getSimpleName());
    //1.定义存储历史电子围栏数据的state，<vin，是否在电子围栏内0:内，1:外> MapState<String, Integer>
    private MapState<String, Byte> lastState = null ;
    @Override
    public void open(Configuration parameters) throws Exception {
        //2.1 定义mapState的描述器（相当于表结构） <String，Byte>
        MapStateDescriptor<String, Byte> lastPeriodStateDesc = new MapStateDescriptor<>(
                "lastPeriodState",
                String.class,
                Byte.class
        );
        //2.2 获取 parameterTool，用来读取配置文件参数
        //2.3 读取状态的超时时间 "vehicle.state.last.period" ,构建ttl设置更新类型和状态可见，
        // 这里失效时间设置为180s，窗口时间是90s，上一个状态生成的时间就是上个窗口开始时间，
        // 那么想要在下一个窗口所有进来的数据都能用上这个是状态，就必须生存180s
        StateTtlConfig ttlConfig = StateTtlConfig.newBuilder(Time.seconds(180))
                .setTtlTimeCharacteristic(StateTtlConfig.TtlTimeCharacteristic.ProcessingTime)
                .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite)
                .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)
                .build();
        //2.4 设置状态描述 StateTtlConfig，开启生命周期时间
        lastPeriodStateDesc.enableTimeToLive(ttlConfig);
        //2.5 获取状态
        lastState = getRuntimeContext().getMapState(lastPeriodStateDesc);
    }

    @Override
    public void process(String vin, ProcessWindowFunction<ElectricFenceModel, ElectricFenceModel, String, TimeWindow>.Context context, Iterable<ElectricFenceModel> elements, Collector<ElectricFenceModel> out) throws Exception {
        //1.创建返回对象
        ElectricFenceModel electricFenceModel = new ElectricFenceModel();
        //2.对窗口内的数据进行排序
        ArrayList<ElectricFenceModel> electricFenceModelList = Lists.newArrayList(elements);
        Collections.sort(electricFenceModelList, Comparator.comparingLong(ElectricFenceModel::getTerminalTimestamp));
        //3.从 state 中获取车辆vin对应的上一次窗口电子围栏lastStateValue标记（车辆上一次窗口是否在电子围栏中）
        // 0：电子围栏内 1：电子围栏外
        Byte lastStateValue = lastState.get(vin);
        //4.如果上次状态为空，初始化赋值
        if (lastStateValue == null){
            lastStateValue = -1;
        }
        //5.判断当前处于电子围栏内还是电子围栏外
        //5.1.定义当前车辆电子围栏内出现的次数
        // java1.8 lambda表达式
        long inElectricFenceCount = electricFenceModelList
                .stream()
                .filter(t -> t.getNowStatus() == 0)
                .count();
        //5.2.定义当前车辆电子围栏外出现的次数
        long outElectricFenceCount = electricFenceModelList
                .stream()
                .filter(t -> t.getNowStatus() == 1)
                .count();
        //6.定义当前窗口的电子围栏状态
        byte currentStateValue = -1;
        //7. 90s内车辆出现在电子围栏内的次数多于出现在电子围栏外的次数，则认为当前状态处于电子围栏内
        if (inElectricFenceCount >= outElectricFenceCount) {
            currentStateValue = 0;
        }else {
            currentStateValue = 1;
        }
        //8. 将当前窗口的电子围栏状态写入到 state 中，供下次判断
        lastState.put(vin,currentStateValue);
        //9.如果当前电子围栏状态与上一次电子围栏状态不同
        //9.1.如果上一次窗口处于电子围栏外，而本次是电子围栏内，则将进入电子围栏的时间写入到数据库中
        if(lastStateValue == 1 && currentStateValue == 0){
            //9.1.1.过滤出来状态为0的第一条数据(这里主要是找出该时间窗口中第一次进入围栏内的时间)
            ElectricFenceModel firstElectricFenceModel = electricFenceModelList.stream()
                    .filter(t -> t.getNowStatus() == 0)
                    .findFirst()
                    .get();
            //9.1.2.拷贝属性给 electricFenceModel 并将进入终端时间赋值，并且将状态告警字段赋值为1 0:出围栏 1:进围栏，将数据collect返回
            BeanUtils.copyProperties(electricFenceModel,firstElectricFenceModel);
            //将第一条进入到电子栅栏的终端时间
            electricFenceModel.setInEleTime(firstElectricFenceModel.getTerminalTime());
            //将进栅栏输入进来 1:进入栅栏的告警
            electricFenceModel.setStatusAlarm(0);
        }else if(lastStateValue == 0 && currentStateValue == 1){
            ElectricFenceModel firstInElectricFence = electricFenceModelList
                    .stream()
                    .filter(t -> t.getNowStatus() == 1)
                    .findFirst()
                    .get();
            //9.1.2.拷贝属性给 electricFenceModel 并将进入终端时间赋值，并且将状态告警字段赋值为1 0:出围栏 1:进围栏，将数据collect返回
            BeanUtils.copyProperties(electricFenceModel,firstInElectricFence);
            //将第一条出到电子栅栏的终端时间
            electricFenceModel.setInEleTime(firstInElectricFence.getTerminalTime());
            //将进栅栏输入进来 1:出栅栏的告警
            electricFenceModel.setStatusAlarm(0);
        }else {
            logger.info("180秒内状态一致~");
        }
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

}
