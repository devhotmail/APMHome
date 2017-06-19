/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.Random;
//import java.util.UUID;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

/**
 *
 * @author 212579464
 */
@Component
public class QRCodeUtil {

    private static final String CHARSET = "utf-8";
    private static final String FORMAT_NAME = "JPG";
    // 二维码尺寸
    private static final int QRCODE_SIZE = 300;
    // LOGO宽度
    private static final int WIDTH = 78;
    // LOGO高度
    private static final int HEIGHT = 78;
    // LOGO路径
    private static final String LOGO_PATH = QRCodeUtil.class.getResource("/").getFile().concat("logo.jpg");
    private static final String BACKGROUND_PATH = QRCodeUtil.class.getResource("/").getFile().concat("background.png");

    private static String suscribeLink;

//    private static QRCodeUtil qrCodeUtil;
//    private static String suscribeLink1;

    String wxSuscribeLink;

    static {
        Properties properties = new Properties();
        try {
            properties.load(QRCodeUtil.class.getResourceAsStream("/wx.properties"));
            suscribeLink = properties.getProperty("suscribeLink");
        } catch (IOException ex) {
            Logger.getLogger(QRCodeUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    private static BufferedImage createImage(String content, String imgPath, boolean needCompress) throws Exception {
        Map<EncodeHintType, Object> hints = new HashMap();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE,
                hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        if (imgPath == null || "".equals(imgPath)) {
            return image;
        }
        // 插入图片
        QRCodeUtil.insertImage(image, imgPath, needCompress);
        return image;
    }

    private static void insertImage(BufferedImage source, String imgPath, boolean needCompress) throws Exception {
        File file = new File(imgPath);
        if (!file.exists()) {
            System.err.println("" + imgPath + "   该文件不存在！");
            return;
        }
        Image src = ImageIO.read(new File(imgPath));
        int width = src.getWidth(null);
        int height = src.getHeight(null);
        if (needCompress) { // 压缩LOGO
            if (width > WIDTH) {
                width = WIDTH;
            }
            if (height > HEIGHT) {
                height = HEIGHT;
            }
            Image image = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            g.drawImage(image, 0, 0, null); // 绘制缩小后的图
            g.dispose();
            src = image;
        }
        // 插入LOGO
        Graphics2D graph = source.createGraphics();
        int x = (QRCODE_SIZE - width) / 2;
        int y = (QRCODE_SIZE - height) / 2;
        graph.drawImage(src, x, y, width, height, null);
//        Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
        graph.setStroke(new BasicStroke(3f));
//        graph.draw(shape);
        graph.dispose();
    }

    public static void encode(String content, String imgPath, String destPath, boolean needCompress) throws Exception {
        BufferedImage image = QRCodeUtil.createImage(content, imgPath, needCompress);
        mkdirs(destPath);
        String file = new Random().nextInt(99999999) + ".jpg";
        ImageIO.write(image, FORMAT_NAME, new File(destPath + "/" + file));
    }

    public static String getQRCodeImageBase64(String code) {
        return getQRCodeImageBase64(code, QRCODE_SIZE);
    }

    public static String getQRCodeImageBase64(String code, int size) {
        Base64 encoder = new Base64();
        String res = "data:image/jpeg;base64,";
        try (ByteArrayOutputStream byteout = new ByteArrayOutputStream()) {
            QRCodeUtil.encodeWithBackground(code, LOGO_PATH, byteout, true, size);
            try (ByteArrayInputStream bytein = new ByteArrayInputStream(byteout.toByteArray())) {
                byte[] bytes = new byte[bytein.available()];
                bytein.read(bytes);
                res += encoder.encodeToString(bytes);
            }
        } catch (IOException ex) {
            Logger.getLogger(QRCodeUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(QRCodeUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    public static void mkdirs(String destPath) {
        File file = new File(destPath);
        // 当文件夹不存在时，mkdirs会自动创建多层目录，区别于mkdir．(mkdir如果父目录不存在则会抛出异常)
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
    }

    public static void encode(String content, String imgPath, String destPath) throws Exception {
        QRCodeUtil.encode(content, imgPath, destPath, false);
    }

    public static void encode(String content, String destPath, boolean needCompress) throws Exception {
        QRCodeUtil.encode(content, null, destPath, needCompress);
    }

    public static void encode(String content, String destPath) throws Exception {
        QRCodeUtil.encode(content, null, destPath, false);
    }

    public static void encode(String content, String imgPath, OutputStream output, boolean needCompress)
            throws Exception {
        BufferedImage image = QRCodeUtil.createImage(content, imgPath, needCompress);
        ImageIO.write(image, FORMAT_NAME, output);
    }

    public static void encodeWithBackground(String content, String imgPath, OutputStream output, boolean needCompress, int size)
            throws Exception {
        BufferedImage image = QRCodeUtil.createImage(suscribeLink.concat("?").concat(content), imgPath, needCompress);
        BufferedImage outimg = addBackground(BACKGROUND_PATH, image, size, content);
        ImageIO.write(outimg, FORMAT_NAME, output);
    }

    public static void encode(String content, OutputStream output) throws Exception {
        QRCodeUtil.encode(content, null, output, false);
    }

    public static void main(String[] args) throws Exception {

        String logoFile = "c:/Temp/logo.jpg";
        String backGroundPath = "c:/Temp/background_nin.png";
        String prefix = "http://weixin.qq.com/r/nCnx6dbEDItzrZKK93yO";
        String text = "1703307223655922";
//		QRCodeUtil.encode(text, logoFile, "c:/Temp", true);

        //    批量生成
        String srcFolder = "c:/Temp/170615";
        String[] files = new File(srcFolder).list();
        for (String item : files) {
            System.out.println(item);
            qrCodeBatchJob(srcFolder + File.separator + item, null, backGroundPath, prefix);
        }

//        BufferedImage image = QRCodeUtil.createImage(prefix + "1703307223655922", logoFile, true);
//        BufferedImage img =addBackground(backGroundPath,image,472,"1703307223655922");
//        ImageIO.write(img, FORMAT_NAME, new File( "c:/Temp/" + text + ".jpg"));
//        
//        
    }

//    private static BufferedImage addBackground(String bgPath, BufferedImage innerImg, int size) throws IOException {
//
//        int sizeOfBackGround = size;
//        int sizeOfCode = 370 * size / 472;
//        int margin = sizeOfCode / 13;
//
//        BufferedImage src = ImageIO.read(new File(bgPath));
//        Image background = src.getScaledInstance(sizeOfBackGround, sizeOfBackGround, Image.SCALE_SMOOTH);
//        Image resizedCode = innerImg.getScaledInstance(sizeOfCode, sizeOfCode, Image.SCALE_SMOOTH);
//
//        BufferedImage bufImg = new BufferedImage(sizeOfBackGround, sizeOfBackGround, BufferedImage.TYPE_INT_RGB);
//        Graphics g = bufImg.createGraphics();
//        g.drawImage(background, 0, 0, null);
//
//        int x = (sizeOfBackGround - sizeOfCode) / 2 + margin;
//        int y = (sizeOfBackGround - sizeOfCode) / 2 + margin + 3 * margin / 4;
//        g.drawImage(resizedCode, x, y, x + sizeOfCode - 2 * margin, y + sizeOfCode - 2 * margin, margin, margin, sizeOfCode - margin, sizeOfCode - margin, null);
//        g.dispose();
//
//        return bufImg;
//
//    }

    private static BufferedImage addBackground(String bgPath, BufferedImage innerImg, int size, String text) throws IOException {

        StringBuffer textSB = new StringBuffer(text);
        textSB.insert(4, "-");
        textSB.insert(9, "-");
        textSB.insert(14, "-");
        
        int sizeOfBackGround = size;
        int sizeOfCode = 320 * size / 472;
        int margin = sizeOfCode / 24;
        int textX = 85 * size / 472;
//        int textX =  (size/2-65)*size / 472;
        
        int textY = 425 * size / 472;

        BufferedImage src = ImageIO.read(new File(bgPath));
        Image background = src.getScaledInstance(sizeOfBackGround, sizeOfBackGround, Image.SCALE_SMOOTH);
        Image resizedCode = innerImg.getScaledInstance(sizeOfCode, sizeOfCode, Image.SCALE_SMOOTH);

        BufferedImage bufImg = new BufferedImage(sizeOfBackGround, sizeOfBackGround, BufferedImage.TYPE_INT_RGB);
        Graphics g = bufImg.createGraphics();
        g.drawImage(background, 0, 0, null);

        int x = (sizeOfBackGround - sizeOfCode) / 2 + margin;
        int y = (sizeOfBackGround - sizeOfCode) / 2 + margin + 4 * margin / 4;

        int fontSize = 30 * size / 472;
        Font font = new Font("TimesRoman", Font.BOLD, fontSize);
        g.setFont(font);
        g.setColor(Color.BLACK);
        g.drawChars(textSB.toString().toCharArray(), 0, textSB.length(), textX, textY);
        
        g.drawImage(resizedCode, x, y, x + sizeOfCode - 2 * margin, y + sizeOfCode - 2 * margin, margin, margin, sizeOfCode - margin, sizeOfCode - margin, null);
        g.dispose();

        return bufImg;

    }

    public static void qrCodeBatchJob(String srcfile, String logoFile, String backGroundPath, String prefix) throws Exception {
        File src = new File(srcfile);
        if (src.isFile() && src.exists()) {
            String outputFolder = src.getAbsolutePath();
            outputFolder = outputFolder.substring(0, outputFolder.indexOf("."));
            mkdirs(outputFolder.concat("472"));
            mkdirs(outputFolder.concat("709"));
            InputStreamReader read = new InputStreamReader(new FileInputStream(src));
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt = null;
            int smallCount = 500;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                System.out.println(lineTxt);
//                String prefix = UUID.randomUUID().toString();
                BufferedImage image = QRCodeUtil.createImage(prefix + "?" + lineTxt, logoFile, true);
                if (smallCount > 0) {
                    BufferedImage imageWithbg = addBackground(backGroundPath, image, 472, lineTxt);
                    ImageIO.write(imageWithbg, FORMAT_NAME, new File(outputFolder + "472/" + lineTxt + ".jpg"));
                } else {
                    BufferedImage imageWithbg790 = addBackground(backGroundPath, image, 709, lineTxt);
                    ImageIO.write(imageWithbg790, FORMAT_NAME, new File(outputFolder + "709/" + lineTxt + ".jpg"));
                }
                smallCount--;
            }
            read.close();
        } else {
            System.out.println("找不到指定的文件");
        }

    }

}
