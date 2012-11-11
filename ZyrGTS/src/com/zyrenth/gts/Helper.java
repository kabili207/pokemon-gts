package com.zyrenth.gts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

import org.apache.commons.codec.binary.Base64;

/**
 * Provides helper functions to the GTS library
 * 
 * @author kabili
 */
public class Helper {
	public enum Generation {
		/**
		 * Generation III includes Ruby, Sapphire, Emerald, Fire Red, and Leaf
		 * Green.
		 */
		III,
		/**
		 * Generation IV includes Diamond, Pearl, Platinum, HeartGold, and
		 * SoulSilver.
		 */
		IV,
		/**
		 * Generation V includes Black and White.
		 */
		V
	}

	public enum Gender {
		Male, Female
	}

	public static String sha1(String data) throws Exception {
		return sha1(data.getBytes());
	}

	public static String sha1(byte[] data) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-1");

		return byteArrayToHexString(md.digest(data));
	}

	/**
	 * Returns a URL safe base 64 encoded string representation of the specified
	 * string
	 * 
	 * @param data
	 *            the string to encode
	 * @return a URL safe base 64 encoded string
	 */
	public static String b64Encode(String data) {
		return b64Encode(data.getBytes());
	}

	/**
	 * Returns a URL safe base 64 encoded string representation of the specified
	 * byte array
	 * 
	 * @param data
	 *            the byte array to encode
	 * @return a URL safe base 64 encoded string
	 */
	public static String b64Encode(byte[] binaryData) {
		// We can't useencodeBase64URLSafeString here because
		// it strips out the "=" padding which we need.
		return Base64.encodeBase64String(binaryData).replace("+", "-").replace("/", "_");
	}

	public static byte[] b64Decode(byte[] binaryData) {
		return Base64.decodeBase64(binaryData);
	}

	public static byte[] b64Decode(String data) {
		return Base64.decodeBase64(data);
	}

	public static String b64DecodeString(byte[] binaryData) {
		return new String(Base64.decodeBase64(binaryData));
	}

	public static String b64DecodeString(String data) {
		return new String(Base64.decodeBase64(data));
	}

	public static String byteArrayToHexString(byte[] b) throws Exception {
		String result = "";
		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

	public static byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);

		// Get the size of the file
		long length = file.length();

		if (length > Integer.MAX_VALUE) {
			// File is too large
		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int) length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file " + file.getName());
		}

		// Close the input stream and return bytes
		is.close();
		return bytes;
	}

	public static String getAppDataDirectory() {
		String OS = System.getProperty("os.name").toUpperCase();
		if (OS.contains("WIN"))
			return System.getenv("APPDATA");
		else if (OS.contains("MAC"))
			return System.getProperty("user.home") + "/Library/Application " + "Support";
		else if (OS.contains("NUX"))
			return System.getProperty("user.home") + "/.config";
		return System.getProperty("user.dir");
	}
}
