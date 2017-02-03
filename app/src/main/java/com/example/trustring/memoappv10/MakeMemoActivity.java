package com.example.trustring.memoappv10;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;

import com.google.api.services.calendar.*;
import com.google.api.services.drive.DriveScopes;
import com.google.api.client.util.DateTime;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import com.google.api.services.drive.model.*;
import com.google.api.services.calendar.model.*;


import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Collection;
import java.util.Date;
import java.util.IllegalFormatCodePointException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.TimeZone;

import cropImage.Crop;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import slideDateTimePicker.SlideDateTimeListener;
import slideDateTimePicker.SlideDateTimePicker;

public class MakeMemoActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    //GOOGLE API ---------------
    GoogleAccountCredential mCredential;

    ProgressDialog mProgress;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String BUTTON_TEXT = "Call Google Calendar API";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR, DriveScopes.DRIVE
    };
    //END
    private static final int SELECT_SINGLE_PICTURE = 101;

    private static final int SELECT_MULTIPLE_PICTURE = 201;

    public static final String IMAGE_TYPE = "image/*";

    private ImageView selectedImagePreview;

    private SimpleDateFormat mFormatter = new SimpleDateFormat("MMMM dd yyyy hh:mm aa");

    String ImagePath = "";

    private ImageView resultView;
    boolean checkFirstTime = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String TAG = "ABCD";
        Log.d(TAG, "onCreate: chay vao on Create");
        checkFirstTime = true;

        super.onCreate(savedInstanceState);

