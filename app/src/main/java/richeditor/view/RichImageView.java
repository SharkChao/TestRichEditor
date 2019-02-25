package richeditor.view;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wangxudong.testricheditor.R;

import richeditor.ImageItem;

public class RichImageView extends FrameLayout {



    private ImageView ivContent;
    private ImageView ivDelete;
    private TextView tvProgress;
    private ImageView ivProgress;
    private ImageView ivUploadGrayBg;
    private DeleteListener mDeleteListener;
    public RichImageView(Context context) {
        super(context);
        init(context);
    }

    public RichImageView( Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RichImageView( Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){

        LayoutInflater.from(context).inflate(R.layout.view_rich_img,this,true);
        ivDelete = findViewById(R.id.ivDelete);
        ivContent = findViewById(R.id.img);
        tvProgress = findViewById(R.id.img_progress_text);
        ivProgress = findViewById(R.id.img_progress);
        ivUploadGrayBg = findViewById(R.id.img_upload_gray);

        ivDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mDeleteListener.onDelete();

            }
        });

        tvProgress.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public void setImgUri(Uri uri){
        ivContent.setImageURI(uri);
    }




    public void uploading(int progress, State state){
        if (state == State.UPLOADING){
            tvProgress.setVisibility(View.VISIBLE);
            ivProgress.setVisibility(View.VISIBLE);
            ivUploadGrayBg.setVisibility(VISIBLE);
            tvProgress.setText(progress + "%");
        }else if (state == State.SUCCESS){
            tvProgress.setVisibility(View.GONE);
            ivProgress.setVisibility(View.GONE);
            ivUploadGrayBg.setVisibility(GONE);
        }else if (state == State.FAIL){
            tvProgress.setVisibility(View.VISIBLE);
            ivProgress.setVisibility(View.GONE);
            ivUploadGrayBg.setVisibility(VISIBLE);
            tvProgress.setText("上传失败，请点击重试");
        }

    }


    public void registerDeleteListener(DeleteListener deleteListener){
        mDeleteListener = deleteListener;
    }

    public interface DeleteListener{
        void onDelete();
    }

    public  enum State{
        INIT,UPLOADING,SUCCESS,FAIL
    }


}
