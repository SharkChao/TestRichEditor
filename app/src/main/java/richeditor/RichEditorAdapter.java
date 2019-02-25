package richeditor;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class RichEditorAdapter extends RecyclerView.Adapter {

    private  IRichEditor richEditor;

    private SparseArray<EditorItem> editorPool= new SparseArray<>();

    private ArrayList<EditorItemData> itemDataList = new ArrayList<>();

    private RecyclerView recyclerView;


    public void setRichEditor(IRichEditor richEditor) {
        this.richEditor = richEditor;
    }

    public void registerItem(EditorItem editorItem){
        if (editorPool.indexOfValue(editorItem) >= 0) {
            return;
        }
        int key = editorPool.size();
        editorItem.setAdapter(this);
        editorPool.put(key, editorItem);
    }

    public void clearData(){
        this.itemDataList.clear();
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView=recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView=null;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return editorPool.get(i).createHolder(viewGroup);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
       int itemType=getItemViewType(i);
       if(itemType==-1){
           return;
       }

        Type type =editorPool.get(itemType).getClass().getGenericSuperclass();
        ParameterizedType p=(ParameterizedType)type;
        //getActualTypeArguments获取参数化类型的数组，泛型可能有多个
        Class dataClass=(Class) p.getActualTypeArguments()[0];
        Class holderClass=(Class) p.getActualTypeArguments()[1];

        if(dataClass.isInstance(itemDataList.get(i))
                &&holderClass.isInstance(viewHolder)){
            editorPool.get(itemType).bindView((EditorItemHolder) viewHolder,itemDataList.get(i),i);
        }

    }

    @Override
    public int getItemViewType(int position) {
       EditorItemData itemData= itemDataList.get(position);
       for(int i=0;i<editorPool.size();i++){
           Type type =editorPool.get(i).getClass().getGenericSuperclass();
           ParameterizedType p=(ParameterizedType)type;
           //getActualTypeArguments获取参数化类型的数组，泛型可能有多个
           Class c=(Class) p.getActualTypeArguments()[0];
           if(c.isInstance(itemData)){
               return i;
           }
       }
        return -1;
    }

    @Override
    public int getItemCount() {
        return itemDataList==null?0:itemDataList.size();
    }


    public ArrayList<EditorItemData> getList() {
        return itemDataList;
    }

    public void delete(EditorItemData data){
        if(richEditor!=null){
            richEditor.delete(data);
        }
    }
}
