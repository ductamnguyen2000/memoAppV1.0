package com.example.trustring.memoappv10;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.security.PrivateKey;

public class MainActivity extends AppCompatActivity {
    public String TAG = "Trust";
    private static final String EXTERNAL_CONTENT_URI_MATCHER =
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString();
    private static final String[] PROJECTION = new String[]{
            MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_ADDED
    };
    private static final String SORT_ORDER = MediaStore.Images.Media.DATE_ADDED + " DESC";
    private static final long DEFAULT_DETECT_WINDOW_SECONDS = 10;
    static String PATHIMAGE = "pathimage";
    static String PATHIMAGECHECK = "pathimagecheck";
    HandlerThread handlerThread = new HandlerThread("content_observer");
    Handler handler = new Handler();
    String pathImage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPermission();
        handlerThread = new HandlerThread("content_observer");
      handlerThread.start();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        checkThreadCapture();
        super.onResume();
        Log.d(TAG, "onResume: ------------------------------ ");

        handler = new Handler(handlerThread.getLooper()) {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };
        final ContentResolver contentResolver = getApplicationContext().getContentResolver();
        getContentResolver().registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                true,
                new ContentObserver(handler) {
                    @Override
                    public boolean deliverSelfNotifications() {
                        Log.d(TAG, "deliverSelfNotifications");
                        return super.deliverSelfNotifications();
                    }

                    @Override
                    public void onChange(boolean selfChange) {
                        super.onChange(selfChange);
                    }

                    @Override
                    public void onChange(boolean selfChange, Uri uri) {

                        Log.d(TAG, "onChange: " + selfChange + ", " + uri.toString());
                        if (uri.toString().startsWith(EXTERNAL_CONTENT_URI_MATCHER)) {
                            Cursor cursor = null;
                            try {
                                cursor = contentResolver.query(uri, PROJECTION, null, null,
                                        SORT_ORDER);
                                Log.d(TAG, "try: 1");
                                if (cursor != null && cursor.moveToFirst()) {
                                    String path = cursor.getString(
                                            cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                                    long dateAdded = cursor.getLong(cursor.getColumnIndex(
                                            MediaStore.Images.Media.DATE_ADDED));
                                    long currentTime = System.currentTimeMillis() / 1000;
                                    Log.d(TAG, "path: " + path + ", dateAdded: " + dateAdded +
                                            ", currentTime: " + currentTime);
                                    Log.d(TAG, "Main Activiy IF before"+pathImage);

                                    //cur.moveToNext();
                                    if (checkThreadCapture() && dateAdded==currentTime){
                                        Intent openActivity = new Intent(MainActivity.this, MakeMemoActivity.class);
                                        openActivity.putExtra(PATHIMAGE, path);
                                        openActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(openActivity);
                                        //finish();
                                        Log.d(TAG, "Main Activiy IF inside");
                                    }
                                    Log.d(TAG, "Main Activiy IF outside");
                                }
                            } catch (Exception e) {
                                Log.d(TAG, "open cursor fail" + e.toString());
                            } finally {
                                if (cursor != null) {
                                    cursor.close();
                                }
                            }
                        }
                        super.onChange(selfChange, uri);
                    }
                }
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
//        //handler = null;
//        handlerThread.quit();
//        handlerThread.interrupt();
//        handlerThread = null;
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCapture:
                Button btnCapture = (Button) findViewById(R.id.btnCapture);
                if (checkThreadCapture()) {
                    btnCapture.setText("Capture Start");
                } else {
                    btnCapture.setText("Capture Stop");
                }
                checkThreadCapture();
                Toast.makeText(MainActivity.this, "Captuer !", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onClick() called with: Captuer = [" + view + "]");
                break;
            case R.id.btnMakeMemo:
                Intent intent = new Intent(MainActivity.this, MakeMemoActivity.class);
                startActivity(intent);

                Toast.makeText(MainActivity.this, "Open new Form !", Toast.LENGTH_LONG).show();
                Log.d(TAG, "onClick() called with: new = [" + view + "]");
                break;
        }
    }

    public void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                //Permisson don't granted
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText(MainActivity.this, "Permission isn't granted ", Toast.LENGTH_SHORT).show();
                }
                // Permisson don't granted and dont show dialog again.
                else {
                    Toast.makeText(MainActivity.this, "Permisson don't granted and dont show dialog again ", Toast.LENGTH_SHORT).show();
                }
                //Register permission
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

            }
        }
    }

    private static boolean matchPath(String path) {
        return path.toLowerCase().contains("screenshot") || path.contains("截屏") ||
                path.contains("截图");
    }

    private static boolean matchTime(long currentTime, long dateAdded) {
        return Math.abs(currentTime - dateAdded) <= DEFAULT_DETECT_WINDOW_SECONDS;
    }

    public boolean checkThreadCapture() {
        Button btnCapture = (Button) findViewById(R.id.btnCapture);
        if (btnCapture.getText().toString().compareTo("Capture Stop")==0) {
            return true;
        } else {
            return false;
        }
    }
}
