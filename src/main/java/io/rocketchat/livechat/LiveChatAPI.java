package io.rocketchat.livechat;

import io.rocketchat.common.data.rpc.RPC;
import io.rocketchat.common.listener.ConnectListener;
import io.rocketchat.common.listener.SubscribeListener;
import io.rocketchat.common.listener.TypingListener;
import io.rocketchat.common.network.Socket;
import io.rocketchat.common.utils.Utils;
import io.rocketchat.livechat.callback.*;
import io.rocketchat.livechat.middleware.LiveChatMiddleware;
import io.rocketchat.livechat.middleware.LiveChatStreamMiddleware;
import io.rocketchat.livechat.rpc.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by sachin on 8/6/17.
 */

// TODO: 30/7/17 Make it singletone like eventbus, add builder class to LiveChatAPI in order to use it anywhere
public class LiveChatAPI extends Socket{

    AtomicInteger integer;
    String sessionId;
    JSONObject userInfo;

    ConnectListener connectListener;

    LiveChatMiddleware liveChatMiddleware;
    LiveChatStreamMiddleware liveChatStreamMiddleware;

    public LiveChatAPI(String url) {
        super(url);
        integer=new AtomicInteger(1);
        liveChatMiddleware =LiveChatMiddleware.getInstance();
        liveChatStreamMiddleware=LiveChatStreamMiddleware.getInstance();
    }

    public void setConnectListener(ConnectListener connectListener) {
        this.connectListener = connectListener;
    }

    public void getInitialData(InitialDataListener listener){
        int uniqueID=integer.getAndIncrement();
        liveChatMiddleware.createCallback(uniqueID,listener, LiveChatMiddleware.ListenerType.GETINITIALDATA);
        sendDataInBackground(LiveChatBasicRPC.getInitialData(uniqueID));
    }

    public void registerGuest(String name, String email,  String dept, AuthListener.RegisterListener listener){
        int uniqueID=integer.getAndIncrement();
        liveChatMiddleware.createCallback(uniqueID,listener, LiveChatMiddleware.ListenerType.REGISTER);
        sendDataInBackground(LiveChatBasicRPC.registerGuest(uniqueID,name,email,dept));
    }

    public void login(String token, AuthListener.LoginListener listener){
        int uniqueID=integer.getAndIncrement();
        liveChatMiddleware.createCallback(uniqueID,listener, LiveChatMiddleware.ListenerType.LOGIN);
        sendDataInBackground(LiveChatBasicRPC.login(uniqueID,token));
    }


    public void sendOfflineMessage( String name, String email,  String message){
        int uniqueID=integer.getAndIncrement();
        sendDataInBackground(LiveChatBasicRPC.sendOfflineMessage(uniqueID,name,email,message));
    }

    public void sendOfflineMessage( String name, String email, String message, MessageListener.OfflineMessageListener listener){
        int uniqueID=integer.getAndIncrement();
        liveChatMiddleware.createCallback(uniqueID,listener, LiveChatMiddleware.ListenerType.SENDOFFLINEMESSAGE);
        sendDataInBackground(LiveChatBasicRPC.sendOfflineMessage(uniqueID,name,email,message));
    }


    private void getChatHistory(String roomID, int limit, Date oldestMessageTimestamp,  Date lasttimestamp, LoadHistoryListener listener){
        int uniqueID = integer.getAndIncrement();
        liveChatMiddleware.createCallback(uniqueID,listener, LiveChatMiddleware.ListenerType.GETCHATHISTORY);
        sendDataInBackground(LiveChatHistoryRPC.loadHistory(uniqueID,roomID,oldestMessageTimestamp,limit,lasttimestamp));
    }


    private void getAgentData( String roomId, AgentListener.AgentDataListener listener){
        int uniqueID = integer.getAndIncrement();
        liveChatMiddleware.createCallback(uniqueID,listener, LiveChatMiddleware.ListenerType.GETAGENTDATA);
        sendDataInBackground(LiveChatBasicRPC.getAgentData(uniqueID,roomId));
    }


