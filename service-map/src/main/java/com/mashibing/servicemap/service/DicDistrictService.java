package com.mashibing.servicemap.service;

import com.mashibing.internalcommon.constant.AmapConfigConstant;
import com.mashibing.internalcommon.constant.CommonStatusEnum;
import com.mashibing.internalcommon.dto.DicDistrict;
import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.servicemap.mapper.DicDistrictMapper;
import com.mashibing.servicemap.remote.DicDistrictClient;
import com.mysql.cj.xdevapi.JsonArray;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DicDistrictService {


    @Autowired
    private DicDistrictClient dicDistrictClient;

    @Autowired
    private DicDistrictMapper dicDistrictMapper;
    /**
     * 插入行政区数据
     * @param keywords
     * @return
     */
    public ResponseResult initDicDistrict(String keywords){
        //  获得数据
        String dicDistrict = dicDistrictClient.initDicDistrict(keywords);
        //  解析结果
        JSONObject districtJSONObject = JSONObject.fromObject(dicDistrict);
        int status = districtJSONObject.getInt(AmapConfigConstant.STATUS);
        if (status == 0){
            return ResponseResult.fail(CommonStatusEnum.MAP_DISTRICT_ERROR.getCode(),CommonStatusEnum.MAP_DISTRICT_ERROR.getValue());
        }
        JSONArray countryArray = districtJSONObject.getJSONArray(AmapConfigConstant.DISTRICT);
        //  解析国家数据
        for (int c = 0; c < countryArray.size(); c++){
            JSONObject countryJsonObject = countryArray.getJSONObject(c);
            String countryCode = countryJsonObject.getString(AmapConfigConstant.ADCODE);
            String countryName = countryJsonObject.getString(AmapConfigConstant.NAME);
            String countryParentAddressCode = "0";
            String countryLevel = countryJsonObject.getString(AmapConfigConstant.LEVEL);
            insertData(countryCode,countryName,countryParentAddressCode,getLevel(countryLevel));

            //  解析省份数据
            JSONArray provinceJsonArray = countryJsonObject.getJSONArray(AmapConfigConstant.DISTRICT);
            for (int p = 0; p < provinceJsonArray.size(); p++){
                JSONObject provinceJsonObject = provinceJsonArray.getJSONObject(p);
                String provinceCode = provinceJsonObject.getString(AmapConfigConstant.ADCODE);
                String provinceName = provinceJsonObject.getString(AmapConfigConstant.NAME);
                String provinceParentAddressCode = countryCode;
                String provinceLevel = provinceJsonObject.getString(AmapConfigConstant.LEVEL);
                insertData(provinceCode,provinceName,provinceParentAddressCode,getLevel(provinceLevel));
                //  解析市县数据
                JSONArray cityJsonArray = provinceJsonObject.getJSONArray(AmapConfigConstant.DISTRICT);
                for (int city = 0; city < cityJsonArray.size(); city++){
                    JSONObject cityJsonObject = cityJsonArray.getJSONObject(city);
                    String cityCode = cityJsonObject.getString(AmapConfigConstant.ADCODE);
                    String cityName = cityJsonObject.getString(AmapConfigConstant.NAME);
                    String cityParentAddressCode = provinceCode;
                    String cityLevel = cityJsonObject.getString(AmapConfigConstant.LEVEL);
                    if (cityLevel.equals("district")){
                        continue;
                    }
                    insertData(cityCode,cityName,cityParentAddressCode,getLevel(cityLevel));
                }
            }
        }

        return ResponseResult.success();
    }

    public int getLevel(String level){
        if (level.equals("country")){
            return 0;
        }else if (level.equals("province")){
            return 1;
        }else if (level.equals("city")){
            return 2;
        }else if (level.equals("district")){
            return 3;
        }
        return 0;
    }

    public void insertData(String addressCode,String addressName,String parentAddressCode,int level){
        DicDistrict dicDistrict = new DicDistrict();
        dicDistrict.setAddressCode(addressCode);
        dicDistrict.setAddressName(addressName);
        dicDistrict.setParentAddressCode(parentAddressCode);
        dicDistrict.setLevel(level);
        dicDistrictMapper.insert(dicDistrict);
    }
}
