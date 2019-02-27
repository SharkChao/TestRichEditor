package richeditor;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.wangxudong.testricheditor.R;

public class TextItem extends EditorItem<TextItem.Data,TextItem.TextHolder> {

    @Override
    String getHtml(Data lastItem, Data nextItem) {
        return null;
    }

    @Override
    TextHolder createHolder(ViewGroup parent) {
        return new TextHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text,null));
    }


    @Override
    void bindView(TextHolder holder, final Data data, int position) {

        if(holder.editText.getTag() instanceof TextWatcher){
            holder.editText.removeTextChangedListener((TextWatcher) holder.editText.getTag());
        }

        holder.editText.setText(data.content);

        TextWatcher textWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                data.content=s.toString();
            }
        };
        holder.editText.addTextChangedListener(textWatcher);
        holder.editText.setTag(textWatcher);
    }


    public static final class TextHolder extends EditorItemHolder{
        EditText editText;
        public TextHolder(@NonNull View itemView) {
            super(itemView);
            editText= itemView.findViewById(R.id.edittext);
        }
    }

    public static class Data extends EditorItemData{

        private String content;

        public Data(String content) {
            this.content = content;
        }

        public Data() {
            this.content="";
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @Override
        boolean append(EditorItemData itemData) {
            if(itemData==null){
                return true;
            }

            if( itemData instanceof TextItem.Data){
                TextItem.Data textData= (Data) itemData;
                this.content+=(textData.content==null?"":textData.content);
                return true;
            }
            return false;
        }
    }


}