    private void sendMessage( String msgId, String roomID, String message, String token){
        int uniqueID = integer.getAndIncrement();
        sendDataInBackground(LiveChatSendMsgRPC.sendMessage(uniqueID, msgId, roomID, message, token));
    }

    private void sendMessage(String msgId, String roomID, String message, String token, MessageListener.MessageAckListener messageAckListener){
        int uniqueID = integer.getAndIncrement();
        liveChatMiddleware.createCallback(uniqueID,messageAckListener, LiveChatMiddleware.ListenerType.SENDMESSAGE);
        sendDataInBackground(LiveChatSendMsgRPC.sendMessage(uniqueID, msgId, roomID, message, token));
    }

    private void sendIsTyping( String roomId, String username, Boolean istyping){
        int uniqueID = integer.getAndIncrement();
        sendDataInBackground(LiveChatTypingRPC.streamNotifyRoom(uniqueID,roomId,username,istyping));
    }


    private void subscribeRoom(String roomID, Boolean enable, SubscribeListener subscribeListener, MessageListener.SubscriptionListener listener){

        String uniqueID=Utils.shortUUID();
        liveChatStreamMiddleware.createSubCallbacks(uniqueID, subscribeListener);
        liveChatStreamMiddleware.subscribeRoom(listener);
        sendDataInBackground(LiveChatSubRPC.streamRoomMessages(uniqueID,roomID,enable));

    }

    private void subscribeLiveChatRoom( String roomID,  Boolean enable,  SubscribeListener subscribeListener,  AgentListener.AgentConnectListener agentConnectListener){

        String uniqueID=Utils.shortUUID();
        liveChatStreamMiddleware.createSubCallbacks(uniqueID, subscribeListener);
        liveChatStreamMiddleware.subscribeLiveChatRoom(agentConnectListener);
        sendDataInBackground(LiveChatSubRPC.streamLivechatRoom(uniqueID,roomID,enable));

    }

    private void subscribeTyping(String roomID, Boolean enable,  SubscribeListener subscribeListener,  TypingListener listener){

        String uniqueID=Utils.shortUUID();
        liveChatStreamMiddleware.createSubCallbacks(uniqueID, subscribeListener);
        liveChatStreamMiddleware.subscribeTyping(listener);
        sendDataInBackground(LiveChatSubRPC.subscribeTyping(uniqueID,roomID,enable));

    }

    private void closeConversation( String roomId){
        int uniqueID = integer.getAndIncrement();
        sendDataInBackground(LiveChatBasicRPC.closeConversation(uniqueID,roomId));
    }

    public void connect(ConnectListener connectListener) {
        createSocket();
        this.connectListener = connectListener;
        super.connectAsync();
    }

    @Override
    protected void onConnected() {
        integer.set(1);
        sendDataInBackground(LiveChatBasicRPC.ConnectObject());
        super.onConnected();
    }

    @Override
    protected void onTextMessage(String text) throws Exception{
        JSONObject object = new JSONObject(text);
        switch (RPC.parse(object.optString("msg"))) {
            case PING:
                sendDataInBackground("{\"msg\":\"pong\"}");
                break;
            case CONNECTED:
                sessionId = object.optString("session");
                if (connectListener != null) {
                    connectListener.onConnect(sessionId);
                }
                break;
            case ADDED:
                if (object.optString("collection").equals("users")) {
                    userInfo = object.optJSONObject("fields");
                }
                break;
            case RESULT:
                liveChatMiddleware.processCallback(Long.valueOf(object.optString("id")), object);
                break;
            case READY:
                liveChatStreamMiddleware.processSubSuccess(object);
                break;
            case CHANGED:
                liveChatStreamMiddleware.processCallback(object);
                break;
            case OTHER:
                //DO SOMETHING
                break;
        }
        super.onTextMessage(text);
    }

    @Override
    protected void onConnectError(Exception websocketException) {
        if (connectListener!=null) {
            connectListener.onConnectError(websocketException);
        }
        super.onConnectError(websocketException);
    }

