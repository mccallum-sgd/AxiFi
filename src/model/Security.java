package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Provides methods for encrypting and decrypting files or strings with AES-256
 * and Hmac SHA-256. <br>
 * <br>
 * Requires Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction
 * Policy Files for the 256 byte key length. Links:
 * <p>
 * <ul>
 * <li><a href=
 * "http://www.oracle.com/technetwork/java/javase/downloads/jce-6-download-429243.html">JCE
 * 6</a>
 * <li><a href=
 * "http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html">JCE
 * 7</a>
 * <li><a href=
 * "http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html">JCE
 * 8</a>
 * </ul>
 * </p>
 * 
 * Credit to: <em>Artjom B.</em> from
 * <a href="https://stackoverflow.com/a/29354222/4036475">StackOverflow</a>
 */

public class Security {
	
	private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
	private static final int IV_BYTES = 16;
	private static final int SALT_BYTES = 16;
	private static final int PBE_ITERATIONS = 65536;
	private static final int PBE_BYTES = 256;

	/*--- INTERFACE ---------------------------------------------------------------------------*/

	/**
	 * Encrypts the specified file with AES-256 and Hmac SHA-256 using the given
	 * key (padded with PBKDF2).
	 * 
	 * @param key
	 *            - the key to use
	 * @param file
	 *            - the file to encrypt
	 * 
	 * @throws IOException
	 *             - if an I/O error occurs
	 */
	public static void encrypt(String key, File file) {
		try {
			byte[] inputBytes = read(file);

			// Generate
			byte[] salt = randBytes(SALT_BYTES);
			byte[] iv = randBytes(IV_BYTES);
			SecretKey secret = generateKey(key, salt);

			// Encrypt
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(iv));
			byte[] outputBytes = cipher.doFinal(inputBytes);

			write(file, salt, iv, outputBytes);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			System.out.println("Error encrypting file...");
			e.printStackTrace();
		}
	}

	/**
	 * Decrypts the specified file previously encrypted with
	 * {@code this#encrypt(String, File)} using the given key (padded with
	 * PBKDF2).
	 * 
	 * @param key
	 *            - the key to use
	 * @param file
	 *            - the file to decrypt
	 * 
	 * @throws IOException
	 *             - if an I/O error occurs
	 * @throws BadPaddingException
	 *             - if the key is invalid
	 */
	public static void decrypt(String key, File file) {
		try {
			byte[] inputBytes = read(file);

			// Parse
			byte[] salt = Arrays.copyOfRange(inputBytes, 0, 16);
			byte[] iv = Arrays.copyOfRange(inputBytes, 16, 32);
			byte[] bytes = Arrays.copyOfRange(inputBytes, 32, inputBytes.length);

			// Generate Key
			SecretKey secret = generateKey(key, salt);

			// Decrypt
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
			byte[] outputBytes = cipher.doFinal(bytes);

			write(file, outputBytes);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			Errors.showError("Your database file could not be decrypted. If you're sure you entered the correct key, please submit an issue: https://github.com/mccallum-sgd/AxiFi/issues/new", "Decryption Error");
			e.printStackTrace();
		}
	}

	/*--- HELPERS ---------------------------------------------------------------------------*/

	private static SecretKey generateKey(String password, byte[] salt) {
		try {
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, PBE_ITERATIONS, PBE_BYTES);
			SecretKey tmp = factory.generateSecret(spec);
			return new SecretKeySpec(tmp.getEncoded(), "AES");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static byte[] randBytes(int size) {
		byte[] bytes = new byte[size];
		new SecureRandom().nextBytes(bytes);
		return bytes;
	}

	private static byte[] read(File file) {
		try {
			FileInputStream inputStream = new FileInputStream(file);
			byte[] inputBytes = new byte[(int) file.length()];
			inputStream.read(inputBytes);
			inputStream.close();
			return inputBytes;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void write(File file, byte[]... bytes) {
		try {
			FileOutputStream outputStream = new FileOutputStream(file);
			for (byte[] b : bytes)
				outputStream.write(b);
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*--- UNIT TESTING ---------------------------------------------------------------------------*/

	public static void main(String[] args) {
		File file = new File("test");
		String password = "password";
		byte[] randBytes = new byte[500];
		new Random().nextBytes(randBytes);

		try {
			// Setup file
			file.createNewFile();
			new FileOutputStream(file).write(randBytes);

			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Test
		byte[] tmp1 = null, tmp2 = null;
		try {
			tmp1 = new byte[(int)file.length()];
			new FileInputStream(file).read(tmp1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		encrypt(password, file);
		decrypt(password, file);
		try {
			tmp2 = new byte[(int)file.length()];
			new FileInputStream(file).read(tmp2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		encrypt(password, file);
		decrypt(password, file);
		
		file.delete();
	
		assert Arrays.equals(tmp1, tmp2): "Encryption and decryption does not match: " + new String(tmp1) + "\t" + new String(tmp2);
	}
}
