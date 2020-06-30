package plus.gifu.foundation.encryption.cipher;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 加密工具类
 * AES/GCM/NoPadding
 *
 * @author yanghongyu
 * @since 20200525
 */

public class AesGcmNoPaddingCipher {

    private static final String ALGORITHM = "AES";

    private static final String CIPHER = "AES/GCM/NoPadding";

    public static byte[] encrypt(byte[] plaintext, byte[] key) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM);
        byte[] ret;
        try {
            Cipher cipher = Cipher.getInstance(CIPHER);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] iv = cipher.getIV();
            byte[] ciphertext = cipher.doFinal(plaintext);
            ret = new byte[12 + ciphertext.length];
            System.arraycopy(iv, 0, ret, 0, 12);
            System.arraycopy(ciphertext, 0, ret, 12, ciphertext.length);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            ret = new byte[]{};
        }
        return ret;
    }

    public static byte[] decrypt(byte[] ciphertext, byte[] key) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM);
        GCMParameterSpec iv = new GCMParameterSpec(128, ciphertext, 0, 12);
        byte[] ret;
        try {
            Cipher cipher = Cipher.getInstance(CIPHER);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);
            ret = cipher.doFinal(ciphertext, 12, ciphertext.length - 12);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            ret = new byte[]{};
        }
        return ret;
    }

}
