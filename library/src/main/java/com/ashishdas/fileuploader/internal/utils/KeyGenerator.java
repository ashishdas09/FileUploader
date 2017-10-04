/*******************************************************************************
 * KeyGenerator.java
 * KeyGenerator
 * <p>
 * Author(s): Ashish Das
 ******************************************************************************/

package com.ashishdas.fileuploader.internal.utils;

import android.text.TextUtils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class KeyGenerator
{
	private static final String HASH_ALGORITHM = "MD5";
	private static final int RADIX = 10 + 26; // 10 digits + 26 letters

	public static synchronized String generate(String str)
	{
		if (!TextUtils.isEmpty(str))
		{
			byte[] md5 = getMD5(str.getBytes());
			if (md5 != null && md5.length > 0)
			{
				BigInteger bi = new BigInteger(md5).abs();
				return bi.toString(RADIX);
			}
			return str.replaceAll("[^a-zA-Z0-9]", "");
		}
		return null;
	}

	private static synchronized byte[] getMD5(byte[] data)
	{
		byte[] hash = null;
		try
		{
			MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
			digest.update(data);
			hash = digest.digest();
		}
		catch (NoSuchAlgorithmException e)
		{
		}
		return hash;
	}
}
