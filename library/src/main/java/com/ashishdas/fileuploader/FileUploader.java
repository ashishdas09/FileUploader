/*******************************************************************************
 * FileUploader.java
 * FileUploader
 * <p>
 * Author(s): Ashish Das
 ******************************************************************************/

package com.ashishdas.fileuploader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ashishdas.fileuploader.internal.FileUploadManager;
import com.ashishdas.fileuploader.internal.Globals;

import java.net.URL;
import java.util.Map;

public class FileUploader
{
	private static final String TAG = FileUploader.class.getSimpleName();

	private static FileUploadManager sFileUploadManager;

	public synchronized static boolean startup(@NonNull final Context context, @NonNull final String url, @NonNull final Map<String, String> headers)
	{
		if (!isStarted())
		{
			try
			{
				if (Globals.isNetworkUrl(url))
				{
					Globals.sURL = new URL(url);
					Globals.sHeaders = headers;
					sFileUploadManager = new FileUploadManager(context);
					return true;
				}
			}
			catch (Exception e)
			{
				Log.e(TAG, "startup() - " + e.getLocalizedMessage(), e);
			}
		}
		return false;
	}

	public synchronized static void shutdown()
	{
		if (isStarted())
		{
			sFileUploadManager.cancelAll();
			sFileUploadManager = null;
		}
	}

	public static synchronized boolean isStarted()
	{
		return sFileUploadManager != null;
	}

	public synchronized static void upload(String filePath, FileUploaderListener fileUploaderListener)
	{
		try
		{
			getInstance().upload(filePath, fileUploaderListener);
		}
		catch (FileUploaderException e)
		{
			Log.e(TAG, "upload()", e);
			if (fileUploaderListener != null)
			{
				fileUploaderListener.onFailed(e);
			}
		}
	}

	public synchronized static boolean isRunning(String filePath)
	{
		try
		{
			return getInstance().isRunning(filePath);
		}
		catch (FileUploaderException e)
		{
			Log.e(TAG, "pause()", e);
		}
		return false;
	}

	public synchronized static boolean pause(String filePath)
	{
		try
		{
			return getInstance().pause(filePath);
		}
		catch (FileUploaderException e)
		{
			Log.e(TAG, "pause()", e);
		}
		return false;
	}

	public synchronized static boolean cancel(String filePath)
	{
		try
		{
			return getInstance().cancel(filePath);
		}
		catch (FileUploaderException e)
		{
			Log.e(TAG, "cancel()", e);
		}
		return false;
	}

	public synchronized static boolean pauseAll()
	{
		try
		{
			return getInstance().pauseAll();
		}
		catch (FileUploaderException e)
		{
			Log.e(TAG, "pauseAll()", e);
		}
		return false;
	}

	public synchronized static boolean resumeAll()
	{
		try
		{
			return getInstance().resumeAll();
		}
		catch (FileUploaderException e)
		{
			Log.e(TAG, "resumeAll()", e);
		}
		return false;
	}

	public synchronized static boolean cancelAll()
	{
		try
		{
			return getInstance().cancelAll();
		}
		catch (FileUploaderException e)
		{
			Log.e(TAG, "cancelAll()", e);
		}
		return false;
	}

	private synchronized static FileUploadManager getInstance() throws FileUploaderException
	{
		if (sFileUploadManager == null)
		{
			throw new FileUploaderException("FileUploader has not been initialized.");
		}
		return sFileUploadManager;
	}
}
