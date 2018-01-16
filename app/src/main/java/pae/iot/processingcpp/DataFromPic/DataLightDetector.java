package pae.iot.processingcpp.DataFromPic;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import org.opencv.android.JavaCameraView;

import pae.iot.processingcpp.CustomStructures.*;

import java.util.List;

public class DataLightDetector extends JavaCameraView {

    private Context context;

    private FlashIDListener flashIDListener;
    private FlashDataListener flashDataListener;
    private FlashPacketListener flashPacketListener;
    private FlashStopListener flashStopListener;

    public DataLightDetector(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public List<String> getEffectList() {
        return mCamera.getParameters().getSupportedFlashModes();
    }

    public boolean isEffectSupported() {
        return (mCamera.getParameters().getFlashMode() != null);
    }

    public String getEffect() {
        return mCamera.getParameters().getFlashMode();
    }


    public void cameraRelease() {
        if(mCamera != null){
            mCamera.release();
        }
    }

    public void sendAck(int identifier){

        switch (identifier){
            case Protocol.FLASH_ID:

                Log.d(Protocol.FLASH_TAG,"Modulating FLASH_ID_SEQ");
                modulateFlash2(Protocol.FLASH_ID_SEQ);
                flashIDListener.onComplete();

                break;

            case Protocol.FLASH_DATA:

                Log.d(Protocol.FLASH_TAG,"Modulating FLASH_DATA_SEQ");
                modulateFlash2(Protocol.FLASH_DATA_SEQ);
                flashDataListener.onComplete();

                break;
            case Protocol.FLASH_PACKET:

                Log.d(Protocol.FLASH_TAG,"Modulating FLASH_PACKET_SEQ");
                modulateFlash2(Protocol.FLASH_DATA_SEQ);
                flashPacketListener.onComplete();

                break;

            case Protocol.FLASH_STOP:

                Log.d(Protocol.FLASH_TAG,"Modulating FLASH_STOP_SEQ");
                modulateFlash2(Protocol.FLASH_STOP_SEQ);
                flashStopListener.onComplete();

                break;
        }
    }

    public void setEffect(String effect) {
        if(mCamera != null) {
            mCamera.getParameters();
            Camera.Parameters params = mCamera.getParameters();
            params.setFlashMode(effect);
            for (int x = 0; x < 1000; ++x);
            mCamera.setParameters(params);
        }
    }

    public boolean modulateFlash(int count){

        boolean result = true;
        synchronized (this){

            mCamera = null;
            mCamera = Camera.open();

            if (mCamera == null)
                return false;

            /* Time to set Flash Parameter*/
            try {
                Camera.Parameters params = mCamera.getParameters();
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(params);
                mCamera.startPreview();
                //Log.d(Protocol.FLASH_TAG,"Start Preview with flash");

                for (int x = 0; x < count*100; ++x) {

                    params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    mCamera.setParameters(params);
                    /*IF WE WANT FREQUENCY*/

                    /*if (x % 2 == 0){
                        params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        mCamera.setParameters(params);
                    }else{
                        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        mCamera.setParameters(params);
                    }*/
                }
            } catch (Exception e) {
                result = false;
                e.printStackTrace();
            }

        }
        return result;
    }

    public boolean modulateFlash2(int count){

        this.setEffect(Camera.Parameters.FLASH_MODE_TORCH);
        for (int x = 0; x < count*100; ++x) {}
        this.setEffect(Camera.Parameters.FLASH_MODE_OFF);

        return true;
    }

    public void setFlashIDListener(FlashIDListener listener) {
        this.flashIDListener = listener;
    }
    public void setFlashDataListener(FlashDataListener listener) {
        this.flashDataListener = listener;
    }
    public void setFlashPacketListener(FlashPacketListener listener) {
        this.flashPacketListener = listener;
    }
    public void setFlashStopListener(FlashStopListener listener) {
        this.flashStopListener = listener;
    }

    public interface FlashIDListener {
        void onComplete();
    }

    public interface FlashDataListener {
        void onComplete();
    }

    public interface FlashPacketListener {
        void onComplete();
    }

    public interface FlashStopListener {
        void onComplete();
    }

}
