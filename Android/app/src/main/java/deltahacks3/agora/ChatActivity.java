package deltahacks3.agora;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ChatActivity extends AppCompatActivity {
    public static final String ROOM_ID_KEY = "room_id_key";

    public static class EventSendMessage {
        public String roomId;
        public ChatMessage chatMsg;
        public EventSendMessage(String roomId, ChatMessage chatMsg) {
            this.roomId = roomId;
            this.chatMsg = chatMsg;
        }
    }

    public static class EventReceiveMessage {
        public ChatMessage chatMsg;
        public EventReceiveMessage(ChatMessage chatMsg) {
            this.chatMsg = chatMsg;
        }
    }

    public static class CloseConnection{}

    private String roomId;

    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private EditText chatEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        this.roomId = getIntent().getStringExtra(ROOM_ID_KEY);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Sector: " + this.roomId);
        }

        // Set-up chat recycler view
        this.chatRecyclerView = (RecyclerView) findViewById(R.id.chat_recycler_view);
        this.chatRecyclerView.setHasFixedSize(true);
        // TODO: currently will kill scrolling by consuming touch events, use dispatch
        /*this.chatRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard(); // if chat is pressed, hide keyboard
                return true;
            }
        });*/

        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setStackFromEnd(true);
        this.chatRecyclerView.setLayoutManager(llm);

        // TODO: add onClickListeners() to the adapter, calls TasksManager -> opens task overview
        // -> Debug for now, have click listener add 'fish' (branches) to check horizontal scroll functionality
        this.chatAdapter = new ChatAdapter();
        this.chatRecyclerView.setAdapter(chatAdapter);

        // set up chat edit text
        this.chatEditText = (EditText) findViewById(R.id.chat_edit_text);
        this.chatEditText.setClickable(true);
        this.chatEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        this.chatEditText.setImeOptions(EditorInfo.IME_ACTION_GO);
        this.chatEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO ||
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    sendChatMessage(chatEditText.getText().toString());
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(ChatActivity.this);
        startChatService();
    }

    @Override
    public void onPause() {
        super.onStop();
        EventBus.getDefault().unregister(ChatActivity.this);
        EventBus.getDefault().post(new CloseConnection());
        stopService(new Intent(this, ChatService.class));
    }

    private void sendChatMessage(String msg) {
        SharedPreferences cache = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String username = cache.getString(LoginActivity.USERNAME_KEY, "");

        if (username.equals("")) {
            this.chatEditText.setClickable(false);
            this.chatEditText.setText(R.string.chat_disabled);
            rejectedChatMessage();
        } else if (!msg.equals("")) {
            EventBus.getDefault().post(new EventSendMessage(roomId, new ChatMessage(username, msg)));
            this.chatEditText.setText(""); // clear the text when sent
        } // else, msg is empty string, so do nothing

        hideSoftKeyboard();
    }

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.chatEditText
                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(this.chatEditText.getWindowToken(), 0);
        }
    }

    private void rejectedChatMessage() {
        new AlertDialog.Builder(getApplicationContext())
                .setTitle("Failed to send message")
                .setMessage("We were unable to send your message, please login again.")
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {}
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    private void displayChatMessage(ChatMessage chatMsg) {
        this.chatAdapter.addNewMessage(chatMsg);
        this.chatRecyclerView.scrollToPosition(this.chatAdapter.getItemCount()-1);
    }

    private void startChatService() {
        Intent intent = new Intent(this, ChatService.class);
        intent.putExtra(ROOM_ID_KEY, this.roomId);
        startService(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ChatActivity.EventReceiveMessage receivedMessage) {
        displayChatMessage(receivedMessage.chatMsg);
    }
}
