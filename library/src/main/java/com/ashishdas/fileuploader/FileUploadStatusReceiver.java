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

public abstract class FileUploadStatusReceiver extends BroadcastReceiver implements FileUploadStatusListener
{

	@Override
	public void onReceive(final Context context, final Intent intent)
	{

	}
}
