package com.mashibing.servicemap.remote;


import com.mashibing.internalcommon.constant.AmapConfigConstant;
import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.internalcommon.response.DirectionResponse;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class MapDirectionClient {

    @Value("${amap.key}")
    private String amapKey;

    @Autowired
    private RestTemplate restTemplate;

    public DirectionResponse direction(String depLongitude, String depLatitude, String destLongitude, String destLatitude){
        //  组装参数调用url

        StringBuilder urlBuild = new StringBuilder();
        urlBuild.append(AmapConfigConstant.DIRECTION_URL);
        urlBuild.append("?");
        urlBuild.append("origin=" + depLongitude + "," + depLatitude);
        urlBuild.append("&");
        urlBuild.append("destination=" + destLongitude + "," + destLatitude);
        urlBuild.append("&");
        urlBuild.append("extensions=base");
        urlBuild.append("&");
        urlBuild.append("output=json");
        urlBuild.append("&");
        urlBuild.append("key=" + amapKey);
        log.info(urlBuild.toString());
        //  调用高德接口
        ResponseEntity<String> directionEntity = restTemplate.getForEntity(urlBuild.toString(), String.class);
        String directionString = directionEntity.getBody();
        log.info("高德地图路径规划，返回的信息：" + directionEntity.getBody());
        //  解析接口
        DirectionResponse directionResponse = parseDirectionEntity(directionString);


        return directionResponse;
    }

    private DirectionResponse parseDirectionEntity(String directionString){
        DirectionResponse directionResponse = null;
        try{
            directionResponse = new DirectionResponse();
            //  最外层
            JSONObject result = JSONObject.fromObject(directionString);
            if(result.has(AmapConfigConstant.STATUS)){
                int status = result.getInt(AmapConfigConstant.STATUS);
                if (status == 1){
                    if (result.has(AmapConfigConstant.ROUTE)){
                        JSONObject routeObject = result.getJSONObject(AmapConfigConstant.ROUTE);
                        JSONArray pathsArray = routeObject.getJSONArray(AmapConfigConstant.PATHS);
                        JSONObject pathObject = pathsArray.getJSONObject(0);

                        if (pathObject.has(AmapConfigConstant.DISTANCE)){
                            int distance = pathObject.getInt(AmapConfigConstant.DISTANCE);
                            directionResponse.setDistance(distance);
                        }
                        if(pathObject.has(AmapConfigConstant.DURATION)){
                            int duration = pathObject.getInt(AmapConfigConstant.DURATION);
                            directionResponse.setDuration(duration);
                        }
                    }
                }
            }
        }catch (Exception e){

        }

        return directionResponse;
    }
}
