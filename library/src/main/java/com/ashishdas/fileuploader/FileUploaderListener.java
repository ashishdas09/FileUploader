/*******************************************************************************
 * FileUploaderListener.java
 * FileUploaderListener
 * <p>
 * Author(s): Ashish Das
 ******************************************************************************/

package com.ashishdas.fileuploader;

import com.ashishdas.fileuploader.internal.FileUploadManager;

/**
 * FileUploaderListener of upload status
 */
public interface FileUploaderListener
{

	void onStarted();

	/**
	 * <p> this will be the the first method called by
	 */
	void onConnecting();

	/**
	 * connected with the http/https server this method will be invoke. If not method
	 */
	void onConnected();

	/**
	 * <p> progress callback.
	 *
	 * @param finished the downloaded length of the file
	 * @param total    the total length of the file same value with method {@link }
	 * @param progress the percent of progress (finished/total)*100
	 */
	void onUploading(long finished, long total, int progress);

	/**
	 * <p> upload complete
	 */
	void onCompleted(String serverResponse);

	/**
	 * <p> if you invoke {@link FileUploadManager#pause(String)} or {@link FileUploadManager#pauseAll()}
	 * this method will be invoke if the downloading task is successfully paused.
	 */
	void onPaused();

	/**
	 * <p> if you invoke {@link FileUploadManager#cancel(String)} or {@link FileUploadManager#cancelAll()}
	 * this method will be invoke if the downloading task is successfully canceled.
	 */
	void onCanceled();

	/**
	 * <p> download fail or exception callback
	 *
	 * @param e download exception
	 */
	void onFailed(FileUploaderException e);
}
