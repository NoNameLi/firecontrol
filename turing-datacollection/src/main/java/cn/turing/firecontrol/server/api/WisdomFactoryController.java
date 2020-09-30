package cn.turing.firecontrol.server.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/datacollection")
public class WisdomFactoryController {

    @RequestMapping("/deviceCommand")
    @ResponseBody
    public String issueDeviceCommand(@RequestBody String body){


        return "";
    }
}
