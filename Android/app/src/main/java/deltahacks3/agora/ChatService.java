package deltahacks3.agora;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatService extends Service {
    private static final String LOG_TAG = ChatService.class.getSimpleName();
    private static final String SERVER_IPv6_ADDR = "172.17.74.204";
    private static final int SERVER_PORT = 8888;

    String roomId;
    DatagramSocket UDPsocket;

    HandlerThread UDPHandlerThread;
    Handler UDPHandler;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO: open connection, listeners for network (?)

        // roomId should not be empty
        this.roomId = intent.getStringExtra(ChatActivity.ROOM_ID_KEY);
        EventBus.getDefault().register(ChatService.this);

//        connectToServer();

        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

    private void connectToServer() {
        try {
            this.UDPsocket = new DatagramSocket(SERVER_PORT, InetAddress.getByName(SERVER_IPv6_ADDR));

            this.UDPHandlerThread = new HandlerThread("UDP Thread");
            this.UDPHandlerThread.start();
            this.UDPHandler = new Handler(this.UDPHandlerThread.getLooper());
            this.UDPHandler.post(new Runnable() {
                @Override
                public void run() {
                    byte[] buffer = new byte[65536];
                    DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);

                    try {
                        while(!UDPsocket.isClosed()) {
                            UDPsocket.receive(incoming);
                            byte[] data = incoming.getData();

                            String jsonStr = new String(data, 0, incoming.getLength());
                            JSONObject jsonObj = new JSONObject(jsonStr);

                            ChatMessage chatMsg = new ChatMessage(
                                    jsonObj.getString("username"), jsonObj.getString("message"));
                            EventBus.getDefault().post(new ChatActivity.EventReceiveMessage(chatMsg));
                        }
                    } catch (JSONException j) {
                        Log.e(LOG_TAG, "JSONException " + j);
                    } catch(IOException e) {
                        Log.e(LOG_TAG, "IOException " + e);
                    }
                }
            });

        } catch (UnknownHostException u) {
            Log.e(LOG_TAG, "Failed to resolve host for " + SERVER_IPv6_ADDR);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failed to open socket to ip: " + e.getMessage());
        }
    }

    // TODO: make this an HTML post
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ChatActivity.EventSendMessage sendMessage) {
        // Client just sent a message
        // TODO: temp for debug
        EventBus.getDefault().post(new ChatActivity.EventReceiveMessage(sendMessage.chatMsg));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ChatActivity.CloseConnection closeConnection) {
        this.UDPsocket.close();
    }
}
