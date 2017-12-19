package pae.iot.processingcpp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import pae.iot.processingcpp.CustomStructures.*;

public class StartActivity extends AppCompatActivity implements View.OnClickListener{

    private Button startCommBtn;
    public TextView exportedDataTextView;
    public TextView exportedIDTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        startCommBtn = (Button) findViewById(R.id.start_comm_btn);
        startCommBtn.setOnClickListener(this);
        exportedDataTextView = (TextView) findViewById(R.id.data_exported);
        exportedIDTextView = (TextView) findViewById(R.id.id_exported);
        // Permissions for Android 6+
        ActivityCompat.requestPermissions(StartActivity.this,
                new String[]{
                        Manifest.permission.CAMERA},
                1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(StartActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.start_comm_btn:

                Intent i = new Intent(StartActivity.this,CameraActivity.class);
                startActivityForResult(i, Protocol.ACTIVITY_INTENT);
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (Protocol.ACTIVITY_INTENT) : {
                if(resultCode != RESULT_CANCELED) {
                    if (resultCode == RESULT_OK) {
                        String finalResult = data.getStringExtra(Protocol.FINAL_STRING_IDENTIFIER);
                        //TODO Parse ID and Data separated
                        String ID = finalResult.substring(0,15);
                        String Data = finalResult.substring(16);
                        Log.d("debbug", finalResult);
                        //String[] parts = finalResult.split(".");
                        Log.d("debbug", "ID: " + ID + "DATA: " + Data);
                        ID = ID.replace("-","");

                        exportedDataTextView.setText(ID);
                        exportedIDTextView.setText(Data);
                    }
                }
                break;
            }
        }
    }

}
