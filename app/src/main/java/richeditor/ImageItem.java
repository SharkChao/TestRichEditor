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
    void bindView(Holder holder, final Data data, int position) {

        holder.itemView.setTag(holder);
        final RichImageView richImg = holder.richImg;
        if (richImg.getTag() == null){
            richImg.setTag(data.mUri);
        }

        richImg.registerDeleteListener(new RichImageView.DeleteListener() {
            @Override
            public void onDelete() {
                delete(data);
                data.progress = 0;
                data.state = RichImageView.State.INIT;
            }
        });
        richImg.setImgUri(data.mUri);
        richImg.uploading(data.progress,data.state);


        if (data.state == RichImageView.State.INIT){
            uploadImg2AliServer(data);
        }

    }

    public static class Data extends EditorItemData{
        Uri mUri;
        RichImageView.State state = RichImageView.State.INIT;
        int progress;

        public Data(Uri uri){
            mUri = uri;
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


    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            Data data = (Data) message.obj;

            if(getAdapter()!=null
                    &&getAdapter().getRecyclerView()!=null){
                LinearLayoutManager layoutManager= (LinearLayoutManager) getAdapter().getRecyclerView().getLayoutManager();
                int size = layoutManager.getChildCount();
                for (int i = 0;i < size;i++){
                    View view = layoutManager.getChildAt(i);
                    Holder holder = (Holder) view.getTag();
                    if (holder != null){
                        RichImageView imageView = holder.richImg;
                        if (imageView.getTag() != null && imageView.getTag() == data.mUri){
                            imageView.uploading(data.progress,data.state);
                        }
                    }

                }
            }
            return true;
        }
    });



    private void uploadImg2AliServer(final Data data){
        data.state = RichImageView.State.UPLOADING;
       new Thread(new Runnable() {
           @Override
           public void run() {
               for (int i = 0;i < 100;i++){
                   try {
                       Thread.sleep(100);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }

                   data.progress = i;
                   data.state = RichImageView.State.UPLOADING;
                   if (i == 99){
                       data.state = RichImageView.State.SUCCESS;
                   }
                   Message message = new Message();
                   message.obj = data;
                   handler.sendMessage(message);

               }

           }
       }).start();
    }

}
