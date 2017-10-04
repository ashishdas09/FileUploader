/*******************************************************************************
 * FileUploadStatusListener.java
 * FileUploadStatusListener
 * <p>
 * Author(s): Ashish Das
 ******************************************************************************/

package com.ashishdas.fileuploader;

/**
 * FileUploadStatusListener of upload status
 */
public interface FileUploadStatusListener
{

	void onStarted(FileUploadRequest request);

	/**
	 * <p> this will be the the first method called by
	 */
	void onConnecting(FileUploadRequest request);

	/**
	 * connected with the http/https server this method will be invoke. If not method
	 */
	void onConnected(FileUploadRequest request);

	/**
	 * <p> progress callback.
	 *
	 * @param finished the downloaded length of the file
	 * @param total    the total length of the file same value with method {@link }
	 * @param progress the percent of progress (finished/total)*100
	 */
	void onUploading(FileUploadRequest request, long finished, long total, int progress);

	/**
	 * <p> upload complete
	 */
	void onCompleted(FileUploadRequest request, String serverResponse);

	/**
	 * <p> if you invoke {@link FileUploadManager#pause(String)} or {@link FileUploadManager#pauseAll()}
	 * this method will be invoke if the downloading task is successfully paused.
	 */
	void onPaused(FileUploadRequest request);

	/**
	 * <p> if you invoke {@link FileUploadManager#cancel(String)} or {@link FileUploadManager#cancelAll()}
	 * this method will be invoke if the downloading task is successfully canceled.
	 */
	void onCanceled(FileUploadRequest request);

	/**
	 * <p> download fail or exception callback
	 *
	 * @param e download exception
	 */
	void onFailed(FileUploadRequest request, FileUploadException e);
}
