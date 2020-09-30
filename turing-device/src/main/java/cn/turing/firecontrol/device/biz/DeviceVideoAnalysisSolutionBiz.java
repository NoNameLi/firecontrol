package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.common.biz.BusinessBiz;
import cn.turing.firecontrol.device.entity.DeviceVideoAnalysisSolution;
import cn.turing.firecontrol.device.mapper.DeviceVideoAnalysisSolutionMapper;
import cn.turing.firecontrol.device.util.Constants;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created on 2019/02/20 11:34
 *
 * @Description
 * @Version V1.0
 */
@Service
public class DeviceVideoAnalysisSolutionBiz extends BusinessBiz<DeviceVideoAnalysisSolutionMapper, DeviceVideoAnalysisSolution> {

    @Autowired
    private DeviceVideoAnalysisDataBiz deviceVideoAnalysisDataBiz;


    /**
     * 所有的分析方案的分析数量，最后分析时间，方案名称
     * @param tenantId
     * @return
     */
    public List<Map<String,Object>> querySolutionStatus(String tenantId){
        List<DeviceVideoAnalysisSolution> solutions = selectListAll();
        List<Map<String,Object>> list = Lists.newArrayList();
        String index = null;
        String type = null;
        Map<String,Object> data = null;
        for(DeviceVideoAnalysisSolution s : solutions){
            String solutionCode = s.getAnalysisSolutionCode();
            if(Constants.AnalysisSolution.FIRE.getCode().equals(solutionCode)){
                index = Constants.AnalysisSolution.FIRE.getDataEsIndex();
                type = Constants.AnalysisSolution.FIRE.getCode();
            }
            else if(Constants.AnalysisSolution.METER.getCode().equals(solutionCode)){
                index = Constants.AnalysisSolution.METER.getDataEsIndex();
                type = Constants.AnalysisSolution.METER.getCode();
            }
            else {
                continue;
            }
            data = deviceVideoAnalysisDataBiz.countData(index,type,tenantId);
            data.put("name",s.getAnalysisSolutionName());
            list.add(data);
        }
        return list;
    }

    /**
     * 查询所有分析方案及该站点用到的各分析方案的数量
     * @param tenantId
     * @return
     */
    public List<Map<String,Object>> querySolutions(String tenantId){
        return mapper.querySolutionsAndDeviceCount(tenantId);
    }












}
