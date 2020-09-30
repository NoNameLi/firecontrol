package cn.turing.firecontrol.datahandler.biz;

import cn.turing.firecontrol.common.biz.BusinessBiz;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.datahandler.base.Constant;
import cn.turing.firecontrol.datahandler.business.BusinessImpl;
import cn.turing.firecontrol.datahandler.entity.NoticeRule;
import cn.turing.firecontrol.datahandler.entity.NoticeRuleUser;
import cn.turing.firecontrol.datahandler.mapper.NoticeRuleMapper;
import cn.turing.firecontrol.datahandler.mapper.NoticeRuleUserMapper;
import cn.turing.firecontrol.datahandler.vo.NoticeRuleVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 2019/03/18 10:23
 *
 * @Description TODO
 * @Version V1.0
 */
@Service
public class NoticeRuleBiz extends BusinessBiz<NoticeRuleMapper, NoticeRule> {

    @Autowired
    private NoticeRuleUserMapper noticeRuleUserMapper;

    @Autowired
    private NoticeRuleUserBiz noticeRuleUserBiz;

    @Autowired
    private NoticeRuleSensorBiz noticeRuleSensorBiz;

    @Autowired
    private BusinessImpl business;

    @Autowired
    private CommonBiz commonBiz;

    public TableResultResponse<NoticeRuleVo> listNoticeRuleForUser(Integer page, Integer limit, String userId,Integer channelId,String ruleName,String noticeType){
        Page<Object> result = PageHelper.startPage(page, limit);
        List<NoticeRuleVo> list = this.mapper.listNoticeRuleForUser(userId,channelId,ruleName,noticeType);
        for(NoticeRuleVo rule : list){
            rule.setChannelName(commonBiz.getChannelName(rule.getChannelId()));
        }
        return new TableResultResponse<NoticeRuleVo>(result.getTotal(),list);
    }

    public TableResultResponse<NoticeRule> listNoticeRule(Integer page, Integer limit,Integer channelId){
        Page<Object> result = PageHelper.startPage(page, limit);
        List<NoticeRule> list = this.mapper.listNoticeRule(channelId);
        for(NoticeRule rule : list){
            rule.setChannelName(commonBiz.getChannelName(rule.getChannelId()));
        }
        return new TableResultResponse<NoticeRule>(result.getTotal(),list);
    }


    public int countBindUser(Long id){
        NoticeRuleUser ru = new NoticeRuleUser();
        ru.setNoticeRuleId(id);
        ru.setDelFlag("0");
        int count = noticeRuleUserBiz.selectList(ru).size();
        return count;
    }



    @Transactional
    public int deleteNoticeRuleById(Long id){
        NoticeRule dbRule = this.mapper.selectByPrimaryKey(id);
        if(dbRule == null || "1".equals(dbRule.getDelFlag())){
            throw new  RuntimeException("记录不存在或已删除");
        }
        NoticeRuleUser ru = new NoticeRuleUser();
        ru.setNoticeRuleId(id);
        ru.setDelFlag("0");
        int count = noticeRuleUserBiz.selectList(ru).size();
        if( count > 0){
            return count;
        }
        NoticeRule rule = new NoticeRule();
        rule.setId(id);
        rule.setDelFlag("1");
        this.updateSelectiveById(rule);
        noticeRuleSensorBiz.deleteByNoticeRuleId(id);
        return 0;
    }

    public NoticeRule queryById(Long id){
        return this.mapper.queryById(id);
    }
}
