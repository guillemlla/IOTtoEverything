package pae.iot.processingcpp;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import pae.iot.processingcpp.CustomStructures.*;
import pae.iot.processingcpp.DataFromPic.*;

public class CameraActivity extends AppCompatActivity implements JavaCameraView.CvCameraViewListener2, JavaCameraView.OnClickListener{

    private DataLightDetector mJavaCameraView;
    private ProgressBar dataProgressBar;
    private TextView resultTextView,finalResultTextView;
    private ImageView playImg;
    private int state = Protocol.IDLE;
    private String finalResultString;
    private String decodedID;
    private String lastFrame;
    private boolean ackFlag;


    private BaseLoaderCallback _baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(Protocol.OPENCV_TAG, "OpenCV loaded successfully");
                    System.loadLibrary("native-lib");
                    mJavaCameraView.setMaxFrameSize(640,480);
                    mJavaCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        finalResultString = "";
        decodedID = "";
        lastFrame = "";
        ackFlag = false;
        state = Protocol.IDLE;
        RecieverHelper.lightReciever.setParityFrame((short)0);
        dataProgressBar = (ProgressBar) findViewById(R.id.data_progress_bar);
        dataProgressBar.getIndeterminateDrawable().setColorFilter(0xFF76ff03, android.graphics.PorterDuff.Mode.MULTIPLY);
        dataProgressBar.setMax(100);
        dataProgressBar.setProgress(0);

        //resultTextView = (TextView) findViewById(R.id.data_decoded);
        mJavaCameraView = (DataLightDetector) findViewById(R.id.main_surface);
        playImg = (ImageView) findViewById(R.id.play_btn);

        mJavaCameraView.setVisibility(SurfaceView.VISIBLE);
        mJavaCameraView.setCvCameraViewListener(this);
        mJavaCameraView.setOnClickListener(this);

        RecieverHelper.lightReciever.setRepeatFlashListener(new RecieverHelper.RepeatFlashListener() {
            @Override
            public void onRepeatRequest() {

                activateFlash();
                Log.d(Protocol.RECEIVER_TAG,"Repeated Flash");

            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        disableCamera();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(Protocol.OPENCV_TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, _baseLoaderCallback);
        } else {
            Log.d(Protocol.OPENCV_TAG, "OpenCV library found inside package. Using it!");
            _baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        disableCamera();
    }

    public void disableCamera() {
        if (mJavaCameraView != null)
            mJavaCameraView.disableView();
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat currentFrame = inputFrame.gray();
        Mat threshold = new Mat();
        lightProcessing(currentFrame.getNativeObjAddr(), threshold.getNativeObjAddr());
        Packet p = new Packet();
        RecieverHelper.lightReciever.processFrame(
                RecieverHelper.lightReciever.downsampling(
                        RecieverHelper.lightReciever.toShort(threshold)
                ),
                p);

        if(!p.isNull()){
            RecieverHelper.lightReciever.setFrameListener(new RecieverHelper.FrameListener() {
                @Override
                public void onFrameDecoded(String frame) {
                    RecieverHelper.lightReciever.setParityFrame(
                            (short) ((RecieverHelper.lightReciever.getParityFrame() + 1) % 2)
                    );
                    Log.d(Protocol.RECEIVER_TAG,"Received frame: "+frame);

                    if(state == Protocol.WAITING_DATA){
                        Log.d(Protocol.STATE_TAG,"SENDING PACKET FLASH");
                        if(!frame.contains("-")){
                        //    mJavaCameraView.setEffect(Camera.Parameters.FLASH_MODE_TORCH);

                            finalResultString += frame;
                            //activateFlash();
                            mJavaCameraView.callOnClick();
                            //resultTextView.setText(frame);
                        //    mJavaCameraView.setEffect(Camera.Parameters.FLASH_MODE_OFF);

                        }else{
                            //mJavaCameraView.setEffect(Camera.Parameters.FLASH_MODE_TORCH);
                            frame = frame.replace("-"," ");
                            finalResultString += frame;
                            Log.d(Protocol.STATE_TAG,"SENDING STOP FLASH");

                            //activateFlash();
                            mJavaCameraView.callOnClick();

                            Log.d(Protocol.STATE_TAG,"STOP_FLASH ENDED");

                            state = Protocol.IDLE;
                            Log.d(Protocol.STATE_TAG,"COMPLETED, GET BACK TO ACTIVITY");
                            Log.d(Protocol.STATE_TAG,"ID: " + decodedID + "DATA: " + finalResultString);
                            finalResultString = decodedID+finalResultString;
                            //Log.d(Protocol.STATE_TAG,"Concatenated: " + finalResultString);
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra(Protocol.FINAL_STRING_IDENTIFIER, finalResultString);
                            setResult(Activity.RESULT_OK, resultIntent);
                            finish();

                            //finalResultTextView.setText(finalResultString);
                            //mJavaCameraView.setEffect(Camera.Parameters.FLASH_MODE_OFF);
                        }
                    }else if(state == Protocol.WAITING_ID){
                        Log.d(Protocol.STATE_TAG,"SENDING ID FLASH");
                        //TODO CHANGE THIS PART (PARSE ID) change ackFlag
                        //When finished
                        decodedID = frame;
                        //activateFlash();
                        mJavaCameraView.callOnClick();

                        state = Protocol.WAITING_DATA;
                        Log.d(Protocol.STATE_TAG,"SWITCHING TO WAITING DATA");
                    }else if(ackFlag){
                        //ackFlag=false;
                        Log.d(Protocol.RECEIVER_TAG,"Delete last packet");
                    }
                    //Current Frame

                    //finalResultTextView.setText(finalResultString);
                }

                @Override
                public void onProgressChanged(int progress) {
                    dataProgressBar.setProgress(progress);
                }
            });
        }

        return currentFrame;
    }

    public native void lightProcessing(long matAddrGray, long outputAddrThreshold);

    public void onCameraViewStarted(int width, int height) {
    }

    public void onCameraViewStopped() {
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch(id){
            case R.id.main_surface:

                /* First time Click: TAP TO START MESSAGE */
                if(state == Protocol.IDLE){
                    Log.d(Protocol.STATE_TAG,"TAP TO START");
                    state = Protocol.START_ID;
                    playImg.setVisibility(View.GONE);
                    //modulateFlash2(Protocol.FLASH_ID_SEQ);
                    mJavaCameraView.setEffect(Camera.Parameters.FLASH_MODE_TORCH);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mJavaCameraView.setEffect(Camera.Parameters.FLASH_MODE_OFF);
                        }
                    }, Protocol.DELAY);
                    //mJavaCameraView.callOnClick();

                    state = Protocol.WAITING_ID;
                    Log.d(Protocol.STATE_TAG,"WAITING ID");
                    //mJavaCameraView.sendAck(Protocol.FLASH_ID);
                }else{

                    activateFlash();
                }

                break;
        }
    }

    public boolean activateFlash(){
        mJavaCameraView.setEffect(Camera.Parameters.FLASH_MODE_TORCH);
        for(int i = 0; i<Protocol.DELAY*2;i++);
        mJavaCameraView.setEffect(Camera.Parameters.FLASH_MODE_OFF);

        return true;
    }
}
