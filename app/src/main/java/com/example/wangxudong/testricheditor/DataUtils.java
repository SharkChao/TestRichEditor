package com.example.wangxudong.testricheditor;

import android.os.Handler;

import richeditor.ImageItem;
import richeditor.view.RichImageView;

public class DataUtils {
    private ImageItem.Data mData;
    private Handler mHandler;
    private Thread mThread;
    private OnUploadListener mListener;
    public DataUtils(ImageItem.Data data){
        mData = data;
    }
    public void upload(){
        mHandler = new Handler();

        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                loadDataFromServer();
            }
        });

        mThread.start();

    }
     int progress;
    private void loadDataFromServer(){
        while (mData.getState() != RichImageView.State.FAIL && mData.getState() != RichImageView.State.SUCCESS) {
            for (; progress < 100; progress++) {
                    try {
                        Thread.sleep(1000);

                        if (mData.getState() == RichImageView.State.PAUSE){
                             while (mData.getState() == RichImageView.State.PAUSE) {

                             }
                        }
                        mData.setProgress(progress);
                        mData.setState(RichImageView.State.UPLOADING);
                        if (progress == 99) {
                            mData.setState(RichImageView.State.SUCCESS);
                        }
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mListener != null) {
                                    mListener.upload(mData);
                                }
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }



            }
        }

    }

    public interface OnUploadListener{
        void upload(ImageItem.Data data);
    }
    public void regsiterUploadListener(OnUploadListener listener){
        mListener = listener;
    }
}
