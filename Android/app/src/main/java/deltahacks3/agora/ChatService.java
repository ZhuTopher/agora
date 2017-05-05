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

        connectToServer();

        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

    private void connectToServer() {
            this.UDPHandlerThread = new HandlerThread("UDP Thread");
            this.UDPHandlerThread.start();
            this.UDPHandler = new Handler(this.UDPHandlerThread.getLooper());
            this.UDPHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        UDPsocket = new DatagramSocket(SERVER_PORT);
                        byte[] buffer = new byte[8192];
                        DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);

                        while(!UDPsocket.isClosed()) {
                            UDPsocket.receive(incoming);
                            byte[] data = incoming.getData();

                            /*String jsonStr = new String(data, 0, incoming.getLength());
                            JSONObject jsonObj = new JSONObject(jsonStr);

                            ChatMessage chatMsg = new ChatMessage(
                                    jsonObj.getString("username"), jsonObj.getString("message"));*/

                            String msg = new String(data);
                            EventBus.getDefault().post(new ChatActivity.EventReceiveMessage(
                                    new ChatMessage("SERVER?", msg)));
                        }
                    /*} catch (JSONException j) {
                        Log.e(LOG_TAG, "JSONException " + j);*/

                    } catch (UnknownHostException u) {
                        Log.e(LOG_TAG, "Failed to resolve host for " + SERVER_IPv6_ADDR);
                    } catch(IOException e) {
                        Log.e(LOG_TAG, "Failed to open socket to ip: " + e.getMessage());
                    }
                }
            });
    }

    // TODO: make this an HTML post
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEventMainThread(ChatActivity.EventSendMessage sendMessage) {
        // Client just sent a message
        // TODO: temp for debug
        // EventBus.getDefault().post(new ChatActivity.EventReceiveMessage(sendMessage.chatMsg));
            try {
                if (this.UDPsocket == null) {
                    this.UDPsocket = new DatagramSocket();
                }
                byte[] bytes = sendMessage.chatMsg.msg.getBytes();
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length,
                        InetAddress.getByName(SERVER_IPv6_ADDR), SERVER_PORT);
                this.UDPsocket.send(packet);
            } catch (UnknownHostException u) {
                Log.e(LOG_TAG, "Failed to resolve host for " + SERVER_IPv6_ADDR);
            } catch (IOException e) {
                Log.e(LOG_TAG, "r.i.p");
            }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ChatActivity.CloseConnection closeConnection) {
        this.UDPsocket.close();
    }
}
