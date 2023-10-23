package cn.tencent.s3.flink.streaming.flatmap;

import cn.tencent.s3.flink.streaming.entity.ElectricFenceModel;
import org.apache.flink.streaming.api.functions.co.RichCoFlatMapFunction;
import org.apache.flink.util.Collector;

import java.util.HashMap;

/**
 * 文件名：ElectricFenceModelFunction
 * 项目名：CarNetworkingSystem
 * 描述：
 * 作者：linker
 * 创建时间：2023/10/22
 * 开发步骤：
 */
public class ElectricFenceModelFunction extends RichCoFlatMapFunction<ElectricFenceModel, HashMap<String,Long>,
        ElectricFenceModel> {
    //接收数据库获取的对象 <vin,id>
    HashMap<String ,Long> vinAndId = null;

    @Override
    public void flatMap1(ElectricFenceModel electricFenceModel, Collector<ElectricFenceModel> collector) throws Exception {
        //判断当前 vin 的配置是否存储
        String vin = electricFenceModel.getVin();
        //当前 vin 对应的 id
        Long id = vinAndId.getOrDefault(vin, null);
        if (id != null) {
            //将获取 id 赋值给 uuid
            electricFenceModel.setUuid(id);
            //当前是否在数据库中存储 true
            electricFenceModel.setInMysql(true);
        }else {
            //如果数据库中不存在当前 vin 的id,其实说明是一辆临时车，所以需要给他一个非常靠后且短期不太可能产生冲突的临时UUID。
            electricFenceModel.setUuid(Long.MAX_VALUE - System.currentTimeMillis());
            //将当前的不存在数据库 false
            electricFenceModel.setInMysql(false);
        }
        collector.collect(electricFenceModel);

    }

    @Override
    public void flatMap2(HashMap<String, Long> stringLongHashMap, Collector<ElectricFenceModel> collector) throws Exception {
        vinAndId = stringLongHashMap;
    }
}
