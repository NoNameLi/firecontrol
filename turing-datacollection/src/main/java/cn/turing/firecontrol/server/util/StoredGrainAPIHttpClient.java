package cn.turing.firecontrol.server.util;


import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author TDS
 */
@Slf4j
public class StoredGrainAPIHttpClient {




    /**
     * 综合接口
     * @param url1
     * @param str
     * @return
     * @throws IOException
     */
    public static  String login(String url1,String str) throws IOException {


        //创建client和post对象
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url1);

        //json形式
        post.addHeader("content-type", "application/json;charset=utf-8");
        post.addHeader("token","Turing2019");

        //json字符串以实体的实行放到post中
        post.setEntity(new StringEntity(str, Charset.forName("utf-8")));
        HttpResponse response = null;

        //获得response对象
        response = client.execute(post);

        if(HttpStatus.SC_OK!=response.getStatusLine().getStatusCode()){
            System.out.println("请求返回不正确");
        }

        String result="";

        //获得字符串形式的结果
        result = EntityUtils.toString(response.getEntity());

       // log.info("返回结果"+result);
        return result;
    }



}
