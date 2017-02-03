package com.example.trustring.memoappv10;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import cropImage.Crop;

public class ImageEditActivity extends AppCompatActivity {
    String TAG = "ABCD";
    String ImagePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit);
        setContentView(R.layout.activity_main);
        //resultView = (ImageView) findViewById(R.id.result_image);
        Log.d(TAG, "onCreate: ");

        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Log.d(TAG, "onCreate beginCrop: destination" + destination.getPath());
        Log.d(TAG, "onCreate beginCrop: source");
        Intent intent = getIntent();
        ImagePath = intent.getStringExtra(MainActivity.PATHIMAGE);
        Crop.of(Uri.fromFile(new java.io.File(ImagePath)), destination).start(this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        Log.d(TAG, "onActivityResult: ");
        if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        }
    }
    private void handleCrop(int resultCode, Intent result) {
        Log.d(TAG, "handleCrop: ");
        if (resultCode == RESULT_OK) {
            //resultView.setImageURI(Crop.getOutput(result));
            Log.d(TAG, "handleCrop: inside "+Crop.getOutput(result).getPath());
            Intent openActivity = new Intent(ImageEditActivity.this, MakeMemoActivity.class);
            openActivity.putExtra(MainActivity.PATHIMAGE, Crop.getOutput(result).getPath());
            openActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(openActivity);
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
