package com.ge.apm.edgeserver.dataupload.fromsite;

import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;


public class ServerKeyUtil {
    private static final Logger logger = Logger.getLogger(ServerKeyUtil.class);

    private static final String SHA1 = "SHA-256";
    private static String KEY= "hcdigital@2016";

    public static byte[] sha1(byte[] input) throws NoSuchAlgorithmException {
        return digest(input, SHA1, null, 1);
    }

    public static byte[] sha1(byte[] input, byte[] salt, int iterations) throws NoSuchAlgorithmException {
        return digest(input, SHA1, salt, iterations);
    }

    private static byte[] digest(byte[] input, String algorithm, byte[] salt, int iterations) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);

        if (salt != null) digest.update(salt);

        byte[] result = digest.digest(input);

        for (int i = 1; i < iterations; i++) {
            digest.reset();
            result = digest.digest(result);
        }
        return result;
    }

    public static String encodeHex(byte[] input) {
        return Hex.encodeHexString(input);
    }

    public static String getServerKey() {
        try {
            String serverKey = "";
            Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
            while (networks.hasMoreElements()) {
                NetworkInterface network = networks.nextElement();
                byte[] mac = network.getHardwareAddress();

                if (mac != null) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mac.length; i++) {
                        sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                    }
                    serverKey = serverKey + sb.toString();
                }
            }
            
            byte[] hashPassword = ServerKeyUtil.sha1(serverKey.getBytes(), KEY.getBytes(), 1024);
            return ServerKeyUtil.encodeHex(hashPassword);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        
        return null;
    }    
}
