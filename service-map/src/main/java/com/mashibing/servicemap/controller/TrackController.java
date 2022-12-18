package com.mashibing.servicemap.controller;

import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.internalcommon.response.TrackResponse;
import com.mashibing.servicemap.service.TrackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("track")
@Slf4j
public class TrackController {

    @Autowired
    private TrackService trackService;

    @PostMapping("add")
    public ResponseResult add(@RequestParam String tid){
        ResponseResult responseResult = trackService.addTrack(tid);
        return responseResult;
    }
}
