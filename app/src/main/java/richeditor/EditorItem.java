package richeditor;

import android.view.ViewGroup;

public abstract class EditorItem< D extends EditorItemData ,H extends EditorItemHolder> {

    private RichEditorAdapter adapter;

    public void setAdapter(RichEditorAdapter adapter) {
        this.adapter = adapter;
    }

    public RichEditorAdapter getAdapter() {
        return adapter;
    }

    abstract String getHtml(D lastItem, D nextItem);

    abstract H createHolder(ViewGroup parent);

    abstract void bindView(H holder,D data,int position);

    protected int getItemCount(){
        return adapter.getItemCount();
    }

    protected void delete(EditorItemData itemData){
        if(adapter!=null){
            adapter.delete(itemData);
        }
    }


}
