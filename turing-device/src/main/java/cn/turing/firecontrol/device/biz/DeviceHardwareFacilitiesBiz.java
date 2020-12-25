package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.common.biz.BusinessBiz;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.util.Query;
import cn.turing.firecontrol.device.entity.*;
import cn.turing.firecontrol.device.mapper.DeviceHardwareFacilitiesMapper;
import cn.turing.firecontrol.device.util.Constant;
import cn.turing.firecontrol.device.util.ESTransportUtil;
import cn.turing.firecontrol.device.vo.TreeVo;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author HanYong
 * @version 2018-07-26 09:18:17
 * @email 3291659070@qq.com
 */
@Service
public class DeviceHardwareFacilitiesBiz extends BusinessBiz<DeviceHardwareFacilitiesMapper, DeviceHardwareFacilities> {

    @Autowired
    private DeviceHardwareFacilitiesMapper deviceHardwareFacilitiesMapper;
    @Autowired
    private DeviceNetworkingUnitBiz dnuBiz;
    @Autowired
    private DeviceSensorBiz dsBiz;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ESTransportUtil esTransportUtil;
    @Autowired
    private DeviceSensorSeriesBiz dssBiz;
    @Autowired
    private DeviceMeasuringPointBiz dmpBiz;
    @Autowired
    protected DeviceSensorTypeBiz dstBiz;
    @Autowired
    private DeviceSensorMpRelationBiz dsmrBiz;

