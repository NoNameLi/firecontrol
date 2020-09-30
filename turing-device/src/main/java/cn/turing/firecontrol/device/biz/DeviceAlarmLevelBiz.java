package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.device.mapper.DeviceAlDnRelationMapper;
import cn.turing.firecontrol.device.mapper.DeviceNoticeMapper;
import cn.turing.firecontrol.device.util.SplitUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.turing.firecontrol.device.entity.DeviceAlarmLevel;
import cn.turing.firecontrol.device.mapper.DeviceAlarmLevelMapper;
import cn.turing.firecontrol.common.biz.BusinessBiz;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:17:57
 */
@Service
public class DeviceAlarmLevelBiz extends BusinessBiz<DeviceAlarmLevelMapper,DeviceAlarmLevel> {

    @Autowired
    private DeviceAlarmLevelMapper dalMapper;
    @Autowired
    private DeviceNoticeMapper dnMapper;
    @Autowired
    private DeviceAlDnRelationMapper darMapper;
    //分页查询等级 按照sort字段排序
    public TableResultResponse selectPageList(Query query,boolean flag){
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<DeviceAlarmLevel> lists = dalMapper.selectPageList();
        //当flag为true时不显示通知方式，当flag为false时显示通知方式
        if(flag){
            return new TableResultResponse(result.getTotal(),lists);
        }else{
            List<Map<String,Object>> maps =new ArrayList<>();
            for(int i=0;i<lists.size();i++){
                //根据报警等级id获取通知方式ids
                List<Integer> intlists = darMapper.selectByAlarmLevelId(lists.get(i).getId(), BaseContextHandler.getTenantID());
                List<String> deviceNotices =new ArrayList<>();
                if(intlists!=null&&intlists.size()>0){
                     deviceNotices = dnMapper.getByIds(SplitUtil.merge(intlists));
                }
                Map<String,Object> map = new HashMap<>();
                map.put("alarmLevel",lists.get(i));
                map.put("notice",deviceNotices);
                maps.add(map);
            }
            return new TableResultResponse(result.getTotal(),maps);
        }
    }

    //根据查询字段计数
    public int selectByCount(String level ,Integer sort,String color) {
        DeviceAlarmLevel entity=new DeviceAlarmLevel();
        entity.setLevel(level);
        entity.setSort(sort);
        entity.setColor(color);
        int count =  dalMapper.selectByCount(entity);
        return count;
    }

    //查询所有的等级，只显示id，跟等级名称
    public List<Map> getAll(){
        return dalMapper.getAll();
    }
}