package cn.turing.firecontrol.datahandler.biz;

import cn.turing.firecontrol.datahandler.feign.IUserFeign;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class CommonBiz {

    @Autowired
    IUserFeign iUserFeign;
    private static Map<Integer,String> channelInfo = new HashMap<>();

    //由于服务有鉴权，必需要有token,故不能使用@PostContruct注解对channelInfo进行初始化
    private void initChannelInfo(){
        if(channelInfo.isEmpty()){
            JSONArray channelArray = iUserFeign.getAllChannel();
            for(int i =0;i<channelArray.size();i++){
                Integer channelId = Integer.valueOf(channelArray.getJSONObject(i).getString("id"));
                String  channelName = channelArray.getJSONObject(i).getString("channelName");
                channelInfo.put(channelId,channelName);
            }
        }
    }

    public String  getChannelName(Integer channelId){
        initChannelInfo();
        return channelInfo.get(channelId);
    }

    public Map<Integer,String> getChannelInfo(){
        initChannelInfo();
        return channelInfo;
    }


}
