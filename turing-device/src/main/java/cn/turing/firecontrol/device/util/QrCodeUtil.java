package cn.turing.firecontrol.device.util;

import cn.turing.firecontrol.common.util.UploadUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author zhangpeng
 * @date 2018-7-9
 */
public class QrCodeUtil {

    /**
     * 创建二维码图片
     *
     * @param content    二维码携带信息
     * @param qrCodeSize 二维码图片大小
     * @param filePath   生成的二维码图片的保存的路径
     */
//    public static void createQrCodeImage(String content, int qrCodeSize, String filePath) {
//        try {
//            BufferedImage bi = createQrCode(content, qrCodeSize);
//            File imgFile = new File(filePath);
//            ImageIO.write(bi, "JPEG", imgFile);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 创建二维码图片
     *
     * @param content    二维码携带信息
     * @param qrCodeSize 二维码图片大小(430*430)
     */
    public static String createQrCodeImage(String content, int qrCodeSize) {
        BufferedImage bi = createQrCode(content, qrCodeSize);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(bi,"jpg", out);
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] b = out.toByteArray();
        Map map = UploadUtil.simpleupload(b,content+".jpg");
        return map.get("url").toString();
    }

    public static BufferedImage createQrCodeBufferedImage(String content, int qrCodeSize) {
        return createQrCode(content, qrCodeSize);
    }

    /**
     * 生成包含字符串信息的二维码图片
     *
     * @param content    二维码携带信息
     * @param qrCodeSize 二维码图片大小
     */
    private static BufferedImage createQrCode(String content, int qrCodeSize) {
        try {
            // 设置二维码纠错级别Map
            Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
            // 矫错级别
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            // 创建比特矩阵(位矩阵)的QR码编码的字符串
            BitMatrix byteMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, qrCodeSize, qrCodeSize, hintMap);
            // 使BufferedImage勾画QRCode  (matrixWidth 是行二维码像素点)
            int matrixWidth = byteMatrix.getWidth();
            int matrixHeight = byteMatrix.getWidth();
            BufferedImage image = new BufferedImage(matrixWidth - 65, matrixWidth - 65, BufferedImage.TYPE_INT_RGB);
            image.createGraphics();
            Graphics2D graphics = (Graphics2D) image.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, matrixWidth, matrixHeight);
            // 使用比特矩阵画并保存图像
            graphics.setColor(Color.BLACK);
            for (int i = 0; i < matrixWidth; i++) {
                for (int j = 0; j < matrixWidth; j++) {
                    if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i - 33, j - 33, 2, 2);
                    }
                }
            }
            return image;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}