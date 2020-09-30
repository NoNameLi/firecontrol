package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.device.config.ApplicationStartListener;
import cn.turing.firecontrol.device.entity.DeviceBuilding;
import cn.turing.firecontrol.device.entity.DeviceSensor;
import cn.turing.firecontrol.device.mapper.DeviceHardwareFacilitiesMapper;
import cn.turing.firecontrol.device.mapper.DeviceSensorMapper;
import cn.turing.firecontrol.device.vo.DeviceBuildingVo;
import cn.turing.firecontrol.device.vo.TreeVo;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import cn.turing.firecontrol.device.mapper.DeviceBuildingMapper;
import cn.turing.firecontrol.common.biz.BusinessBiz;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 
 *
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:17:58
 */
@Service
public class DeviceBuildingBiz extends BusinessBiz<DeviceBuildingMapper, DeviceBuilding> {

    @Autowired
    private DeviceBuildingMapper deviceBuildingMapper;
    @Autowired
    private DeviceSensorMapper deviceSensorMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private DeviceSensorBiz dsBiz;
    @Autowired
    private DeviceHardwareFacilitiesMapper deviceHardwareFacilitiesMapper;


    //删除建筑物，传感器关联删除
    public void deleteById(Integer id){
        //根据建筑ID，查询关联传感器
       List<DeviceSensor> list = deviceSensorMapper.selectByBuildingId(id,null);
       for (DeviceSensor deviceSensor:list) {
           deviceSensorMapper.deleteByPrimaryKey(deviceSensor.getId());
       }
       deviceBuildingMapper.deleteByPrimaryKey(id);
    }

    public List<DeviceBuilding> selectByOid(Integer id) {
        List<DeviceBuilding> list = deviceBuildingMapper.selectByOid(id);
        return list;
    }

//    public List selectAll() {
//        List<Map<String,Object>> resultlist=new ArrayList<Map<String,Object>>();
//        List<DeviceBuilding> buildingList = deviceBuildingMapper.selectAllZxqy();
//        String jaonstr = redisTemplate.opsForHash().get("Area","86").toString();
//        JSONObject jsonObject = JSONObject.parseObject(jaonstr);
//        for(DeviceBuilding deviceBuilding:buildingList){
//            Map map1 = new LinkedHashMap();
//            Map map2 = new LinkedHashMap();
//            Map map3 = new LinkedHashMap();
//            Set<Map<String,Object>> list2=new HashSet<Map<String,Object>>();
//            Set<Map<String,Object>> list3=new HashSet<Map<String,Object>>();
//            Set<Map<String,Object>> list1=new HashSet<>();
//            String zxqy = deviceBuilding.getZxqy();//6位编码
//            String province = jsonObject.get(zxqy.substring(0,2)+"0000").toString();//省
//            String jaonstr1 = redisTemplate.opsForHash().get("Area",zxqy.substring(0,2)+"0000").toString();
//            JSONObject jsonObject1 = JSONObject.parseObject(jaonstr1);
//            String city = jsonObject1.get(zxqy.substring(0,4)+"00").toString();//市
//            String jaonstr2 = redisTemplate.opsForHash().get("Area",zxqy.substring(0,4)+"00").toString();
//            JSONObject jsonObject2 = JSONObject.parseObject(jaonstr2);
//            String district = jsonObject2.get(zxqy).toString();//区
//            List<DeviceBuilding> buildinglist = deviceBuildingMapper.selectByZxqy(zxqy);
//            for(DeviceBuilding building:buildinglist){
//               Map map4 = new HashMap();
//               map4.put("code",building.getId()+"");
//               map4.put("name",building.getBName());
//               map4.put("zxqy",building.getZxqy());
//               list3.add(map4);
//            }
//            map3.put("code",zxqy);
//            map3.put("name",district);
//            map3.put("childList",list3);
//            list2.add(map3);
//            map2.put("code",zxqy.substring(0,4)+"00");
//            map2.put("name",city);
//            map2.put("childList",list2);
//            list1.add(map2);
//            map1.put("code",zxqy.substring(0,2)+"0000");
//            map1.put("name",province);
//            map1.put("childList",list1);
//            resultlist.add(map1);
//        }
//        return resultlist;
//    }

    public DeviceBuilding selectByBname(String buildingName) {
        return deviceBuildingMapper.selectByBname(buildingName);
    }

    //查看联网单位绑定的建筑id
    public List<Integer> selectByOidResultIds(Integer oid){
        return deviceBuildingMapper.selectByOidResultIds(oid);
    }

    //根据联网单位ids查询
    public List<DeviceBuilding> selectByOids(String ids){
        return deviceBuildingMapper.selectByOids(ids);
    }

