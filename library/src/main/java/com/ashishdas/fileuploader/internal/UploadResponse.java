/*******************************************************************************
 * UploadResponse.java
 * UploadResponse
 * <p>
 * Author(s): Ashish Das
 ******************************************************************************/

package com.ashishdas.fileuploader.internal;

import com.ashishdas.fileuploader.FileUploadException;
import com.ashishdas.fileuploader.FileUploadRequest;
import com.ashishdas.fileuploader.FileUploadStatus;

public class UploadResponse
{
	private UploadInfo mUploadInfo;
	private UploadInfoDelivery mDelivery;

	private final FileUploadRequest mFileUploadRequest;
	private UploadStatusListener mUploadStatusListener;

	public UploadResponse(FileUploadRequest fileUploadRequest, UploadInfoDelivery delivery, UploadStatusListener uploadStatusListener)
	{
		mDelivery = delivery;
		mUploadInfo = new UploadInfo();
		mFileUploadRequest = fileUploadRequest;
		mUploadStatusListener = uploadStatusListener;
	}

	public void onStarted(long length)
	{
		mUploadInfo.setLength(length);
		mUploadInfo.setFileUploadStatus(FileUploadStatus.Started);
		notifyStatus();
	}

	public void onConnecting()
	{
		mUploadInfo.setFileUploadStatus(FileUploadStatus.Connecting);
		notifyStatus();
	}

	public void onConnected()
	{
		mUploadInfo.setFileUploadStatus(FileUploadStatus.Connected);
		notifyStatus();
	}

	public void onUploadProgress(long finished, int percent)
	{
		mUploadInfo.setPercent(percent);
		mUploadInfo.setFinished(finished);
		mUploadInfo.setFileUploadStatus(FileUploadStatus.Uploading);
		postDelivery();
	}

	public void onUploadCompleted(String serverResponse)
	{
		mUploadInfo.setPercent(100);
		mUploadInfo.setFinished(mUploadInfo.getLength());
		mUploadInfo.setServerResponse(serverResponse);
		mUploadInfo.setFileUploadStatus(FileUploadStatus.Completed);
		postDelivery();
	}

	public void onUploadPaused()
	{
		mUploadInfo.setFileUploadStatus(FileUploadStatus.Paused);
		postDelivery();
	}

	public void onUploadCanceled()
	{
		mUploadInfo.setFileUploadStatus(FileUploadStatus.Canceled);
		postDelivery();
	}

	public void onUploadFailed(FileUploadException e)
	{
		if (e != null)
		{
			mUploadInfo.setErrorCode(e.getErrorCode());
			mUploadInfo.setErrorMessage(e.getLocalizedMessage());
		}
		mUploadInfo.setFileUploadStatus(FileUploadStatus.Failed);
		postDelivery();
	}

	private void notifyStatus()
	{
		UploadInfoDelivery.notifyStatus(mDelivery.getContext(), mFileUploadRequest, mUploadInfo, mUploadStatusListener);
	}

	private void postDelivery()
	{
		mDelivery.post(mFileUploadRequest, mUploadInfo, mUploadStatusListener);
	}
}
