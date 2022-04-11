package com.lily.flashsale.web;

import com.lily.flashsale.service.FlashsaleActivityService;
import com.lily.flashsale.services.FlashsaleOverSellService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FlashsaleOverSellController {

    @Autowired
    private FlashsaleOverSellService flashsaleOverSellService;



    /**
     * easy handle purchase requests
     * @param flashsaleActivityId
     * @return
     */
//    @ResponseBody
//    @RequestMapping("/flashsale/{flashsaleActivityId}")
    public String flashsale(@PathVariable long flashsaleActivityId){
        return flashsaleOverSellService.processFlashsale(flashsaleActivityId);
    }
    @Autowired
    private FlashsaleActivityService flashsaleActivityService;

    /**
     * Use lua script to handle purchase requests
     * @param flashsaleActivityId
     * @return
     */
    @ResponseBody
    @RequestMapping("/flashsale/{flashsaleActivityId}")
    public String flashsaleCommodity(@PathVariable long flashsaleActivityId) {
        boolean stockValidateResult = flashsaleActivityService.flashsaleStockValidator(flashsaleActivityId);
        return stockValidateResult ? "恭喜你秒杀成功" : "商品已经售完，下次再来";
    }
}
