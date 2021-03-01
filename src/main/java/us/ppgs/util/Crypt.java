package us.ppgs.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Crypt {
	
	public static class CryptException extends RuntimeException {

		private static final long serialVersionUID = 1L;

		CryptException(Exception e) {
			super(e);
		}
	}
	
	private Crypt() {}
	
	public static byte[] cryptXOR(byte[] key, byte[] data) {
		byte[] ret = new byte[data.length];
		for (int i=0; i<data.length; i++) {
			ret[i] = (byte) (key[i % key.length] ^ data[i]);
		}

		return ret;
	}

	public static byte[] encryptMD5(String password) {
		return encrypt(password, "MD5");
	}

	public static byte[] encryptSHA1(String password) {
		return encrypt(password, "SHA1");
	}
	
	private static byte[] encrypt(String password, String digest) {
		
		MessageDigest md;
		try {
			md = MessageDigest.getInstance(digest);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		return md.digest(password.getBytes());
	}

	public static byte[] PBKDF2(String password, byte[] salt, int derivedKeyLength, int iterations) {

		// SHA-1 generates 160 bit hashes, so that's what makes sense here

		// Pick an iteration count that works for you. The NIST recommends at
		// least 1,000 iterations:
		// http://csrc.nist.gov/publications/nistpubs/800-132/nist-sp800-132.pdf

		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength);

		// PBKDF2 with SHA-1 as the hashing algorithm. Note that the NIST
		// specifically names SHA-1 as an acceptable hashing algorithm for PBKDF2
		try {
			SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

			return f.generateSecret(spec).getEncoded();
		}
		catch (Exception e) {
			throw new CryptException(e);
		}
	}
	
	public static String bytesToHex(byte[] ep) {
		StringBuilder sb = new StringBuilder();
		for (byte b : ep) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}
}