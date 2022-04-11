package com.lily.flashsale.service;

import com.lily.flashsale.db.dao.FlashsaleActivityDao;
import com.lily.flashsale.db.dao.FlashsaleCommodityDao;
import com.lily.flashsale.db.po.FlashsaleActivity;
import com.lily.flashsale.db.po.FlashsaleCommodity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ActivityHtmlPageService {

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private FlashsaleActivityDao flashsaleActivityDao;

    @Autowired
    private FlashsaleCommodityDao flashsaleCommodityDao;

    /**
     * 创建html页面
     *
     * @throws Exception
     */
    public void createActivityHtml(long flashsaleActivityId) {

        PrintWriter writer = null;
        try {
            FlashsaleActivity flashsaleActivity = flashsaleActivityDao.queryFlashsaleActivityById(flashsaleActivityId);
            FlashsaleCommodity flashsaleCommodity = flashsaleCommodityDao.queryFlashsaleCommodityById(flashsaleActivity.getCommodityId());
            // 获取页面数据
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("flashsaleActivity", flashsaleActivity);
            resultMap.put("flashsaleCommodity", flashsaleCommodity);
            resultMap.put("flashsalePrice", flashsaleActivity.getFlashsalePrice());
            resultMap.put("oldPrice", flashsaleActivity.getOldPrice());
            resultMap.put("commodityId", flashsaleActivity.getCommodityId());
            resultMap.put("commodityName", flashsaleCommodity.getCommodityName());
            resultMap.put("commodityDesc", flashsaleCommodity.getCommodityDesc());

            // 创建thymeleaf上下文对象
            Context context = new Context();
            // 把数据放入上下文对象
            context.setVariables(resultMap);

            // 创建输出流
            File file = new File("src/main/resources/templates/" + "flashsale_item_" + flashsaleActivityId + ".html");
            writer = new PrintWriter(file);
            // 执行页面静态化方法
            templateEngine.process("flashsale_item", context, writer);
        } catch (Exception e) {
            log.error(e.toString());
            log.error("页面静态化出错：" + flashsaleActivityId);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}

