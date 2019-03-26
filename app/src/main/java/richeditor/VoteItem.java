package richeditor;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wangxudong.testricheditor.R;
import com.hupu.first.richeditor.view.VotingView;

public class VoteItem extends EditorItem<VoteItem.Data, VoteItem.VoteHolder>{

    @Override
    String getHtml(Data lastItem, Data nextItem) {
        return null;
    }

    @Override
    VoteHolder createHolder(ViewGroup parent) {
        return new VoteHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vote,null));
    }

    @Override
    void bindView(VoteHolder holder, final Data data, int position) {
        holder.votingView.displayLayout(data.id,data.localJson);
        holder.votingView.registerDeleteListener(new VotingView.DeleteListener() {
            @Override
            public void onDelete() {
                delete(data);
            }
        });
    }

    public static class VoteHolder extends EditorItemHolder{

        private VotingView votingView;
        public VoteHolder(@NonNull View itemView) {
            super(itemView);
            votingView = itemView.findViewById(R.id.view_vote);
        }
    }

    public static class Data extends EditorItemData{

        private String id;
        private String localJson;

        public Data(String id, String localJson) {
            this.id = id;
            this.localJson = localJson;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLocalJson() {
            return localJson;
        }

        public void setLocalJson(String localJson) {
            this.localJson = localJson;
        }

        @Override
        boolean append(EditorItemData itemData) {
            return false;
        }
    }
}
