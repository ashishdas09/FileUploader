/*******************************************************************************
 * UploadInfoDelivery.java
 * UploadInfoDelivery
 * <p>
 * Author(s): Ashish Das
 ******************************************************************************/

package com.ashishdas.fileuploader.internal;

import android.os.Handler;

import com.ashishdas.fileuploader.FileUploaderListener;

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

	public void post(final UploadInfo status)
	{
		mUploadStatusPoster.execute(new UploadStatusDeliveryRunnable(status));
	}

	private class UploadStatusDeliveryRunnable implements Runnable
	{
		private final UploadInfo mUploadInfo;

		public UploadStatusDeliveryRunnable(final UploadInfo uploadInfo)
		{
			this.mUploadInfo = uploadInfo;
		}

		@Override
		public synchronized void run()
		{
			FileUploaderListener callBack = mUploadInfo.getFileUploaderListener();
			if (callBack != null)
			{
				switch (mUploadInfo.getUploadStatus())
				{
					case Connecting:
						callBack.onConnecting();
						break;
					case Connected:
						callBack.onConnected();
						break;
					case Uploading:
						callBack.onUploading(mUploadInfo.getFinished(), mUploadInfo.getLength(), mUploadInfo.getPercent());
						break;
					case Completed:
						callBack.onUploading(mUploadInfo.getLength(), mUploadInfo.getLength(), mUploadInfo.getPercent());
						callBack.onCompleted(mUploadInfo.getServerResponse());
						break;
					case Paused:
						callBack.onPaused();
						break;
					case Canceled:
						callBack.onCanceled();
						break;
					case Failed:
						callBack.onFailed(mUploadInfo.getFileUploaderException());
						break;
				}
			}
		}
	}
}