    public TableResultResponse<DeviceBuilding> selectQuery(Query query, String buildingName) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        Map<String ,Object> map = new HashMap<>();
        map.put("buildingName",buildingName);
        List<DeviceBuilding> list = deviceBuildingMapper.selectQuery(map);
        return new TableResultResponse(result.getTotal(),list);
    }

    public Map selectFirst() {
        List<DeviceBuilding> buildingList = deviceBuildingMapper.selectAllZxqy();
        Map map = new HashMap();
        for(int i=0;i<1;i++){
            if(buildingList==null||buildingList.size()==0){
                map.put("id",-1);
                return map;
            }
            String zxqy = buildingList.get(i).getZxqy();//6位编码
            List<DeviceBuilding> buildinglist = deviceBuildingMapper.selectByZxqy(zxqy);
            for(int j=0;j<1;j++){
                DeviceBuilding deviceBuilding = buildinglist.get(j);
                map.put("id",deviceBuilding.getId());
            }
        }
        return map;
    }


    //根据6位编码查询建筑
    public List<Integer> selectByZxqzResultIds(String zxqz){
       return deviceBuildingMapper.selectByZxqzResultIds(zxqz);
    }

    //查询所有建筑名
    public List<String> getBname() {
        return deviceBuildingMapper.getBname();
    }

    public List<DeviceBuilding> selectByZxqz(String zxqy) {
        return deviceBuildingMapper.selectByZxqy(zxqy);
    }

    public List<DeviceBuilding> selectByBnameLike(String bName,String tenantId) {
        Map map = new HashMap();
        map.put("bName",bName);
        map.put("tenantId",tenantId);
        return deviceBuildingMapper.selectByBnameLike(map);
    }

    public List selectAll(Integer channelId) {
        List<DeviceBuilding> buildingList = deviceBuildingMapper.selectAllZxqy();//查询所有建筑
        String jaonstr = redisTemplate.opsForHash().get("Area","86").toString();
        JSONObject jsonObject = JSONObject.parseObject(jaonstr);
        List<TreeVo> resultlist = new ArrayList();
        for(DeviceBuilding deviceBuilding:buildingList){
            //String status = dsBiz.getBuildingStatus(deviceBuilding.getBName(),channelId);// 查询建筑的状态(故障/报警/正常)
            String status = dsBiz.getTotalBuildingStatus(deviceBuilding.getId(),channelId);// 查询所有传感器上建筑的状态(故障/报警/正常)
            String zxqy = deviceBuilding.getZxqy();//6位编码
            //String zxqy = "429004";
            String province = jsonObject.get(zxqy.substring(0,2)+"0000").toString();//省
            String jaonstr1 = redisTemplate.opsForHash().get("Area",zxqy.substring(0,2)+"0000").toString();
            JSONObject jsonObject1 = JSONObject.parseObject(jaonstr1);
            Object temp = jsonObject1.get(zxqy.substring(0,4)+"00");//市
            if(temp==null){
                if(jsonObject1.get(zxqy)==null){
                    List<TreeVo> list = new LinkedList<>();
                    list.add(new TreeVo(zxqy.substring(0,2)+"0000","-1",province,null));
                    list.add(new TreeVo(deviceBuilding.getId()+"",zxqy,deviceBuilding.getBName(),status));
                    resultlist.addAll(list);
                }else{
                    String city1 = jsonObject1.get(zxqy).toString();//市
                    List<TreeVo> list = new LinkedList<>();
                    list.add(new TreeVo(zxqy.substring(0,2)+"0000","-1",province,null));
                    list.add(new TreeVo(zxqy,zxqy.substring(0,2)+"0000",city1,null));
                    list.add(new TreeVo(deviceBuilding.getId()+"",zxqy,deviceBuilding.getBName(),status));
                    resultlist.addAll(list);
                }

            }else{
                String city = jsonObject1.get(zxqy.substring(0,4)+"00").toString();//市
                String jaonstr2 = redisTemplate.opsForHash().get("Area",zxqy.substring(0,4)+"00").toString();
                JSONObject jsonObject2 = JSONObject.parseObject(jaonstr2);
                Object districtTemp = jsonObject2.get(zxqy);
                if(districtTemp==null){
                    String city1 = jsonObject1.get(zxqy).toString();//市
                    List<TreeVo> list = new LinkedList<>();
                    list.add(new TreeVo(zxqy.substring(0,2)+"0000","-1",province,null));
                    list.add(new TreeVo(zxqy,zxqy.substring(0,2)+"0000",city1,null));
                    list.add(new TreeVo(deviceBuilding.getId()+"",zxqy,deviceBuilding.getBName(),status));
                    resultlist.addAll(list);
                }else {
                    String district = jsonObject2.get(zxqy).toString();//区
                    List<TreeVo> list = new LinkedList<>();
                    list.add(new TreeVo(zxqy.substring(0,2)+"0000","-1",province,null));
                    list.add(new TreeVo(zxqy.substring(0,4)+"00",zxqy.substring(0,2)+"0000",city,null));
                    list.add(new TreeVo(zxqy,zxqy.substring(0,4)+"00",district,null));
                    list.add(new TreeVo(deviceBuilding.getId()+"",zxqy,deviceBuilding.getBName(),status));
                    resultlist.addAll(list);
                }
            }
        }
        List<TreeVo> tree = removeDuplicate(resultlist);//去重
        List<TreeVo> treeList = new LinkedList<TreeVo>();
        treeList = listGetStree(tree);//设置层级关系
        return treeList;
    }

    private static List<TreeVo> listGetStree(List<TreeVo> list) {
        List<TreeVo> treeList = new ArrayList<TreeVo>();
        for (TreeVo tree : list) {
            //找到根
            if (tree.getPid().equals("-1")) {
                treeList.add(tree);
            }
            //找到子
            for (TreeVo treeNode : list) {
                if (treeNode.getPid().equals(tree.getCode())) {
                    if (tree.getChildList() == null) {
                        tree.setChildList(new ArrayList<TreeVo>());
                    }
                    tree.getChildList().add(treeNode);
                }
            }
        }
        return treeList;
    }
    private static List<TreeVo> removeDuplicate(List<TreeVo> list) {
        Set<TreeVo> set = new TreeSet<TreeVo>(new Comparator<TreeVo>() {
            @Override
            public int compare(TreeVo a, TreeVo b) {
                // 字符串则按照asicc码升序排列
                return a.getCode().compareTo(b.getCode());
            }
        });
        set.addAll(list);
        return new ArrayList<TreeVo>(set);
    }

    //查看建筑名称是否重复
    public Integer selectByCount(String bName){
        return deviceBuildingMapper.selectByCount(bName);
    }

    public DeviceBuilding getById(Integer id) {
        return deviceBuildingMapper.getById(id);
    }

    public List<DeviceBuilding> selectBySensor(Integer channelId) {
        return deviceBuildingMapper.selectBySensor(channelId);
    }

    //查询全部没有被删除的记录
    public List<DeviceBuilding> getAll(Integer buildingId){
        return mapper.getAll(buildingId);
    }
    //查询全部记录(包括被删除的)
    public List<DeviceBuilding> getAllAndDelflag() {
        return mapper.getAllAndDelflag();
    }

    public Map<String,Object> getTotalCountAndArea(){
        return mapper.getTotalCountAndArea();
    }
    public DeviceBuildingVo getMaxAlarmBuilding(@Param("channelId")Integer channelId){
        DeviceBuildingVo rs=null;
        List<DeviceBuildingVo> list = mapper.getAlarmBuilding(channelId);
        if(!list.isEmpty()){
           rs = list.stream().max(Comparator.comparing(DeviceBuildingVo::getNum)).get();
        }else {
            rs=mapper.getLatestBuilding();
        }
        return rs;
    }
    public List<DeviceBuildingVo> getAllBuildingStatus(){
        //状态[0=故障/1=报警/2=正常/3=未启用/4=离线]
        //状态1->0->4->2
        List<DeviceBuildingVo> allGis = mapper.getAllGis();
        Map<String,Object> map =new HashMap<>();
        if(CollectionUtils.isNotEmpty(allGis)){
            for (DeviceBuildingVo b : allGis) {
                map.put("buildingId",b.getId());
                List<String> bs = deviceSensorMapper.getAllStatusById(map);
                if(CollectionUtils.isNotEmpty(bs)){
                    if(bs.contains("1")){
                        b.setStatus("报警");
                    }else if(bs.contains("0")){
                        b.setStatus("故障");
                    }else if(bs.contains("4")){
                        b.setStatus("离线");
                    }else {
                        b.setStatus("正常");
                    }

                }else {
                    b.setStatus("正常");
                }
            }
        }
        List<DeviceBuildingVo> allGis1 = deviceHardwareFacilitiesMapper.getAllGis();
        if(CollectionUtils.isNotEmpty(allGis1)){
            for (DeviceBuildingVo b : allGis1) {
                map.put("hydrantId",b.getId());
                List<String> hs= deviceSensorMapper.getAllStatusById(map);
                if(CollectionUtils.isNotEmpty(hs)){
                    if(hs.contains("1")){
                        b.setStatus("报警");
                    }else if(hs.contains("0")){
                        b.setStatus("故障");
                    }else if(hs.contains("4")){
                        b.setStatus("离线");
                    }else {
                        b.setStatus("正常");
                    }

                }else {
                    b.setStatus("正常");
                }
            }
        }
        if(CollectionUtils.isNotEmpty(allGis) && CollectionUtils.isNotEmpty(allGis1)){
            allGis.addAll(allGis1);
        }

        return allGis;
    }

   /* public static void main(String[] args) {
        List<String> bs=new ArrayList<>();
        bs.add("0");
        bs.add("4");
        bs.add("0");
        bs.add("4");
        String status="";
        if(CollectionUtils.isNotEmpty(bs)){
            if(bs.contains("1")){
                status="报警";
            }else if(bs.contains("0")){
                status="故障";
            }else if(bs.contains("4")){
                status="离线";
            }else {
                status="正常";
            }

        }
        System.out.println(status);
    }*/
}