package com.mashibing.servicemap.service;

import com.mashibing.internalcommon.constant.AmapConfigConstant;
import com.mashibing.internalcommon.dto.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DicDistrictService {


    @Value("${amap.key}")
    private String amapkey;
    /**
     * 插入行政区数据
     * @param keywords
     * @return
     */
    public ResponseResult initDicDistrict(String keywords){
        //  拼接url
        //  &key=<用户的key>
        StringBuilder url = new StringBuilder();
        url.append(AmapConfigConstant.DISTRICT_URL);
        url.append("?");
        url.append("keywords=" + keywords);
        url.append("&");
        url.append("subdistrict=2");
        url.append("&");
        url.append("key=" + amapkey );
        log.info(url.toString());
        //  解析结果

        //  插入数据
        return ResponseResult.success();
    }
}
