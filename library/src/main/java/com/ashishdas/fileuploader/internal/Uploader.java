/*******************************************************************************
 * Uploader.java
 * Uploader
 * <p>
 * Author(s): Ashish Das
 ******************************************************************************/

package com.ashishdas.fileuploader.internal;

import com.ashishdas.fileuploader.FileUploaderException;

import java.io.File;
import java.util.concurrent.Executor;

public class Uploader
{
	public interface OnDestroyedListener
	{
		void onDestroyed(String key, Uploader uploader);
	}

	private String mKey;

	private UploadResponse mResponse;
	private Executor mExecutor;
	private OnDestroyedListener mListener;

	private UploadStatus mStatus;
	private UploadRequest mUploadRequest;

	private UploadTask mUploadTask;

	public Uploader(File file, UploadResponse response,
	                Executor executor, String key, OnDestroyedListener listener)
	{
		mUploadRequest = new UploadRequest(file);
		mResponse = response;
		mExecutor = executor;
		mKey = key;
		mListener = listener;
		mUploadTask = null;
	}

	public boolean isRunning()
	{
		return mStatus == UploadStatus.Started
				|| mStatus == UploadStatus.Connecting
				|| mStatus == UploadStatus.Connected
				|| mStatus == UploadStatus.Uploading;
	}

	public void start()
	{
		mStatus = UploadStatus.Started;
		mResponse.onStarted(mUploadRequest.getLength());
		upload();
	}

	public void pause()
	{
		if (mUploadTask != null)
		{
			mUploadTask.pause();
		}
		if (mStatus != UploadStatus.Uploading)
		{
			mUploadListener.onPaused();
		}
	}

	public void cancel()
	{
		if (mUploadTask != null)
		{
			mUploadTask.cancel();
		}
		if (mStatus != UploadStatus.Uploading)
		{
			mUploadListener.onCanceled();
		}
	}

	public void onDestroy()
	{
		// trigger the onDestroy callback tell download manager
		mListener.onDestroyed(mKey, this);
	}

	private void upload()
	{
		mStatus = UploadStatus.Uploading;

		mUploadTask = new UploadTask(mUploadRequest, mUploadListener);
		mExecutor.execute(mUploadTask);
	}

	private UploadTask.OnUploadListener mUploadListener = new UploadTask.OnUploadListener()
	{
		@Override
		public void onConnecting()
		{
			mResponse.onConnecting();
		}

		@Override
		public void onConnected()
		{
			mResponse.onConnected();
		}

		@Override
		public void onProgress(long finished, int percent)
		{
			mResponse.onUploadProgress(finished, percent);
		}

		@Override
		public void onCompleted(String serverResponse)
		{
			if (isComplete())
			{
				mStatus = UploadStatus.Completed;
				mResponse.onUploadCompleted(serverResponse);
				onDestroy();
			}
		}

		@Override
		public void onPaused()
		{
			if (!isDownloading())
			{
				mStatus = UploadStatus.Paused;
				mResponse.onUploadPaused();
				onDestroy();
			}
		}

		@Override
		public void onCanceled()
		{
			if (!isDownloading())
			{
				mStatus = UploadStatus.Canceled;
				mResponse.onUploadCanceled();
				onDestroy();
			}
		}

		@Override
		public void onFailed(FileUploaderException ue)
		{
			if (!isDownloading())
			{
				mStatus = UploadStatus.Failed;
				mResponse.onUploadFailed(ue);
				onDestroy();
			}
		}
	};

	private boolean isComplete()
	{
		if (mUploadTask != null)
		{
			return mUploadTask.isComplete();
		}
		return true;
	}

	private boolean isDownloading()
	{
		if (mUploadTask != null)
		{
			return mUploadTask.isDownloading();
		}
		return false;
	}
}
