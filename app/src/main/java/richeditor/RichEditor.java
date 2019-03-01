package richeditor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import richeditor.view.RichImageView;

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
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(rvEditor, layoutParams);
        richEditorAdapter = new RichEditorAdapter();
        richEditorAdapter.setRichEditor(this);
        layoutManager = new LinearLayoutManager(getContext());
        rvEditor.setLayoutManager(layoutManager);
        richEditorAdapter.registerItem(new TextItem());
        rvEditor.setAdapter(richEditorAdapter);
        closeDefaultAnimator();
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                focusItem(richEditorAdapter.getItemCount()-1);
            }
        });
        add(new TextItem.Data());


        rvEditor.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                setDataState(newState != RecyclerView.SCROLL_STATE_IDLE);
            }
        });
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

            if (!(list.get(0) instanceof TextItem.Data)){
                handledList.add(new TextItem.Data());
            }

            for (int i = 1; i < list.size(); i++) {
                EditorItemData current = list.get(i);
                EditorItemData last = handledList.get(handledList.size() - 1);
                if (last.append(current)) {
                    continue;
                } else {
                    handledList.add(current);
                    if (!(current  instanceof TextItem.Data)){
                        handledList.add(new TextItem.Data());
                    }
                }
            }
        }

        return handledList;
    }


    @Override
    public void add(EditorItemData itemData) {
        ArrayList<EditorItemData>list = new ArrayList<>();
        if (itemData != null){
            list.add(itemData);
        }

        setList(list);
    }


    private void focusLastItem() {
        focusItem(richEditorAdapter.getItemCount()-1);
    }



    private void addListData(List<EditorItemData> addList) {
        ArrayList<EditorItemData> list = richEditorAdapter.getList();

        if (richEditorAdapter.getItemCount() == 0){
            list.add(new TextItem.Data());
            return;
        }
        int cursorPosition = getCursorPosition();

        if (list != null) {
            //光标在edittext中间时需要拆分。
            ViewGroup parent = (ViewGroup) rvEditor.getLayoutManager().findViewByPosition(cursorPosition);
            if (parent != null && parent.getChildCount() > 0 && parent.getChildAt(0) instanceof EditText){
                int index = ((EditText) parent.getChildAt(0)).getSelectionStart();
                if (index != ((EditText) parent.getChildAt(0)).getText().length() && ((EditText) parent.getChildAt(0)).length()>0){
                    TextItem.Data data = (TextItem.Data) list.get(cursorPosition);
                    String temp = data.getContent();
                    data.setContent(temp.substring(0,index));
                    list.add(cursorPosition + 1,new TextItem.Data(temp.substring(index,temp.length())));
                }
            }

            if (cursorPosition == list.size() - 1){
                list.addAll(addList);
            }else {
                if (list.get(cursorPosition + 1) != null){
                    addList.get(addList.size() - 1).append(list.get(cursorPosition + 1));
                    list.addAll(cursorPosition + 1,addList);
                    list.remove(cursorPosition + 1 +addList.size() - 1);
                }


            }
        }


    }


    public int getCursorPosition(){
        for (int i = 0;i < richEditorAdapter.getList().size();i++){
            ViewGroup parent = (ViewGroup) rvEditor.getChildAt(i);

            if (parent != null && parent.getChildCount() > 0 && parent.getChildAt(0) instanceof EditText){
                EditText editText = (EditText) parent.getChildAt(0);
                if (editText.hasFocus()){
                    return rvEditor.getChildLayoutPosition(parent);
                }

            }

        }
        return richEditorAdapter.getList().size() -1;
    }

    public RecyclerView getRvEditor() {
        return rvEditor;
    }

    @Override
    public void setList(ArrayList<EditorItemData> list) {
        addListData(handleItemDataList(list));
        richEditorAdapter.notifyDataSetChanged();
        focusLastItem();
    }

    public RichEditorAdapter getRichEditorAdapter() {
        return richEditorAdapter;
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


    private void setDataState(boolean isScrolling){
        ArrayList<EditorItemData> list = richEditorAdapter.getList();
        LinearLayoutManager manager = (LinearLayoutManager) rvEditor.getLayoutManager();
        int firstVisibleItemPosition = manager.findFirstVisibleItemPosition();
        int lastVisibleItemPosition = manager.findLastVisibleItemPosition();

        List<EditorItemData> visibleList;
        if (lastVisibleItemPosition < list.size() -1){
             visibleList = list.subList(firstVisibleItemPosition,lastVisibleItemPosition+1);
        }else {
            visibleList = list;
        }

        for (EditorItemData data : isScrolling?list:visibleList){
            if (data instanceof ImageItem.Data){
                ImageItem.Data temp = (ImageItem.Data) data;
                if (isScrolling && temp.state == RichImageView.State.UPLOADING){
                    temp.setState(RichImageView.State.PAUSE);
                }
                if (!isScrolling && temp.state == RichImageView.State.PAUSE){
                    temp.setState(RichImageView.State.UPLOADING);
                }

            }
        }
    }

    /**
     * 打开默认局部刷新动画
     */
    public void openDefaultAnimator() {
        this.rvEditor.getItemAnimator().setAddDuration(120);
        this.rvEditor.getItemAnimator().setChangeDuration(250);
        this.rvEditor.getItemAnimator().setMoveDuration(250);
        this.rvEditor.getItemAnimator().setRemoveDuration(120);
        ((SimpleItemAnimator) this.rvEditor.getItemAnimator()).setSupportsChangeAnimations(true);
    }

    /**
     * 关闭默认局部刷新动画
     */
    public void closeDefaultAnimator() {
        this.rvEditor.getItemAnimator().setAddDuration(0);
        this.rvEditor.getItemAnimator().setChangeDuration(0);
        this.rvEditor.getItemAnimator().setMoveDuration(0);
        this.rvEditor.getItemAnimator().setRemoveDuration(0);
        ((SimpleItemAnimator) this.rvEditor.getItemAnimator()).setSupportsChangeAnimations(false);
    }


}
