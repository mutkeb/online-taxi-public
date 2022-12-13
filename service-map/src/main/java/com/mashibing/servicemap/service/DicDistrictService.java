package com.mashibing.servicemap.service;

import com.mashibing.internalcommon.constant.AmapConfigConstant;
import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.servicemap.remote.DicDistrictClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DicDistrictService {


    @Autowired
    private DicDistrictClient dicDistrictClient;
    /**
     * 插入行政区数据
     * @param keywords
     * @return
     */
    public ResponseResult initDicDistrict(String keywords){
        //  获得数据
        String dicDistrict = dicDistrictClient.initDicDistrict(keywords);
        log.info(dicDistrict.toString());
        //  解析结果

        //  插入数据
        return ResponseResult.success();
    }
}
