package com.mashibing.servicemap.remote;

import com.mashibing.internalcommon.constant.AmapConfigConstant;
import com.mashibing.internalcommon.dto.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DicDistrictClient {

    @Value("${amap.key}")
    private String amapkey;

    @Autowired
    private RestTemplate restTemplate;

    public String initDicDistrict(String keywords){
        //  获得数据
        //  拼接url
        StringBuilder url = new StringBuilder();
        url.append(AmapConfigConstant.DISTRICT_URL);
        url.append("?");
        url.append("keywords=" + keywords);
        url.append("&");
        url.append("subdistrict=2");
        url.append("&");
        url.append("key=" + amapkey );
        ResponseEntity<String> result = restTemplate.getForEntity(url.toString(), String.class);

        return result.getBody();
    }
}
