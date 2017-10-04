/*******************************************************************************
 * UploadServiceHelper.java
 * UploadServiceHelper
 * <p>
 * Author(s): Ashish Das
 ******************************************************************************/

package com.ashishdas.fileuploader.internal;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PersistableBundle;
import android.util.Log;

import com.ashishdas.fileuploader.FileUploadManager;
import com.ashishdas.fileuploader.FileUploadRequest;
import com.ashishdas.fileuploader.internal.services.FileUploadJobService;
import com.ashishdas.fileuploader.internal.services.FileUploadService;

public class UploadServiceHelper
{
	private static final String TAG = "UploadServiceHelper";

	public static final int JOBID_UPLOAD = 901;
	private static final int JOBID_PAUSE = 902;
	private static final int JOBID_CANCEL = 903;
	private static final int JOBID_PAUSE_ALL = 904;
	private static final int JOBID_CANCEL_ALL = 905;
	private static final int JOBID_RESUME_ALL = 906;
	private static final int JOBID_DESTORY = 907;

	public static final String ACTION_UPLOAD_BROAD_CAST = "com.ashishdas.fileuploader.ACTION_BROAD_CAST";
	private static final String ACTION_UPLOAD = "com.ashishdas.fileuploader.ACTION_UPLOAD";
	private static final String ACTION_PAUSE = "com.ashishdas.fileuploader.ACTION_PAUSE";
	private static final String ACTION_CANCEL = "com.ashishdas.fileuploader.ACTION_CANCEL";
	private static final String ACTION_PAUSE_ALL = "com.ashishdas.fileuploader.ACTION_PAUSE_ALL";
	private static final String ACTION_CANCEL_ALL = "com.ashishdas.fileuploader.ACTION_CANCEL_ALL";
	private static final String ACTION_RESUME_ALL = "com.ashishdas.fileuploader.ACTION_RESUME_ALL";

	public static final String EXTRA_FILE_UPLOAD_REQUEST = "extra_file_upload_request";
	public static final String EXTRA_FILE_UPLOAD_INFO = "extra_file_upload_info";

	public static synchronized void intentUpload(Context context, FileUploadRequest request)
	{
		Intent intent = getIntent(context, JOBID_UPLOAD);
		intent.setAction(ACTION_UPLOAD);
		intent.putExtra(EXTRA_FILE_UPLOAD_INFO, request);
		context.startService(intent);
	}

	public static synchronized void intentPause(Context context, FileUploadRequest request)
	{
		Intent intent = getIntent(context, JOBID_PAUSE);
		intent.setAction(ACTION_PAUSE);
		intent.putExtra(EXTRA_FILE_UPLOAD_INFO, request);
		context.startService(intent);
	}

	public static void intentCancel(Context context, FileUploadRequest request)
	{
		Intent intent = getIntent(context, JOBID_CANCEL);
		intent.setAction(ACTION_CANCEL);
		intent.putExtra(EXTRA_FILE_UPLOAD_INFO, request);
		context.startService(intent);
	}

	public static synchronized void intentPauseAll(Context context)
	{
		Intent intent = getIntent(context, JOBID_PAUSE_ALL);
		intent.setAction(ACTION_PAUSE_ALL);
		context.startService(intent);
	}

	public static synchronized void intentCancelAll(Context context)
	{
		Intent intent = getIntent(context, JOBID_CANCEL_ALL);
		intent.setAction(ACTION_CANCEL_ALL);
		context.startService(intent);
	}

	public static synchronized void intentResumeAll(Context context)
	{
		Intent intent = getIntent(context, JOBID_RESUME_ALL);
		intent.setAction(ACTION_RESUME_ALL);
		context.startService(intent);
	}

	public static synchronized void intentDestory(Context context)
	{
		Intent intent = getIntent(context, JOBID_DESTORY);
		context.stopService(intent);
	}

	private static synchronized Intent getIntent(Context context, int jobId)
	{
		if (isLollipopAndAbove())
		{
			createJobScheduler(context, jobId, null);
			return new Intent(context, FileUploadJobService.class);
		}
		return new Intent(context, FileUploadService.class);
	}

	public static synchronized void handleCommands(Intent intent)
	{
		if (FileUploadManager.isStarted() && intent != null)
		{
			String action = intent.getAction();
			try
			{
				FileUploadRequest request = intent.getParcelableExtra(UploadServiceHelper.EXTRA_FILE_UPLOAD_INFO);
				switch (action)
				{
					case UploadServiceHelper.ACTION_UPLOAD:
						FileUploadManager.upload(request, null);
						break;
					case UploadServiceHelper.ACTION_PAUSE:
						FileUploadManager.pause(request);
						break;
					case UploadServiceHelper.ACTION_CANCEL:
						FileUploadManager.cancel(request);
						break;
					case UploadServiceHelper.ACTION_PAUSE_ALL:
						FileUploadManager.pauseAll();
						break;
					case UploadServiceHelper.ACTION_CANCEL_ALL:
						FileUploadManager.cancelAll();
					case UploadServiceHelper.ACTION_RESUME_ALL:
						FileUploadManager.resumeAll();
						break;
				}
			}
			catch (Exception e)
			{
			}
		}
	}

	public static synchronized boolean isAllDone()
	{
		return FileUploadManager.isStarted() ? FileUploadManager.isAllDone() : true;
	}

	public static synchronized void setOnAllTaskCompletedListener(final FileUploadManager.OnAllTaskCompletedListener onAllTaskCompletedListener)
	{
		if (FileUploadManager.isStarted())
		{
			FileUploadManager.setAllTaskCompletedListener(onAllTaskCompletedListener);
		}
	}

	public static synchronized void pauseAll()
	{
		if (FileUploadManager.isStarted())
		{
			FileUploadManager.pauseAll();
		}
	}

	public static synchronized void sendBroadCast(final Context context, final FileUploadRequest request, final UploadInfo uploadInfo)
	{
		Intent intent = new Intent();
		intent.setAction(ACTION_UPLOAD_BROAD_CAST);
		intent.putExtra(EXTRA_FILE_UPLOAD_REQUEST, request);
		intent.putExtra(EXTRA_FILE_UPLOAD_INFO, uploadInfo);
		context.sendBroadcast(intent);
	}

	private static synchronized boolean isLollipopAndAbove()
	{
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private static synchronized void createJobScheduler(final Context context, final int jobId, final PersistableBundle extras)
	{
		Log.d(TAG, "PersistableBundle: " + extras);

		JobInfo.Builder jobInfoBuilder = new JobInfo.Builder(jobId, new ComponentName(context, FileUploadJobService.class));
		if (extras != null)
		{
			jobInfoBuilder.setExtras(extras);
		}
		JobInfo jobInfo = jobInfoBuilder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
		                                .setOverrideDeadline(0)
		                                .setPersisted(true)
		                                .build();

		JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
		int resultCode = jobScheduler.schedule(jobInfo);
		if (resultCode == JobScheduler.RESULT_SUCCESS)
		{
			Log.d(TAG, "Job scheduled!");
		}
		else
		{
			Log.d(TAG, "Job not scheduled");
		}
	}
}
