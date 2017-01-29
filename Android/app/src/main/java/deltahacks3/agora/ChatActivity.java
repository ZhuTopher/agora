package deltahacks3.agora;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ChatActivity extends AppCompatActivity {

    RecyclerView chatRecyclerView;
    ChatAdapter chatAdapter;
    EditText chatEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Set-up chat recycler view
        this.chatRecyclerView = (RecyclerView) findViewById(R.id.chat_recycler_view);
        this.chatRecyclerView.setHasFixedSize(true);
        this.chatRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard(); // if chat is pressed, hide keyboard
                return true;
            }
        });

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
//        this.chatEditText.setClickable(true);
//        this.chatEditText.setImeActionLabel("Send", KeyEvent.KEYCODE_ENTER);
        /*this.chatEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    sendChatMessage(chatEditText.getText().toString());
                    return true;
                }

                return false;
            }
        });*/

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

    private void sendChatMessage(String msg) {
        SharedPreferences cache = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String username = cache.getString(LoginActivity.USERNAME_KEY, "");

        if (username.equals("")) {
            rejectedChatMessage();
            this.chatEditText.setClickable(false);
            this.chatEditText.setText(R.string.chat_disabled);
        } else if (!msg.equals("")) {
            displayChatMessage(new ChatMessage(username, msg)); // TODO: username
            this.chatEditText.setText(""); // clear the text when sent
        } // else, msg is empty string, so do nothinig

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
}
