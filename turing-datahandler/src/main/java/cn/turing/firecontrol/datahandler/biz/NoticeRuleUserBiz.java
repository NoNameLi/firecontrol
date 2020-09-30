package cn.turing.firecontrol.datahandler.biz;

import cn.turing.firecontrol.common.biz.BusinessBiz;
import cn.turing.firecontrol.datahandler.entity.NoticeRule;
import cn.turing.firecontrol.datahandler.entity.NoticeRuleUser;
import cn.turing.firecontrol.datahandler.feign.IUserFeign;
import cn.turing.firecontrol.datahandler.mapper.NoticeRuleUserMapper;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created on 2019/03/18 10:25
 *
 * @Description TODO
 * @Version V1.0
 */
@Service
public class NoticeRuleUserBiz extends BusinessBiz<NoticeRuleUserMapper, NoticeRuleUser> {

    @Autowired
    private NoticeRuleBiz  noticeRuleBiz;

    @Autowired
    private IUserFeign userFeign;

    public static final String DEL_FLAG_YES = "1";

    public static final String DEL_FLAG_NO = "0";

    /**
     * 添加推送规则与用户的关联关系
     * @param noticeRlueId
     * @param userId
     */
    @Transactional
    public void addNoticeRuleUser(Long noticeRlueId,String userId,String noticeType){
        /**
         * 校验参数
         */
//        NoticeRule rule = noticeRuleBiz.selectById(noticeRlueId);
//        if(rule == null || DEL_FLAG_YES.equals(rule.getDelFlag())){
//            throw new IllegalArgumentException("对应的规则不存在");
//        }

//        JSONObject  userInfo = userFeign.getUserInfoByUserId(userId);
//        if(userInfo == null || !"200".equals(userInfo.getString("status")) || userInfo.getJSONObject("data") == null){
//            throw new IllegalArgumentException("对应的用户不存在");
//        }

        NoticeRuleUser ru = new NoticeRuleUser();
        ru.setNoticeRuleId(noticeRlueId);
        ru.setUserId(userId);
        ru.setNoticeType(noticeType);

        List<NoticeRuleUser> list = this.mapper.select(ru);

        for(NoticeRuleUser u : list){
            if(DEL_FLAG_NO.equals(u.getDelFlag())){
                return;
            }
        }

        this.insertSelective(ru);
    }

    public void configNoticeRuleForUser(Long noticeRlueId,String userId,String status,String noticeType){
        NoticeRuleUser dto = new NoticeRuleUser();
        dto.setNoticeRuleId(noticeRlueId);
        dto.setUserId(userId);
        NoticeRuleUser entity = this.mapper.queryByNoticeRuleIdAndUserId(noticeRlueId,userId,noticeType);
        if(entity == null){
            if("0".equals(status)){
                this.addNoticeRuleUser(noticeRlueId,userId,noticeType);
            }
        }else{
            dto.setId(entity.getId());
            dto.setDelFlag(status);
            this.mapper.updateByPrimaryKeySelective(dto);
        }
    }


    /**
     * 删除推送规则与用户的关联关系
     * @param id
     */
    @Transactional
    public void delNoticeRuleUser(Long id){
        NoticeRuleUser dbRu = this.selectById(id);
        if(dbRu == null || DEL_FLAG_YES.equals(dbRu.getDelFlag())){
            throw new IllegalArgumentException("关联关系不存在或已删除");
        }
        dbRu.setDelFlag(DEL_FLAG_YES);
        this.updateSelectiveById(dbRu);
    }

    public List<NoticeRuleUser> listByUserIds(String userIds,String noticeType){
        StringBuilder sb = new StringBuilder();
        for(String str : userIds.split(",")){
            sb.append("'")
                    .append(str)
                    .append("'")
                    .append(",");
        }
        return this.mapper.listByUserIds(sb.substring(0,sb.length() - 1),noticeType);
    }

    public List<NoticeRuleUser> queryByNoticeRuleId(Long noticeRuleId,String noticeType){
        return this.mapper.queryByNoticeRuleId(noticeRuleId,noticeType);
    }
}
