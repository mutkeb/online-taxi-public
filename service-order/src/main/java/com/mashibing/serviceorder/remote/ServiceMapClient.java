package com.mashibing.serviceorder.remote;

import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.internalcommon.response.TerminalResponse;
import jdk.nashorn.internal.ir.Terminal;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("service-map")
public interface ServiceMapClient {

    @PostMapping("/terminal/aroundsearch")
    public ResponseResult<List<TerminalResponse>> aroundSearch(@RequestParam String center, @RequestParam Long radius);

}
