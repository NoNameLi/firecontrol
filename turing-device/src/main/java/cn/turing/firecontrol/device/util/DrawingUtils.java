package cn.turing.firecontrol.device.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

import cn.turing.firecontrol.common.util.UUIDUtils;
import cn.turing.firecontrol.common.util.UploadUtil;

/**
 * Java 图片生成
 *
 * @author houzhenghai
 * @date 2018年10月19日 下午4:59:35
 */
public class DrawingUtils {

    private static float jPEGcompression = 0.75f;// 图片清晰比率

    /**
     * @Description : 将二维码图片和文字生成到一张图片上
     * @Param : originalImg 原图
     * @Param : qrCodeImg 二维码地址
     * @Param : shareDesc 图片文字
     * @return : java.lang.String
     * @Author : houzhenghai
     * @Date : 2018/8/15
     */
    public static void generateImg(List<Map<String,Object>> qrCodeImgList) throws Exception {
        // 加载原图图片
        BufferedImage imageLocal = ImageIO.read(new URL("http://pcrr9nemc.bkt.clouddn.com/timg1.jpg"));
        // 以原图片为模板
        Graphics2D g = imageLocal.createGraphics();
        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        g.setComposite(ac);
        g.setBackground(Color.WHITE);
        for(int i =0;i<qrCodeImgList.size();i++){
            // 加载用户的二维码
            BufferedImage imageCode = ImageIO.read(new URL(qrCodeImgList.get(i).get("qrCodeImg").toString()));
            // 设置文本样式
            g.setFont(new Font("微软雅黑", Font.PLAIN, 15));
            g.setColor(Color.black);
            if(i<3){
                // 在模板上添加用户二维码(地址,左边距,上边距,图片宽度,图片高度,未知)
                g.drawImage(imageCode, 100+(imageCode.getWidth()+120)*i, 100, 160, 158, null);
                // 计算文字长度，计算居中的x点坐标
                g.drawString(qrCodeImgList.get(i).get("bName").toString(), 100+(imageCode.getWidth()+120)*i, 90);
                String facilitiesNo =qrCodeImgList.get(i).get("facilitiesNo").toString();
                System.out.println(facilitiesNo.length());
                String equipmentType =qrCodeImgList.get(i).get("equipmentType").toString();
                String positionDescription =qrCodeImgList.get(i).get("positionDescription").toString();
                g.drawString(facilitiesNo, imageCode.getWidth()*(i+1)+(facilitiesNo.length()+100)*i+10, 110);
                g.drawString(equipmentType, imageCode.getWidth()*(i+1)+(facilitiesNo.length()+100)*i+10, 130);
                g.drawString(positionDescription, imageCode.getWidth()*(i+1)+(facilitiesNo.length()+100)*i+10, 150);
            }
            if(i>=3 && i<6){
                // 在模板上添加用户二维码(地址,左边距,上边距,图片宽度,图片高度,未知)
                g.drawImage(imageCode, 100+(imageCode.getWidth()+120)*(i-3), imageCode.getHeight()+60, 160, 158, null);
                // 计算文字长度，计算居中的x点坐标
                g.drawString(qrCodeImgList.get(i).get("bName").toString(), 100+(imageCode.getWidth()+120)*(i-3), imageCode.getHeight()+50);
                String facilitiesNo =qrCodeImgList.get(i).get("facilitiesNo").toString();
                System.out.println(facilitiesNo.length());
                String equipmentType =qrCodeImgList.get(i).get("equipmentType").toString();
                String positionDescription =qrCodeImgList.get(i).get("positionDescription").toString();
                g.drawString(facilitiesNo, imageCode.getWidth()*(i-2)+(facilitiesNo.length()+100)*(i-3)+10, 80+imageCode.getHeight());
                g.drawString(equipmentType, imageCode.getWidth()*(i-2)+(facilitiesNo.length()+100)*(i-3)+10, 100+imageCode.getHeight());
                g.drawString(positionDescription, imageCode.getWidth()*(i-2)+(facilitiesNo.length()+100)*(i-3)+10, 120+imageCode.getHeight());
            }
            if(i>=6){
                // 在模板上添加用户二维码(地址,左边距,上边距,图片宽度,图片高度,未知)
                g.drawImage(imageCode, 100+(imageCode.getWidth()+120)*(i-6), imageCode.getHeight()*2+20, 160, 158, null);
                // 计算文字长度，计算居中的x点坐标
                g.drawString(qrCodeImgList.get(i).get("bName").toString(), 100+(imageCode.getWidth()+120)*(i-6), imageCode.getHeight()*2+10);
                String facilitiesNo =qrCodeImgList.get(i).get("facilitiesNo").toString();
                System.out.println(facilitiesNo.length());
                String equipmentType =qrCodeImgList.get(i).get("equipmentType").toString();
                String positionDescription =qrCodeImgList.get(i).get("positionDescription").toString();
                g.drawString(facilitiesNo, imageCode.getWidth()*(i-5)+(facilitiesNo.length()+100)*(i-6)+10, 40+imageCode.getHeight()*2);
                g.drawString(equipmentType, imageCode.getWidth()*(i-5)+(facilitiesNo.length()+100)*(i-6)+10, 60+imageCode.getHeight()*2);
                g.drawString(positionDescription, imageCode.getWidth()*(i-5)+(facilitiesNo.length()+100)*(i-6)+10, 80+imageCode.getHeight()*2);
            }
        }
//        // 设置文本样式
//        g.setFont(new Font("微软雅黑", Font.PLAIN + Font.BOLD, 16));
//        g.setColor(Color.WHITE);
//        // 计算文字长度，计算居中的x点坐标
//        String caewm = "长按二维码";
//        g.drawString(caewm, 105, imageLocal.getHeight() - 10);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        saveAsJPEG(imageLocal, out);
        out.close();
        //Map map = UploadUtil.simpleupload(out.toByteArray(), UUIDUtils.generateUuid()+".jpg");
        //return map.get("url").toString();
        File file = new File("F:\\"+UUIDUtils.generateUuid()+".jpg");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(out.toByteArray());
    }

