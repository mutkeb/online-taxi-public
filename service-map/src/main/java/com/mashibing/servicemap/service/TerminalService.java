package com.mashibing.servicemap.service;

import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.internalcommon.response.TerminalResponse;
import com.mashibing.servicemap.remote.TerminalClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TerminalService {

    @Autowired
    private TerminalClient terminalClient;

    public ResponseResult add(String name,String desc){

        return terminalClient.add(name,desc);
    }

    public ResponseResult<List<TerminalResponse>> aroundSearch(String center, Long radius){
        return terminalClient.aroundSearch(center,radius);
    }

    public ResponseResult trsearch(String tid,Long starttime,Long endtime){
        return terminalClient.trsearch(tid,starttime,endtime);
    }
}
