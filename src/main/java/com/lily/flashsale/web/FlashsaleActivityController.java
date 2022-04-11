package com.lily.flashsale.web;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.lily.flashsale.db.dao.OrderDao;
import com.lily.flashsale.db.dao.FlashsaleActivityDao;
import com.lily.flashsale.db.dao.FlashsaleCommodityDao;
import com.lily.flashsale.db.po.Order;
import com.lily.flashsale.db.po.FlashsaleActivity;
import com.lily.flashsale.db.po.FlashsaleCommodity;
import com.lily.flashsale.service.FlashsaleActivityService;
import com.lily.flashsale.util.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class FlashsaleActivityController {

    @Autowired
    private FlashsaleActivityDao flashsaleActivityDao;

    @Autowired
    private FlashsaleCommodityDao flashsaleCommodityDao;

    @Autowired
    FlashsaleActivityService flashsaleActivityService;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    RedisService redisService;


    @RequestMapping("/addFlashsaleActivity")
    public String addFlashsaleActivity() {
        return "add_activity";
    }

    @RequestMapping("/addFlashsaleActivityAction")
    public String addFlashsaleActivityAction(
            @RequestParam("name") String name,
            @RequestParam("commodityId") long commodityId,
            @RequestParam("flashsalePrice") BigDecimal flashsalePrice,
            @RequestParam("oldPrice") BigDecimal oldPrice,
            @RequestParam("flashsaleNumber") long flashsaleNumber,
            @RequestParam("startTime") String startTime,
            @RequestParam("endTime") String endTime,
            Map<String, Object> resultMap
    ) throws ParseException {
        startTime = startTime.substring(0, 10) +  startTime.substring(11);
        endTime = endTime.substring(0, 10) +  endTime.substring(11);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-ddhh:mm");
        FlashsaleActivity flashsaleActivity = new FlashsaleActivity();
        flashsaleActivity.setName(name);
        flashsaleActivity.setCommodityId(commodityId);
        flashsaleActivity.setFlashsalePrice(flashsalePrice);
        flashsaleActivity.setOldPrice(oldPrice);
        flashsaleActivity.setTotalStock(flashsaleNumber);
        flashsaleActivity.setAvailableStock(new Integer("" + flashsaleNumber));
        flashsaleActivity.setLockStock(0L);
        flashsaleActivity.setActivityStatus(1);
        flashsaleActivity.setStartTime(format.parse(startTime));
        flashsaleActivity.setEndTime(format.parse(endTime));
        flashsaleActivityDao.inertFlashsaleActivity(flashsaleActivity);
        resultMap.put("flashsaleActivity", flashsaleActivity);
        return "add_success";
    }

    @RequestMapping("/flashsales")
    public String activityList(Map<String, Object> resultMap) {
        try (Entry entry = SphU.entry("flashsales")) {
            List<FlashsaleActivity> flashsaleActivities = flashsaleActivityDao.queryFlashsaleActivitysByStatus(1);
            resultMap.put("flashsaleActivities", flashsaleActivities);
            return "flashsale_activity";
        } catch (BlockException ex) {
            log.error("查询秒杀活动的列表被限流 "+ex.toString());
            return "wait";
        }
    }


    @RequestMapping("/item/{flashsaleActivityId}")
    public String itemPage(Map<String, Object> resultMap, @PathVariable long flashsaleActivityId) {
        FlashsaleActivity flashsaleActivity;
        FlashsaleCommodity flashsaleCommodity;

        String flashsaleActivityInfo = redisService.getValue("flashsaleActivity:" + flashsaleActivityId);
        if (StringUtils.isNotEmpty(flashsaleActivityInfo)) {
            log.info("redis缓存数据:" + flashsaleActivityInfo);
            flashsaleActivity = JSON.parseObject(flashsaleActivityInfo, FlashsaleActivity.class);
        } else {
            flashsaleActivity = flashsaleActivityDao.queryFlashsaleActivityById(flashsaleActivityId);
        }

        String flashsaleCommodityInfo = redisService.getValue("flashsaleCommodity:" + flashsaleActivity.getCommodityId());
        if (StringUtils.isNotEmpty(flashsaleCommodityInfo)) {
            log.info("redis缓存数据:" + flashsaleCommodityInfo);
            flashsaleCommodity = JSON.parseObject(flashsaleActivityInfo, FlashsaleCommodity.class);
        } else {
            flashsaleCommodity = flashsaleCommodityDao.queryFlashsaleCommodityById(flashsaleActivity.getCommodityId());
        }

        resultMap.put("flashsaleActivity", flashsaleActivity);
        resultMap.put("flashsaleCommodity", flashsaleCommodity);
        resultMap.put("flashsalePrice", flashsaleActivity.getFlashsalePrice());
        resultMap.put("oldPrice", flashsaleActivity.getOldPrice());
        resultMap.put("commodityId", flashsaleActivity.getCommodityId());
        resultMap.put("commodityName", flashsaleCommodity.getCommodityName());
        resultMap.put("commodityDesc", flashsaleCommodity.getCommodityDesc());
        return "flashsale_item";
    }

    /**
     * 处理抢购请求
     * @param userId
     * @param flashsaleActivityId
     * @return
     */
    @RequestMapping("/flashsale/buy/{userId}/{flashsaleActivityId}")
    public ModelAndView flashsaleCommodity(@PathVariable long userId, @PathVariable long flashsaleActivityId) {
        boolean stockValidateResult = false;

        ModelAndView modelAndView = new ModelAndView();
        try {
            /*
             * 判断用户是否在已购名单中
             */
            if (redisService.isInLimitMember(flashsaleActivityId, userId)) {
                //提示用户已经在限购名单中，返回结果
                modelAndView.addObject("resultInfo", "对不起，您已经在限购名单中");
                modelAndView.setViewName("flashsale_result");
                return modelAndView;
            }
            /*
             * 确认是否能够进行秒杀
             */
            stockValidateResult = flashsaleActivityService.flashsaleStockValidator(flashsaleActivityId);
            if (stockValidateResult) {
                Order order = flashsaleActivityService.createOrder(flashsaleActivityId, userId);
                modelAndView.addObject("resultInfo","秒杀成功，订单创建中，订单ID：" + order.getOrderNo());
                modelAndView.addObject("orderNo",order.getOrderNo());
            } else {
                modelAndView.addObject("resultInfo","对不起，商品库存不足");
            }
        } catch (Exception e) {
            log.error("秒杀系统异常" + e.toString());
            modelAndView.addObject("resultInfo","秒杀失败");
        }
        modelAndView.setViewName("flashsale_result");
        return modelAndView;
    }

    /**
     * Order tracking
     * @param orderNo
     * @return
     */
    @RequestMapping("/flashsale/orderQuery/{orderNo}")
    public ModelAndView orderQuery(@PathVariable String orderNo) {
        log.info("订单查询，订单号：" + orderNo);
        Order order = orderDao.queryOrder(orderNo);
        ModelAndView modelAndView = new ModelAndView();

        if (order != null) {
            modelAndView.setViewName("order");
            modelAndView.addObject("order", order);
            FlashsaleActivity flashsaleActivity = flashsaleActivityDao.queryFlashsaleActivityById(order.getFlashsaleActivityId());
            modelAndView.addObject("flashsaleActivity", flashsaleActivity);
        } else {
            modelAndView.setViewName("order_wait");
        }
        return modelAndView;
    }

    /**
     * Pay order
     * @return
     */
    @RequestMapping("/flashsale/payOrder/{orderNo}")
    public String payOrder(@PathVariable String orderNo) throws Exception {
        flashsaleActivityService.payOrderProcess(orderNo);
        return "redirect:/flashsale/orderQuery/" + orderNo;
    }

    /**
     * Get current server time
     * @return
     */
    @ResponseBody
    @RequestMapping("/flashsale/getSystemTime")
    public String getSystemTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String date = df.format(new Date());// new Date()为获取当前系统时间
        return date;
    }
}
