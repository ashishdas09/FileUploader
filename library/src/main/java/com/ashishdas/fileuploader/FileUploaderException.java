/*******************************************************************************
 * FileUploaderException.java
 * FileUploaderException
 * <p>
 * Author(s): Ashish Das
 ******************************************************************************/

package com.ashishdas.fileuploader;

import android.text.TextUtils;

public class FileUploaderException extends Exception
{
	private String errorMessage;
	private int errorCode;

	public FileUploaderException(String detailMessage)
	{
		super(detailMessage);
		this.errorMessage = detailMessage;
	}

	public FileUploaderException(int errorCode, String detailMessage)
	{
		super(detailMessage);
		this.errorCode = errorCode;
		this.errorMessage = detailMessage;
	}

	public FileUploaderException(int errorCode, Throwable throwable)
	{
		super(throwable);
		this.errorCode = errorCode;
		setErrorMessage(throwable);
	}

	public FileUploaderException(int errorCode, String detailMessage, Throwable throwable)
	{
		super(detailMessage, throwable);
		this.errorCode = errorCode;
		this.errorMessage = detailMessage;
		setErrorMessage(throwable);
	}

	public int getErrorCode()
	{
		return errorCode;
	}

	private void setErrorMessage(Throwable throwable)
	{
		if (throwable == null)
		{
			return;
		}
		String message = throwable.getLocalizedMessage();
		if (!TextUtils.isEmpty(message))
		{
			if (TextUtils.isEmpty(this.errorMessage))
			{
				this.errorMessage = message;
				return;
			}
			this.errorMessage += ": " + message;
		}
	}
}
