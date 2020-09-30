package cn.turing.firecontrol.server.util;


import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * 调齐心花园的接口
 */
@Slf4j
public class APIHttpClient {


    public  static  void main(String [] args){
        Random random=new Random();
        for (int i=0;i<=20;i++){
            Integer integer= random.nextInt(2);
            System.out.println(integer+":q");
        }

           //  saveData();

    }
    public static  String saveData(String url1,String str) throws IOException {

        String url = url1;
        String param = str;
        //创建client和post对象
        CloseableHttpClient client = HttpClients.createDefault();

        HttpPost post = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(3000).setConnectionRequestTimeout(3000)
                .setSocketTimeout(3000).build();
        //json形式
        post.addHeader("content-type", "application/json;charset=utf-8");
        post.addHeader("accept","application/json");
        post.setConfig(requestConfig);
        //json字符串以实体的实行放到post中
        post.setEntity(new StringEntity(param, Charset.forName("utf-8")));

        HttpResponse response = null;

        //获得response对象
        response = client.execute(post);



        if(HttpStatus.SC_OK!=response.getStatusLine().getStatusCode()){
            System.out.println("请求返回不正确");
        }

        String result="";

        //获得字符串形式的结果
        result = EntityUtils.toString(response.getEntity());

        log.info("返回结果"+result);
        return result;
    }

    public static  String saveData(String url1,String str,String uuid,String body){
        String  appkey="420115QXHY";
        String  appsec="QXHY123456";
        Date dNow = new Date();
        SimpleDateFormat ft =
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

        String url = url1;
        String param = str;
        //创建client和post对象
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        //json形式
        post.addHeader("content-type", "application/json;charset=utf-8");
        post.addHeader("accept","application/json");
        post.addHeader("XCOMM-GUID",uuid);
        post.addHeader("XCOMM-HASH",DigestUtils.md5DigestAsHex((body+appsec).getBytes()));
        post.addHeader("XCOMM-DISTRICTCODE",appkey);
        post.addHeader("XCOMM-SENTAT", ft.format(dNow));
        //json字符串以实体的实行放到post中
        post.setEntity(new StringEntity(param, Charset.forName("utf-8")));
        HttpResponse response = null;
        try {
            //获得response对象
            response = client.execute(post);
            log.info("获得response对象");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(HttpStatus.SC_OK!=response.getStatusLine().getStatusCode()){
            System.out.println("请求返回不正确");
        }

        String result="";
        try {
            //获得字符串形式的结果
            result = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("返回结果"+result);
        return result;
    }

    public static  String saveDatav2(String url1,String str) throws IOException {

        String url = url1;
        String param = str;
        //创建client和post对象
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        //json形式
        post.addHeader("content-type", "application/json;charset=utf-8");
        post.addHeader("accept","application/json");

        //json字符串以实体的实行放到post中
        post.setEntity(new StringEntity(param, Charset.forName("utf-8")));
        HttpResponse response = null;

        //获得response对象
        response = client.execute(post);



        if(HttpStatus.SC_OK!=response.getStatusLine().getStatusCode()){
            System.out.println("请求返回不正确");
        }

        String result="";

        //获得字符串形式的结果
        result = EntityUtils.toString(response.getEntity());

        log.info("返回结果"+result);
        return result;
    }

    /**
     *
     * @param url1
     * @param str
     * @return
     * @throws IOException
     */
    public static  String gainToken(String url1,String str) throws IOException {
        String url = url1;
        String param = str;
        //创建client和post对象
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        //json形式
        post.addHeader("content-type", "application/json;charset=utf-8");
        post.addHeader("accept","application/json");
        //json字符串以实体的实行放到post中
        post.setEntity(new StringEntity(param, Charset.forName("utf-8")));
        HttpResponse response = null;
        //获得response对象
        response = client.execute(post);
        if(HttpStatus.SC_OK!=response.getStatusLine().getStatusCode()){
            System.out.println("请求返回不正确");
        }

        String result="";

        //获得字符串形式的结果
        result = EntityUtils.toString(response.getEntity());

        log.info("返回结果"+result);
        return result;
    }


    /**
     * 推送消火栓压力设备信息！
     * @param url1
     * @param str
     * @return
     * @throws IOException
     */
    public static  String pushTopSail(String url1,String str,String token) throws IOException {

        String url = url1;
        String param = str;
        //创建client和post对象
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        //json形式
        post.addHeader("content-type", "application/json;charset=utf-8");
        post.addHeader("accept","application/json");
        post.addHeader("Authorization",token);

        //json字符串以实体的实行放到post中
        post.setEntity(new StringEntity(param, Charset.forName("utf-8")));
        HttpResponse response = null;

        //获得response对象
        response = client.execute(post);



        if(HttpStatus.SC_OK!=response.getStatusLine().getStatusCode()){
            System.out.println("请求返回不正确");
        }

        String result="";

        //获得字符串形式的结果
        result = EntityUtils.toString(response.getEntity());

        log.info("返回结果"+result);
        return result;
    }

}
