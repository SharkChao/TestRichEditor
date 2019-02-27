package com.example.wangxudong.testricheditor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import richeditor.ImageItem;

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
        Observable.just(context)
                .map(new Function<Context, OSS>() {
                    @Override
                    public OSS apply(Context context) throws Exception {
                        return getOSS(context);
                    }
                })
                .map(new Function<OSS, String>() {
                    @Override
                    public String apply(OSS oss) throws Exception {
                        File file = new File(ImageUtils.getFilePathByUri(context,data.getUri()));
                        String path = ImageUtils.saveBitmap(file.getAbsolutePath(), "test");
                        PutObjectRequest put = new PutObjectRequest(bucketName,"app/"+getUUIDByRules32Image(),path);
                        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
                            @Override
                            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                                if (listener == null){
                                    return;
                                }
                                listener.onProgress(position,currentSize,totalSize);
                            }
                        });
                        oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                            @Override
                            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                                if (listener == null){
                                    return;
                                }
                                String imageUrl = request.getObjectKey();
                                listener.onSuccess(position,data.getUri().getPath(),prefix + imageUrl);
                            }

                            @Override
                            public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                                serviceException.printStackTrace();
                                clientException.printStackTrace();
                                if (listener == null){
                                    return;
                                }
                                listener.onFailure(position);
                            }
                        });
                        return data.getUri().getPath();
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
