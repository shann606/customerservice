package com.ecom.customerservice.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import lombok.extern.slf4j.Slf4j;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Slf4j
public class AesEncryptionUtil {

	private AesEncryptionUtil() {

	}

	private static final String ALGO = "AES/GCM/NoPadding";
	private static final int GCM_TAG_LENGTH = 128; // bits

	// Generate AES key (store in Vault, AWS KMS, etc.)
	public static String generateKey() throws NoSuchAlgorithmException {
		KeyGenerator keyGen;
		SecretKey key = null;
		try {
			keyGen = KeyGenerator.getInstance("AES");

			keyGen.init(256); // requires JCE unlimited strength
			key = keyGen.generateKey();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw e;
		}
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}

	public static String encrypt(String plainText, String secretKeyBase64) throws Exception {
		byte[] encrypted = null;
		try {
			byte[] keyBytes = Base64.getDecoder().decode(secretKeyBase64);
			SecretKey key = new javax.crypto.spec.SecretKeySpec(keyBytes, "AES");

			Cipher cipher = Cipher.getInstance(ALGO);

			byte[] iv = new byte[12];
			java.security.SecureRandom.getInstanceStrong().nextBytes(iv);

			GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
			cipher.init(Cipher.ENCRYPT_MODE, key, spec);

			byte[] cipherText = cipher.doFinal(plainText.getBytes());

			// final format: Base64(IV + cipher)
			encrypted = new byte[iv.length + cipherText.length];
			System.arraycopy(iv, 0, encrypted, 0, iv.length);
			System.arraycopy(cipherText, 0, encrypted, iv.length, cipherText.length);
		} catch (Exception e) {
			log.info("Exception occured in encrypt");
			e.printStackTrace();
			throw e;
		}

		return "$" + Base64.getEncoder().encodeToString(encrypted);
	}

	public static String decrypt(String encryptedBase64, String secretKeyBase64) throws Exception {
		encryptedBase64 = encryptedBase64.substring(1);
		byte[] cipherBytes;
		Cipher cipher = Cipher.getInstance(ALGO);

		try {
			byte[] encryptedBytes = Base64.getDecoder().decode(encryptedBase64);

			byte[] keyBytes = Base64.getDecoder().decode(secretKeyBase64);
			SecretKey key = new javax.crypto.spec.SecretKeySpec(keyBytes, "AES");

			byte[] iv = new byte[12];
			System.arraycopy(encryptedBytes, 0, iv, 0, iv.length);

			cipherBytes = new byte[encryptedBytes.length - iv.length];
			System.arraycopy(encryptedBytes, iv.length, cipherBytes, 0, cipherBytes.length);

			cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
		} catch (Exception e) {
			log.info("Exception occured in decrypt");
			e.printStackTrace();
			throw e;
		}

		return new String(cipher.doFinal(cipherBytes));
	}
}
