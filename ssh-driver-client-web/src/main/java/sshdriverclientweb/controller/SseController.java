package sshdriverclientweb.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class SseController {

    public static Map<String,SseEmitter> sseEmitterMap = new HashMap<>();

    /**
     * 建立连接
     * @param driverId
     * @return
     */
    @GetMapping("/connect/{driverId}")
    public SseEmitter connect(@PathVariable String driverId ){
        System.out.println("司机ID:" + driverId);
        SseEmitter sseEmitter = new SseEmitter(0L);

        sseEmitterMap.put(driverId,sseEmitter);
        return sseEmitter;
    }

    /**
     * 发送消息
     * @param driverId  消息接收者
     * @param content   消息内容
     * @return
     */
    @GetMapping("/push")
    public String push(@RequestParam String driverId,@RequestParam String content){

        try {
            sseEmitterMap.get(driverId).send(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "给用户:" + driverId + "发送了消息:" + content;
    }
}
