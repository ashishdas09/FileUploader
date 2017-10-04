/*******************************************************************************
 * UploadInfoDelivery.java
 * UploadInfoDelivery
 * <p>
 * Author(s): Ashish Das
 ******************************************************************************/

package com.ashishdas.fileuploader.internal;

import android.content.Context;
import android.os.Handler;

import com.ashishdas.fileuploader.FileUploadRequest;

import java.util.concurrent.Executor;

public class UploadInfoDelivery
{
	private Context mContext;
	private Executor mUploadStatusPoster;

	public UploadInfoDelivery(final Context context, final Handler handler)
	{
		mContext = context;
		mUploadStatusPoster = new Executor()
		{
			@Override
			public void execute(Runnable command)
			{
				handler.postAtFrontOfQueue(command);
			}
		};
	}

	public Context getContext()
	{
		return mContext;
	}

	public void post(final FileUploadRequest fileUploadRequest, final UploadInfo uploadInfo, UploadStatusListener uploadStatusListener)
	{
		mUploadStatusPoster.execute(new UploadStatusDeliveryRunnable(fileUploadRequest, uploadInfo, uploadStatusListener));
	}

	private class UploadStatusDeliveryRunnable implements Runnable
	{
		private final UploadInfo mUploadInfo;
		private final FileUploadRequest mFileUploadRequest;
		private final UploadStatusListener mFileUploadStatusListener;

		public UploadStatusDeliveryRunnable(final FileUploadRequest fileUploadRequest, final UploadInfo uploadInfo,
		                                    final UploadStatusListener uploadStatusListener)
		{
			mUploadInfo = uploadInfo;
			mFileUploadRequest = fileUploadRequest;
			mFileUploadStatusListener = uploadStatusListener;
		}

		@Override
		public synchronized void run()
		{
			UploadInfoDelivery.notifyStatus(mContext, mFileUploadRequest, mUploadInfo, mFileUploadStatusListener);
		}
	}

	public synchronized static void notifyStatus(final Context context, final FileUploadRequest request, final UploadInfo uploadInfo,
	                                             final UploadStatusListener uploadStatusListener)
	{
		if (uploadStatusListener != null)
		{
			uploadStatusListener.onUploadStatus(request, uploadInfo);
		}

		UploadServiceHelper.sendBroadCast(context, request, uploadInfo);
	}
}