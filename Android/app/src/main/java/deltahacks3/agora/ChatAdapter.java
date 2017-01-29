package deltahacks3.agora;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private List<ChatMessage> msgList;

    public ChatAdapter() {
        this.msgList = new ArrayList<>();
    }

    public ChatAdapter(List<ChatMessage> tasksList) {
        this.msgList = tasksList;
    }

    @Override
    public int getItemCount() {
        return this.msgList.size();
    }

    @Override
    public void onBindViewHolder(MessageViewHolder msgViewHolder, int i) {
        ChatMessage msg = this.msgList.get(i);
        msgViewHolder.username.setText(msg.username);
        msgViewHolder.chatMessage.setText(msg.msg);
//        msgViewHolder.profilePic.setImageBitmap(); // TODO:
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.chat_message, viewGroup, false);

        return new MessageViewHolder(itemView);
    }

    public void addNewMessage(ChatMessage chatMsg) {
        this.msgList.add(chatMsg);
        this.notifyItemInserted(msgList.size() - 1);
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        // Task card views
        private TextView username, chatMessage;
        private ImageView profilePic;

        public MessageViewHolder(View v) {
            super(v);
            this.username = (TextView) v.findViewById(R.id.msg_username);
            this.chatMessage = (TextView) v.findViewById(R.id.msg_text_view);
            this.profilePic = (ImageView) v.findViewById(R.id.msg_profile_pic);
        }
    }
}
