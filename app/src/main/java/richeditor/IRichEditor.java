package richeditor;

import java.util.ArrayList;

public interface IRichEditor {
    void add(EditorItemData data);
    void setList(ArrayList<EditorItemData> list);
    void delete(EditorItemData data);
}
