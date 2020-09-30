package cn.turing.firecontrol.device.rest;

import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.auth.client.annotation.IgnoreClientToken;
import cn.turing.firecontrol.auth.client.annotation.IgnoreUserToken;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.rest.BaseController;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.common.validator.ValidatorUtils;
import cn.turing.firecontrol.device.biz.DeviceFireMainBiz;
import cn.turing.firecontrol.device.biz.DeviceFireMainSensorBiz;
import cn.turing.firecontrol.device.biz.DeviceNoticeBiz;
import cn.turing.firecontrol.device.entity.*;
import cn.turing.firecontrol.device.util.Constants;
import cn.turing.firecontrol.device.util.POIUtil;
import cn.turing.firecontrol.device.util.SplitUtil;
import cn.turing.firecontrol.device.util.TrimUtil;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("deviceFireMain")
@CheckClientToken
@CheckUserToken
@Api(tags = "消防主机模块")
public class DeviceFireMainController extends BaseController<DeviceFireMainBiz,DeviceFireMain,Integer> {


    @Autowired
    private DeviceFireMainSensorBiz dfmsBiz;

    @ApiOperation("分页获取数据,按条件搜索数据")
    @RequestMapping(value = "/pageList",method = RequestMethod.GET)
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "当前页",paramType = "query"),
            @ApiImplicitParam(name = "limit",value = "当前显示条数",paramType = "query")
    })
    public TableResultResponse<Map> list(@RequestParam String page, @RequestParam String limit, DeviceFireMain deviceFireMain ){
        //查询列表数据
        Map<String,Object> params = new LinkedHashMap();
        params.put("page",page);
        params.put("limit",limit);
        Query query = new Query(params);
        return baseBiz.selectPageList(query,deviceFireMain);
    }

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("添加消防主机")
    public ObjectRestResponse<DeviceFireMain> add(@RequestBody DeviceFireMain entity){
        ObjectRestResponse<DeviceFireMain> responseResult =  new ObjectRestResponse<DeviceFireMain>();
        TrimUtil.trimObject(entity);
        TrimUtil.trimNull(entity.getServerIp(),entity.getPort());
        String serverIp = entity.getServerIp();
        String port = entity.getPort();
        if(ValidatorUtils.hasAnyBlank(serverIp,port)){
            return responseResult;
        }
        if(!isIP(serverIp)){
            throw new RuntimeException("消防主机ip不合法");
        }
        if(!isPort(port)){
            throw new RuntimeException("端口号不合法");
        }
        //判重标准：主机+ip唯一  忽略租户隔离判重
        if(baseBiz.selectIgnoreTenantByCount(serverIp,port)>0){
            throw new RuntimeException("消防主机已存在，不可重复添加");
        }
        entity.setId(null);
        baseBiz.insertSelective(entity);
        return responseResult;
    }


    @RequestMapping(value = "/update",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("修改消防主机")
    public ObjectRestResponse<DeviceFireMain> update(@RequestBody DeviceFireMain entity){
        ObjectRestResponse<DeviceFireMain> responseResult =  new ObjectRestResponse<DeviceFireMain>();
        TrimUtil.trimObject(entity);
        TrimUtil.trimNull(entity.getServerIp(),entity.getPort());
        String serverIp = entity.getServerIp();
        String port = entity.getPort();
        if(ValidatorUtils.hasAnyBlank(serverIp,port)){
            return responseResult;
        }
        if(!isIP(serverIp)){
            throw new RuntimeException("消防主机ip不合法");
        }
        if(!isPort(port)){
            throw new RuntimeException("端口号不合法");
        }
        DeviceFireMain deviceFireMain = baseBiz.selectById(entity.getId());
        //对象不存在
        if(deviceFireMain==null){
            throw  new RuntimeException(Constants.API_MESSAGE_OBJECT_NOT_FOUND);
        }
        //判重标准：主机+ip唯一
        if(!(serverIp.equalsIgnoreCase(deviceFireMain.getServerIp())&&port.equalsIgnoreCase(deviceFireMain.getPort()))&&baseBiz.selectIgnoreTenantByCount(serverIp,port)>0){
            throw new RuntimeException("消防主机已存在");
        }
        baseBiz.updateSelectiveById(entity);
        return responseResult;
    }

    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("删除消防主机，级联删除传感器")
    public ObjectRestResponse<DeviceFireMain> delete(@RequestParam String ids){
        ObjectRestResponse<DeviceFireMain> responseResult =  new ObjectRestResponse<DeviceFireMain>();
        if(StringUtils.isBlank(ids)){
            return responseResult;
        }
        Integer[] idDel = SplitUtil.splitInt(ids);
        DeviceFireMain deviceFireMain = null;
        //批量假删除
        for(int i=0;i<idDel.length;i++){
            //删除消防主机
            deviceFireMain = new DeviceFireMain();
            deviceFireMain.setId(idDel[i]);
            deviceFireMain.setDelFlag("1");
            baseBiz.updateSelectiveById(deviceFireMain);
            //删除消防主机下面的传感器
            dfmsBiz.deleteByFireMain(idDel[i]);
        }
        return responseResult;
    }

    @RequestMapping(value = "/deleteQuery",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("删除主机前的查询")
    public ObjectRestResponse<Object> deleteQuery(@RequestParam String ids){
        ObjectRestResponse<Object> responseResult =  new ObjectRestResponse<Object>();
        if(StringUtils.isBlank(ids)){
            return responseResult;
        }
        Integer[] idDel = SplitUtil.splitInt(ids);
        List<Integer> retInt = new ArrayList<>();
        for(int i=0;i<idDel.length;i++){
            if(dfmsBiz.selectByFireMainIdCount(idDel[i])>0){
                retInt.add(idDel[i]);
            }
        }
        return responseResult.data(retInt);
    }

    @RequestMapping(value = "/get",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询")
    public ObjectRestResponse<DeviceFireMain> get(@RequestParam Integer id){
        ObjectRestResponse<DeviceFireMain> responseResult =  new ObjectRestResponse<DeviceFireMain>();
        return responseResult.data(baseBiz.selectById(id));
    }


    @RequestMapping(value = "/sensorImport",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("导入消防主机")
    public ObjectRestResponse sensorImport(@RequestParam(value = "file", required = false) MultipartFile file ) {
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        if (file == null) {
            throw new RuntimeException(Constants.API_MESSAGE_PARAM_REQUIRED);
        }else if(file.getSize()>10*1024*1024){
            throw new RuntimeException("文件大小不能超过10M!");
        }else{
            String fileName = file.getOriginalFilename();
            InputStream inputStream= null;
            List<String[]> excel  = null;
            POIUtil poiUtil=new POIUtil();
            try {
                inputStream = file.getInputStream();
                excel = poiUtil.readExcel(fileName,inputStream);
                inputStream.close();
            } catch (Exception e) {
                throw new RuntimeException("导入出错!");
            }
            //模板的字段
            boolean serverIpFlag=false;
            boolean portFlag=false;
            boolean positionDescriptionFlag=false;
            //模板字段的位置标记
            int serverIpCount=0;
            int portCount=0;
            int positionDescriptionCount=0;
            if(excel.size()>1){
                String[] s =  excel.get(0);
                for(int i=0;i<s.length;i++){
                    if("服务器IP".equals(s[i])||"服务器IP(必填)".equals(s[i])||"服务器IP（必填）".equals(s[i])){
                        serverIpFlag=true;
                        serverIpCount=i;
                    }
                    if("端口号".equals(s[i])||"端口号(必填)".equals(s[i])||"端口号（必填）".equals(s[i])){
                        portFlag=true;
                        portCount=i;
                    }
                    if("位置描述".equals(s[i])||"位置描述(必填)".equals(s[i])||"位置描述（必填）".equals(s[i])){
                        positionDescriptionFlag=true;
                        positionDescriptionCount=i;
                    }
                }
                //判断Excel表模板是否正确
                if(serverIpFlag&&portFlag&&positionDescriptionFlag){

                    //判断Excel里面IP+端口号是否重复
                    Set<String> ipAndPort = new HashSet<>();
                    List<DeviceFireMain> deviceFireMains = baseBiz.getIgnoreTenantAll();
                    DeviceFireMain deviceFireMain = null;
                    Set<String> ipAndPortBase = new HashSet<>();
                    for(int i=0;i<deviceFireMains.size();i++){
                        deviceFireMain = deviceFireMains.get(i);
                        if(deviceFireMain!=null){
                            ipAndPortBase.add(deviceFireMain.getServerIp()+","+deviceFireMain.getPort());
                        }
                    }
                    //数据检验
                    for (int i=1;i<excel.size();i++){
                        String[] exs = new String[s.length];
                        String[] str = excel.get(i);
                        if(str.length<s.length) {
                            System.arraycopy(str,0,exs,0,str.length);
                        }else {
                            exs = str;
                        }
                        for(int j=1;j<s .length;j++){
                            if(StringUtils.isNotBlank(exs[j])){
                                exs[j] = exs[j].trim();
                            }
                            if(j==serverIpCount){
                                //判断服务器IP是否为空
                                if(StringUtils.isBlank(exs[j])){
                                    throw new RuntimeException("第"+exs[0]+"行的服务器IP为空，无法导入");
                                }
                                if(!isIP(exs[j])){
                                    throw new RuntimeException("第"+exs[0]+"行的服务器IP不合法，无法导入");
                                }
                                if(exs[j].length()>15){
                                    throw new RuntimeException("第"+exs[0]+"行的服务器IP长度超过15个字符");
                                }
                            }
                            else if(j==portCount){
                                //判断端口号
                                if(StringUtils.isBlank(exs[j])){
                                    throw new RuntimeException("第"+exs[0]+"行的端口号为空，无法导入");
                                }
                                if(!isPort(exs[j])){
                                    throw new RuntimeException("第"+exs[0]+"行的端口号不合法，无法导入");
                                }
                                if(exs[j].length()>5){
                                    throw new RuntimeException("第"+exs[0]+"行的端口号长度超过5个字符");
                                }
                                //判断ip+端口组合是否在Excel中重复
                                if(!ipAndPort.add(exs[serverIpCount]+","+exs[portCount])){
                                    throw new RuntimeException("文件中服务器IP和端口号组合"+exs[serverIpCount]+" "+exs[portCount]+"存在重复");
                                }
                                //判断ip+端口在数据库中是否存在
                                if(!ipAndPortBase.add(exs[serverIpCount]+","+exs[portCount])){
                                    throw new RuntimeException("第"+exs[0]+"行的服务器IP和地址已存在");
                                }
                            }else if(j==positionDescriptionCount){
                                //判断位置描述是否为空  excel最后一行数据为空
                                if(StringUtils.isBlank(exs[j])){
                                    throw new RuntimeException("第"+exs[0]+"行的位置描述为空，无法导入");
                                }
                                if(exs[j].length()>50){
                                    throw new RuntimeException("第"+exs[0]+"行的位置描述字符长度超过50个字符");
                                }
                            }
                        }
                    }
                    //去除第一条数据
                    excel.remove(0);
                    int insertCount = 0;
                    DeviceFireMain fireMain = null;
                    for(int i=0;i<excel.size();i++){
                        String [] temp = excel.get(i);
                        fireMain = new DeviceFireMain();
                        fireMain.setServerIp(temp[serverIpCount]);
                        fireMain.setPort(temp[portCount]);
                        fireMain.setGis("");
                        fireMain.setPositionDescription(temp[positionDescriptionCount]);
                        try {
                            TrimUtil.trimObject(fireMain);
                            baseBiz.insertSelective(fireMain);
                            insertCount = insertCount+1;
                        }catch (Exception e){
                            throw new RuntimeException("插入数据异常！");
                        }
                    }
                    responseResult.setData(insertCount);
                    return responseResult;
                }else{
                    throw new RuntimeException("Excel模板错误!");
                }
            }else{
                throw new RuntimeException("文件内容为空!");
            }
        }
    }

    @IgnoreClientToken
    @IgnoreUserToken
    @RequestMapping(value = "selectById",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("根据id查询消防主机")
    public ObjectRestResponse selectById(@RequestParam Integer id){
        ObjectRestResponse responseResult = new ObjectRestResponse();
        DeviceFireMain deviceFireMain = baseBiz.getById(id);
        responseResult.setData(deviceFireMain);
        return responseResult;
    }


    /**
     * Ip地址判断<br>
     * 符号 '\d'等价的正则表达式'[0-9]',匹配数字0-9<br>
     * {1,3}表示匹配三位以内的数字（包括三位数）
     *
     * @param str
     * @return
     */
    public static boolean isIP(String str) {

        // 匹配 1
        // String regex = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
        // 匹配 2
        String regex = "[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}";
        // 匹配1 和匹配2均可实现Ip判断的效果
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(str).matches();
    }

    /**
     * 判断端口号是否合法
     *
     * @param portStr
     * @return
     */
    public  boolean isPort(String portStr) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(portStr);
        if (isNum.matches() && portStr.length() < 6 && Integer.valueOf(portStr) >= 1
                && Integer.valueOf(portStr) <= 65535) {
            return true;
        }
        return false;
    }
}