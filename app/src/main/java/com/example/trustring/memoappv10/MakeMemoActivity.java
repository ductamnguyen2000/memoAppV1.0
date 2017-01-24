package com.example.trustring.memoappv10;

import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.IllegalFormatCodePointException;

import slideDateTimePicker.SlideDateTimeListener;
import slideDateTimePicker.SlideDateTimePicker;

public class MakeMemoActivity extends AppCompatActivity {
    private static final int SELECT_SINGLE_PICTURE = 101;

    private static final int SELECT_MULTIPLE_PICTURE = 201;

    public static final String IMAGE_TYPE = "image/*";

    private ImageView selectedImagePreview;

    private SimpleDateFormat mFormatter = new SimpleDateFormat("MMMM dd yyyy hh:mm aa");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_memo);
        TextView tvFrom = (TextView) findViewById(R.id.tvFrom);
        tvFrom.setText(mFormatter.format(new Date()));


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, 2);
        TextView tvTo = (TextView) findViewById(R.id.tvTo);
        tvTo.setText(mFormatter.format(calendar.getTime()));
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLoadImage:
                Intent intent = new Intent();
                intent.setType(IMAGE_TYPE);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), SELECT_SINGLE_PICTURE);
                selectedImagePreview = (ImageView) findViewById(R.id.imageView);
                break;
            case R.id.tvFrom:
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listenerfrom)
                        .setInitialDate(new Date())
                        .build()
                        .show();
                break;
            case R.id.tvTo:
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listenerto)
                        .setInitialDate(new Date())
                        .build()
                        .show();
                break;
        }
    }

    private SlideDateTimeListener listenerfrom = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            TextView tvFrom = (TextView) findViewById(R.id.tvFrom);
            TextView tvTo = (TextView) findViewById(R.id.tvTo);
            tvFrom.setText(mFormatter.format(date));
            try {
                Date dateTo = mFormatter.parse(tvTo.getText().toString());
                if (date.compareTo(dateTo) <1) {

                } else {
                    tvTo.setText(mFormatter.format(date));
                    Toast.makeText(MakeMemoActivity.this, "Date From > Date To!", Toast.LENGTH_SHORT).show();
                }
            } catch (ParseException e) {

            }

        }
        // Optional cancel listener
        @Override
        public void onDateTimeCancel() {

        }
    };

    private SlideDateTimeListener listenerto = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            TextView tvFrom = (TextView) findViewById(R.id.tvFrom);
            TextView tvTo = (TextView) findViewById(R.id.tvTo);
            try {
                Date dateFrom = mFormatter.parse(tvFrom.getText().toString());
                if (dateFrom.compareTo(date) <1) {
                    tvTo.setText(mFormatter.format(date));
                } else {
                    tvTo.setText(dateFrom.toString());
                    Toast.makeText(MakeMemoActivity.this, "Date From > Date To!", Toast.LENGTH_SHORT).show();
                }
            } catch (ParseException e) {

            }
        }

        // Optional cancel listener
        @Override
        public void onDateTimeCancel() {

        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_SINGLE_PICTURE) {

                Uri selectedImageUri = data.getData();
                try {
                    selectedImagePreview.setImageBitmap(new UserPicture(selectedImageUri, getContentResolver()).getBitmap());
                } catch (IOException e) {
                    Log.e(MainActivity.class.getSimpleName(), "Failed to load image", e);
                }
                // original code
//                String selectedImagePath = getPath(selectedImageUri);
//                selectedImagePreview.setImageURI(selectedImageUri);
            } else if (requestCode == SELECT_MULTIPLE_PICTURE) {
                //And in the Result handling check for that parameter:
                if (Intent.ACTION_SEND_MULTIPLE.equals(data.getAction())
                        && data.hasExtra(Intent.EXTRA_STREAM)) {
                    // retrieve a collection of selected images
                    ArrayList<Parcelable> list = data.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
                    // iterate over these images
                    if (list != null) {
                        for (Parcelable parcel : list) {
                            Uri uri = (Uri) parcel;
                            // handle the images one by one here
                        }
                    }

                    // for now just show the last picture
                    if (!list.isEmpty()) {
                        Uri imageUri = (Uri) list.get(list.size() - 1);

                        try {
                            selectedImagePreview.setImageBitmap(new UserPicture(imageUri, getContentResolver()).getBitmap());
                        } catch (IOException e) {
                            Log.e(MainActivity.class.getSimpleName(), "Failed to load image", e);
                        }
                        // original code
//                        String selectedImagePath = getPath(imageUri);
//                        selectedImagePreview.setImageURI(imageUri);
//                        displayPicture(selectedImagePath, selectedImagePreview);
                    }
                }
            }
        } else {
            // report failure
            Toast.makeText(getApplicationContext(), "msg_failed_to_get_intent_data", Toast.LENGTH_LONG).show();
            Log.d(MainActivity.class.getSimpleName(), "Failed to get intent data, result code is " + resultCode);
        }
    }
}