    /**
     * 以JPEG编码保存图片
     *
     * @param imageToSave
     *            要处理的图像图片
     * @param fos
     *            文件输出流
     * @throws IOException
     */
    private static void saveAsJPEG(BufferedImage imageToSave, ByteArrayOutputStream fos) throws IOException {
        ImageWriter imageWriter = ImageIO.getImageWritersBySuffix("jpg").next();
        ImageOutputStream ios = ImageIO.createImageOutputStream(fos);
        imageWriter.setOutput(ios);
        if (jPEGcompression >= 0 && jPEGcompression <= 1f) {
            // new Compression
            JPEGImageWriteParam jpegParams = (JPEGImageWriteParam) imageWriter.getDefaultWriteParam();
            jpegParams.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT);
            jpegParams.setCompressionQuality(jPEGcompression);

        }
        // new Write and clean up
        ImageIO.setUseCache(false);
        imageWriter.write(new IIOImage(imageToSave, null, null));
        ios.close();
        imageWriter.dispose();
    }

    /**
     * 图片流远程上传
     *
     * @author houzhenghai
     * @date 2018年10月19日 下午5:07:24
     * @param inStream
     * @return
     * @throws Exception
     */
    //   private static String urlImgDownInputStream(InputStream inStream) throws Exception {
//        String md5 = MD5Utils.MD5(TimeUtils.getTimestamp().toString());
//        HttpClient httpclient = new DefaultHttpClient();
//        try {
//            HttpPost post = new HttpPost(IMG_UPLOAD_PATH);// 文件服务器上传图片地址
//            MultipartEntity mpEntity = new MultipartEntity();
//            ContentBody contb = new InputStreamBody(inStream, md5 + ".png");
//            mpEntity.addPart("Filedata", contb);
//            post.setEntity(mpEntity);
//            HttpResponse httpResponse = httpclient.execute(post);
//            HttpEntity entity = httpResponse.getEntity();
//            String jsonStr = EntityUtils.toString(entity);
//            JSONObject ob = JSONObject.parseObject(jsonStr);
//            if (!ob.isEmpty() && ob.containsKey("pic_id")) {
//                return ob.getString("pic_id");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            httpclient.getConnectionManager().shutdown();
//        }
//        return null;
//    }

    /**
     * test
     *
     * @param args
     * @throws
     */
    public static void main(String[] args) {
        long starttime = System.currentTimeMillis();
        System.out.println("开始：" + starttime);
        try {
            //String originalImg = "http://pcrr9nemc.bkt.clouddn.com/timg1.jpg";
            String qrCodeImg = "http://pcrr9nemc.bkt.clouddn.com/1542960547.png";
            //String shareDesc = "222222222222222222";
            List list = new ArrayList();
            Map map = new HashMap();
            map.put("qrCodeImg",qrCodeImg);
            map.put("bName","未来之光7栋1单元");
            map.put("facilitiesNo","编号：N123456789");
            map.put("equipmentType","类型：灭火器");
            map.put("positionDescription","位置：1F-大厅东侧");
            list.add(map);
            Map map1 = new HashMap();
            map1.put("qrCodeImg",qrCodeImg);
            map1.put("bName","未来之光7栋1单元");
            map1.put("facilitiesNo","编号：N123654741236985");
            map1.put("equipmentType","类型：灭火器");
            map1.put("positionDescription","位置：1F-大厅东侧");
            list.add(map1);
            Map map2 = new HashMap();
            map2.put("qrCodeImg",qrCodeImg);
            map2.put("bName","未来之光7栋1单元");
            map2.put("facilitiesNo","编号：N123654741236985");
            map2.put("equipmentType","类型：灭火器");
            map2.put("positionDescription","位置：1F-大厅东侧");
            list.add(map2);
            Map map3 = new HashMap();
            map3.put("qrCodeImg",qrCodeImg);
            map3.put("bName","未来之光7栋1单元");
            map3.put("facilitiesNo","编号：N123654741236985");
            map3.put("equipmentType","类型：灭火器");
            map3.put("positionDescription","位置：1F-大厅东侧");
            list.add(map3);
            Map map4 = new HashMap();
            map4.put("qrCodeImg",qrCodeImg);
            map4.put("bName","未来之光7栋1单元");
            map4.put("facilitiesNo","编号：N123654741236985");
            map4.put("equipmentType","类型：灭火器");
            map4.put("positionDescription","位置：1F-大厅东侧");
            list.add(map4);
            Map map5 = new HashMap();
            map5.put("qrCodeImg",qrCodeImg);
            map5.put("bName","未来之光7栋1单元");
            map5.put("facilitiesNo","编号：N123654741236985");
            map5.put("equipmentType","类型：灭火器");
            map5.put("positionDescription","位置：1F-大厅东侧");
            list.add(map5);
            Map map6 = new HashMap();
            map6.put("qrCodeImg",qrCodeImg);
            map6.put("bName","未来之光7栋1单元");
            map6.put("facilitiesNo","编号：N123654741236985");
            map6.put("equipmentType","类型：灭火器");
            map6.put("positionDescription","位置：1F-大厅东侧");
            list.add(map6);
            Map map7 = new HashMap();
            map7.put("qrCodeImg",qrCodeImg);
            map7.put("bName","未来之光7栋1单元");
            map7.put("facilitiesNo","编号：N123654741236985");
            map7.put("equipmentType","类型：灭火器");
            map7.put("positionDescription","位置：1F-大厅东侧");
            list.add(map7);
            Map map8 = new HashMap();
            map8.put("qrCodeImg",qrCodeImg);
            map8.put("bName","未来之光7栋1单元");
            map8.put("facilitiesNo","编号：N123654741236985");
            map8.put("equipmentType","类型：灭火器");
            map8.put("positionDescription","位置：1F-大厅东侧");
            list.add(map8);
            //String img = generateImg(list);
            generateImg(list);
            //System.out.println("生成完毕,url=" + img);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("结束：" + (System.currentTimeMillis() - starttime) / 1000);
    }

}
