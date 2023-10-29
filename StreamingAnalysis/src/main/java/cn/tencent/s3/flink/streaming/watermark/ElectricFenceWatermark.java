package cn.tencent.s3.flink.streaming.watermark;

import cn.tencent.s3.flink.streaming.entity.ElectricFenceModel;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;

import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * 文件名：ElectricFenceWatermark
 * 项目名：CarNetworkingSystem
 * 描述：
 * 作者：linker
 * 创建时间：2023/10/29
 * 开发步骤：
 */
public class ElectricFenceWatermark implements
        AssignerWithPeriodicWatermarks<ElectricFenceModel>, Serializable {
    Long currentMaxTimestamp = 0L;
    @Nullable
    @Override
    public Watermark getCurrentWatermark() {
        return new Watermark(currentMaxTimestamp);
    }

    @Override
    public long extractTimestamp(ElectricFenceModel electricFenceModel, long l) {
        currentMaxTimestamp = Math.max(electricFenceModel.getTerminalTimestamp(),currentMaxTimestamp);
        System.out.println("分配水印"+electricFenceModel.getTerminalTimestamp());
        return electricFenceModel.getTerminalTimestamp();
    }
}
