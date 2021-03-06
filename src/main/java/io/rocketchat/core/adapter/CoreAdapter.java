package io.rocketchat.core.adapter;

import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.common.data.model.UserObject;
import io.rocketchat.common.listener.ConnectListener;
import io.rocketchat.common.listener.TypingListener;
import io.rocketchat.core.callback.*;
import io.rocketchat.core.model.*;

import java.util.ArrayList;

/**
 * Created by sachin on 21/7/17.
 */
public class CoreAdapter implements ConnectListener,
        HistoryListener,
        LoginListener,
        AccountListener.getPermissionsListener,
        AccountListener.getPublicSettingsListener,
        RoomListener.GetRoomListener,
        RoomListener.RoomRolesListener,
        EmojiListener,
        SubscriptionListener.GetSubscriptionListener,
        UserListener.getUserRoleListener,
        MessageListener.MessageAckListener,
        MessageListener.SubscriptionListener,
        TypingListener{
    @Override
    public void onLoadHistory(ArrayList<RocketChatMessage> list, int unreadNotLoaded, ErrorObject error) {

    }

    @Override
    public void onLogin(TokenObject token, ErrorObject error) {

    }

    @Override
    public void onGetRooms(ArrayList<RoomObject> rooms, ErrorObject error) {

    }

    @Override
    public void onGetSubscriptions(ArrayList<SubscriptionObject> subscriptions, ErrorObject error) {

    }

    @Override
    public void onUserRoles(ArrayList<UserObject> users, ErrorObject error) {

    }

    @Override
    public void onConnect(String sessionID) {

    }

    @Override
    public void onDisconnect(boolean closedByServer) {

    }

    @Override
    public void onConnectError(Exception websocketException) {

    }

    @Override
    public void onMessageAck(RocketChatMessage message, ErrorObject error) {

    }

    @Override
    public void onMessage(String roomId, RocketChatMessage message) {

    }

    @Override
    public void onGetPermissions(ArrayList<Permission> permissions, ErrorObject error) {

    }

    @Override
    public void onGetPublicSettings(ArrayList<PublicSetting> settings, ErrorObject error) {

    }

    @Override
    public void onGetRoomRoles(ArrayList<RoomRole> roles, ErrorObject error) {

    }

    @Override
    public void onListCustomEmoji(ArrayList<Emoji> emojis, ErrorObject error) {

    }

    @Override
    public void onTyping(String roomId, String user, Boolean istyping) {

    }
}
