package cn.turing.firecontrol.datahandler.rest;

import cn.turing.firecontrol.datahandler.listener.abnormalHandler.VideoAbnormalHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created on 2019/01/03 10:49
 *
 * @Description TODO
 * @Version V1.0
 */
@Controller
@RequestMapping("test")
public class ViewController {

    @Resource
    private Environment environment;
    @Autowired
    private VideoAbnormalHandler abnormalHandler;

    @RequestMapping("index")
    public String index(){
        return "index";
    }

    @ResponseBody
    @GetMapping("testValue")
    public String testValue(String name){
        return environment.getProperty(name);
    }


}