    @Override
    protected void onDisconnected(boolean closedByServer) {
        if (connectListener!=null) {
            connectListener.onDisconnect(closedByServer);
        }
        super.onDisconnected(closedByServer);
    }


    // TODO: 30/7/17 Add methods for unsubscribing events
    public class ChatRoom{

        String userName;
        String roomId;
        String userId;
        String visitorToken;
        String authToken;

        public ChatRoom(String userName, String roomId, String userId, String visitorToken, String authToken) {
            this.userName = userName;
            this.roomId = roomId;
            this.userId = userId;
            this.visitorToken = visitorToken;
            this.authToken = authToken;
        }

        public ChatRoom(String s){
            try {
                JSONObject object=new JSONObject(s);
                this.userName=object.getString("userName");
                this.roomId=object.getString("roomId");
                this.userId=object.getString("userId");
                this.visitorToken=object.getString("visitorToken");
                this.authToken=object.getString("authToken");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public void login(AuthListener.LoginListener listener){
            LiveChatAPI.this.login(authToken,listener);
        }

        public void getChatHistory(int limit, Date oldestMessageTimestamp,Date lasttimestamp,LoadHistoryListener listener){
            LiveChatAPI.this.getChatHistory(roomId,limit,oldestMessageTimestamp,lasttimestamp,listener);
        }

        public void getAgentData(AgentListener.AgentDataListener listener){
            LiveChatAPI.this.getAgentData(roomId,listener);
        }

        /**
         * Used for sending messages to server
         * @param message to be sent
         * @return MessageID
         */
        public String sendMessage(String message){
            String uuid=Utils.shortUUID();
            LiveChatAPI.this.sendMessage(uuid,roomId,message,visitorToken);
            return uuid;
        }

        /**
         * Used for sending messages to server with messageAcknowledgement
         * @param message
         * @param messageAckListener Returns ack to particular message
         * @return MessageID
         */

        public String sendMessage(String message,MessageListener.MessageAckListener messageAckListener){
            String uuid=Utils.shortUUID();
            LiveChatAPI.this.sendMessage(uuid,roomId,message,visitorToken,messageAckListener);
            return uuid;
        }

        public void sendIsTyping(Boolean istyping){
            LiveChatAPI.this.sendIsTyping(roomId,userName,istyping);
        }

        public void subscribeRoom(SubscribeListener subscribeListener,MessageListener.SubscriptionListener listener){
            LiveChatAPI.this.subscribeRoom(roomId,false,subscribeListener,listener);
        }

        public void subscribeLiveChatRoom(SubscribeListener subscribeListener,AgentListener.AgentConnectListener agentConnectListener){
            LiveChatAPI.this.subscribeLiveChatRoom(roomId,false,subscribeListener,agentConnectListener);
        }

        public void subscribeTyping(SubscribeListener subscribeListener,TypingListener listener){
            LiveChatAPI.this.subscribeTyping(roomId,false,subscribeListener,listener);
        }

        public void closeConversation(){
            LiveChatAPI.this.closeConversation(roomId);
        }



        public String getUserName() {
            return userName;
        }

        public String getRoomId() {
            return roomId;
        }

        public String getUserId() {
            return userId;
        }

        public String getVisitorToken() {
            return visitorToken;
        }

        public String getAuthToken() {
            return authToken;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"userName\":\"" + userName + '\"' +
                    ",\"roomId\":\"" + roomId + '\"' +
                    ",\"userId\":\"" + userId + '\"' +
                    ",\"visitorToken\":\"" + visitorToken + '\"' +
                    ",\"authToken\":\"" + authToken + '\"' +
                    '}';
        }
    }

    public ChatRoom createRoom(String userID,String authToken){
        String userName = null;
        if (userInfo!=null) {
            userName = userInfo.optString("username");
        }
        String visitorToken= LiveChatBasicRPC.visitorToken;
        String roomID=Utils.shortUUID();
        return new ChatRoom(userName,roomID,userID,visitorToken,authToken);
    }

    public ChatRoom createRoom(String s){
        return new ChatRoom(s);
    }
}
