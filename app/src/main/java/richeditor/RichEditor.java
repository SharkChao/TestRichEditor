package richeditor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import java.util.ArrayList;

public class RichEditor extends FrameLayout implements IRichEditor {

    private RecyclerView rvEditor;
    private RichEditorAdapter richEditorAdapter;
    private LinearLayoutManager layoutManager;


    public RichEditor(@NonNull Context context) {
        super(context);
        init();
    }

    public RichEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RichEditor(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        removeAllViews();
        rvEditor = new RecyclerView(getContext());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(rvEditor, layoutParams);
        richEditorAdapter = new RichEditorAdapter();
        richEditorAdapter.setRichEditor(this);
        layoutManager = new LinearLayoutManager(getContext());
        rvEditor.setLayoutManager(layoutManager);
        richEditorAdapter.registerItem(new TextItem());
        rvEditor.setAdapter(richEditorAdapter);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                focusItem(richEditorAdapter.getItemCount()-1);
            }
        });
        add(new TextItem.Data());
    }


    /**
     * 立即让某一项获取焦点
     */
    private void focusItem(int position){

        if(position>=richEditorAdapter.getItemCount()){
            position=richEditorAdapter.getItemCount()-1;
        }

        int delay=0;
        if(layoutManager.findFirstCompletelyVisibleItemPosition()>position
                ||layoutManager.findLastCompletelyVisibleItemPosition()<position){
            layoutManager.scrollToPosition(position);
            delay=100;
        }

        final int finalPosition = position;
        postDelayed(new Runnable() {
            @Override
            public void run() {
                RecyclerView.ViewHolder lastHolder = rvEditor.findViewHolderForAdapterPosition(finalPosition);
                if (lastHolder != null && lastHolder instanceof TextItem.TextHolder) {
                    final TextItem.TextHolder textHolder = (TextItem.TextHolder) lastHolder;
                    textHolder.editText.setSelection(textHolder.editText.getText().length());
                    textHolder.editText.requestFocus();
                    InputMethodManager inputManager =
                            (InputMethodManager) textHolder.editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.showSoftInput(textHolder.editText, 0);
                }
            }
        },delay);

    }



    public void registerWidget(EditorItem editorItem) {
        richEditorAdapter.registerItem(editorItem);
    }


    private ArrayList<EditorItemData> handleItemDataList(ArrayList<EditorItemData> list) {
        ArrayList<EditorItemData> handledList = new ArrayList<>();
        if (list == null || list.isEmpty()) {
            handledList.add(new TextItem.Data());
        } else {
            handledList.add(list.get(0));
            for (int i = 1; i < list.size() - 1; i++) {
                EditorItemData current = list.get(i);
                EditorItemData last = handledList.get(handledList.size() - 1);
                if (last.append(current)) {
                    continue;
                } else {
                    handledList.add(current);
                }
            }
        }

        return handledList;
    }


    @Override
    public void add(EditorItemData itemData) {
        addData(itemData);
        richEditorAdapter.notifyDataSetChanged();
        focusLastItem();
    }

    private void focusLastItem() {
        focusItem(richEditorAdapter.getItemCount()-1);
    }


    private void addData(EditorItemData itemData) {
        if (itemData != null) {
            richEditorAdapter.getList().add(itemData);
        }

        if (richEditorAdapter.getItemCount() == 0
                || !(richEditorAdapter.getList().get(richEditorAdapter.getItemCount() - 1) instanceof TextItem.Data)) {
            richEditorAdapter.getList().add(new TextItem.Data());
        }
    }

    @Override
    public void setList(ArrayList<EditorItemData> list) {
        richEditorAdapter.getList().clear();
        if(list!=null){
            richEditorAdapter.getList().addAll(handleItemDataList(list));
        }
        add(null);
        focusLastItem();
    }

    @Override
    public void delete(EditorItemData data) {

        int index = richEditorAdapter.getList().indexOf(data);
        if (index > -1) {
            if (index > 0 && index < richEditorAdapter.getItemCount() - 1) {

                richEditorAdapter.getList().remove(data);

                if (richEditorAdapter.getList().get(index - 1).append(richEditorAdapter.getList().get(index))) {
                    richEditorAdapter.getList().remove(index);
                    richEditorAdapter.notifyDataSetChanged();
                    focusItem(index - 1);
                }
            }
        }
    }
}
