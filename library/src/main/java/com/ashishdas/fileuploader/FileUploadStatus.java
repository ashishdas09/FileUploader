/*******************************************************************************
 * FileUploadStatus.java
 * FileUploadStatus
 * <p>
 * Author(s): Ashish Das
 ******************************************************************************/

package com.ashishdas.fileuploader;

public enum FileUploadStatus
{
	Started(1),
	Connecting(2),
	Connected(3),
	Uploading(4),
	Paused(5),
	Completed(6),
	Canceled(7),
	Failed(8);

	private int mStatusCode;

	FileUploadStatus(int code)
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
		return "FileUploadStatus{" +
				"mStatusCode=" + mStatusCode +
				'}';
	}

	public static FileUploadStatus getStatus(int code)
	{
		if (code == FileUploadStatus.Started.toInt())
		{
			return FileUploadStatus.Started;
		}
		else if (code == FileUploadStatus.Connecting.toInt())
		{
			return FileUploadStatus.Connecting;
		}
		else if (code == FileUploadStatus.Connected.toInt())
		{
			return FileUploadStatus.Connected;
		}
		else if (code == FileUploadStatus.Uploading.toInt())
		{
			return FileUploadStatus.Uploading;
		}
		else if (code == FileUploadStatus.Completed.toInt())
		{
			return FileUploadStatus.Completed;
		}
		else if (code == FileUploadStatus.Paused.toInt())
		{
			return FileUploadStatus.Paused;
		}
		else if (code == FileUploadStatus.Canceled.toInt())
		{
			return FileUploadStatus.Canceled;
		}
		else if (code == FileUploadStatus.Failed.toInt())
		{
			return FileUploadStatus.Failed;
		}

		return null;
	}
}
