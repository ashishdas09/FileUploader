/*******************************************************************************
 * UploadStatusListener.java
 * UploadStatusListener
 * <p>
 * Author(s): Ashish Das
 ******************************************************************************/

package com.ashishdas.fileuploader.internal;

import com.ashishdas.fileuploader.FileUploadRequest;

public interface UploadStatusListener
{
	void onUploadStatus(FileUploadRequest request, UploadInfo uploadInfo);
}
