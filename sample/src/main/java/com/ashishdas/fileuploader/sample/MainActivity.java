/*******************************************************************************
 * MainActivity.java
 * MainActivity
 * <p>
 * Author(s): Ashish Das
 ******************************************************************************/

package com.ashishdas.fileuploader.sample;

import android.Manifest;
import android.content.ClipData;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ashishdas.fileuploader.FileUploadException;
import com.ashishdas.fileuploader.FileUploadManager;
import com.ashishdas.fileuploader.FileUploadRequest;
import com.ashishdas.fileuploader.FileUploadServiceManager;
import com.ashishdas.fileuploader.FileUploadStatusListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
	private static final String TAG = MainActivity.class.getSimpleName();
	private static final String KEY_UPLOAD_REQUEST = "Image";
	private static final int ACTIVITY_REQUEST_CODE = 999;

	private TextView tvServerUrl;
	private ImageView ivImagePreview;
	private ImageButton ibDelete;
	private Button btnChooseImage;
	private ProgressBar pbUploadProgress;
	private Button btnUpload;
	private ToggleButton tbServiceOption;

	private Context mContext;
	private String mImageFilePath;
	private String mLastServerUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		tvServerUrl = (TextView) findViewById(R.id.tv_server_url);
		ivImagePreview = (ImageView) findViewById(R.id.iv_image_preview);
		ibDelete = (ImageButton) findViewById(R.id.ib_delete);
		btnChooseImage = (Button) findViewById(R.id.btn_choose_image);
		pbUploadProgress = (ProgressBar) findViewById(R.id.pb_upload_progress);
		btnUpload = (Button) findViewById(R.id.btn_upload);
		tbServiceOption = (ToggleButton) findViewById(R.id.tb_service_option);

		ibDelete.setOnClickListener(this);
		btnChooseImage.setOnClickListener(this);
		btnUpload.setOnClickListener(this);

		tvServerUrl.setText("https://androidexample.com/media/UploadToServer.php");
		mLastServerUrl = tvServerUrl.getText().toString();
		FileUploadManager.startup(mContext, mLastServerUrl, null);

		setUploadEnabled(false);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		FileUploadServiceManager.addFileUploadStatusListener(mFileUploadStatusListener);
		if (tbServiceOption.isChecked() && FileUploadManager.isRunning(KEY_UPLOAD_REQUEST))
		{
			setUploadEnabled(true);
			btnUpload.setEnabled(false);
			tbServiceOption.setEnabled(false);
		}
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		FileUploadServiceManager.removeFileUploadStatusListener(mFileUploadStatusListener);
	}

	@Override
	public void onClick(final View view)
	{
		switch (view.getId())
		{
			case R.id.ib_delete:
				setUploadEnabled(false);
				break;
			case R.id.btn_choose_image:
				Dexter.withActivity(this)
				      .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
				      .withListener(new PermissionListener()
				      {
					      @Override
					      public void onPermissionGranted(PermissionGrantedResponse response)
					      {
						      openImageChooser();
					      }

					      @Override
					      public void onPermissionDenied(PermissionDeniedResponse response)
					      {

					      }

					      @Override
					      public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token)
					      {

					      }
				      }).check();
				break;
			case R.id.btn_upload:
				String serverUrl = tvServerUrl.getText().toString().trim();
				if (!TextUtils.isEmpty(serverUrl))
				{
					if (!mLastServerUrl.equals(serverUrl))
					{
						FileUploadManager.shutdown();
						mLastServerUrl = serverUrl;
						FileUploadManager.startup(mContext, serverUrl, null);
					}

					tbServiceOption.setEnabled(false);
					btnUpload.setEnabled(false);
					FileUploadRequest request = new FileUploadRequest(KEY_UPLOAD_REQUEST, mImageFilePath);
					if (tbServiceOption.isChecked())
					{
						FileUploadServiceManager.upload(request);
						return;
					}
					FileUploadManager.upload(request, mFileUploadStatusListener);
				}
				break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == RESULT_OK)
		{
			if (requestCode == ACTIVITY_REQUEST_CODE)
			{
				// Get the uri from data
				Uri selectedImageUri = getUriFromData(data);
				if (null != selectedImageUri)
				{
					try
					{
						mImageFilePath = getPathFromURI(this, selectedImageUri);
						Log.i(TAG, "Image Path : " + mImageFilePath);
						// Set the image in ImageView
						ivImagePreview.setImageURI(selectedImageUri);
						setUploadEnabled(true);
					}
					catch (Exception e)
					{
						String log = "Please select a valid image file. " + selectedImageUri.toString();
						Log.e(TAG, log);
						Toast.makeText(mContext, log, Toast.LENGTH_LONG).show();
					}
				}
			}
		}
	}

	private void setUploadEnabled(boolean enabled)
	{
		tbServiceOption.setEnabled(enabled);
		btnUpload.setEnabled(enabled);
		pbUploadProgress.setProgress(0);
		if (!enabled)
		{
			mImageFilePath = "";
			ibDelete.setVisibility(View.GONE);
			btnChooseImage.setVisibility(View.VISIBLE);
			ivImagePreview.setImageDrawable(null);
			return;
		}
		ibDelete.setVisibility(View.VISIBLE);
		btnChooseImage.setVisibility(View.GONE);
	}

	/* Choose an image from Gallery */
	private void openImageChooser()
	{
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"), ACTIVITY_REQUEST_CODE);
	}

	/* Get the Uri from the data */
	private Uri getUriFromData(Intent data)
	{
		Uri uri = null;
		if (data != null)
		{
			uri = data.getData();
			if (uri == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
			{
				ClipData clipData = data.getClipData();
				if (clipData != null)
				{
					for (int i = 0; i < clipData.getItemCount(); i++)
					{
						ClipData.Item item = clipData.getItemAt(i);
						uri = item.getUri();
						break;
					}
				}
			}
		}
		return uri;
	}

	/* Get the real path from the URI */
	private String getPathFromURI(Context context, Uri contentUri)
	{
		String realPath = null;
		final String[] projection = {MediaStore.Images.Media.DATA};
		Cursor cursor = null;
		try
		{
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
			{
				cursor = context.getContentResolver().query(contentUri, projection, null, null, null);
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				cursor.moveToFirst();
				realPath = cursor.getString(column_index);
			}
			// SDK >= 11 && SDK < 19
			else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
			{
				CursorLoader cursorLoader = new CursorLoader(context, contentUri, projection, null, null, null);
				cursor = cursorLoader.loadInBackground();
				if (cursor != null)
				{
					int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					cursor.moveToFirst();
					realPath = cursor.getString(column_index);
				}
			}
			// SDK > 19 (Android 4.4)
			else
			{
				String wholeID = DocumentsContract.getDocumentId(contentUri);
				// Split at colon, use second item in the array
				String id = wholeID.split(":")[1];
				// where id is equal to
				String sel = MediaStore.Images.Media._ID + "=?";
				cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, sel, new String[]{id}, null);
				int columnIndex = cursor.getColumnIndex(projection[0]);
				if (cursor.moveToFirst())
				{
					realPath = cursor.getString(columnIndex);
				}
			}
		}
		finally
		{
			if (cursor != null)
			{
				cursor.close();
			}
		}
		return realPath;
	}

	private FileUploadStatusListener mFileUploadStatusListener = new FileUploadStatusListener()
	{
		@Override
		public void onStarted(final FileUploadRequest request)
		{
			Log.d(TAG, "onStarted()");
			pbUploadProgress.setProgress(0);
		}

		@Override
		public void onConnecting(final FileUploadRequest request)
		{
			Log.d(TAG, "onConnecting()");
		}

		@Override
		public void onConnected(final FileUploadRequest request)
		{
			Log.d(TAG, "onConnected()");
		}

		@Override
		public void onUploading(final FileUploadRequest request, final long finished, final long total, final int progress)
		{
			pbUploadProgress.setProgress(progress);
			Log.d(TAG, "onUploading total: " + total + ", progress: " + progress);
		}

		@Override
		public void onCompleted(final FileUploadRequest request, final String serverResponse)
		{
			Log.d(TAG, "onCompleted()");
			Toast.makeText(mContext, "ServerResponse : " + serverResponse, Toast.LENGTH_LONG).show();
			btnUpload.setEnabled(true);
		}

		@Override
		public void onPaused(final FileUploadRequest request)
		{
			Log.d(TAG, "onPaused()");
		}

		@Override
		public void onCanceled(final FileUploadRequest request)
		{
			Log.d(TAG, "onCanceled()");
		}

		@Override
		public void onFailed(final FileUploadRequest request, final FileUploadException e)
		{
			btnUpload.setEnabled(true);
			Log.e(TAG, "onFailed() : " + e.getLocalizedMessage(), e);
			Toast.makeText(mContext, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
		}
	};
}
