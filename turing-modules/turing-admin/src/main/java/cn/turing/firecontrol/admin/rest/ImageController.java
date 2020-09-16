package cn.turing.firecontrol.admin.rest;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.turing.firecontrol.common.exception.base.ParamErrorException;
import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.util.UUIDUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("image")
public class ImageController {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;


	private static Random random = new Random();
	private static int width = 170;// 定义图片的width
	private static int height = 36;// 定义图片的height
	private static int codeCount = 4;// 定义图片上显示验证码的个数
	private static int xx = 30; 
	private static int fontHeight = 25;
	private static  int codeY = 25;
	private static char[] codeSequence = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R',
	        'S', 'T', 'U', 'V', 'W', 'X', 'Y', '3', '4', '5', '6', '7', '8', '9' };

    @RequestMapping("test")
    public String test(String test){
        return test;
    }
    
    /**
     * 生成一个map集合（工具方法）
     * code为生成的验证码
     * codePic为生成的验证码BufferedImage对象
     * @return
     */
    public static Map<String,Object> generateCodeAndPic() {
        // 定义图像buffer
        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics gd = buffImg.getGraphics();
        // 创建一个随机数生成器类
        Random random = new Random();
        // 将图像填充为白色
        gd.setColor(Color.WHITE);
        gd.fillRect(0, 0, width, height);

        // 创建字体，字体的大小应该根据图片的高度来定。
        Font font = new Font("Fixedsys", Font.BOLD, fontHeight);
        // 设置字体。
        gd.setFont(font);
        // 画边框。
//        gd.setColor(Color.BLACK);
        gd.drawRect(0, 0, width - 1, height - 1);

        // 随机产生40条干扰线，使图象中的认证码不易被其它程序探测到。
//          gd.setColor(Color.BLACK);
//          for (int i = 0; i < 30; i++) {
//        	  int x = random.nextInt(width);
//        	  int y = random.nextInt(height);
//        	  int xl = random.nextInt(12);
//    		  int yl = random.nextInt(12);
//        	  gd.drawLine(x, y, x + xl, y + yl);
//        }

        // randomCode用于保存随机产生的验证码，以便用户登录后进行验证。
        StringBuffer randomCode = new StringBuffer();
        int red = 0, green = 0, blue = 0;

        // 随机产生codeCount数字的验证码。
        for (int i = 0; i < codeCount; i++) {
            // 得到随机产生的验证码数字。
            String code = String.valueOf(codeSequence[random.nextInt(codeSequence.length-1)]);
            // 产生随机的颜色分量来构造颜色值，这样输出的每位数字的颜色值都将不同。
            red = random.nextInt(255);
            green = random.nextInt(255);
            blue = random.nextInt(255);
            // 用随机产生的颜色将验证码绘制到图像中。
            gd.setColor(new Color(red, green, blue));
            gd.drawString(code, (i + 1) * xx, codeY);

            //使图片扭曲
//            shear(gd, width, height, gd.getColor());
            // 将产生的四个随机数组合在一起。
            randomCode.append(code);
        }
        Map<String,Object> map  =new HashMap<String,Object>();
        //存放验证码
        map.put("code", randomCode);
        //存放生成的验证码BufferedImage对象
        map.put("codePic", buffImg);
        return map;
    }

    /**
     * 获取图片Code
     * @author Administrator
     *
     */
    @RequestMapping(value = "/getCode",method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("获取图片Code")
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //用MAP生成的验证码和验证码图片
        Map<String, Object> codeMap = generateCodeAndPic();

        // 将四位数字的验证码保存到Session中。
//        HttpSession session = req.getSession();
//        session.setAttribute("code", codeMap.get("code").toString());
        Cookie cookie = new Cookie("image",UUIDUtils.generateUuid());
        resp.addCookie(cookie);
        String key = "valicode:image:" + cookie.getValue();
        String code = codeMap.get("code").toString();
        redisTemplate.opsForValue().set(key,code,5*60*1000L, TimeUnit.MILLISECONDS);

        // 禁止图像缓存。
        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setDateHeader("Expires", -1);

        resp.setContentType("image/jpeg");

        // 将图像输出到Servlet输出流中。
        ServletOutputStream sos;
        try {
            sos = resp.getOutputStream();
            ImageIO.write((RenderedImage) codeMap.get("codePic"), "jpeg", sos);
            sos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    //干扰
    private static void shear(Graphics g, int w1, int h1, Color color) {
        shearX(g, w1, h1, color);
        shearY(g, w1, h1, color);
    }
     
    private static void shearX(Graphics g, int w1, int h1, Color color) {
        int period = random.nextInt(2);
        boolean borderGap = true;
        int frames = 1;
        int phase = random.nextInt(2);
        for (int i = 0; i < h1; i++) {
            double d = (double) (period >> 1)
                    * Math.sin((double) i / (double) period
                            + (6.2831853071795862D * (double) phase)
                            / (double) frames);
            g.copyArea(0, i, w1, 1, (int) d, 0);
            if (borderGap) {
                g.setColor(color);
                g.drawLine((int) d, i, 0, i);
                g.drawLine((int) d + w1, i, w1, i);
            }
        }
    }
 
    private static void shearY(Graphics g, int w1, int h1, Color color) {
        int period = random.nextInt(40) + 10; // 50;
        boolean borderGap = true;
        int frames = 20;
        int phase = 7;
        for (int i = 0; i < w1; i++) {
            double d = (double) (period >> 1)
                    * Math.sin((double) i / (double) period
                            + (6.2831853071795862D * (double) phase)
                            / (double) frames);
            g.copyArea(i, 0, 1, 10, 0, (int) d);
            g.copyArea(i, 0, 1, h1, 0, (int) d);
            if (borderGap) {
                g.setColor(color);
                g.drawLine(i, (int) d, i, 0);
                g.drawLine(i, (int) d + h1, i, h1);
            }
        }
    }
    
    /**
     * 验证Code
     * @param request
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "/checkCode",method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("获取图片Code")
    public ObjectRestResponse checkCode(HttpServletRequest request, @RequestParam String code,@CookieValue("image")String image)
            throws ServletException, IOException {
        ObjectRestResponse responseResult =  new ObjectRestResponse();
        // 验证验证码
        String sessionCode = redisTemplate.opsForValue().get("valicode:image:" + image);
        if (code != null && !"".equals(code) && sessionCode != null && !"".equals(sessionCode)) {
            if (!code.equalsIgnoreCase(sessionCode)) {
                throw  new ParamErrorException("验证码错误");
            }
        } else {
            throw  new ParamErrorException("参数错误");
        }
        return responseResult;
    }

}


