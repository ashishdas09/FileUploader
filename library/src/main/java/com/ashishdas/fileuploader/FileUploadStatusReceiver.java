/*******************************************************************************
 * FileUploadStatusReceiver.java
 * FileUploadStatusReceiver
 * <p>
 * Author(s): Ashish Das
 ******************************************************************************/

package com.ashishdas.fileuploader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.ashishdas.fileuploader.internal.UploadInfo;
import com.ashishdas.fileuploader.internal.UploadServiceHelper;
import com.ashishdas.fileuploader.internal.utils.Utils;

public abstract class FileUploadStatusReceiver extends BroadcastReceiver implements FileUploadStatusListener
{
	@Override
	public void onReceive(final Context context, final Intent intent)
	{
		final String action = intent.getAction();

		if (TextUtils.isEmpty(action) || !action.equals(UploadServiceHelper.ACTION_UPLOAD_BROAD_CAST))
		{
			return;
		}

		final FileUploadRequest request = intent.getParcelableExtra(UploadServiceHelper.EXTRA_FILE_UPLOAD_REQUEST);
		if (request == null)
		{
			return;
		}

		final UploadInfo uploadInfo = intent.getParcelableExtra(UploadServiceHelper.EXTRA_FILE_UPLOAD_INFO);
		if (uploadInfo == null)
		{
			return;
		}

		Utils.notifyStatusListener(this, request, uploadInfo);
		if(FileUploadServiceManager.isStarted())
		{
			FileUploadServiceManager.notifyStatusListener(request, uploadInfo);
		}
	}
}
