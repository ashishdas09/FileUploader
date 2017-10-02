/*******************************************************************************
 * UploadStatus.java
 * UploadStatus
 * <p>
 * Author(s): Ashish Das
 ******************************************************************************/

package com.ashishdas.fileuploader.internal;

public enum UploadStatus
{
	Started(91),
	Connecting(92),
	Connected(93),
	Uploading(94),
	Completed(95),
	Paused(96),
	Canceled(97),
	Failed(98);

	private int mStatusCode;

	UploadStatus(int code)
	{
		mStatusCode = code;
	}

	public int toInt()
	{
		return mStatusCode;
	}

	@Override
	public String toString()
	{
		switch (this)
		{
			case Started:
				return "Started";
			case Connecting:
				return "Connecting";
			case Connected:
				return "connected";
			case Uploading:
				return "Uploading";
			case Completed:
				return "Completed";
			case Paused:
				return "Paused";
			case Canceled:
				return "Canceled";
			case Failed:
				return "Failed";
		}
		return "UploadStatus{" +
				"mStatusCode=" + mStatusCode +
				'}';
	}

	public static UploadStatus getStatus(int code)
	{
		if (code == UploadStatus.Started.toInt())
		{
			return UploadStatus.Started;
		}
		else if (code == UploadStatus.Connecting.toInt())
		{
			return UploadStatus.Connecting;
		}
		else if (code == UploadStatus.Connected.toInt())
		{
			return UploadStatus.Connected;
		}
		else if (code == UploadStatus.Uploading.toInt())
		{
			return UploadStatus.Uploading;
		}
		else if (code == UploadStatus.Completed.toInt())
		{
			return UploadStatus.Completed;
		}
		else if (code == UploadStatus.Paused.toInt())
		{
			return UploadStatus.Paused;
		}
		else if (code == UploadStatus.Canceled.toInt())
		{
			return UploadStatus.Canceled;
		}
		else if (code == UploadStatus.Failed.toInt())
		{
			return UploadStatus.Failed;
		}

		return null;
	}
}