    public List selectAll(String facilityType) {
        List<DeviceHardwareFacilities> hardwareFacilitiesList = deviceHardwareFacilitiesMapper.selectAllZxqy(facilityType);
        String jaonstr = redisTemplate.opsForHash().get("Area", "86").toString();
        JSONObject jsonObject = JSONObject.parseObject(jaonstr);
        List<TreeVo> resultlist = new ArrayList();
        for (DeviceHardwareFacilities deviceHardwareFacilities : hardwareFacilitiesList) {
            String status = dsBiz.HardwareFacilities(deviceHardwareFacilities.getHydrantName());
            String zxqy = deviceHardwareFacilities.getZxqy();//6位编码
            String province = jsonObject.get(zxqy.substring(0, 2) + "0000").toString();//省
            String jaonstr1 = redisTemplate.opsForHash().get("Area", zxqy.substring(0, 2) + "0000").toString();
            JSONObject jsonObject1 = JSONObject.parseObject(jaonstr1);
            Object temp = jsonObject1.get(zxqy.substring(0, 4) + "00");//市
            if (temp == null) {
                if (jsonObject1.get(zxqy) == null) {
                    List<TreeVo> list = new LinkedList<>();
                    list.add(new TreeVo(zxqy.substring(0, 2) + "0000", "-1", province, status));
                    resultlist.addAll(list);
                } else {
                    String city1 = jsonObject1.get(zxqy).toString();//市
                    List<TreeVo> list = new LinkedList<>();
                    list.add(new TreeVo(zxqy.substring(0, 2) + "0000", "-1", province, null));
                    list.add(new TreeVo(zxqy, zxqy.substring(0, 2) + "0000", city1, status));
                    resultlist.addAll(list);
                }
            } else {
                String city = jsonObject1.get(zxqy.substring(0, 4) + "00").toString();//市
                String jaonstr2 = redisTemplate.opsForHash().get("Area", zxqy.substring(0, 4) + "00").toString();
                JSONObject jsonObject2 = JSONObject.parseObject(jaonstr2);
                Object districtTemp = jsonObject2.get(zxqy);
                if (districtTemp == null) {
                    String city1 = jsonObject1.get(zxqy).toString();//市
                    List<TreeVo> list = new LinkedList<>();
                    list.add(new TreeVo(zxqy.substring(0, 2) + "0000", "-1", province, null));
                    list.add(new TreeVo(zxqy, zxqy.substring(0, 2) + "0000", city1, status));
                    resultlist.addAll(list);
                } else {
                    String district = jsonObject2.get(zxqy).toString();//区
                    List<TreeVo> list = new LinkedList<>();
                    list.add(new TreeVo(zxqy.substring(0, 2) + "0000", "-1", province, null));
                    list.add(new TreeVo(zxqy.substring(0, 4) + "00", zxqy.substring(0, 2) + "0000", city, null));
                    list.add(new TreeVo(zxqy, zxqy.substring(0, 4) + "00", district, status));
                    resultlist.addAll(list);
                }
            }
        }
        List<TreeVo> tree = removeDuplicate(resultlist);
        List<TreeVo> treeList = new LinkedList<TreeVo>();
        treeList = listGetStree(tree);
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

    public String getDistrictByZxqy(String zxqy) {
        String jaonstr = redisTemplate.opsForHash().get("Area", "86").toString();
        JSONObject jsonObject = JSONObject.parseObject(jaonstr);
        String jaonstr1 = redisTemplate.opsForHash().get("Area", zxqy.substring(0, 2) + "0000").toString();
        JSONObject jsonObject1 = JSONObject.parseObject(jaonstr1);
        Object cityTemp = jsonObject1.get(zxqy.substring(0, 4) + "00");//市
        if (cityTemp == null) {//如果市不存在，直接返回区
            if (jsonObject1.get(zxqy) == null) {
                return jsonObject.get(zxqy.substring(0, 2) + "0000").toString();
            }
            String district = jsonObject1.get(zxqy).toString();
            return district;
        }
        String jaonstr2 = redisTemplate.opsForHash().get("Area", zxqy.substring(0, 4) + "00").toString();
        JSONObject jsonObject2 = JSONObject.parseObject(jaonstr2);
        Object districtTemp = jsonObject2.get(zxqy);//区
        if (districtTemp == null) {//如果区不存在，直接返回市
            String city = cityTemp.toString();
            return city;
        }
        String district = jsonObject2.get(zxqy).toString();//区
        return district;
    }

    public Map selectFirst(String facilityType) {
        List<DeviceHardwareFacilities> hardwareFacilitiesList = deviceHardwareFacilitiesMapper.selectAllZxqy(facilityType);
        Map map = new HashMap();
        for (int i = 0; i < 1; i++) {
            if (hardwareFacilitiesList == null || hardwareFacilitiesList.size() == 0) {
                map.put("zxqy", -1);
                return map;
            }
            String zxqy = hardwareFacilitiesList.get(i).getZxqy();//6位编码
            map.put("zxqy", zxqy);
        }
        return map;
    }

    public TableResultResponse<Map> selectQuery(Query query, String ids, DeviceHardwareFacilities hardwareFacilities) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        Map<String, Object> map = new HashMap<>();
        //当ids为-1或者空时查询全部
        if (!(ids == null || "-1".equals(ids))) {
            String[] idstr = ids.split(",");
            List idlist = Arrays.asList(idstr);
            map.put("ids", idlist);
        }
        hardwareFacilities.setFacilityType("0");
        map.put("hardwareFacilities", hardwareFacilities);
        List<DeviceHardwareFacilities> lists = mapper.selectQuery(map);
        List<Map<String, Object>> maps = new ArrayList<>();
        for (int i = 0; i < lists.size(); i++) {
            Map<String, Object> temp = new HashMap<>();
            DeviceHardwareFacilities entity = lists.get(i);
            temp.put("id", entity.getId());
            temp.put("hydrantName", entity.getHydrantName());
            temp.put("area", entity.getArea());
            temp.put("protectionRadius", entity.getProtectionRadius());
            temp.put("positionDescription", entity.getPositionDescription());
            //消火栓类型
            if ("0".equals(entity.getHydrantType())) {
                temp.put("hydrantType", "地下式");
            } else if ("1".equals(entity.getHydrantType())) {
                temp.put("hydrantType", "地上式");
            } else if ("2".equals(entity.getHydrantType())) {
                temp.put("hydrantType", "直埋伸缩");
            }
            //出水口
            if ("0".equals(entity.getOutlet())) {
                temp.put("outlet", "单口式");
            } else if ("1".equals(entity.getOutlet())) {
                temp.put("outlet", "双口式");
            } else if ("2".equals(entity.getOutlet())) {
                temp.put("outlet", "三出水口式");
            }
            //水管道
            if ("0".equals(entity.getWaterPipe())) {
                temp.put("waterPipe", "高压给水");
            } else if ("1".equals(entity.getWaterPipe())) {
                temp.put("waterPipe", "临时高压给水");
            } else if ("2".equals(entity.getWaterPipe())) {
                temp.put("waterPipe", "低压给水");
            }
            //标记
            if (StringUtils.isNotBlank(entity.getGis())) {
                temp.put("gis", "已标记");
            } else {
                temp.put("gis", "未标记");
            }
            maps.add(temp);

        }

        return new TableResultResponse(result.getTotal(), maps);
    }


    //根据6位编码查询室外消防栓
    public List<Integer> selectByZxqzResultIds(String code, String facilityType) {
        DeviceHardwareFacilities entity = new DeviceHardwareFacilities();
        entity.setZxqy(code);
        entity.setHydrantType(facilityType);
        return mapper.selectByZxqzResultIds(entity);
    }


