package com.ashishdas.fileuploader.internal;

import com.ashishdas.fileuploader.FileUploadRequest;
import com.ashishdas.fileuploader.FileUploadStatusListener;
import com.ashishdas.fileuploader.internal.utils.Utils;
import com.ashishdas.fileuploader.internal.utils.WeakReferenceList;

public class FileUploadStatusObserver
{
	private WeakReferenceList<FileUploadStatusListener> mListeners = new WeakReferenceList<>();

	public void addCallback(FileUploadStatusListener callback)
	{
		if (!mListeners.has(callback))
		{
			mListeners.add(callback);
		}
	}

	public void removeCallback(FileUploadStatusListener callback)
	{
		mListeners.remove(callback);
	}

	public void notifyStatusListener(final FileUploadRequest request, final UploadInfo uploadInfo)
	{
		if (mListeners.size() > 0)
		{
			for (FileUploadStatusListener listener : mListeners)
			{
				Utils.notifyStatusListener(listener, request, uploadInfo);
			}
		}
	}
}
