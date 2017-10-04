/*******************************************************************************
 * UploadInfo.java
 * UploadInfo
 * <p>
 * Author(s): Ashish Das
 ******************************************************************************/

package com.ashishdas.fileuploader.internal;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.ashishdas.fileuploader.FileUploadException;
import com.ashishdas.fileuploader.FileUploadStatus;

public class UploadInfo implements Parcelable
{
	private FileUploadStatus mStatus;

	private int mPercent; // the percent of progress (finished/total)*100
	private long mLength; // the total length of the file same value with method
	private long mFinished; // the downloaded length of the file

	private String mServerResponse;

	private int mErrorCode;
	private String mErrorMessage;

	public UploadInfo()
	{
	}

	protected UploadInfo(Parcel in)
	{
		mStatus = FileUploadStatus.getStatus(in.readInt());

		mPercent = in.readInt();
		mLength = in.readLong();
		mFinished = in.readLong();

		mServerResponse = in.readString();

		mErrorCode = in.readInt();
		mErrorMessage = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeInt(mStatus != null ? mStatus.toInt() : 0);

		dest.writeInt(mPercent);
		dest.writeLong(mLength);
		dest.writeLong(mFinished);

		dest.writeString(mServerResponse);

		dest.writeInt(mErrorCode);
		dest.writeString(mErrorMessage);
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	public static final Creator<UploadInfo> CREATOR = new Creator<UploadInfo>()
	{
		@Override
		public UploadInfo createFromParcel(Parcel in)
		{
			return new UploadInfo(in);
		}

		@Override
		public UploadInfo[] newArray(int size)
		{
			return new UploadInfo[size];
		}
	};

	public FileUploadStatus getStatus()
	{
		return mStatus;
	}

	public void setFileUploadStatus(final FileUploadStatus status)
	{
		mStatus = status;
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

	public String getServerResponse()
	{
		return mServerResponse;
	}

	public void setServerResponse(final String serverResponse)
	{
		mServerResponse = serverResponse;
	}

	public void setErrorMessage(final String errorMessage)
	{
		mErrorMessage = errorMessage;
	}

	public void setErrorCode(final int errorCode)
	{
		mErrorCode = errorCode;
	}

	public FileUploadException getFileUploadException()
	{
		if (TextUtils.isEmpty(mErrorMessage))
		{
			return null;
		}
		return new FileUploadException(mErrorCode, mErrorMessage);
	}

	@Override
	public String toString()
	{
		return "UploadInfo{" +
				"mStatus=" + mStatus +
				", mPercent=" + mPercent +
				", mLength=" + mLength +
				", mFinished=" + mFinished +
				", mServerResponse='" + mServerResponse + '\'' +
				", mException=" + getFileUploadException() +
				'}';
	}
}
