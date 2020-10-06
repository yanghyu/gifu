package com.github.yanghyu.gifu.foundation.encryption.cipher;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.Base64;

public class ECCipher {

    /**
     * 供应商名称
     */
    private static final String PROVIDER_NAME = "BC";

    /**
     * ECC 算法
     */
    private static final String ALGORITHM = "EC";

    /**
     * ECC 加密器
     */
    private static final String CIPHER = "ECIES";

    /**
     * key size（bit）
     */
    private final static int KEY_SIZE = 256;

    /**
     * ECC签名算法
     */
    private final static String SIGNATURE = "SHA256withECDSA";

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    /**
     * 生成密钥对
     */
    public static KeyPair getKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM, PROVIDER_NAME);//BouncyCastle
        keyPairGenerator.initialize(KEY_SIZE, new SecureRandom());
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * 获取公钥
     */
    public static String getPublicKey(KeyPair keyPair) {
        ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();
        byte[] bytes = publicKey.getEncoded();
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 获取私钥
     */
    public static String getPrivateKey(KeyPair keyPair) {
        ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();
        byte[] bytes = privateKey.getEncoded();
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 公钥加密
     */
    public static byte[] encrypt(byte[] content, ECPublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER, PROVIDER_NAME);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(content);
    }

    /**
     * 私钥解密
     */
    public static byte[] decrypt(byte[] content, ECPrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER, PROVIDER_NAME);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(content);
    }

    /**
     * 私钥签名
     */
    public static byte[] sign(String content, ECPrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance(SIGNATURE);
        signature.initSign(privateKey);
        signature.update(content.getBytes());
        return signature.sign();
    }

    /**
     * 公钥验签
     */
    public static boolean verify(String content, byte[] sign, ECPublicKey publicKey) throws Exception {
        Signature signature = Signature.getInstance(SIGNATURE);
        signature.initVerify(publicKey);
        signature.update(content.getBytes());
        return signature.verify(sign);
    }


}
