package com.mashibing.serviceprice.controller;


import com.mashibing.internalcommon.dto.PriceRule;
import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.serviceprice.service.PriceRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author mutkeb
 * @since 2022-12-22
 */
@RestController
@RequestMapping("price-rule")
public class PriceRuleController {

    @Autowired
    private PriceRuleService priceRuleService;

    @PostMapping("/add")
    public ResponseResult add(@RequestBody PriceRule priceRule){
        return priceRuleService.add(priceRule);
    }

    @PostMapping("/edit")
    public ResponseResult edit(@RequestBody PriceRule priceRule){
        return priceRuleService.edit(priceRule);
    }

    /**
     * 查询最新计价规则
     * @param fareType
     * @return
     */

    @GetMapping("/get-newest-version")
    public ResponseResult getNewestVersion(@RequestParam String fareType){
        return priceRuleService.getNewestVersion(fareType);
    }

    /**
     * 判断是否是最新版本
     */
    @GetMapping("/is-new")
    public ResponseResult isNew(@RequestParam String fareType,@RequestParam Integer fareVersion){
        return priceRuleService.isNew(fareType,fareVersion);
    }
}
