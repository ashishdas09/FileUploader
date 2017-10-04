/*******************************************************************************
 * FileUploadManager.java
 * FileUploadManager
 * <p>
 * Author(s): Ashish Das
 ******************************************************************************/

package com.ashishdas.fileuploader;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ashishdas.fileuploader.internal.FileUploadStatusObserver;
import com.ashishdas.fileuploader.internal.UploadInfo;
import com.ashishdas.fileuploader.internal.UploadManager;
import com.ashishdas.fileuploader.internal.UploadStatusListener;
import com.ashishdas.fileuploader.internal.utils.Utils;

import java.net.URL;
import java.util.LinkedHashMap;
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

	private static Map<String, FileUploadStatusObserver> sFileUploadStatusObserverMap = new LinkedHashMap<>();
	private static Handler sHandler = new Handler(Looper.getMainLooper());

	public synchronized static boolean startup(@NonNull final Context context, @NonNull final String url, @NonNull final Map<String, String> headers)
	{
		if (!isStarted())
		{
			try
			{
				if (Utils.isNetworkUrl(url))
				{
					Utils.sURL = new URL(url);
					Utils.sHeaders = headers;
					mContext = context.getApplicationContext();
					sUploadManager = new UploadManager(mContext);
					sFileUploadStatusObserverMap.clear();
					sHandler = new Handler(Looper.getMainLooper());
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

	public static synchronized void upload(final @NonNull FileUploadRequest fileUploadRequest, final FileUploadStatusListener fileUploadStatusListener)
	{
		try
		{
			Utils.assertFileUploadInfo(fileUploadRequest);
			final String key = fileUploadRequest.getKey();
			if (fileUploadStatusListener != null)
			{
				FileUploadStatusObserver observer = getObserver(key);
				if (observer == null)
				{
					observer = new FileUploadStatusObserver();
				}
				else
				{
					sFileUploadStatusObserverMap.remove(key);
				}
				observer.addCallback(fileUploadStatusListener);
				sFileUploadStatusObserverMap.put(key, observer);
			}
			if (!isRunning(fileUploadRequest))
			{
				getInstance().upload(fileUploadRequest, sUploadStatusListener);
			}
		}
		catch (Exception e)
		{
			Log.e(TAG, "upload()", e);
			if (fileUploadStatusListener != null)
			{
				fileUploadStatusListener.onFailed(fileUploadRequest, new FileUploadException(e.getLocalizedMessage()));
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

	private static UploadStatusListener sUploadStatusListener = new UploadStatusListener()
	{
		@Override
		public void onUploadStatus(final FileUploadRequest request, final UploadInfo uploadInfo)
		{
			notifyUploadStatus(request, uploadInfo);
		}
	};

	private synchronized static void notifyUploadStatus(final FileUploadRequest request, final UploadInfo uploadInfo)
	{
		try
		{
			final String key = request.getKey();
			final FileUploadStatusObserver observer = getObserver(key);
			if (observer != null)
			{
				observer.notifyStatusListener(request, uploadInfo);
			}
			if (uploadInfo.getStatus().toInt() > FileUploadStatus.Paused.toInt())
			{
				removeObserver(key);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private synchronized static void removeObserver(final String key)
	{
		if (isEmptyObserverMap())
		{
			return;
		}

		sHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				if (observerContainsKey(key))
				{
					sFileUploadStatusObserverMap.remove(key);
				}
			}
		});
	}

	private synchronized static FileUploadStatusObserver getObserver(String key)
	{
		if (observerContainsKey(key))
		{
			return sFileUploadStatusObserverMap.get(key);
		}
		return null;
	}

	private synchronized static boolean observerContainsKey(String key)
	{
		if (isEmptyObserverMap())
		{
			return false;
		}
		return sFileUploadStatusObserverMap.containsKey(key);
	}

	private synchronized static boolean isEmptyObserverMap()
	{
		return sFileUploadStatusObserverMap.isEmpty();
	}
}
