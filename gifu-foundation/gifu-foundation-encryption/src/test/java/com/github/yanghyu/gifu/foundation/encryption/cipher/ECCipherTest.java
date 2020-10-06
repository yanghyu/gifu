package com.github.yanghyu.gifu.foundation.encryption.cipher;

import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.Base64;

import static com.github.yanghyu.gifu.foundation.encryption.cipher.ECCipher.*;

class ECCipherTest {

    public static void main(String[] args) {
        try {
            KeyPair keyPair = getKeyPair();
            ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();
            ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();

            System.out.println("publicKey[" + publicKey.getEncoded().length + "]:\n" + getPublicKey(keyPair));
            System.out.println("privateKey[" + privateKey.getEncoded().length + "]:\n" + getPrivateKey(keyPair));

            //测试文本
            String content = "abcdefg12345";

            //加密
            byte[] cipherTxt = encrypt(content.getBytes(), publicKey);
            //解密
            byte[] clearTxt = decrypt(cipherTxt, privateKey);
            //打印
            System.out.println("content:" + content);
            System.out.println("cipherTxt[" + cipherTxt.length + "]:" + Base64.getEncoder().encodeToString(cipherTxt));
            System.out.println("clearTxt:" + new String(clearTxt));

            //签名
            byte[] sign = sign(content, privateKey);
            //验签
            boolean ret = verify(content, sign, publicKey);
            //打印
            System.out.println("content:" + content);
            System.out.println("sign[" + sign.length + "]:" + Base64.getEncoder().encodeToString(sign));
            System.out.println("verify:" + ret);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[main]-Exception:" + e.toString());
        }
    }

}