package com.example.wangxudong.testricheditor;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

import richeditor.EditorItem;
import richeditor.EditorItemData;
import richeditor.ImageItem;
import richeditor.RichEditor;
import richeditor.TextItem;
import richeditor.VoteItem;

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

                richEditor.add(new ImageItem.Data(uri));

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

  /*      ArrayList<EditorItemData> list=new ArrayList<>();
        list.add(new TextItem.Data("wwwww"));
        list.add(new TextItem.Data("dddddd"));
        list.add(new TextItem.Data("gggggg"));
        list.add(new TextItem.Data("rrrrrr"));


        richEditor.setList(list);*/

    }
}
