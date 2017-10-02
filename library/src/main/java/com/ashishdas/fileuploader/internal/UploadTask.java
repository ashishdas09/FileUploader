/*******************************************************************************
 * DownloadTask.java
 * DownloadTask
 * <p>
 * Author(s): Ashish Das
 ******************************************************************************/

package com.ashishdas.fileuploader.internal;

import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import com.ashishdas.fileuploader.FileUploaderException;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.util.Map;

public class UploadTask implements Runnable
{
	public interface OnUploadListener
	{
		void onConnecting();

		void onConnected();

		void onProgress(long finished, int percent);

		void onCompleted(String serverResponse);

		void onPaused();

		void onCanceled();

		void onFailed(FileUploaderException ue);
	}

	private static final String TAG = UploadTask.class.getSimpleName();

	private static final String HTTP_POST = "POST";
	private static final String HTTP_HEADER_CONNECTION = "Connection";
	private static final String HTTP_HEADER_VAL_KEEP_ALIVE = "Keep-Alive";

	private static final String HTTP_HEADER_CACHE_CONTROL = "Cache-Control";
	private static final String HTTP_HEADER_VAL_NO_CACHE = "no-cache";

	private static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
	private static final String HTTP_HEADER_VAL_MULTIPART = "multipart/form-data;boundary=";
	private static final String HTTP_HEADER_CONTENT_DISPOSITION = "Content-Disposition";

	private static final String HTTP_HEADER_CONTENT_LENGTH = "Content-length";

	private static final String HTTP_CONTENT_DISPOSITION_NAME = "media";

	private static final String LINE_END = "\r\n";
	private static final String TWO_HYPHENS = "--";
	private final String BOUNDARY = "*****" + Long.toString(System.currentTimeMillis()) + "*****";

	private final UploadRequest mUploadRequest;

	private final OnUploadListener mOnUploadListener;

	private volatile UploadStatus mStatus;

	private volatile UploadStatus mCommend;

	private String mResponse = null;

	public UploadTask(UploadRequest mUploadRequest, OnUploadListener listener)
	{
		this.mResponse = null;
		this.mUploadRequest = mUploadRequest;
		this.mOnUploadListener = listener;
	}

	public void cancel()
	{
		mCommend = UploadStatus.Canceled;
	}

	public void pause()
	{
		mCommend = UploadStatus.Paused;
	}

	public boolean isDownloading()
	{
		return mStatus == UploadStatus.Uploading;
	}

	public boolean isComplete()
	{
		return mStatus == UploadStatus.Completed;
	}

	public boolean isPaused()
	{
		return mStatus == UploadStatus.Paused;
	}

	public boolean isCanceled()
	{
		return mStatus == UploadStatus.Canceled;
	}

	public boolean isFailed()
	{
		return mStatus == UploadStatus.Failed;
	}

	@Override
	public void run()
	{
		Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
		try
		{
			mStatus = UploadStatus.Connecting;
			mOnUploadListener.onConnecting();
			executeUpload();
			synchronized (mOnUploadListener)
			{
				mStatus = UploadStatus.Completed;
				mOnUploadListener.onCompleted(mResponse);
			}
		}
		catch (FileUploaderException e)
		{
			handleUploadException(e);
		}
	}

	private void handleUploadException(FileUploaderException exception)
	{
		if (exception != null)
		{
			int errorCode = exception.getErrorCode();

			if (errorCode == UploadStatus.Failed.toInt())
			{
				synchronized (mOnUploadListener)
				{
					mStatus = UploadStatus.Failed;
					mOnUploadListener.onFailed(exception);
				}
				return;
			}
			else if (errorCode == UploadStatus.Paused.toInt())
			{
				synchronized (mOnUploadListener)
				{
					mStatus = UploadStatus.Paused;
					mOnUploadListener.onPaused();
				}
				return;
			}
			else if (errorCode == UploadStatus.Canceled.toInt())
			{
				synchronized (mOnUploadListener)
				{
					mStatus = UploadStatus.Canceled;
					mOnUploadListener.onCanceled();
				}
				return;
			}
		}

		throw new IllegalArgumentException("Unknown state");
	}

	private void executeUpload() throws FileUploaderException
	{
		HttpURLConnection httpConnection = null;
		try
		{
			httpConnection = (HttpURLConnection) Globals.sURL.openConnection();
			httpConnection.setDoOutput(true);

			// Setup the request:
			httpConnection.setRequestMethod(HTTP_POST);
			httpConnection.setRequestProperty(HTTP_HEADER_CONNECTION, HTTP_HEADER_VAL_KEEP_ALIVE);
			httpConnection.setRequestProperty(HTTP_HEADER_CACHE_CONTROL, HTTP_HEADER_VAL_NO_CACHE);
			httpConnection.setRequestProperty(HTTP_HEADER_CONTENT_TYPE, HTTP_HEADER_VAL_MULTIPART + BOUNDARY);

			final Map<String, String> headers = Globals.sHeaders;
			if (headers != null && !headers.isEmpty())
			{
				for (String key : headers.keySet())
				{
					httpConnection.setRequestProperty(key, headers.get(key));
				}
			}

			String tail = LINE_END + TWO_HYPHENS + BOUNDARY + TWO_HYPHENS + LINE_END;
			long fileLength = mUploadRequest.getLength() + tail.length();

			String fileHeader = TWO_HYPHENS + BOUNDARY + LINE_END +
					HTTP_HEADER_CONTENT_DISPOSITION + ": form-data; name=\"" + HTTP_CONTENT_DISPOSITION_NAME + "\";filename=\"" +
					TextUtils.isEmpty(mUploadRequest.getFileName()) + "\"" + LINE_END + LINE_END;

			long requestLength = fileHeader.length() + fileLength;
			httpConnection.setRequestProperty(HTTP_HEADER_CONTENT_LENGTH, "" + requestLength);
			httpConnection.setFixedLengthStreamingMode((int) requestLength);
			httpConnection.connect();

			synchronized (mOnUploadListener)
			{
				mStatus = UploadStatus.Connected;
				mOnUploadListener.onConnected();
			}

			// Wrap the attachment:
			requestBody(httpConnection, fileHeader, mUploadRequest.getFile(), tail);

			// Get response:
			captureResponse(httpConnection);
		}
		catch (ProtocolException e)
		{
			throw new FileUploaderException(UploadStatus.Failed.toInt(), "Protocol error", e);
		}
		catch (IOException e)
		{
			throw new FileUploaderException(UploadStatus.Failed.toInt(), "IO error", e);
		}
		finally
		{
			if (httpConnection != null)
			{
				httpConnection.disconnect();
			}
		}
	}

