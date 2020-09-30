package cn.turing.firecontrol.device.config;

import com.alibaba.fastjson.*;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import java.io.*;
import java.util.ArrayList;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class ApplicationStartListener implements CommandLineRunner {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            init();
            log.info("完成初始化加载省市区数据");
        }catch(Exception e){
            log.error("初始化加载省市区数据失败,请检查auth服务是否正常启动,1分钟后自动重试!",e);
        }
    }
    private void init(){
          try {
              // 读取son文件
              String filePath = "pca.json";//类路径，编译后classes目录下
              Resource resource = new ClassPathResource(filePath);
              System.out.println("------------"+resource.getURI());
              InputStream is = ApplicationStartListener.class.getClassLoader().getResourceAsStream(filePath);

              String content = IOUtils.toString(is, "utf-8");

//              String content = FileUtils.readFileToString(file);
              //System.out.println(content);
              JSONObject jsonObject = JSONObject.parseObject(content);
//              System.out.println(jsonObject.get("86"));
              JSONObject jsonObject1 = JSONObject.parseObject(jsonObject.get("86").toString());//省
//              //判断是否已存在
//              Object object1 = redisTemplate.opsForHash().get("Area","86");
//              if(object1==null){
                  //不存在，添加
                  redisTemplate.opsForHash().put("Area","86",jsonObject1.toString());
                  redisTemplate.persist("Area");
//              }
              Set<String> s=jsonObject1.keySet();
              ArrayList<String> list = new ArrayList<String>();//省key
              for (String string : s) {
                  //System.out.println(jsonObject1.get(string));
                  list.add(string);
              }
              ArrayList<String> list1 = new ArrayList<String>();//市key
              for(String c:list){
                  //System.out.println(c);
                  //System.out.println(jsonObject.get(c));
                  if(!jsonObject.containsKey(c)){
                      continue;
                  }
                  JSONObject jsonObject2 = JSONObject.parseObject(jsonObject.get(c).toString());//市
                  //判断是否已存在
//                  Object object2 = redisTemplate.opsForHash().get("Area",c);
//                  if(object2==null){
                      //不存在，添加
                      redisTemplate.opsForHash().put("Area",c,jsonObject2.toString());
                      redisTemplate.persist("Area");
//                  }
                  Set<String> s1=jsonObject2.keySet();
                  ArrayList<String> list2 = new ArrayList<String>();//区key
                  for (String string : s1) {
//                      System.out.println(jsonObject2.get(string));
                      list2.add(string);
                  }
                  for(String a:list2){
                      if(!jsonObject.containsKey(a)){
                          continue;
                      }
                      JSONObject jsonObject3 = JSONObject.parseObject(jsonObject.get(a).toString());//区
//                      System.out.println(jsonObject3.toString());
                      //判断是否已存在
//                      Object object3 = redisTemplate.opsForHash().get("Area",a);
//                      if(object3==null){
                          //不存在，添加
                          redisTemplate.opsForHash().put("Area",a,jsonObject3.toString());
                          redisTemplate.persist("Area");
//                      }
                  }
              }
          } catch (Exception e) {
              e.printStackTrace();
          }
    }
}
