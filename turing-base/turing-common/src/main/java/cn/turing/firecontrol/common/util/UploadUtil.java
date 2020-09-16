package cn.turing.firecontrol.common.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import com.qiniu.common.Zone;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.persistent.FileRecorder;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;

/**
 * 七牛云文件上传
 *
 */
public class UploadUtil {

    private static Logger logger = LoggerFactory.getLogger(UploadUtil.class);
        // 设置好账号的ACCESS_KEY和SECRET_KEY
        private final static String ACCESS_KEY = "X2jhAsCSqnc6pQQ9T4vArJsGUeqHgXtuQdsv3hq7";
        private final static String SECRET_KEY = "2tmEzlPUvG6oBAx-0CO39wNSKAGMAFpHKDYcQGor";
        // 域名
        private final static String domainOfBucket = "http://file.tmc.turing.ac.cn/";
        // 密匙配置
        private final static Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
        // 创建上传对象
        private final static UploadManager uploadManager = new UploadManager(new Configuration(Zone.huanan()));

        /**
         * 覆盖上传凭证
         * @param bucketname
         *            空间名称
         * @param key
         *            上传到七牛云后保存的文件名
         */
        public String getUpToken(String bucketname, String key) {

            return auth.uploadToken(bucketname, key);

        }

        /**
         * 简单上传凭证
         * @param bucketname
         *            空间名称
         */
        public static String getUpToken(String bucketname) {
            StringMap putPolicy = new StringMap();
            // 自定义上传回复凭证
            putPolicy.put("returnBody",
                    "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"bucket\":\"$(bucket)\",\"fsize\":$(fsize)}");
            long expireSeconds = 3600;
            return auth.uploadToken(bucketname, null, expireSeconds, putPolicy);

        }

        /**
         * 简单上传
         * @param filePath
         *            文件路径 （也可以是字节数组、或者File对象）
         * @param key
         *            上传到七牛云上的文件的名称 （同一个空间下，名称【key】是唯一的）
         * @param bucketName
         *            空间名称 （这里是为了获取上传凭证）
         */
        public static Map simpleupload(String filePath, String key, String bucketName) {
            Map<String, Object> map=new HashMap<String, Object>();
            try {
                String token = getUpToken(bucketName);
                // 调用put方法上传
                Response res = uploadManager.put(filePath, key, token);
                if (res.statusCode == 200 && res.isOK()) {
                    logger.debug("上传成功");
                    map.put("url", domainOfBucket+key);
                    map.put("result", "success");
                }
            } catch (QiniuException e) {
                e.printStackTrace();
                map.put("result", "fail");
            }
            return map;
        }

    /**
     * 上传字节数组
     * @param bytes
     * @param key
     * @return
     */
    public static Map<String,String> simpleupload(byte[] bytes, String key) {
        Map<String, String> map=new HashMap<String, String>();
        String bucketName = "firecontrol";
        try {
            String token = getUpToken(bucketName);
            // 调用put方法上传
            Response res = uploadManager.put(bytes, key, token);
            if (res.statusCode == 200 && res.isOK()) {
                logger.debug("上传成功");
                map.put("url", domainOfBucket+key);
                map.put("result", "success");
            }
        } catch (QiniuException e) {
            e.printStackTrace();
            map.put("result", "fail");
        }
        return map;
    }





        /**
         *  断点续传
         * @throws IOException
         * @param filePath
         *            文件路径 （也可以是字节数组、或者File对象）
         * @param key
         *            上传到七牛上的文件的名称 （同一个空间下，名称【key】是唯一的）
         * @param bucketName
         *            空间名称 （这里是为了获取上传凭证）
         */

        private static Object breakpointUpload(String filePath, String key, String bucketName) throws IOException {
            Map<String, Object> map=new HashMap<String, Object>();
            Configuration cfg = new Configuration(Zone.zone0());
            // 设置凭证
            String bucket = "firecontrol";
            // 设置一个临时文件存放地址
            String localTempDir = Paths.get(System.getenv("java.io.tmpdir"), bucket).toString();
            // 实例化recorder对象
            FileRecorder fileRecorder = new FileRecorder(localTempDir);
            // 实例化上传对象，并且传入一个recorder对象
            UploadManager uploadManager = new UploadManager(cfg,fileRecorder);
            // token值
            String token = getUpToken(bucketName);
            try {
                // 调用put方法上传
                Response res = uploadManager.put(filePath, key, token);
                // 是否上传成功
                if (res.statusCode == 200 && res.isOK()) {
                    logger.debug("上传成功");
                    map.put("url", domainOfBucket+key);
                    map.put("result", "success");
                }
            } catch (QiniuException e) {
                logger.debug("上传失败");
                e.printStackTrace();
                map.put("result", "fail");
            }
            return map;

        }


    /**
     * 删除一个文件
     *
     * @param key
     */
    public static void deleteImg(String key) {
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(Zone.zone0());
        BucketManager bucketManager = new BucketManager(auth,cfg);
        try {
            Response res = bucketManager.delete("firecontrol", key);
            if (res.statusCode == 200 && res.isOK()) {
                logger.debug("删除成功");
            }
        } catch (QiniuException e) {
            //如果遇到异常，说明删除失败
            System.err.println(e.code());
            System.err.println(e.response.toString());
        }
    }



    //测试
        public static void main(String[] args) throws IOException {

            //简单上传
            Object map=simpleupload("C:\\Users\\Administrator\\Desktop\\a.jpg","a.jpg","firecontrol");
            System.out.println(map);
            // 断点续传
//            Object map= breakpointUpload("C:\\Users\\Administrator\\Desktop\\a.jpg",
//                    "a.jpg",
//                    "firecontrol");
//            System.out.println(map);
        }
    }

