package com.ecom.customerservice.util;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.util.Base64;

public class AesEncryptionUtil {
	
	
	 private AesEncryptionUtil() {
		
	}

    private static final String ALGO = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;   // bits

    // Generate AES key (store in Vault, AWS KMS, etc.)
    public static String generateKey() throws Exception {
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); // requires JCE unlimited strength
        SecretKey key = keyGen.generateKey();
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static String encrypt(String plainText, String secretKeyBase64) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(secretKeyBase64);
        SecretKey key = new javax.crypto.spec.SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance(ALGO);

        byte[] iv = new byte[12];
        java.security.SecureRandom.getInstanceStrong().nextBytes(iv);

        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);

        byte[] cipherText = cipher.doFinal(plainText.getBytes());

        // final format: Base64(IV + cipher)
        byte[] encrypted = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, encrypted, 0, iv.length);
        System.arraycopy(cipherText, 0, encrypted, iv.length, cipherText.length);

        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decrypt(String encryptedBase64, String secretKeyBase64) throws Exception {
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedBase64);

        byte[] keyBytes = Base64.getDecoder().decode(secretKeyBase64);
        SecretKey key = new javax.crypto.spec.SecretKeySpec(keyBytes, "AES");

        byte[] iv = new byte[12];
        System.arraycopy(encryptedBytes, 0, iv, 0, iv.length);

        byte[] cipherBytes = new byte[encryptedBytes.length - iv.length];
        System.arraycopy(encryptedBytes, iv.length, cipherBytes, 0, cipherBytes.length);

        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH, iv));

        return new String(cipher.doFinal(cipherBytes));
    }
}
