/*******************************************************************************
 * FileUploadJobService.java
 * FileUploadJobService
 * <p>
 * Author(s): Ashish Das
 ******************************************************************************/

package com.ashishdas.fileuploader.internal.services;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;

import com.ashishdas.fileuploader.FileUploadManager;
import com.ashishdas.fileuploader.internal.UploadServiceHelper;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class FileUploadJobService extends JobService
{
	private JobParameters mParameters = null;

	@Override
	public boolean onStartJob(final JobParameters params)
	{
		if (params.getJobId() == UploadServiceHelper.JOBID_UPLOAD)
		{
			if (mParameters != null)
			{
				jobFinished(mParameters, false);
			}
			mParameters = params;
		}
		else
		{
			jobFinished(params, false);
		}
		return true;
	}

	@Override
	public boolean onStopJob(final JobParameters params)
	{
		if (params.getJobId() == UploadServiceHelper.JOBID_UPLOAD)
		{
			boolean shouldReschedule = !UploadServiceHelper.isAllDone();
			return shouldReschedule;
		}
		return false;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		UploadServiceHelper.setOnAllTaskCompletedListener(mOnAllTaskCompletedListener);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		UploadServiceHelper.pauseAll();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		UploadServiceHelper.handleCommands(intent);
		return super.onStartCommand(intent, flags, startId);
	}

	private FileUploadManager.OnAllTaskCompletedListener mOnAllTaskCompletedListener = new FileUploadManager.OnAllTaskCompletedListener()
	{
		@Override
		public void onAllTaskCompleted()
		{
			if (mParameters != null)
			{
				jobFinished(mParameters, false);
			}
		}
	};
}
