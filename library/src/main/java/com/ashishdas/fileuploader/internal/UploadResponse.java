/*******************************************************************************
 * UploadResponse.java
 * UploadResponse
 * <p>
 * Author(s): Ashish Das
 ******************************************************************************/

package com.ashishdas.fileuploader.internal;

import com.ashishdas.fileuploader.FileUploaderException;
import com.ashishdas.fileuploader.FileUploaderListener;

public class UploadResponse
{
	private UploadInfoDelivery mDelivery;

	private UploadInfo mUploadInfo;

	public UploadResponse(UploadInfoDelivery delivery, FileUploaderListener fileUploaderListener)
	{
		mDelivery = delivery;
		mUploadInfo = new UploadInfo();
		mUploadInfo.setFileUploaderListener(fileUploaderListener);
	}

	public void onStarted(long length)
	{
		mUploadInfo.setLength(length);
		mUploadInfo.setUploadStatus(UploadStatus.Started);
		mUploadInfo.getFileUploaderListener().onStarted();
	}

	public void onConnecting()
	{
		mUploadInfo.setUploadStatus(UploadStatus.Connecting);
		mDelivery.post(mUploadInfo);
	}

	public void onConnected()
	{
		mUploadInfo.setUploadStatus(UploadStatus.Connected);
		mDelivery.post(mUploadInfo);
	}

	public void onUploadProgress(long finished, int percent)
	{
		mUploadInfo.setFinished(finished);
		mUploadInfo.setPercent(percent);
		mUploadInfo.setUploadStatus(UploadStatus.Uploading);
		mDelivery.post(mUploadInfo);
	}

	public void onUploadCompleted(String serverResponse)
	{
		mUploadInfo.setFinished(mUploadInfo.getLength());
		mUploadInfo.setPercent(100);
		mUploadInfo.setUploadStatus(UploadStatus.Completed);
		mUploadInfo.setServerResponse(serverResponse);
		mDelivery.post(mUploadInfo);
	}

	public void onUploadPaused()
	{
		mUploadInfo.setUploadStatus(UploadStatus.Paused);
		mDelivery.post(mUploadInfo);
	}

	public void onUploadCanceled()
	{
		mUploadInfo.setUploadStatus(UploadStatus.Canceled);
		mDelivery.post(mUploadInfo);
	}

	public void onUploadFailed(FileUploaderException e)
	{
		mUploadInfo.setFileUploaderException(e);
		mUploadInfo.setUploadStatus(UploadStatus.Failed);
		mDelivery.post(mUploadInfo);
	}
}
