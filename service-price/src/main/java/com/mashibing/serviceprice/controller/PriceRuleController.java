package com.mashibing.serviceprice.controller;


import com.mashibing.internalcommon.dto.PriceRule;
import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.serviceprice.service.PriceRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

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
}
