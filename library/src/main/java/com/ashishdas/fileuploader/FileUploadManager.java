/*******************************************************************************
 * FileUploadManager.java
 * FileUploadManager
 * <p>
 * Author(s): Ashish Das
 ******************************************************************************/

package com.ashishdas.fileuploader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ashishdas.fileuploader.internal.Globals;
import com.ashishdas.fileuploader.internal.UploadManager;

import java.net.URL;
import java.util.Map;

public class FileUploadManager
{
	public interface OnAllTaskCompletedListener
	{
		void onAllTaskCompleted();
	}

	private static final String TAG = FileUploadManager.class.getSimpleName();

	private static Context mContext;
	private static UploadManager sUploadManager;

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
					mContext = context.getApplicationContext();
					sUploadManager = new UploadManager(mContext);

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
			sUploadManager.cancelAll();
			sUploadManager = null;
		}
	}

	public static synchronized boolean isStarted()
	{
		return sUploadManager != null;
	}

	public static synchronized Context getContext()
	{
		return isStarted() ? mContext : null;
	}

	public synchronized static void upload(final FileUploadRequest fileUploadRequest, final FileUploadStatusListener fileUploadStatusListener)
	{
		try
		{
			getInstance().upload(fileUploadRequest, fileUploadStatusListener);
		}
		catch (FileUploadException e)
		{
			Log.e(TAG, "upload()", e);
			if (fileUploadStatusListener != null)
			{
				fileUploadStatusListener.onFailed(fileUploadRequest, e);
			}
		}
	}

	public synchronized static boolean isRunning(final @NonNull FileUploadRequest fileUploadRequest)
	{
		try
		{
			return getInstance().isRunning(fileUploadRequest);
		}
		catch (FileUploadException e)
		{
			Log.e(TAG, "isRunning()", e);
		}
		return false;
	}

	public synchronized static boolean isRunning(final @NonNull String key)
	{
		try
		{
			return getInstance().isRunning(key);
		}
		catch (FileUploadException e)
		{
			Log.e(TAG, "isRunning()", e);
		}
		return false;
	}

	public synchronized static boolean pause(final @NonNull FileUploadRequest fileUploadRequest)
	{
		try
		{
			return getInstance().isRunning(fileUploadRequest);
		}
		catch (FileUploadException e)
		{
			Log.e(TAG, "pause()", e);
		}
		return false;
	}

	public synchronized static boolean pause(final @NonNull String key)
	{
		try
		{
			return getInstance().pause(key);
		}
		catch (FileUploadException e)
		{
			Log.e(TAG, "pause()", e);
		}
		return false;
	}

	public synchronized static boolean cancel(final @NonNull FileUploadRequest fileUploadRequest)
	{
		try
		{
			return getInstance().cancel(fileUploadRequest);
		}
		catch (FileUploadException e)
		{
			Log.e(TAG, "cancel()", e);
		}
		return false;
	}

	public synchronized static boolean cancel(final @NonNull String key)
	{
		try
		{
			return getInstance().cancel(key);
		}
		catch (FileUploadException e)
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
		catch (FileUploadException e)
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
		catch (FileUploadException e)
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
		catch (FileUploadException e)
		{
			Log.e(TAG, "cancelAll()", e);
		}
		return false;
	}

	public synchronized static boolean isAllDone()
	{
		try
		{
			return getInstance().isAllDone();
		}
		catch (FileUploadException e)
		{
			Log.e(TAG, "isAllDone()", e);
		}
		return false;
	}

	public static void setAllTaskCompletedListener(final OnAllTaskCompletedListener allTaskCompletedListener)
	{
		try
		{
			getInstance().setAllTaskCompletedListener(allTaskCompletedListener);
		}
		catch (FileUploadException e)
		{
			Log.e(TAG, "setAllTaskCompletedListener()", e);
		}
	}

	private synchronized static UploadManager getInstance() throws FileUploadException
	{
		if (sUploadManager == null)
		{
			throw new FileUploadException("FileUploadManager has not been initialized.");
		}
		return sUploadManager;
	}
}
