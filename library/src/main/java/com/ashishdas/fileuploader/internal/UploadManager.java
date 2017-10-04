/*******************************************************************************
 * UploadManager.java
 * UploadManager
 * <p>
 * Author(s): Ashish Das
 ******************************************************************************/

package com.ashishdas.fileuploader.internal;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ashishdas.fileuploader.FileUploadManager;
import com.ashishdas.fileuploader.FileUploadRequest;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UploadManager
{
	private static final String TAG = UploadManager.class.getSimpleName();
	private static final int MAX_THREAD_NUMBER = 5;

	private Context mContext;
	private Map<String, Uploader> mUploaderMap;
	private ExecutorService mExecutorService;
	private UploadInfoDelivery mDelivery;
	private FileUploadManager.OnAllTaskCompletedListener mAllTaskCompletedListener;
	private Handler mHandler = new Handler(Looper.getMainLooper());

	public UploadManager(final Context context)
	{
		mContext = context;
		mUploaderMap = new LinkedHashMap<>();
		mExecutorService = Executors.newFixedThreadPool(MAX_THREAD_NUMBER);
		mDelivery = new UploadInfoDelivery(mContext, mHandler);
	}

	public void setAllTaskCompletedListener(final FileUploadManager.OnAllTaskCompletedListener allTaskCompletedListener)
	{
		mAllTaskCompletedListener = allTaskCompletedListener;
	}

	public void upload(final FileUploadRequest fileUploadRequest, final UploadStatusListener uploadStatusListener)
	{
		try
		{
			final String key = fileUploadRequest.getKey();
			if (check(key))
			{
				UploadResponse uploadResponse = new UploadResponse(fileUploadRequest, mDelivery, uploadStatusListener);
				Uploader uploader = new Uploader(new File(fileUploadRequest.getFilePath()), uploadResponse, mExecutorService, key, mFileUploaderDestroyedListener);
				mUploaderMap.put(key, uploader);
				uploader.start();
			}
		}
		catch (Exception e)
		{
			Log.e(TAG, "upload()", e);

			if (uploadStatusListener != null)
			{
				UploadInfo uploadInfo = new UploadInfo();
				uploadInfo.setErrorMessage(e.getLocalizedMessage());
				uploadStatusListener.onUploadStatus(fileUploadRequest, uploadInfo);
			}
		}
	}

	public boolean isRunning(final @NonNull FileUploadRequest fileUploadRequest)
	{
		return isRunning(fileUploadRequest.getKey());
	}

	public boolean isRunning(final String key)
	{
		try
		{
			if (mUploaderMap.containsKey(key))
			{
				Uploader uploader = mUploaderMap.get(key);
				if (uploader != null)
				{
					return uploader.isRunning();
				}
			}
		}
		catch (Exception e)
		{
			Log.e(TAG, "isRunning()", e);
		}
		return false;
	}


	public boolean pause(final @NonNull FileUploadRequest fileUploadRequest)
	{
		return pause(fileUploadRequest.getKey());
	}

	public boolean pause(final String key)
	{
		try
		{
			if (mUploaderMap.containsKey(key))
			{
				Uploader uploader = mUploaderMap.get(key);
				if (uploader != null)
				{
					if (uploader.isRunning())
					{
						uploader.pause();
					}
				}
				mUploaderMap.remove(key);

				return true;
			}
		}
		catch (Exception e)
		{
			Log.e(TAG, "pause()", e);
		}
		return false;
	}

	public boolean cancel(final @NonNull FileUploadRequest fileUploadRequest)
	{
		return cancel(fileUploadRequest.getKey());
	}

	public boolean cancel(final String key)
	{
		try
		{
			if (mUploaderMap.containsKey(key))
			{
				Uploader uploader = mUploaderMap.get(key);
				if (uploader != null)
				{
					uploader.cancel();
				}
				mUploaderMap.remove(key);
				return true;
			}
		}
		catch (Exception e)
		{
			Log.e(TAG, "cancel()", e);
		}
		return false;
	}

	public boolean pauseAll()
	{
		mHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				for (Uploader uploader : mUploaderMap.values())
				{
					if (uploader != null)
					{
						if (uploader.isRunning())
						{
							uploader.pause();
						}
					}
				}
			}
		});
		return true;
	}

	public boolean resumeAll()
	{
		mHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				for (Uploader uploader : mUploaderMap.values())
				{
					if (uploader != null)
					{
						if (!uploader.isRunning())
						{
							uploader.start();
						}
					}
				}
			}
		});
		return true;
	}

	public boolean cancelAll()
	{
		mHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				for (Uploader uploader : mUploaderMap.values())
				{
					if (uploader != null)
					{
						if (uploader.isRunning())
						{
							uploader.cancel();
						}
					}
				}
			}
		});
		return true;
	}

	public boolean isAllDone()
	{
		return mUploaderMap.isEmpty();
	}

	private boolean check(String key) throws Exception
	{
		if (mUploaderMap.containsKey(key))
		{
			Uploader uploader = mUploaderMap.get(key);
			if (uploader != null)
			{
				if (uploader.isRunning())
				{
					Log.w(TAG, "check() - Task has been started!");
					return false;
				}
				else
				{
					throw new IllegalStateException("Uploader instance with same key has not been destroyed!");
				}
			}
		}
		return true;
	}

	private Uploader.OnDestroyedListener mFileUploaderDestroyedListener = new Uploader.OnDestroyedListener()
	{
		@Override
		public void onDestroyed(final String key, final Uploader uploader)
		{
			mHandler.post(new Runnable()
			{
				@Override
				public void run()
				{
					if (mUploaderMap.containsKey(key))
					{
						removeUploadTask(key);
					}
				}
			});
		}
	};

	private void removeUploadTask(String key)
	{
		try
		{
			mUploaderMap.remove(key);
		}
		catch (Exception e)
		{

		}

		if (mAllTaskCompletedListener != null && isAllDone())
		{
			synchronized (mAllTaskCompletedListener)
			{
				mAllTaskCompletedListener.onAllTaskCompleted();
			}
		}
	}
}
