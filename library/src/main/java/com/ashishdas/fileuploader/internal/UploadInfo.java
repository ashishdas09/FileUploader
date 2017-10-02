/*******************************************************************************
 * UploadInfo.java
 * UploadInfo
 * <p>
 * Author(s): Ashish Das
 ******************************************************************************/

package com.ashishdas.fileuploader.internal;

import com.ashishdas.fileuploader.FileUploaderException;
import com.ashishdas.fileuploader.FileUploaderListener;

public class UploadInfo
{
	private long mTime;
	private int mPercent;
	private long mLength;
	private long mFinished;
	private boolean mAcceptRanges;
	private String mServerResponse;
	private FileUploaderException mFileUploaderException;
	private UploadStatus mUploadStatus;
	private FileUploaderListener mFileUploaderListener;

	public UploadInfo()
	{
	}

	public long getTime()
	{
		return mTime;
	}

	public void setTime(final long time)
	{
		mTime = time;
	}

	public int getPercent()
	{
		return mPercent;
	}

	public void setPercent(final int percent)
	{
		mPercent = percent;
	}

	public long getLength()
	{
		return mLength;
	}

	public void setLength(final long length)
	{
		mLength = length;
	}

	public long getFinished()
	{
		return mFinished;
	}

	public void setFinished(final long finished)
	{
		mFinished = finished;
	}

	public boolean isAcceptRanges()
	{
		return mAcceptRanges;
	}

	public void setAcceptRanges(final boolean acceptRanges)
	{
		mAcceptRanges = acceptRanges;
	}

	public String getServerResponse()
	{
		return mServerResponse;
	}

	public void setServerResponse(final String serverResponse)
	{
		mServerResponse = serverResponse;
	}

	public FileUploaderException getFileUploaderException()
	{
		return mFileUploaderException;
	}

	public void setFileUploaderException(final FileUploaderException fileUploaderException)
	{
		mFileUploaderException = fileUploaderException;
	}

	public UploadStatus getUploadStatus()
	{
		return mUploadStatus;
	}

	public void setUploadStatus(final UploadStatus uploadStatus)
	{
		mUploadStatus = uploadStatus;
	}

	public FileUploaderListener getFileUploaderListener()
	{
		return mFileUploaderListener;
	}

	public void setFileUploaderListener(final FileUploaderListener fileUploaderListener)
	{
		mFileUploaderListener = fileUploaderListener;
	}

	@Override
	public String toString()
	{
		return "UploadInfo{" +
				"mTime=" + mTime +
				", mPercent=" + mPercent +
				", mLength=" + mLength +
				", mFinished=" + mFinished +
				", mAcceptRanges=" + mAcceptRanges +
				", mServerResponse='" + mServerResponse +
				", mFileUploaderException=" + mFileUploaderException +
				", mUploadStatus=" + mUploadStatus +
				", mFileUploaderListener=" + mFileUploaderListener +
				'}';
	}
}
