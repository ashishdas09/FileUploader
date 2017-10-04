/*******************************************************************************
 * FileUploadService.java
 * FileUploadService
 * <p>
 * Author(s): Ashish Das
 ******************************************************************************/

package com.ashishdas.fileuploader.internal.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.ashishdas.fileuploader.internal.UploadServiceHelper;

public class FileUploadService extends Service
{
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
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
}
