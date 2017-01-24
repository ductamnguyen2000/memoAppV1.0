package com.example.trustring.memoappv10;

import android.content.Intent;
import android.os.CancellationSignal;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.security.PrivateKey;

public class MainActivity extends AppCompatActivity {
    public String  TAG = "Trust";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    public void onClick (View view)
    {
        switch (view.getId()) {
            case R.id.btnCapture:
                Toast.makeText(MainActivity.this,"Captuer !",Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onClick() called with: Captuer = [" + view + "]");
                break;
            case R.id.btnMakeMemo:
                Intent intent = new Intent(MainActivity.this,MakeMemoActivity.class);
                startActivity(intent);

                Toast.makeText(MainActivity.this,"Open new Form !",Toast.LENGTH_LONG).show();
                Log.d(TAG, "onClick() called with: new = [" + view + "]");
                break;
        }
    }
}