//        LinearLayout activityLayout = new LinearLayout(this);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.MATCH_PARENT);
//        activityLayout.setLayoutParams(lp);
//        activityLayout.setOrientation(LinearLayout.VERTICAL);
//        activityLayout.setPadding(16, 16, 16, 16);
//
//        ViewGroup.LayoutParams tlp = new ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT);
//
//        mCallApiButton = new Button(this);
//        mCallApiButton.setText(BUTTON_TEXT);
//        mCallApiButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mCallApiButton.setEnabled(false);
//                mOutputText.setText("");
//                getResultsFromApi();
//                mCallApiButton.setEnabled(true);
//            }
//        });
//        activityLayout.addView(mCallApiButton);
//
//        mOutputText = new TextView(this);
//        mOutputText.setLayoutParams(tlp);
//        mOutputText.setPadding(16, 16, 16, 16);
//        mOutputText.setVerticalScrollBarEnabled(true);
//        mOutputText.setMovementMethod(new ScrollingMovementMethod());
//        mOutputText.setText(
//                "Click the \'" + BUTTON_TEXT +"\' button to test the API.");
//        activityLayout.addView(mOutputText);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Calling Google API ...");

        setContentView(R.layout.activity_make_memo);

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        Log.d(TAG, "onCreate: chay dc on Create");
    }

    @Override
    protected void onStart() {

        TextView tvFrom = (TextView) findViewById(R.id.tvFrom);
        TextView tvTo = (TextView) findViewById(R.id.tvTo);
        tvFrom.setText(mFormatter.format(new Date()));
        tvTo.setText(mFormatter.format(new Date()));
        Intent intent = getIntent();
        Log.d("ABCD", "Truoc khi Goi chinh sua anh ");
        if (intent.getStringExtra(MainActivity.PATHIMAGE) != null) {
            ImagePath =intent.getStringExtra(MainActivity.PATHIMAGE);
            Log.d("ABCD", "Goi chinh sua anh"+ImagePath);
            //Crop.pickImage(this);
            //Uri destination = Uri.fromFile(new java.io.File(getCacheDir(), "cropped"));
            //resultView = (ImageView) findViewById(R.id.result_image);
            //resultView.setImageDrawable(null);
            //Crop.of(Uri.fromFile(new java.io.File(ImagePath)),destination).start(this);
            selectedImagePreview = (ImageView) findViewById(R.id.imageView);
            selectedImagePreview.setImageURI(Uri.parse(new java.io.File(intent.getStringExtra(MainActivity.PATHIMAGE)).toString()));
        }
        super.onStart();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        Log.d("ABCD", "onCreateOptionsMenu: ");
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLoadImage:
                Log.d("ABCDEF", "CLICKKKK  ");
                Intent intent = new Intent();
                intent.setType(IMAGE_TYPE);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                Log.d("ABCDEF", "CLICKKKK 2 ");
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), SELECT_SINGLE_PICTURE);
                Log.d("ABCDEF", "CLICKKKK 3 ");
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
            case R.id.btnUpload:
                Log.d("ABCDEF", "CLICKKKK  ");
                getResultsFromApi();
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private SlideDateTimeListener listenerfrom = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            TextView tvFrom = (TextView) findViewById(R.id.tvFrom);
            TextView tvTo = (TextView) findViewById(R.id.tvTo);
            tvFrom.setText(mFormatter.format(date));

            try {
                Date dateTo = mFormatter.parse(tvTo.getText().toString());
                if (date.compareTo(dateTo) < 1) {

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
                if (dateFrom.compareTo(date) < 1) {
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
        Log.d("ABCDEF", "onActivityResult ");
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
//                    mOutputText.setText(
//                            "This app requires Google Play Services. Please install " +
//                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_SINGLE_PICTURE) {

                Uri selectedImageUri = data.getData();
                Log.e("FILE PATH =======", "Load image" + selectedImageUri.getPath());
                try {
                    selectedImagePreview.setImageBitmap(new UserPicture(selectedImageUri, getContentResolver()).getBitmap());
                    ImagePath = getImagePath(selectedImageUri);
                    checkFirstTime = false;
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

    //GOOGLE API

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            // mOutputText.setText("No network connection available.");
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */


    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     *
     * @param requestCode  The request code passed in
     *                     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     *
     * @return true if Google Play Services is available and up to
     * date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     *
     * @param connectionStatusCode code describing the presence (or lack of)
     *                             Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                MakeMemoActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * An asynchronous task that handles the Google Calendar API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.calendar.Calendar mService = null;
        private com.google.api.services.drive.Drive mServicedrive = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {

            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Android memoappv10")
                    .build();
            mServicedrive = new com.google.api.services.drive.Drive.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Android memoappv10")
                    .build();
            Log.d("ABC", "MakeRequestTask ");
        }

        /**
         * Background task to call Google Calendar API.
         *
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                Log.d("ABC", "List<String> getDataFromApi() ");
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch a list of the next 10 events from the primary calendar.
         *
         * @return List of Strings describing returned events.
         * @throws IOException
         */
        private List<String> getDataFromApi() throws IOException {

// Upload file capture to Google Drive
            File fileMetadata = new File();
            File file = new File();
            if (ImagePath != null) {


                fileMetadata.setName("Capture -" + System.currentTimeMillis());
                fileMetadata.setMimeType("image/png");
                Log.d("ABC", "getDataFromApi: " + ImagePath);
                java.io.File filePath = new java.io.File(ImagePath);
                FileContent mediaContent = new FileContent("image/png", filePath);
                file = mServicedrive.files().create(fileMetadata, mediaContent)
                        .setFields("id")
                        .execute();
                System.out.println("File ID: " + file.getId());
            }
            //Get View
            EditText memoname = (EditText) findViewById(R.id.dtxTitle);
            EditText memodes = (EditText) findViewById(R.id.dtxDes);

            TextView tvFrom = (TextView) findViewById(R.id.tvFrom);
            TextView tvTo = (TextView) findViewById(R.id.tvTo);
            Event event2 = new Event()
                    .setSummary(memoname.getText().toString())
                    .setDescription(memodes.getText().toString());
            Date dtFrom=null;
            Date dtTo=null;
            try{
                dtFrom=mFormatter.parse(tvFrom.getText().toString());
               dtTo=mFormatter.parse(tvTo.getText().toString());
            }catch (Exception e){}

            DateTime startDateTime = new DateTime(dtFrom);
            EventDateTime start2 = new EventDateTime()
                    .setDateTime(startDateTime);
            event2.setStart(start2);

            DateTime endDateTime = new DateTime(dtTo);
            EventDateTime end2 = new EventDateTime()
                    .setDateTime(endDateTime);
            event2.setEnd(end2);
            if (ImagePath != null) {
                List<EventAttachment> attachments = new ArrayList<EventAttachment>();

                attachments.add(new EventAttachment()
                        .setFileUrl("https://drive.google.com/file/d/" + file.getId() + "/view?usp=drive_web")
                        .setFileId(file.getId())
                        .setMimeType(file.getMimeType())
                        .setTitle(file.getName()));
                event2.setAttachments(attachments);
            }
            String calendarId = "primary";
            event2 = mService.events().insert(calendarId, event2).setSupportsAttachments(true).execute();
            System.out.printf("Event created: %s\n", event2.getHtmlLink());

            return null;

        }


        @Override
        protected void onPreExecute() {
            Log.d("ABC", "onPreExecute: ");
            // mOutputText.setText("");
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            Log.d("ABC", "onPostExecute: ");
            if (output == null || output.size() == 0) {
                //  mOutputText.setText("No results returned.");
            } else {
                output.add(0, "Data retrieved using the Google Calendar API:");
                // mOutputText.setText(TextUtils.join("\n", output));
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            MakeMemoActivity.REQUEST_AUTHORIZATION);
                } else {
//                    mOutputText.setText("The following error occurred:\n"
//                            + mLastError.getMessage());
                }
            } else {
                //  mOutputText.setText("Request cancelled.");
            }
        }
    }

    public String getImagePath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }
}
