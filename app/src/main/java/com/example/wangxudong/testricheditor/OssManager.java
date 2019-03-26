package com.example.wangxudong.testricheditor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.alibaba.sdk.android.oss.model.ResumableUploadRequest;
import com.alibaba.sdk.android.oss.model.ResumableUploadResult;

import java.util.HashMap;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import richeditor.ImageItem;
import richeditor.view.RichImageView;

public class OssManager {
    /**
     * 图片上传的地址
     */
    private static String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
    /**
     * Bucket是OSS上的命名空间
     */
    private static String bucketName = "sharkchao";
    /**
     * 图片的访问地址的前缀
     * 其实就是： bucketName + endpoint
     */
    private static String prefix = "https://"+bucketName+".oss-cn-hangzhou.aliyuncs.com/";


    /**
     * 图片保存到OSS服务器的目录
     */
    private static String dir = "app/";
    private OSS mOSS;
    private static OssManager mInstance;

    public static OssManager getInstance(){
        if (mInstance == null){
            synchronized (OssManager.class){
                if (mInstance == null){
                    mInstance = new OssManager();
                }
            }
        }
        return mInstance;
    }
    private  OSS getOSS(Context context){
        if (mOSS == null){
            OSSCredentialProvider provider = OSSConfig.newCustomSignerCredentialProvider();
            ClientConfiguration config = new ClientConfiguration();
            config.setConnectionTimeout(15 * 1000);
            config.setSocketTimeout(15 * 1000);
            config.setMaxConcurrentRequest(5);
            config.setMaxErrorRetry(2);
            mOSS = new OSSClient(context,endpoint,provider,config);
        }
        return mOSS;
    }

    @SuppressLint("CheckResult")
    public  void upload(final Context context, final int position, final ImageItem.Data data, final OnUploadListener listener){
        final Activity activity = (Activity) context;
        Observable.just(context)
                .map(new Function<Context, OSS>() {
                    @Override
                    public OSS apply(Context context) throws Exception {
                        return getOSS(context);
                    }
                })
                .map(new Function<OSS, String>() {
                    @Override
                    public String apply(final OSS oss) throws Exception {
                        PutObjectRequest put = new PutObjectRequest(bucketName,"app/"+getUUIDByRules32Image(),data.getPath());
                        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
                            @Override
                            public void onProgress(PutObjectRequest request, final long currentSize, final long totalSize) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (listener == null){
                                            return;
                                        }
                                        listener.onProgress(position,currentSize,totalSize);
                                    }
                                });

                            }

                        });

                        oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                            @Override
                            public void onSuccess(final PutObjectRequest request, final PutObjectResult result) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (listener == null){
                                            return;
                                        }
                                        String imageUrl = request.getObjectKey();

                                        listener.onSuccess(position,data.getPath(),prefix + imageUrl);
                                    }
                                });

                            }

                            @Override
                            public void onFailure(PutObjectRequest request, final ClientException clientException, final ServiceException serviceException) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
//                                        serviceException.printStackTrace();
//                                        clientException.printStackTrace();
                                        if (listener == null){
                                            return;
                                        }
                                        listener.onFailure(position);
                                    }
                                });

                            }
                        });
                        return data.getPath();
                    }
                }).subscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe();
    }
    @SuppressLint("CheckResult")

    public  void uploadResume(final Context context, final int position, final ImageItem.Data data, final OnUploadListener listener){
        final Activity activity = (Activity) context;

        Observable.just(context)
                .map(new Function<Context, OSS>() {
                    @Override
                    public OSS apply(Context context) throws Exception {
                        return getOSS(context);
                    }
                })
                .map(new Function<OSS, String>() {

                    OSSAsyncTask<ResumableUploadResult> task = null;
                    @Override
                    public String apply(final OSS oss) throws Exception {

                        ResumableUploadRequest request = new ResumableUploadRequest(bucketName, "app/"+getUUIDByRules32Image(), data.getPath());

                        request.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
                            @Override
                            public void onProgress(PutObjectRequest request, final long currentSize, final long totalSize) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (listener == null){
                                            return;
                                        }
                                        if (data.getState() == RichImageView.State.PAUSE){
                                            task.cancel();
                                        }
                                        listener.onProgress(position,currentSize,totalSize);
                                    }
                                });

                            }

                        });

                       task = oss.asyncResumableUpload(request, new OSSCompletedCallback<ResumableUploadRequest, ResumableUploadResult>() {
                            @Override
                            public void onSuccess(final ResumableUploadRequest request, final ResumableUploadResult result) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (listener == null) {
                                            return;
                                        }

                                        String imageUrl = request.getObjectKey();
                                        Log.e("tag_url", result.getServerCallbackReturnBody());

                                        listener.onSuccess(position, data.getPath(), prefix + imageUrl);
                                    }
                                });
                            }

                            @Override
                            public void onFailure(final ResumableUploadRequest request, final ClientException clientException, final ServiceException serviceException) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        serviceException.printStackTrace();
                                        clientException.printStackTrace();
                                        if (listener == null) {
                                            return;
                                        }
                                        listener.onFailure(position);
                                    }
                                });
                            }
                        });


                        return data.getPath();
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public interface OnUploadListener {
        /**
         * 上传的进度
         */
        void onProgress(int position, long currentSize, long totalSize);

        /**
         * 成功上传
         */
        void onSuccess(int position, String uploadPath, String imageUrl);

        /**
         * 上传失败
         */
        void onFailure(int position);
    }

    /**
     * 上传到后台的图片的名称
     */
    public static String getUUIDByRules32Image() {
        StringBuffer generateRandStr = null;
        try {
            String rules = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            int rpoint = 0;
            generateRandStr = new StringBuffer();
            Random rand = new Random();
            int length = 32;
            for (int i = 0; i < length; i++) {
                if (rules != null) {
                    rpoint = rules.length();
                    int randNum = rand.nextInt(rpoint);
                    generateRandStr.append(rules.substring(randNum, randNum + 1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (generateRandStr == null) {
            return "getUUIDByRules32Image.png";
        }
        return generateRandStr + ".png";
    }


}
