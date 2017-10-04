/*******************************************************************************
 * FileUploadServiceManager.java
 * FileUploadServiceManager
 * <p>
 * Author(s): Ashish Das
 ******************************************************************************/

package com.ashishdas.fileuploader;

import android.content.Context;
import android.support.annotation.NonNull;

import com.ashishdas.fileuploader.internal.UploadServiceHelper;

import java.util.Map;

public class FileUploadServiceManager
{
	private static final String TAG = FileUploadManager.class.getSimpleName();

	public synchronized static boolean startup(@NonNull final Context context, @NonNull final String url, @NonNull final Map<String, String> headers)
	{
		return FileUploadManager.startup(context, url, headers);
	}

	public synchronized static void shutdown()
	{
		FileUploadManager.shutdown();
	}

	public static synchronized boolean isStarted()
	{
		return FileUploadManager.isStarted();
	}

	public static synchronized boolean upload(FileUploadRequest request)
	{
		if (isStarted())
		{
			UploadServiceHelper.intentUpload(FileUploadManager.getContext(), request);
			return true;
		}
		return false;
	}

	public static synchronized boolean pause(FileUploadRequest request)
	{
		if (isStarted())
		{
			UploadServiceHelper.intentPause(FileUploadManager.getContext(), request);
			return true;
		}
		return false;
	}

	public static synchronized boolean cancel(FileUploadRequest request)
	{
		if (isStarted())
		{
			UploadServiceHelper.intentCancel(FileUploadManager.getContext(), request);
			return true;
		}
		return false;
	}

	public static synchronized boolean pauseAll()
	{
		if (isStarted())
		{
			UploadServiceHelper.intentPauseAll(FileUploadManager.getContext());
			return true;
		}
		return false;
	}

	public static synchronized boolean cancelAll()
	{
		if (isStarted())
		{
			UploadServiceHelper.intentCancelAll(FileUploadManager.getContext());
			return true;
		}
		return false;
	}

	public static synchronized boolean resumeAll()
	{
		if (isStarted())
		{
			UploadServiceHelper.intentResumeAll(FileUploadManager.getContext());
			return true;
		}
		return false;
	}

	public static synchronized boolean destory()
	{
		if (isStarted())
		{
			UploadServiceHelper.intentDestory(FileUploadManager.getContext());
			return true;
		}
		return false;
	}
}