    //查看硬件设施名称是否重复
    public Integer selectByCount(String hydrantName, String facilityType) {
        DeviceHardwareFacilities entity = new DeviceHardwareFacilities();
        entity.setHydrantName(hydrantName);
        entity.setFacilityType(facilityType);
        return mapper.selectByCount(entity);
    }

    public TableResultResponse getHardwareFacilities(Query query, String zxqy, Integer channelId) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        Map<String, Object> map = new HashMap<>();
        map.put("zxqy", zxqy);
        map.put("channelId", channelId);
        map.put("facilityType", "0");
        //查询条件对应消火栓
        List<DeviceHardwareFacilities> list = mapper.getHardwareFacilities(map);
        List<Map<String, Object>> resultlist = new ArrayList();
        for (DeviceHardwareFacilities deviceHardwareFacilities : list) {
            Map<String, Object> resultmap = new HashMap<>();
            resultmap.put("hydrantId", deviceHardwareFacilities.getId());//消火栓id
            resultmap.put("hydrantName", deviceHardwareFacilities.getHydrantName());
            resultmap.put("positionDescription", deviceHardwareFacilities.getPositionDescription());
//            Integer count = dsBiz.selectFaultCount(deviceHardwareFacilities.getId(),channelId);//故障数量
//            resultmap.put("count",count);
            if (deviceHardwareFacilities.getOid() != null) {
                //根据单位id查询单位
                DeviceNetworkingUnit deviceNetworkingUnit = dnuBiz.selectById(deviceHardwareFacilities.getOid());
                if (deviceNetworkingUnit == null) {//如果单位为空,结束本次循环
                    resultlist.add(resultmap);
                    continue;
                }
                resultmap.put("oName", deviceNetworkingUnit.getOName());
                resultmap.put("oPhone", deviceNetworkingUnit.getOPhone());
            }
            resultlist.add(resultmap);
        }
        return new TableResultResponse(result.getTotal(), resultlist);
    }

    //根据硬件设施的名称查询硬件设施,和类型查询
    public List<DeviceHardwareFacilities> selectByNameAndType(String facilityType, String hydrantName) {
        DeviceHardwareFacilities eneity = new DeviceHardwareFacilities();
        eneity.setFacilityType(facilityType);
        eneity.setHydrantName(hydrantName);
        return mapper.selectByNameAndType(eneity);
    }

    /*    //根据硬件设施的名称查询分页硬件设施,和类型查询
        public TableResultResponse selectByNameAndTypeQuery(Query query,String facilityType,String hydrantName){
            Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
            DeviceHardwareFacilities eneity = new DeviceHardwareFacilities();
            eneity.setFacilityType(facilityType);
            eneity.setHydrantName(hydrantName);
            List<DeviceHardwareFacilities> resultlist =  mapper.selectByNameAndType(eneity);
            return new TableResultResponse(result.getTotal(),resultlist);
        }*/
