package deltahacks3.agora;

public class ChatMessage {
    public String username;
    public String msg;

    // public long timestamp; // unix time
    public String profilePic; // TODO: What type

    // this shouldn't really ever be used
    public ChatMessage() {
        this("Anonymous", "");
    }

    public ChatMessage(String username, String msg) {
        this.username = username;
        this.msg = msg;
    }
}
