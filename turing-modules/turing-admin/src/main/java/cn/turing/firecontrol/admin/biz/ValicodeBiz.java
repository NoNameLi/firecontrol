package cn.turing.firecontrol.admin.biz;

import cn.turing.firecontrol.common.constant.RedisKeyConstants;
import cn.turing.firecontrol.common.exception.base.BusinessException;
import cn.turing.firecontrol.common.exception.base.ParamErrorException;
import cn.turing.firecontrol.common.util.DateUtil;
import cn.turing.firecontrol.common.util.SmsUtil;
import cn.turing.firecontrol.common.util.StringHelper;
import com.aliyuncs.exceptions.ClientException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ValicodeBiz {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private final static Integer MAX_CODE_COUNT = 6;//次
    private final static Long MAX_CODE_TIME = 5*60*1000L;//毫秒



    /**
     * 发送短信验证码
     * @param phone
     * @param username
     * @param count
     */
    public void sendSmsCode(String phone,String username,Integer count){
        String codeKey = RedisKeyConstants.VALICODE_SMS_CODE + username;
        String countKey = RedisKeyConstants.VALICODE_SMS_COUNT + username;
        //判断当天发送次数是否已达上限
        Object codeCount = redisTemplate.opsForValue().get(countKey);
        int countCode;
        Date now = new Date();
        if(null == codeCount){
            countCode = 0;
            redisTemplate.opsForValue().set(countKey,countCode,DateUtil.getMisToEndOfDay(now),TimeUnit.MILLISECONDS);
        }else{
            countCode = (Integer)codeCount;
            if(countCode >= MAX_CODE_COUNT){
                throw new RuntimeException("当天发送验证码的次数已达上限");
            }
        }
        String code = StringHelper.generateValicode(count);
        try {
            SmsUtil.sendValicode(phone,code,username);
            redisTemplate.opsForValue().set(codeKey,code,MAX_CODE_TIME, TimeUnit.MILLISECONDS);
            redisTemplate.opsForValue().set(countKey,countCode + 1,DateUtil.getMisToEndOfDay(now),TimeUnit.MILLISECONDS);
        } catch (ClientException e) {
            e.printStackTrace();
            throw new BusinessException("验证码发送失败");
        }
    }

    public void validateCode(String username,String code){
        String code1 = (String) redisTemplate.opsForValue().get(RedisKeyConstants.VALICODE_SMS_CODE + username);
        if(StringUtils.isBlank(code1)){
            throw new RuntimeException("请重新获取验证码");
        }
        if(!code.equals(code1)){
            throw new ParamErrorException("验证码错误");
        }
    }


}