	/**
	 * Write data file into header and data output stream.
	 */
	private void requestBody(HttpURLConnection httpConnection, String fileHeader, File file, String tail) throws FileUploaderException
	{
		FileUploaderException exception = null;
		DataOutputStream request = null;
		try
		{
			request = new DataOutputStream(httpConnection.getOutputStream());

			// Start content wrapper:
			request.writeBytes(fileHeader);
			request.flush();

			buildData(request, file);

			// End content wrapper:
			request.writeBytes(tail);
		}
		catch (Exception e)
		{
			exception = new FileUploaderException(UploadStatus.Failed.toInt(), "File error", e);
		}

		if (request != null)
		{
			try
			{
				request.flush();
				request.close();
			}
			catch (IOException e)
			{
			}
		}

		if (exception != null)
		{
			throw exception;
		}
	}

	/**
	 * Write data file into data output stream.
	 */
	private void buildData(DataOutputStream dataOutputStream, File file) throws FileUploaderException
	{
		try
		{
			FileInputStream fileInputStream = new FileInputStream(file);
			int bytesAvailable = fileInputStream.available();

			synchronized (mOnUploadListener)
			{
				mStatus = UploadStatus.Uploading;
				mOnUploadListener.onProgress(mUploadRequest.getProgress(), 0);
			}

			int maxBufferSize = 1024;
			int bufferSize = Math.min(bytesAvailable, maxBufferSize);
			byte[] buffer = new byte[bufferSize];

			int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			while (bytesRead > 0)
			{
				checkPausedOrCanceled();
				try
				{
					dataOutputStream.write(buffer, 0, bufferSize);
					dataOutputStream.flush();
					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					synchronized (mOnUploadListener)
					{
						final int lastUploadedPercent = mUploadRequest.getPercent();
						mUploadRequest.setProgress(mUploadRequest.getProgress() + bufferSize);
						final int currentPrecent = mUploadRequest.getPercent();
						if (currentPrecent > lastUploadedPercent)
						{
							mOnUploadListener.onProgress(mUploadRequest.getProgress(), currentPrecent);
						}
					}
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);
				}
				catch (IOException e)
				{
					throw new FileUploaderException(UploadStatus.Failed.toInt(), e);
				}
			}
		}
		catch (Exception e)
		{
			throw new FileUploaderException(UploadStatus.Failed.toInt(), e);
		}
	}

	private void captureResponse(HttpURLConnection httpConnection) throws FileUploaderException
	{
		mResponse = "";
		InputStream inputStream = null;
		BufferedReader responseStreamReader = null;
		try
		{
			try
			{
				inputStream = httpConnection.getInputStream();
			}
			catch (IOException e)
			{
				throw new FileUploaderException(UploadStatus.Failed.toInt(), "http get inputStream error", e);
			}

			responseStreamReader = new BufferedReader(new InputStreamReader(inputStream));
			String line = "";
			StringBuilder stringBuilder = new StringBuilder();
			try
			{
				while ((line = responseStreamReader.readLine()) != null)
				{
					stringBuilder.append(line).append("\n");
				}

				Log.d(TAG, "Server Response: " + stringBuilder + ", FilePath: " + mUploadRequest.getFile().getAbsoluteFile());

				if (TextUtils.isEmpty(stringBuilder))
				{
					throw new Exception();
				}
				mResponse = stringBuilder.toString();
			}
			catch (Exception e)
			{
				throw new FileUploaderException(UploadStatus.Failed.toInt(), "Invalid Server Response");
			}
		}
		finally
		{
			try
			{
				close(inputStream);
				close(responseStreamReader);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private static final void close(Closeable closeable) throws IOException
	{
		if (closeable != null)
		{
			synchronized (UploadTask.class)
			{
				closeable.close();
			}
		}
	}

	private void checkPausedOrCanceled() throws FileUploaderException
	{
		if (mCommend == UploadStatus.Canceled)
		{
			// cancel
			throw new FileUploaderException(UploadStatus.Canceled.toInt(), "Upload canceled!");
		}
		else if (mCommend == UploadStatus.Paused)
		{
			// pause
			throw new FileUploaderException(UploadStatus.Paused.toInt(), "Upload paused!");
		}
	}
}