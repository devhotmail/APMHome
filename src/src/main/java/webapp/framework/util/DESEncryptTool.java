package webapp.framework.util;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.lang.ArrayUtils;

public class DESEncryptTool {

    private static String strDefaultKey = "itpschina";
    private Cipher encryptCipher;
    private Cipher decryptCipher;

    public String byteArr2HexStr(byte[] arrB)
            throws Exception {
        int iLen = arrB.length;

        StringBuffer sb = new StringBuffer(iLen * 2);
        for (int i = 0; i < iLen; i++) {
            int intTmp = arrB[i];
            while (intTmp < 0) {
                intTmp += 256;
            }
            if (intTmp < 16) {
                sb.append("0");
            }
            sb.append(Integer.toString(intTmp, 16));
        }
        return sb.toString();
    }

    public byte[] hexStr2ByteArr(String strIn)
            throws Exception {
        byte[] arrB = strIn.getBytes();
        int iLen = arrB.length;

        byte[] arrOut = new byte[iLen / 2];
        for (int i = 0; i < iLen; i += 2) {
            String strTmp = new String(arrB, i, 2);
            arrOut[(i / 2)] = ((byte) Integer.parseInt(strTmp, 16));
        }
        return arrOut;
    }

    public DESEncryptTool() throws Exception {
        this(strDefaultKey);
    }

    public DESEncryptTool(String strKey) throws Exception {
        Key key = getKey(strKey.getBytes());

        this.encryptCipher = Cipher.getInstance("DES");
        this.encryptCipher.init(1, key);

        this.decryptCipher = Cipher.getInstance("DES");
        this.decryptCipher.init(2, key);
    }

    public byte[] encrypt(byte[] arrB)
            throws Exception {
        return this.encryptCipher.doFinal(arrB);
    }

    public String encrypt(String strIn)
            throws Exception {
        return byteArr2HexStr(encrypt(strIn.getBytes()));
    }

    public byte[] decrypt(byte[] arrB)
            throws Exception {
        return this.decryptCipher.doFinal(arrB);
    }

    public String decrypt(String strIn)
            throws Exception {
        return new String(decrypt(hexStr2ByteArr(strIn)));
    }

    private Key getKey(byte[] arrBTmp)
            throws Exception {
        byte[] arrB = new byte[8];
        for (int i = 0; (i < arrBTmp.length) && (i < arrB.length); i++) {
            arrB[i] = arrBTmp[i];
        }
        Key key = new SecretKeySpec(arrB, "DES");

        return key;
    }

    public static void main(String[] args) {
        try {
            DESEncryptTool des = new DESEncryptTool();

            int LOOP = 3;
            String CHARSET = "gb2312";

            int count = LOOP;
            while (count > 0) {
                String testValue = String.valueOf(Math.random());
                System.out.println(testValue);
                System.out.println(des.decrypt(des.encrypt(testValue)));
                System.out.println();

                byte[] data = testValue.getBytes(CHARSET);
                System.out.println(ArrayUtils.toString(data));
                System.out.println(ArrayUtils.toString(des.decrypt(des.encrypt(data))));
                System.out.println("------------------------------------------");
                count--;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
