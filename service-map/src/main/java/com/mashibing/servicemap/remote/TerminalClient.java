package com.mashibing.servicemap.remote;

import com.mashibing.internalcommon.constant.AmapConfigConstant;
import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.internalcommon.response.TerminalResponse;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

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
        TerminalResponse termnialResponse = new TerminalResponse();
        termnialResponse.setTid(tid);
        return ResponseResult.success(termnialResponse);
    }

    public ResponseResult aroundSearch(String center,Long radius){
        //  url拼装
        StringBuilder url = new StringBuilder();
        url.append(AmapConfigConstant.TERMINAL_ARROUNDSEARCH);
        url.append("?");
        url.append("key="  + amapKey);
        url.append("&");
        url.append("sid=" + sid);
        url.append("&");
        url.append("center=" + center);
        url.append("&");
        url.append("radius=" + radius);
        log.info("周边搜索请求:" + url.toString());
        ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity(url.toString(), null, String.class);
        log.info("周边搜索响应:" + stringResponseEntity.getBody());
        String body = stringResponseEntity.getBody();
        JSONObject result = JSONObject.fromObject(body);
        JSONObject data = result.getJSONObject("data");
        JSONArray results = data.getJSONArray("results");
        //  响应数据
        List<TerminalResponse> responseList = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            JSONObject jsonObject = results.getJSONObject(i);
            long carId = jsonObject.getLong("desc");
            String tid = jsonObject.getString("tid");
            TerminalResponse terminalResponse = new TerminalResponse();
            terminalResponse.setCarId(carId);
            terminalResponse.setTid(tid);
            responseList.add(terminalResponse);
        }
        return ResponseResult.success(responseList);
    }
}