//根据硬件设施的名称查询硬件设施,和类型和代号查询
    public List<DeviceHardwareFacilities> selectByNameAndTypeAndCode(String facilityType, String hydrantName, String code) {
        DeviceHardwareFacilities eneity = new DeviceHardwareFacilities();
        eneity.setFacilityType(facilityType);
        eneity.setHydrantName(hydrantName);
        eneity.setZxqy(code);
        return mapper.selectByNameAndTypeAndCode(eneity);
    }

    public DeviceHardwareFacilities getById(Integer id) {
        return mapper.getById(id);
    }

    public List<DeviceHardwareFacilities> getAll(String facilityType) {
        return mapper.getAll(facilityType);
    }

    public List<DeviceHardwareFacilities> selectByHydrantNameLike(String hydrantName, String tenantId) {
        Map map = new HashMap();
        map.put("hydrantName", hydrantName);
        map.put("tenantId", tenantId);
        return mapper.selectByHydrantNameLike(map);
    }

    public List<String> getProvince(String zxqy) {
        List<String> list = new LinkedList<>();
        list.add(zxqy.substring(0, 2) + "0000");
        String jaonstr1 = redisTemplate.opsForHash().get("Area", zxqy.substring(0, 2) + "0000").toString();
        JSONObject jsonObject1 = JSONObject.parseObject(jaonstr1);
        Object temp = jsonObject1.get(zxqy.substring(0, 4) + "00");//市
        if (temp == null) {
            list.add(zxqy);
            return list;
        }
        list.add(zxqy.substring(0, 4) + "00");
        list.add(zxqy);
        return list;
    }

    public TableResultResponse getAllHardwareFacilitiesList(Query query, Integer channelId) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<DeviceHardwareFacilities> list = mapper.getAll("0");
        List resultlist = new ArrayList();
        for (DeviceHardwareFacilities deviceHardwareFacilities : list) {
            Map map = new HashMap();
            map.put("hydrantName", deviceHardwareFacilities.getHydrantName());
            map.put("address", deviceHardwareFacilities.getPositionDescription());
            if ("0".equals(deviceHardwareFacilities.getOutlet())) {
                map.put("outlet", "单口式");
            }
            if ("1".equals(deviceHardwareFacilities.getOutlet())) {
                map.put("outlet", "双口式");
            }
            if ("2".equals(deviceHardwareFacilities.getOutlet())) {
                map.put("outlet", "三出水口式");
            }
            List measuringPointList = new ArrayList();
            List<DeviceSensor> sensorList = dsBiz.getByHydrantId(deviceHardwareFacilities.getId(), channelId);
            if (sensorList.size() == 0) {
                map.put("measuringPointList", measuringPointList);
                resultlist.add(map);
                continue;
            }
            for (DeviceSensor deviceSensor : sensorList) {
                if (!"3".equals(deviceSensor.getStatus())) {
                    DeviceSensorType deviceSensorType = dstBiz.selectById(deviceSensor.getSensorTypeId());
                    //查询传感器系列
                    DeviceSensorSeries deviceSensorSeries = dssBiz.selectBySensorTypeId(deviceSensorType.getId());
                    //查询测点ids
                    List<Integer> ids = dsmrBiz.selectBySensorSeriesId(deviceSensorSeries.getId());
                    JSONObject jasonObject = getESlastData(deviceSensor.getSensorNo());
                    for (Integer id : ids) {
                        DeviceMeasuringPoint deviceMeasuringPoint = dmpBiz.selectById(id);
                        Map measuringPointMap = new HashMap();
                        if (deviceMeasuringPoint == null) {
                            resultlist.add(map);
                            continue;
                        }
                        if (jasonObject.containsKey(deviceMeasuringPoint.getCodeName())) {
                            measuringPointMap.put("measuringPoint", deviceMeasuringPoint.getMeasuringPoint());
                            JSONObject jasonObject1 = JSONObject.parseObject(jasonObject.get(deviceMeasuringPoint.getCodeName()).toString());
                            measuringPointMap.put("status", jasonObject1.get("alarmStatus"));
                            if ("0".equals(jasonObject1.get("alarmStatus").toString()) || "4".equals(jasonObject1.get("alarmStatus").toString())) {
                                measuringPointMap.put("state", "故障");
                            }
                            if ("1".equals(jasonObject1.get("alarmStatus").toString())) {
                                measuringPointMap.put("state", "报警");
                            }
                            if ("2".equals(jasonObject1.get("alarmStatus").toString())) {
                                measuringPointMap.put("state", "正常");
                            }
                            if (jasonObject1.get("alarmValue") != null) {
                                measuringPointMap.put("data", jasonObject1.get("alarmValue") + deviceMeasuringPoint.getDataUnit());
                            }
                            measuringPointList.add(measuringPointMap);
                        }
                    }
                }
                map.put("measuringPointList", measuringPointList);
            }
            resultlist.add(map);
        }
        return new TableResultResponse(result.getTotal(), resultlist);
    }

    //获取es  传感器的最后一条数据
    public JSONObject getESlastData(String deviceid) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        JSONObject jsonObject = new JSONObject();
        //mac地址
        if (org.apache.commons.lang.StringUtils.isNotBlank(deviceid)) {
            queryBuilder.must(QueryBuilders.matchQuery("deviceid", deviceid));
            SortBuilder sortBuilder = SortBuilders.fieldSort("uploadtime").unmappedType("date").order(SortOrder.DESC);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().query(queryBuilder).size(1).sort(sortBuilder);
            SearchResponse searchResponse = esTransportUtil.query(Constant.ESConstant.ES_INDEX_SENSOR, null, searchSourceBuilder);
            SearchHits searchHits = searchResponse.getHits();
            if (searchHits.getTotalHits().value > 0) {
                jsonObject = JSONObject.parseObject(searchHits.getAt(0).getSourceAsString());
            }
        }
        return jsonObject;
    }

    public List<String> getHydrantName() {
        return mapper.getHydrantName();
    }

    public DeviceHardwareFacilities selectByHydrantName(String hydrantName) {
        return mapper.selectByHydrantName(hydrantName);
    }

    public List<DeviceHardwareFacilities> getAllAndDelflag(String facilityType) {
        return mapper.getAllAndDelflag(facilityType);
    }
}