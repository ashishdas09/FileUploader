/*******************************************************************************
 * Globals.java
 * Globals
 * <p>
 * Author(s): Ashish Das
 ******************************************************************************/

package com.ashishdas.fileuploader.internal;

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
}
