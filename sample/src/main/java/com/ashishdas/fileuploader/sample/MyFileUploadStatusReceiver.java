/*******************************************************************************
 * FileUploadStatusReceiver.java
 * FileUploadStatusReceiver
 * <p>
 * Author(s): Ashish Das
 ******************************************************************************/

package com.ashishdas.fileuploader.sample;

import android.util.Log;

import com.ashishdas.fileuploader.FileUploadException;
import com.ashishdas.fileuploader.FileUploadRequest;
import com.ashishdas.fileuploader.FileUploadStatusReceiver;

public class MyFileUploadStatusReceiver extends FileUploadStatusReceiver
{
	private static final String TAG = "MyStatusReceiver";

	@Override
	public void onStarted(final FileUploadRequest request)
	{
		Log.d(TAG, "onStarted()");
	}

	@Override
	public void onConnecting(final FileUploadRequest request)
	{
		Log.d(TAG, "onConnecting()");
	}

	@Override
	public void onConnected(final FileUploadRequest request)
	{
		Log.d(TAG, "onConnected()");
	}

	@Override
	public void onUploading(final FileUploadRequest request, final long finished, final long total, final int progress)
	{
		Log.d(TAG, "onUploading total: " + total + ", progress: " + progress);
	}

	@Override
	public void onCompleted(final FileUploadRequest request, final String serverResponse)
	{
		Log.d(TAG, "onCompleted() ServerResponse : " + serverResponse);
	}

	@Override
	public void onPaused(final FileUploadRequest request)
	{
		Log.d(TAG, "onPaused()");
	}

	@Override
	public void onCanceled(final FileUploadRequest request)
	{
		Log.d(TAG, "onCanceled()");
	}

	@Override
	public void onFailed(final FileUploadRequest request, final FileUploadException e)
	{
		Log.e(TAG, "onFailed() : " + e.getLocalizedMessage(), e);
	}
}
