package com.yupi.springbootinit.bimq;

import com.rabbitmq.client.Channel;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.manager.AiManager;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.service.ChartService;
import com.yupi.springbootinit.utils.ExcelUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.yupi.springbootinit.bimq.BiConstant.BI_MODEL_ID;
import static com.yupi.springbootinit.bimq.BiConstant.BI_QUEUE_NAME;

@Component
@Slf4j
public class BiMessageConsumer {
    @Resource
    private ChartService chartService;
    @Resource
    private AiManager aiManager;

   @SneakyThrows
   @RabbitListener(queues={BI_QUEUE_NAME},ackMode ="MANUAL")
    public void reciveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag){
        //使用日志记录器打印接受到的消息内容
        log.info("receiveMessage message={}",message);
        if(StringUtils.isBlank(message)){
            //消息内容为空，拒绝，重新放回队列中
            channel.basicNack(deliveryTag,false,false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"消息为空");
        }
        long charId=Long.parseLong(message);
       Chart updateChart =chartService.getById(charId);
       if(updateChart==null){
           //如果图表为空，拒绝消息并抛出业务异常
           channel.basicNack(deliveryTag,false,false);
           throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"图表为空");
       }
       // 先修改图表任务状态为 “执行中”。等执行成功后，修改为 “已完成”、保存执行结果；执行失败后，状态修改为 “失败”，记录任务失败信息。;
       updateChart.setStatus("running");
       boolean b = chartService.updateById(updateChart);
       if (!b) {
           ThrowUtils.handleChartUpdateError(updateChart.getId(), "更新图表执行中状态失败");
       }
       // 调用 AI
       // 构造用户输入
       StringBuilder userInput = new StringBuilder();
       userInput.append("分析需求：").append("\n");
       // 拼接分析目标
       String userGoal = updateChart.getGoal();
       if (StringUtils.isNotBlank(updateChart.getChartType())) {
           userGoal += "，请使用" + updateChart.getChartType();
       }
       userInput.append(userGoal).append("\n");
       userInput.append("原始数据：").append("\n");
       // 压缩后的数据
       String csvData =updateChart.getChartData();
       userInput.append(csvData).append("\n");
       String result = aiManager.doChat(BI_MODEL_ID, userInput.toString());
       String[] splits = result.split("【【【【【");
       if (splits.length < 3) {
           ThrowUtils.handleChartUpdateError(updateChart.getId(), "AI 生成错误");
       }
       String genChart = splits[1].trim();
       String genResult = splits[2].trim();
       Chart updateChartResult = new Chart();
       updateChartResult.setId(updateChart.getId());
       updateChartResult.setGenChart(genChart);
       updateChartResult.setGenResult(genResult);
       // todo 建议定义状态为枚举值
       updateChartResult.setStatus("succeed");
       boolean updateResult = chartService.updateById(updateChartResult);
       if (!updateResult) {
           ThrowUtils.handleChartUpdateError(updateChart.getId(),  "更新图表成功状态失败");
       }
        channel.basicAck(deliveryTag,false);


    }

}
