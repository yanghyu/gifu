package plus.gifu.foundation.encryption;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class AesGcmNoPaddingCipherTest {

    @Test
    public void test() {
        byte[] key = new byte[] {(byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80,(byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80};
        byte[] plaintext = new byte[] {(byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x00};
        System.out.println(Arrays.toString(plaintext));
        System.out.println(plaintext.length);
        byte[] ciphertext = AesGcmNoPaddingCipher.encrypt(plaintext, key);
        System.out.println(Arrays.toString(ciphertext));
        System.out.println(ciphertext.length);
        byte[] plaintextResult = AesGcmNoPaddingCipher.decrypt(ciphertext, key);
        System.out.println(Arrays.toString(plaintextResult));
        System.out.println(plaintextResult.length);
        Assertions.assertArrayEquals(plaintext, plaintextResult);
    }

}