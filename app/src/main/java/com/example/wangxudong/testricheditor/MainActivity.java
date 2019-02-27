package com.example.wangxudong.testricheditor;

import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import richeditor.EditorItem;
import richeditor.EditorItemData;
import richeditor.ImageItem;
import richeditor.RichEditor;
import richeditor.TextItem;
import richeditor.VoteItem;
import richeditor.view.RichImageView;

public class MainActivity extends AppCompatActivity {

    RichEditor richEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        richEditor=findViewById(R.id.editor);
        findViewById(R.id.btn_add_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.mipmap.temp);
                final ImageItem.Data data = new ImageItem.Data(uri);
                richEditor.add(data);


               /* DataUtils dataUtils = new DataUtils(data);
                dataUtils.regsiterUploadListener(new DataUtils.OnUploadListener() {
                    @Override
                    public void upload(ImageItem.Data data) {
                        int position = richEditor.getRichEditorAdapter().getList().indexOf(data);
                        richEditor.getRichEditorAdapter().notifyItemChanged(position, 1);
                    }
                });
                dataUtils.upload();*/
                int position = richEditor.getRichEditorAdapter().getList().indexOf(data);
               OssManager.getInstance().upload(MainActivity.this, position, data, new OssManager.OnUploadListener() {
                   @Override
                   public void onProgress(int position, long currentSize, long totalSize) {
                       data.setState(RichImageView.State.UPLOADING);
                       data.setProgress((int) (currentSize/totalSize));
                   }

                   @Override
                   public void onSuccess(int position, String uploadPath, String imageUrl) {
                        data.setState(RichImageView.State.SUCCESS);
                   }

                   @Override
                   public void onFailure(int position) {
                        data.setState(RichImageView.State.FAIL);
                   }
               });

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
                DataUtils dataUtils = new DataUtils((ImageItem.Data) richEditor.getRichEditorAdapter().getList().get(i));
                dataUtils.regsiterUploadListener(new DataUtils.OnUploadListener() {
                    @Override
                    public void upload(ImageItem.Data data) {
                        int position = richEditor.getRichEditorAdapter().getList().indexOf(data);
                        richEditor.getRichEditorAdapter().notifyItemChanged(position, 1);
                    }
                });
                dataUtils.upload();
            }
        }
    }



}
