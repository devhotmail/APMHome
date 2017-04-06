/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.utils;

import com.ge.apm.service.utils.audio.AudioAttributes;
import com.ge.apm.service.utils.audio.Encoder;
import com.ge.apm.service.utils.audio.EncoderException;
import com.ge.apm.service.utils.audio.EncodingAttributes;
import com.ge.apm.service.utils.audio.InputFormatException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author 212579464
 */
public class AmrAudioUtil {
    public static String changeAmrToMp3Base64(InputStream ins) throws FileNotFoundException, IOException {
        File temp = new File(System.getProperty("java.io.tmpdir"), "apm");
        if (!temp.exists()) {
            temp.mkdirs();
            temp.deleteOnExit();
        }
        File srcFile = new File(temp.getAbsolutePath().concat(File.separator).concat(UUID.randomUUID().toString().concat(".amr")));
        try (FileOutputStream outStream = new FileOutputStream(srcFile)) {
            byte[] b = new byte[1024];
            while ((ins.read(b)) != -1) {
                outStream.write(b);
            }
            ins.close();
            outStream.flush();
        }
        String outPutFile = srcFile.getAbsolutePath().replace(".amr", ".mp3");
        changeToMp3(srcFile.getAbsolutePath(), outPutFile);

        Base64 encoder;
        byte[] bytes;
        try (InputStream resIs = new FileInputStream(outPutFile)) {
            encoder = new Base64();
            bytes = new byte[resIs.available()];
            resIs.read(bytes);
        }
        File outFile = new File(outPutFile);
        String res =  encoder.encodeToString(bytes);
        outFile.delete();
        srcFile.delete();
        return res;
    }

    public static void changeToMp3(String sourcePath, String targetPath) {
        File source = new File(sourcePath);
        File target = new File(targetPath);
        AudioAttributes audio = new AudioAttributes();
        Encoder encoder = new Encoder();

        audio.setCodec("libmp3lame");
        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setFormat("mp3");
        attrs.setAudioAttributes(audio);

        try {
            encoder.encode(source, target, attrs);
        } catch (IllegalArgumentException | InputFormatException  e) {
        } catch (EncoderException e) {
            //e.printStackTrace();
        }
    }
}
