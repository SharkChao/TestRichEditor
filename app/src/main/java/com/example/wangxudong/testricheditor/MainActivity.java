package com.example.wangxudong.testricheditor;

import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        richEditor=findViewById(R.id.editor);
        findViewById(R.id.btn_add_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                TakePhoto takePhoto = getTakePhoto();
                CompressConfig config = new CompressConfig.Builder()
                        .setMaxSize(50 * 1024)
                        .setMaxPixel(800).create();
                takePhoto.onEnableCompress(config,true);
                takePhoto.onPickMultiple(9);
            }
        });
        findViewById(R.id.btn_add_vote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                richEditor.add(new VoteItem.Data("1","{\n" +
                        "\t\"title\": \"投票测试标题\",\n" +
                        "\t\"attr\": \"radio\",\n" +
                        "\t\"select_pos\": \"3\",\n" +
                        "\t\"name\": [\"测试选项1\", \"测试选项2\", \"测试选项3\"\n" +
                        "\n" +
                        "\t]\n" +
                        "}"));

            }
        });

        richEditor.registerWidget(new ImageItem());
        richEditor.registerWidget(new VoteItem());
    }

    private void uploadList(){
        for (int i = 0 ; i < richEditor.getRichEditorAdapter().getList().size();i++){
            if (richEditor.getRichEditorAdapter().getList().get(i) instanceof ImageItem.Data){
                final ImageItem.Data data = (ImageItem.Data) richEditor.getRichEditorAdapter().getList().get(i);
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
}
