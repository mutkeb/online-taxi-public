package com.mashibing.servicemap.remote;

import com.mashibing.internalcommon.constant.AmapConfigConstant;
import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.internalcommon.request.PointDTO;
import com.mashibing.internalcommon.request.PointRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
@Slf4j
public class PointClient {

    @Value("${amap.key}")
    private String amapKey;

    @Value("${amap.sid}")
    private String sid;

    @Autowired
    private RestTemplate restTemplate;

    public ResponseResult upload(PointRequest pointRequest){
        //  url组装
        StringBuilder url = new StringBuilder();
        url.append(AmapConfigConstant.POINT_UPLOAD_URL);
        url.append("?");
        url.append("key="  + amapKey);
        url.append("&");
        url.append("sid=" + sid);
        url.append("&");
        url.append("tid=" + pointRequest.getTid());
        url.append("&");
        url.append("trid=" + pointRequest.getTrid());
        url.append("&");
        url.append("points=");
        url.append("%5B");
        PointDTO[] points = pointRequest.getPoints();
        for (PointDTO point : points) {
            url.append("%7B");
            String locatetime = point.getLocatetime();
            String location = point.getLocation();
            url.append("%22location%22");
//            url.append("location");
            url.append("%3A");
            url.append("%22"+location+"%22");
//            url.append(location);
            url.append("%2C");

            url.append("%22locatetime%22");
//            url.append("locatetime");
            url.append("%3A");
            url.append(locatetime);

            url.append("%7D");
        }
        url.append("%5D");
        log.info("高德地图请求：" + url.toString());
        ResponseEntity<String> entity = restTemplate.postForEntity(URI.create(url.toString()), null, String.class);
        log.info("高德地图响应" + entity.getBody());

        return ResponseResult.success();
    }
}
