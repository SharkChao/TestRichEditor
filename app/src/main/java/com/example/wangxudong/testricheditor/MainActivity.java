package com.example.wangxudong.testricheditor;

import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.wangxudong.testricheditor.utils.SharedPreferencesMgr;

import org.devio.takephoto.app.TakePhoto;
import org.devio.takephoto.app.TakePhotoActivity;
import org.devio.takephoto.compress.CompressConfig;
import org.devio.takephoto.model.TImage;
import org.devio.takephoto.model.TResult;

import java.util.ArrayList;
import java.util.List;

import richeditor.EditorItem;
import richeditor.EditorItemData;
import richeditor.ImageItem;
import richeditor.RichEditor;
import richeditor.TextItem;
import richeditor.VoteItem;
import richeditor.view.RichImageView;

public class MainActivity extends TakePhotoActivity {

    RichEditor richEditor;
    TakePhoto takePhoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        richEditor=findViewById(R.id.editor);


        initTakePhoto();
        findViewById(R.id.btn_add_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto.onPickMultiple(9);

            }
        });
        findViewById(R.id.btn_add_vote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                richEditor.add(new VoteItem.Data("1",getResources().getString(R.string.vote_local_json)));

            }
        });

        richEditor.registerWidget(new ImageItem());
        richEditor.registerWidget(new VoteItem());

        richEditor.restore(SharedPreferencesMgr.getString("temp_string",""));

    }

    private void initTakePhoto() {
        takePhoto = getTakePhoto();
        CompressConfig config = new CompressConfig.Builder()
                .setMaxSize(50 * 1024)
                .setMaxPixel(800).create();
        takePhoto.onEnableCompress(config,true);
    }

    /**
     * 上传至oss服务器
     */
    private void uploadList(){
        for (int i = 0 ; i < richEditor.getRichEditorAdapter().getList().size();i++){
            if (richEditor.getRichEditorAdapter().getList().get(i) instanceof ImageItem.Data){
                final ImageItem.Data data = (ImageItem.Data) richEditor.getRichEditorAdapter().getList().get(i);
                if (data.getState() == RichImageView.State.INIT && TextUtils.isEmpty(data.getLinkUrl())){
                    OssManager.getInstance().upload(this, i, data , new OssManager.OnUploadListener() {
                        @Override
                        public void onProgress(int position, long currentSize, long totalSize) {

                            data.setState(RichImageView.State.UPLOADING);
                            data.setProgress((int) (100*currentSize/totalSize));
                            richEditor.getRichEditorAdapter().notifyItemChanged(position,1);
                        }

                        @Override
                        public void onSuccess(int position, String uploadPath, String imageUrl) {
                            data.setState(RichImageView.State.SUCCESS);
                            data.setLinkUrl(imageUrl);
                            richEditor.getRichEditorAdapter().notifyItemChanged(position,1);
                        }

                        @Override
                        public void onFailure(int position) {
                            data.setState(RichImageView.State.FAIL);
                            richEditor.getRichEditorAdapter().notifyItemChanged(position,1);
                        }
                    });
                }

            }
        }


    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        ArrayList<TImage> images = result.getImages();
        ArrayList<EditorItemData>list = new ArrayList<>();
        for (TImage image : images){
            ImageItem.Data data = new ImageItem.Data(image.getCompressPath());
            list.add(data);
        }
        richEditor.setList(list);
        uploadList();
    }

    @Override
    protected void onDestroy() {
        SharedPreferencesMgr.setString("temp_string",richEditor.save());
        super.onDestroy();
    }
}
