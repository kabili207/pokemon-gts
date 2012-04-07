package com.zyrenth.gts;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

public class Helper
{
	public static String sha1(String data) throws Exception
	{
		return sha1(data.getBytes());
	}
	
	public static String sha1(byte[] data) throws Exception
	{
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		
		return byteArrayToHexString(md.digest(data));
	}
	
	public static String b64Encode(String data)
	{
		return b64Encode(data.getBytes());
	}

	public static String b64Encode(byte[] binaryData)
	{
		// We can't useencodeBase64URLSafeString here because
		// it strips out the "=" padding which we need.
		return Base64.encodeBase64String(binaryData)
			.replace("+", "-").replace("/", "_");
	}
	
	public static byte[] b64Decode(byte[] binaryData)
	{
		return Base64.decodeBase64(binaryData);
	}
	
	public static byte[] b64Decode(String data)
	{
		return Base64.decodeBase64(data);
	}
	
	public static String b64DecodeString(byte[] binaryData)
	{
		return new String(Base64.decodeBase64(binaryData));
	}
	
	public static String b64DecodeString(String data)
	{
		return new String(Base64.decodeBase64(data));
	}
	
	public static String byteArrayToHexString(byte[] b) throws Exception
	{
		String result = "";
		for (int i = 0; i < b.length; i++)
		{
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}
}
