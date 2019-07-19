package com.compa.readerocr;

import com.compa.readerocr.utils.CommonUtils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class LoginActivity extends Activity {
	ImageView viewImage;
	final int MULTIPLE_PERMISSIONS = 10;

	Button btnStart;

	ProgressDialog progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		String[] PERMISSIONS = { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
				Manifest.permission.INTERNET };

		if (!hasPermissions(this, PERMISSIONS)) {
			ActivityCompat.requestPermissions(this, PERMISSIONS, MULTIPLE_PERMISSIONS);
		} else {
			CommonUtils.cleanFolder();
		}

		btnStart = (Button) findViewById(R.id.btnStart);
		btnStart.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == MotionEvent.ACTION_UP) {
				//	callLogin();


						selectImage();

				}
				return false;
			}
		});

		// CommonUtils.getAllExamAnswer();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	private void selectImage() {
		final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
		AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
		builder.setTitle("Add Photo!");
		builder.setItems(options, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (options[item].equals("Take Photo"))
				{
					callLogin();
				}
				else if (options[item].equals("Choose from Gallery"))
				{
					Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent, 2);
				}
				else if (options[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			//if (requestCode == 1) {
			//	callLogin();
			//} else
			if (requestCode == 2) {
				Uri selectedImage = data.getData();
				String[] filePath = { MediaStore.Images.Media.DATA };
				Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
				c.moveToFirst();
				int columnIndex = c.getColumnIndex(filePath[0]);
				String picturePath = c.getString(columnIndex);
				c.close();
				Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
				Log.w("path image from gallery", picturePath+"");
				viewImage.setImageBitmap(thumbnail);
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// Call API to login
	public void callLogin() {

		Intent recognizeActivity = new Intent(getApplicationContext(), RecognizeTextActivity.class);
		// Clears History of Activity
		Bundle b = new Bundle();
		// Information to validate. Enter any to pass
		b.putString("language", ((Spinner) findViewById(R.id.languageSpiner)).getSelectedItem().toString());
		b.putString("threshold", ((Spinner) findViewById(R.id.thresholdText)).getSelectedItem().toString());
		recognizeActivity.putExtras(b);
		recognizeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(recognizeActivity);

	}

	public static boolean hasPermissions(Context context, String... permissions) {
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
			for (String permission : permissions) {
				if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
		case MULTIPLE_PERMISSIONS: {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				// permissions granted.
			} else {
				this.finish();
			}
			return;
		}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}
}
