package com.mashibing.servicemap.remote;

import com.mashibing.internalcommon.constant.AmapConfigConstant;
import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.internalcommon.response.TermnialResponse;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class TerminalClient {

    @Value("${amap.key}")
    private String amapKey;

    @Value("${amap.sid}")
    private String sid;

    @Autowired
    private RestTemplate restTemplate;

    public ResponseResult add(String name,String desc){
        //  url组装
        StringBuilder url = new StringBuilder();
        url.append(AmapConfigConstant.TERMINAL_ADD_URL);
        url.append("?");
        url.append("key="  + amapKey);
        url.append("&");
        url.append("sid=" + sid);
        url.append("&");
        url.append("name=" + name);
        url.append("&");
        url.append("desc=" + desc);
        log.info("终端请求：" + url.toString());
        ResponseEntity<String> entity = restTemplate.postForEntity(url.toString(), null, String.class);
        log.info("终端响应：" + entity.getBody());
        String body = entity.getBody();
        JSONObject result = JSONObject.fromObject(body);
        JSONObject data = result.getJSONObject("data");
        String tid = data.getString("tid");
        TermnialResponse termnialResponse = new TermnialResponse();
        termnialResponse.setTid(tid);
        return ResponseResult.success(termnialResponse);
    }
}
