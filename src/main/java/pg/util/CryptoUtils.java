package pg.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;

public final class CryptoUtils {

    private static final String CIPHER = "PBEWithMD5AndDES";
    private static final int ITERATION_COUNT = 20;
    private static final char[] KEY_SPEC = "1!2@3#4$5%6^7&8*9(0)".toCharArray();
    private static final byte[] SALT = {
            (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
            (byte) 0xab, (byte) 0xcd, (byte) 0x98, (byte) 0x63
    };

    private CryptoUtils() { }

    public static String encrypt(String value) throws GeneralSecurityException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(CIPHER);
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(KEY_SPEC));
        Cipher pbeCipher = Cipher.getInstance(CIPHER);
        pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, ITERATION_COUNT));
        return base64Encode(pbeCipher.doFinal(value.getBytes(StandardCharsets.UTF_8)));
    }

    private static String base64Encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String decrypt(String value) throws GeneralSecurityException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(CIPHER);
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(KEY_SPEC));
        Cipher pbeCipher = Cipher.getInstance(CIPHER);
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, ITERATION_COUNT));
        return new String(pbeCipher.doFinal(base64Decode(value)), StandardCharsets.UTF_8);
    }

    private static byte[] base64Decode(String property) {
        return Base64.getDecoder().decode(property);
    }

}
