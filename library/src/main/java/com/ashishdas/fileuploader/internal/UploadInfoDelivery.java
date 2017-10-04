/*******************************************************************************
 * UploadInfoDelivery.java
 * UploadInfoDelivery
 * <p>
 * Author(s): Ashish Das
 ******************************************************************************/

package com.ashishdas.fileuploader.internal;

import android.os.Handler;

import com.ashishdas.fileuploader.FileUploadRequest;
import com.ashishdas.fileuploader.FileUploadStatusListener;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;

public class UploadInfoDelivery
{
	private Executor mUploadStatusPoster;

	public UploadInfoDelivery(final Handler handler)
	{
		mUploadStatusPoster = new Executor()
		{
			@Override
			public void execute(Runnable command)
			{
				handler.postAtFrontOfQueue(command);
			}
		};
	}

	public void post(final FileUploadRequest fileUploadRequest, final UploadInfo uploadInfo, WeakReference<FileUploadStatusListener> fileUploadStatusListener)
	{
		mUploadStatusPoster.execute(new UploadStatusDeliveryRunnable(fileUploadRequest, uploadInfo, fileUploadStatusListener));
	}

	private class UploadStatusDeliveryRunnable implements Runnable
	{
		private final UploadInfo mUploadInfo;
		private final FileUploadRequest mFileUploadRequest;
		private final WeakReference<FileUploadStatusListener> mFileUploadStatusListener;

		public UploadStatusDeliveryRunnable(final FileUploadRequest fileUploadRequest, final UploadInfo uploadInfo,
		                                    final WeakReference<FileUploadStatusListener> fileUploadStatusListener)
		{
			mUploadInfo = uploadInfo;
			mFileUploadRequest = fileUploadRequest;
			mFileUploadStatusListener = fileUploadStatusListener;
		}

		@Override
		public synchronized void run()
		{
			UploadInfoDelivery.notifyStatus(mFileUploadRequest, mUploadInfo, mFileUploadStatusListener);
		}
	}

	public synchronized static void notifyStatus(final FileUploadRequest request, final UploadInfo uploadInfo,
	                                             final WeakReference<FileUploadStatusListener> fileUploadStatusListener)
	{
		if (fileUploadStatusListener != null)
		{
			FileUploadStatusListener listener = fileUploadStatusListener.get();
			if (listener != null)
			{
				switch (uploadInfo.getStatus())
				{
					case Connecting:
						listener.onConnecting(request);
						break;
					case Connected:
						listener.onConnected(request);
						break;
					case Uploading:
						listener.onUploading(request, uploadInfo.getFinished(), uploadInfo.getLength(), uploadInfo.getPercent());
						break;
					case Completed:
						listener.onUploading(request, uploadInfo.getFinished(), uploadInfo.getLength(), uploadInfo.getPercent());
						listener.onCompleted(request, uploadInfo.getServerResponse());
						break;
					case Paused:
						listener.onPaused(request);
						break;
					case Canceled:
						listener.onCanceled(request);
						break;
					case Failed:
						listener.onFailed(request, uploadInfo.getFileUploadException());
						break;
				}
			}
		}


	}
}