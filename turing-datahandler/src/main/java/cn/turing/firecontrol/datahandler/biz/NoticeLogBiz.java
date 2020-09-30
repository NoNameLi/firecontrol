package cn.turing.firecontrol.datahandler.biz;

import cn.turing.firecontrol.common.biz.BaseBiz;
import cn.turing.firecontrol.common.biz.BusinessBiz;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.datahandler.entity.NoticeLog;
import cn.turing.firecontrol.datahandler.feign.IUserFeign;
import cn.turing.firecontrol.datahandler.mapper.NoticeLogMapper;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 2019/03/18 10:22
 *
 * @Description TODO
 * @Version V1.0
 */
@Service
public class NoticeLogBiz extends BaseBiz<NoticeLogMapper, NoticeLog> {

    @Autowired
    IUserFeign iUserFeign;
    private static Map<Integer,String> channelInfo = new HashMap<>();

    public TableResultResponse<NoticeLog> listNoticeLog(Integer page,Integer limit,String mobilePhone,String noticeType){
        Page p = PageHelper.startPage(page,limit);
        List<NoticeLog> list = this.mapper.listNoticeLog(mobilePhone,noticeType);

        if(channelInfo.isEmpty()){
            JSONObject jsonObject = iUserFeign.getChannelList();
            JSONArray channelArray = jsonObject.getJSONArray("data");
            for(int i =0;i<channelArray.size();i++){
                Integer channelId = Integer.valueOf(channelArray.getJSONObject(i).getString("id"));
                String  channelName = channelArray.getJSONObject(i).getString("channelName");
                channelInfo.put(channelId,channelName);
            }
        }

        for(NoticeLog log : list){
           log.setChannelName(channelInfo.get(log.getChannelId()));
        }

        return new TableResultResponse<NoticeLog>(p.getTotal(),list);
    }

    public NoticeLog queryLastLog(Long sensorId,Integer channelId,String userId){
        return this.mapper.queryLastLog(sensorId,channelId,userId);
    }
}
