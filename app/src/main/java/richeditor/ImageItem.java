package richeditor;

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
        richImg.setImgUri(data.mUri);
        richImg.uploading(data.progress,data.state);
    }

    public static class Data extends EditorItemData{
        Uri mUri;
        RichImageView.State state = RichImageView.State.INIT;
        int progress;

        public Data(Uri uri){
            mUri = uri;
        }


        public void setState(RichImageView.State state) {
            this.state = state;
        }

        public RichImageView.State getState() {
            return state;
        }

        public Uri getUri() {
            return mUri;
        }

        public void setProgress(int progress) {
            this.progress = progress;
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
