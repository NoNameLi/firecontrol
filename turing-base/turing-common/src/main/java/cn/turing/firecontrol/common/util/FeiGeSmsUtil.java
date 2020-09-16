package cn.turing.firecontrol.common.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * Hello world!
 *
 */
public class FeiGeSmsUtil {
    //接口地址
    private static final String requestUrl = "http://api.feige.ee/SmsService/Template";
    //账号
    private String account;
    //密钥
    private String pwd;
    //短信签名
    private String signId;

    public FeiGeSmsUtil(String account, String pwd, String signId){
        this.account = account;
        this.pwd = pwd;
        this.signId = signId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getSignId() {
        return signId;
    }

    public void setSignId(String signId) {
        this.signId = signId;
    }

    /**
     * 发送电气火灾短信通知
     * @param templateId 短信模板
     * @param phones     电话号码
     * @param param      参数
     */
    public void sendMsg(String templateId,String[] phones,AlarmParam param) {
        sendMsg(templateId,phones,param.toParam());
    }

    /**
     * 发送视频监控报警通知
     * @param templateId 短信模板
     * @param phones 电话号码
     * @param deviceName 参数
     */
    public void sendMsg(String templateId,String[] phones,String deviceName){
        String[] params = {deviceName};
        sendMsg(templateId,phones,params);
    }


    public void sendMsg(String templateId,String[] phones,String[] params) {
        StringBuffer phonesStringBuffer = new StringBuffer();
        StringBuffer contentStringBuffer = new StringBuffer();
        for(int i=0;i<phones.length;i++){
            if(i==phones.length-1){
                phonesStringBuffer.append(phones[i]);
            }else {
                phonesStringBuffer.append(phones[i]+",");
            }
        }
        for(int i=0;i<params.length;i++){
            if(i==params.length-1){
                contentStringBuffer.append(params[i]);
            }else {
                contentStringBuffer.append(params[i]+"||");
            }
        }
        try {
            List<NameValuePair> formparams = new ArrayList<NameValuePair>();
            formparams.add(new BasicNameValuePair("Account",account));
            formparams.add(new BasicNameValuePair("Pwd",pwd));
            formparams.add(new BasicNameValuePair("Content",contentStringBuffer.toString()));
            formparams.add(new BasicNameValuePair("Mobile",phonesStringBuffer.toString()));
            formparams.add(new BasicNameValuePair("templateId",templateId));
            formparams.add(new BasicNameValuePair("signId",signId));
            Post(formparams);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }




    public static void Post( List<NameValuePair> formparams) throws Exception {
        CloseableHttpAsyncClient httpClient = HttpAsyncClients.createDefault();

        httpClient.start();

        HttpPost requestPost=new HttpPost(requestUrl);

        requestPost.setEntity(new UrlEncodedFormEntity(formparams,"utf-8"));

        httpClient.execute(requestPost, new FutureCallback<HttpResponse>() {
            public void failed(Exception arg0) {

                System.out.println("Exception: " + arg0.getMessage());
            }
            public void completed(HttpResponse arg0) {
                InputStream stram= null;
                BufferedReader reader = null;
                System.out.println("Response: " + arg0.getStatusLine());
                try {
                    stram= arg0.getEntity().getContent();
                    reader = new BufferedReader(new InputStreamReader(stram));
                    System.out.println(	reader.readLine());
                } catch (UnsupportedOperationException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    try {
                        stram.close();
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            public void cancelled() {
                // TODO Auto-generated method stub
            }
        }).get();
        System.out.println("Done");
        httpClient.close();
    }

    /**
     * 报警通知模板所需要的参数
     */
    @Data
    @AllArgsConstructor
    public class AlarmParam{
        //建筑名
        private String buildingName;
        //设备系列名
        private String deviceSeriesName;
        //设备状态
        private String status;
        public String[] toParam(){
            return new String[]{buildingName,deviceSeriesName,status};
        }
    }



    public static void main(String[] args){
        String[] phones = new String[]{"13476008959"};
        FeiGeSmsUtil smsUtil = new FeiGeSmsUtil("fe33063","572a813626be4d8fc9231a544","57252");
//        AlarmParam alarmParam = smsUtil.new AlarmParam("未来之光7栋","鑫豪斯三相","报警");
//        smsUtil.sendMsg("42473",phones,alarmParam);
        String deviceName = "测试设备";
        smsUtil.sendMsg("55145",phones,deviceName);
    }
}
