package plus.gifu.foundation.encryption.digest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class SHA256Digest {

    private static final Base64.Encoder ENCODER = Base64.getEncoder();

    private static final String DIGEST = "SHA-256";

    /**
     * SHA256算法计算摘要
     * @param data 输入数据（要求非空，长度大于1）
     * @return 摘要结果
     * @throws NoSuchAlgorithmException 异常
     */
    public static byte[] digestBySHA256(byte[] data) throws NoSuchAlgorithmException {
        if (data == null || data.length == 0) {
            return new byte[0];
        }
        MessageDigest md = MessageDigest.getInstance(DIGEST);
        md.update(data);
        return md.digest();
    }

    /**
     * 求取字符串的摘要，结果以BASE64的编码形式返回
     * @param str 输入字符串（要求非空，长度大于1）
     * @return base64格式摘要
     */
    public static String digestBySHA256(String str) {
        try {
            return ENCODER.encodeToString(digestBySHA256(str.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            // 不会有异常啦
            e.printStackTrace();
            return "";
        }
    }

}
