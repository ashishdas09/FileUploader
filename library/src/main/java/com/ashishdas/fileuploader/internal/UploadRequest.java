/*******************************************************************************
 * UploadRequest.java
 * UploadRequest
 * <p>
 * Author(s): Ashish Das
 ******************************************************************************/

package com.ashishdas.fileuploader.internal;

import android.text.TextUtils;

import java.io.File;

public class UploadRequest
{
	private File mFile;
	private String mFileName;
	private long mProgress;
	private long mLength;

	public UploadRequest(final File file)
	{
		mFile = file;
		mFileName = file.getName();
		try
		{
			mLength = mFile.length();
		}
		catch (Exception e)
		{
		}
	}

	void setFile(final File file)
	{
		mFile = file;
	}

	public File getFile()
	{
		return mFile;
	}

	public String getFileName()
	{
		return TextUtils.isEmpty(mFileName) ? "" : mFileName;
	}

	public void setFileName(final String fileName)
	{
		mFileName = fileName;
	}

	public long getProgress()
	{
		return mProgress;
	}

	public void setProgress(final long progress)
	{
		mProgress = progress;
	}

	public long getLength()
	{
		return mLength;
	}

	public int getPercent()
	{
		int percent = 0;
		try
		{
			percent = (int) (getProgress() * 100 / getLength());
		}
		catch (Exception e)
		{

		}
		return percent;
	}

	@Override
	public String toString()
	{
		return "UploadRequest{" +
				"mFile=" + mFile +
				", mFileName=" + mFileName +
				", mProgress=" + mProgress +
				", mLength=" + mLength +
				'}';
	}
}
