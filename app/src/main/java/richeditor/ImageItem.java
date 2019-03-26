package richeditor;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.wangxudong.testricheditor.R;

import java.util.HashMap;

import richeditor.view.RichImageView;

public class ImageItem extends EditorItem<ImageItem.Data,ImageItem.Holder> {


    @Override
    String getHtml(Data lastItem, Data nextItem) {
        return null;
    }

    @Override
    Holder createHolder(ViewGroup parent) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image,null));
    }



    @Override
    void bindView(final Holder holder, final Data data, int position) {

        final RichImageView richImg = holder.richImg;

        richImg.registerDeleteListener(new RichImageView.DeleteListener() {
            @Override
            public void onDelete() {
                delete(data);
                data.state = RichImageView.State.INIT;
                data.progress = 0;
            }
        });

        richImg.setImagePath(data.getPath());
        richImg.uploading(data.progress,data.state);
    }

    public static class Data extends EditorItemData{
        String mPath;
        String linkUrl;

        RichImageView.State state = RichImageView.State.INIT;
        int progress;

        public Data(String path){
            mPath = path;
        }

        public Data(String path, String linkUrl) {
            this.mPath = path;
            this.linkUrl = linkUrl;
        }

        public void setState(RichImageView.State state) {
            this.state = state;
        }

        public RichImageView.State getState() {
            return state;
        }

        public String getPath() {
            return mPath;
        }

        public void setProgress(int progress) {
            this.progress = progress;
        }

        public String getLinkUrl() {
            return linkUrl;
        }

        public void setLinkUrl(String linkUrl) {
            this.linkUrl = linkUrl;
        }

        @Override
        boolean append(EditorItemData itemData) {
            return false;
        }

    }

    public static  class Holder extends EditorItemHolder{

        RichImageView richImg;
        public Holder(@NonNull View itemView) {
            super(itemView);
            richImg = itemView.findViewById(R.id.rich_img);
        }

    }




}
