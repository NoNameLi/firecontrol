package cn.turing.firecontrol.admin.biz;

import cn.turing.firecontrol.admin.entity.LoginLog;
import cn.turing.firecontrol.admin.mapper.LoginLogMapper;
import cn.turing.firecontrol.common.biz.BaseBiz;
import cn.turing.firecontrol.common.biz.BusinessBiz;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 
 *
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:18:17
 */
@Service
public class LoginLogBiz extends BaseBiz<LoginLogMapper,LoginLog> {

    /**
     * 分页查询登录日志
     * @param page
     * @param limit
     * @return
     */
    public TableResultResponse<LoginLog> list(Integer page, Integer limit){
        Page<LoginLog> pageInfo = PageHelper.startPage(page,limit);
        List<LoginLog> loginLogs = mapper.queryAll();
        return new TableResultResponse<LoginLog>(pageInfo.getTotal(),loginLogs);
    }


}