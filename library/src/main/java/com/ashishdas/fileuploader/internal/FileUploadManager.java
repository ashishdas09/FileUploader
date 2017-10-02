/*******************************************************************************
 * FileUploadManager.java
 * FileUploadManager
 * <p>
 * Author(s): Ashish Das
 ******************************************************************************/

package com.ashishdas.fileuploader.internal;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.ashishdas.fileuploader.FileUploaderException;
import com.ashishdas.fileuploader.FileUploaderListener;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileUploadManager
{
	private static final String TAG = FileUploadManager.class.getSimpleName();
	private static final int MAX_THREAD_NUMBER = 5;

	private Context mContext;
	private Map<String, Uploader> mUploaderMap;
	private ExecutorService mExecutorService;
	private UploadInfoDelivery mDelivery;
	private Handler mHandler = new Handler(Looper.getMainLooper());

	public FileUploadManager(final Context context)
	{
		mContext = context;
		mUploaderMap = new LinkedHashMap<>();
		mExecutorService = Executors.newFixedThreadPool(MAX_THREAD_NUMBER);
		mDelivery = new UploadInfoDelivery(mHandler);
	}

	public void upload(String filePath, FileUploaderListener fileUploaderListener)
	{
		try
		{
			File file = new File(filePath);
			if (!(file.exists() && file.isFile()))
			{
				throw new Exception("File is not exist");
			}
			final String key = getKey(filePath);
			if (!TextUtils.isEmpty(key) && check(key))
			{
				UploadResponse uploadResponse = new UploadResponse(mDelivery, fileUploaderListener);
				Uploader uploader = new Uploader(file, uploadResponse, mExecutorService, key, mFileUploaderDestroyedListener);
				mUploaderMap.put(key, uploader);
				uploader.start();
			}
		}
		catch (Exception e)
		{
			Log.e(TAG, "upload()", e);

			if (fileUploaderListener != null)
			{
				fileUploaderListener.onFailed(new FileUploaderException(e.getLocalizedMessage()));
			}
		}
	}

	public boolean isRunning(String filePath)
	{
		try
		{
			String key = getKey(filePath);
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

	public boolean pause(String filePath)
	{
		try
		{
			String key = getKey(filePath);
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

	public boolean cancel(String filePath)
	{
		try
		{
			String key = getKey(filePath);
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

	private static String getKey(String filePath) throws Exception
	{
		if (TextUtils.isEmpty(filePath))
		{
			throw new NullPointerException("File or File path can't be null! [filePath: " + filePath + "]");
		}
		return KeyGenerator.generate(filePath);
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
						mUploaderMap.remove(key);
					}
				}
			});
		}
	};
}
