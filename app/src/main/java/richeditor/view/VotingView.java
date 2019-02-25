package richeditor.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wangxudong.testricheditor.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 投票view
 */
public class VotingView extends FrameLayout {
    String id;
    String localJson;
    Context mContext;

    String title;
    String attr;
    String select_pos = "2";

    ImageView mDelete;
    TextView mTtitleView;
    LinearLayout mContentView;
    DeleteListener mDeleteListener;

    public VotingView(Context context) {
        this(context,null);
    }

    public VotingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.view_voting_layout,this,true);

        mDelete = findViewById(R.id.vote_delete);
        mTtitleView = findViewById(R.id.vote_title);
        mContentView = findViewById(R.id.vote_content);

        mDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDeleteListener != null){
                    mDeleteListener.onDelete();
                }
            }
        });
    }

    /**
     * 根据json展示布局
     * @param id
     * @param localJson
     */
    public void displayLayout(String id,String localJson){
        if (TextUtils.isEmpty(id) || TextUtils.isEmpty(localJson)){
            return;
        }

        this.id = id;
        this.localJson = localJson;


        try {
            JSONObject object = new JSONObject(localJson);
            if (object != null){
                mContentView.removeAllViews();
                title = object.optString("title");
                attr = object.optString("attr");
                select_pos = object.optString("current_pos");
                if (!TextUtils.isEmpty(title)){
                    if ("radio".equals(attr)){
                        mTtitleView.setText(title);
                    }else {
                        mTtitleView.setText(title + "(最多可选" + select_pos + "项)");
                    }
                }

                JSONArray array = object.optJSONArray("name");
                if (array != null){
                    for (int i = 0;i < array.length();i++){
                        String name = array.optString(i);
                        if (!TextUtils.isEmpty(name)){
                            if (i != array.length() - 1){
                                grantLayout(name,true,attr);
                            }else {
                                grantLayout(name,false,attr);
                            }
                        }
                    }
                }
            }
        }catch (JSONException e){

        }
        invalidate();
    }

    private void grantLayout(String name,boolean hasline,String attr){
        View viewGroup=LayoutInflater.from(mContext).inflate(R.layout.view_voting_item,null);
        TextView textView= (TextView) viewGroup.findViewById(R.id.item_title);
        mContentView.addView(viewGroup);
        if(hasline){
            View view=new View(mContext);
            view.setBackgroundColor(mContext.getResources().getColor(R.color.voting_line_color));
            mContentView.addView(view, LinearLayout.LayoutParams.MATCH_PARENT,1);
        }
        ////单多选
        //type-》radio||checkbox
        if("checkbox".equals(attr)){
            viewGroup.findViewById(R.id.radio).setBackgroundResource(R.drawable.voteing_rect_icon_day);
        }else {
            viewGroup.findViewById(R.id.radio).setBackgroundResource(R.drawable.voteing_circle_icon_day);
        }


        textView.setText(name);
    }

    public interface DeleteListener{
        void onDelete();
    }

    public void registerDeleteListener(DeleteListener deleteListener){
        mDeleteListener = deleteListener;
    }
}
