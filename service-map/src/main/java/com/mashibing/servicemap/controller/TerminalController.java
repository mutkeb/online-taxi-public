package com.mashibing.servicemap.controller;


import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.servicemap.service.TerminalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/terminal")
public class TerminalController {

    @Autowired
    private TerminalService terminalService;

    @PostMapping("add")
    public ResponseResult add(@RequestParam String name,@RequestParam String desc){

        return terminalService.add(name,desc);
    }

    @PostMapping("/aroundsearch")
    public ResponseResult aroundSearch(@RequestParam String center,@RequestParam Long radius){
        return terminalService.aroundSearch(center,radius);
    }
}
