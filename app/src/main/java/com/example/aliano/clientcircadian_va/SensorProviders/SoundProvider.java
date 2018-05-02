package com.example.aliano.clientcircadian_va.SensorProviders;

import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.example.aliano.clientcircadian_va.Data.Data;
import com.example.aliano.clientcircadian_va.Data.SoundData;


/**
 * Created by John on 21.05.2015.
 */
public class SoundProvider implements Runnable, ProviderInterface {
    private static SoundProvider INSTANCE = new SoundProvider();
    private SoundData data;
    private boolean stop;
    private boolean isRecording;

    private SoundProvider(){
        data = new SoundData();
        stop = false;
        isRecording = false;
    }

    public static SoundProvider getInstance(){
        return INSTANCE;
    }

    @Override
    public void run() {
//        stop = false;
//        MediaRecorder mediaRecorder = new MediaRecorder();
//        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//        mediaRecorder.setOutputFile("/dev/null");
//
//        try {
//            if(isRecording == false){
//                mediaRecorder.prepare();
//                mediaRecorder.start();
//                isRecording = true;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            stop = true;
//        } catch (IllegalStateException e) {  // the MIC is already in use ?
//            e.printStackTrace();
//            stop = true;
//        }
//        int counter = 0;
//        while (!stop){
//            int amplitude = mediaRecorder.getMaxAmplitude();
//
//
//            if(counter > 1){
//
//                // Obtain maximum amplitude since last call of getMaxAmplitude()
//                //convert amplitude into dB
//                double db = 10 * Math.log(amplitude / 2700.0);
//                data.setData(db);
//
//
//                Log.w("SoundProvider", "####################################  DB = " + db);
////                Log.w("SoundProvider", "####################################  amplitude = " + amplitude);
////                Log.w("SoundProvider", "####################################  amplitude / 2700.0 = " + (amplitude / 2700.0));
//
//            }
//            counter++;
//            try { Thread.sleep(1000);}
//            catch (InterruptedException e) {e.printStackTrace();}
//        }
//        // Don't forget to release
//        mediaRecorder.reset();
//        mediaRecorder.release();
//        isRecording = false;
    }

    @Override
    public Sensor getSensor() {
        return null;
    }

    public Data getData() {
        return data;
    }

    @Override
    public void setSensorMng(SensorManager sensorMng) {

    }

    public void stop(){
        this.stop = true;
    }

}
