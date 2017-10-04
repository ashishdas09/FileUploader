/*******************************************************************************
 * FileUploadRequest.java
 * FileUploadRequest
 * <p>
 * Author(s): Ashish Das
 ******************************************************************************/

package com.ashishdas.fileuploader;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.ashishdas.fileuploader.internal.utils.KeyGenerator;

import java.io.File;

public class FileUploadRequest implements Parcelable
{
	private String mKey;
	private String mFilePath;

	public FileUploadRequest(final String key, final File file)
	{
		mKey = key;
		mFilePath = getPathFromFile(file);
	}

	public FileUploadRequest(final String key, final String filePath)
	{
		mKey = key;
		mFilePath = filePath;
	}

	public FileUploadRequest(final File file)
	{
		mFilePath = getPathFromFile(file);
		mKey = getKeyFromFilePath(mFilePath);
	}

	public FileUploadRequest(final String filePath)
	{
		mFilePath = filePath;
		mKey = getKeyFromFilePath(mFilePath);
	}

	protected FileUploadRequest(Parcel in)
	{
		mKey = in.readString();
		mFilePath = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeString(mKey);
		dest.writeString(mFilePath);
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	public static final Creator<FileUploadRequest> CREATOR = new Creator<FileUploadRequest>()
	{
		@Override
		public FileUploadRequest createFromParcel(Parcel in)
		{
			return new FileUploadRequest(in);
		}

		@Override
		public FileUploadRequest[] newArray(int size)
		{
			return new FileUploadRequest[size];
		}
	};

	public String getKey()
	{
		return mKey;
	}

	public String getFilePath()
	{
		return mFilePath;
	}

	@Override
	public String toString()
	{
		return "FileUploadRequest{" +
				"mKey='" + mKey + '\'' +
				", mFilePath='" + mFilePath + '\'' +
				'}';
	}

	private String getPathFromFile(File file)
	{
		return (file != null && !(file.exists() && file.isFile())) ? file.getAbsolutePath() : "";
	}

	private String getKeyFromFilePath(String filePath)
	{
		if (!TextUtils.isEmpty(filePath))
		{
			return KeyGenerator.generate(filePath);
		}
		return "";
	}
}
