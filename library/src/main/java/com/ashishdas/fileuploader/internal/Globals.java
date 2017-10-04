/*******************************************************************************
 * Globals.java
 * Globals
 * <p>
 * Author(s): Ashish Das
 ******************************************************************************/

package com.ashishdas.fileuploader.internal;

import android.text.TextUtils;

import com.ashishdas.fileuploader.FileUploadException;
import com.ashishdas.fileuploader.FileUploadRequest;

import java.io.File;
import java.net.URL;
import java.util.Map;

public class Globals
{
	public static URL sURL;
	public static Map<String, String> sHeaders;

	/**
	 * @return True iff the url is a network url.
	 */
	public static boolean isNetworkUrl(String url)
	{
		if (url == null || url.length() == 0)
		{
			return false;
		}
		return isHttpUrl(url) || isHttpsUrl(url);
	}

	/**
	 * @return True iff the url is an http: url.
	 */
	private static boolean isHttpUrl(String url)
	{
		return (null != url) &&
				(url.length() > 6) &&
				url.substring(0, 7).equalsIgnoreCase("http://");
	}

	/**
	 * @return True iff the url is an https: url.
	 */
	private static boolean isHttpsUrl(String url)
	{
		return (null != url) &&
				(url.length() > 7) &&
				url.substring(0, 8).equalsIgnoreCase("https://");
	}

	/**
	 * Run time assertion which throws an exception if the file not exists
	 *
	 * @throws FileUploadException
	 */
	public static void assertFileUploadInfo(final FileUploadRequest fileUploadRequest) throws FileUploadException
	{
		if (fileUploadRequest != null)
		{
			assertFileExists(fileUploadRequest.getFilePath());
			assertKey(fileUploadRequest.getKey());
			return;
		}
		throw new FileUploadException("FileUploadRequest must not be null");
	}

	public static void assertFileExists(String filePath) throws FileUploadException
	{
		if (!TextUtils.isEmpty(filePath))
		{
			File file = new File(filePath);
			if (!(file.exists() && file.isFile()))
			{
				throw new FileUploadException("File is not exist");
			}
			return;
		}
		throw new FileUploadException("File path must not be empty");
	}

	public static void assertKey(String key) throws FileUploadException
	{
		if (TextUtils.isEmpty(key))
		{
			throw new FileUploadException("Unique key must not be empty");
		}
	}
}
