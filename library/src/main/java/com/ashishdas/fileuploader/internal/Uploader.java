/*******************************************************************************
 * Uploader.java
 * Uploader
 * <p>
 * Author(s): Ashish Das
 ******************************************************************************/

package com.ashishdas.fileuploader.internal;

import com.ashishdas.fileuploader.FileUploadException;
import com.ashishdas.fileuploader.FileUploadStatus;

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

	private FileUploadStatus mStatus;
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
		return mStatus == FileUploadStatus.Started
				|| mStatus == FileUploadStatus.Connecting
				|| mStatus == FileUploadStatus.Connected
				|| mStatus == FileUploadStatus.Uploading;
	}

	public void start()
	{
		mStatus = FileUploadStatus.Started;
		mResponse.onStarted(mUploadRequest.getLength());
		upload();
	}

	public void pause()
	{
		if (mUploadTask != null)
		{
			mUploadTask.pause();
		}
		if (mStatus != FileUploadStatus.Uploading)
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
		if (mStatus != FileUploadStatus.Uploading)
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
		mStatus = FileUploadStatus.Uploading;

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
				mStatus = FileUploadStatus.Completed;
				mResponse.onUploadCompleted(serverResponse);
				onDestroy();
			}
		}

		@Override
		public void onPaused()
		{
			if (!isDownloading())
			{
				mStatus = FileUploadStatus.Paused;
				mResponse.onUploadPaused();
				onDestroy();
			}
		}

		@Override
		public void onCanceled()
		{
			if (!isDownloading())
			{
				mStatus = FileUploadStatus.Canceled;
				mResponse.onUploadCanceled();
				onDestroy();
			}
		}

		@Override
		public void onFailed(FileUploadException ue)
		{
			if (!isDownloading())
			{
				mStatus = FileUploadStatus.Failed;
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
