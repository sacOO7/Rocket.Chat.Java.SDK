package com.rocketchat.core.db;

import com.rocketchat.common.data.lightdb.collection.Collection;
import com.rocketchat.common.data.rpc.RPC;
import com.rocketchat.core.db.Document.FileDocument;
import com.rocketchat.core.db.Document.MessageDocument;
import java.util.Observable;
import org.json.JSONObject;

/**
 * Created by sachin on 16/9/17.
 */
public class RoomDbManager extends Observable {
    private Collection<String, FileDocument> roomFilesCollection;
    private Collection<String, MessageDocument> mentionedMessagesCollection;
    private Collection<String, MessageDocument> starredMessagesCollection;
    private Collection<String, MessageDocument> pinnedMessagesCollection;
    private Collection<String, MessageDocument> snipetedMessagesCollection;

    private static final String COLLECTION_TYPE_FILES = "room_files";
    private static final String COLLECTION_TYPE_MENTIONED_MESSAGES = "rocketchat_mentioned_message";
    private static final String COLLECTION_TYPE_STARRED_MESSAGES = "rocketchat_starred_message";
    private static final String COLLECTION_TYPE_PINNED_MESSAGES = "rocketchat_pinned_message";
    private static final String COLLECTION_TYPE_SNIPETED_MESSAGES = "rocketchat_snippeted_message";

    public RoomDbManager() {
        roomFilesCollection = new Collection<>();
        mentionedMessagesCollection = new Collection<>();
        starredMessagesCollection = new Collection<>();
        pinnedMessagesCollection = new Collection<>();
        snipetedMessagesCollection = new Collection<>();
    }

    public Collection<String, FileDocument> getRoomFilesCollection() {
        return roomFilesCollection;
    }

    public Collection<String, MessageDocument> getMentionedMessagesCollection() {
        return mentionedMessagesCollection;
    }

    public Collection<String, MessageDocument> getStarredMessagesCollection() {
        return starredMessagesCollection;
    }

    public Collection<String, MessageDocument> getPinnedMessagesCollection() {
        return pinnedMessagesCollection;
    }

    public Collection<String, MessageDocument> getSnipetedMessagesCollection() {
        return snipetedMessagesCollection;
    }

    public void update(JSONObject object, RPC.MsgType type) {
        String collectionName = object.optString("collection");
        switch (collectionName) {
            case COLLECTION_TYPE_FILES:
                updateRoomFiles(object, type);
                break;
            case COLLECTION_TYPE_MENTIONED_MESSAGES:
                updateMentionedMessages(object, type);
                break;
            case COLLECTION_TYPE_PINNED_MESSAGES:
                updatePinnedMessages(object, type);
                break;
            case COLLECTION_TYPE_STARRED_MESSAGES:
                updateStarredMessages(object, type);
                break;
            case COLLECTION_TYPE_SNIPETED_MESSAGES:
                updateSnipettedMessages(object, type);
                break;
        }
    }

    public void updateRoomFiles(JSONObject object, RPC.MsgType type) {
        System.out.println("Got into update room files");
    }

    public void updateMentionedMessages(JSONObject object, RPC.MsgType type) {
        System.out.println("Got into mentioned messages");
    }

    public void updateStarredMessages(JSONObject object, RPC.MsgType type) {
        System.out.println("Got into starred messages");
    }

    public void updatePinnedMessages(JSONObject object, RPC.MsgType type) {
        System.out.println("Got into pinned messages");
    }

    public void updateSnipettedMessages(JSONObject object, RPC.MsgType type) {
        System.out.println("Got into snipetted messages");
    }

    public enum Type {
        STREAM_COLLECTION,
        LOCAL_COLLECTION
    }

    public static Type getCollectionType(JSONObject object) {
        String collectionName = object.optString("collection");
        if (collectionName.equals(COLLECTION_TYPE_FILES) ||
                collectionName.equals(COLLECTION_TYPE_MENTIONED_MESSAGES) ||
                collectionName.equals(COLLECTION_TYPE_STARRED_MESSAGES) ||
                collectionName.equals(COLLECTION_TYPE_PINNED_MESSAGES) ||
                collectionName.equals(COLLECTION_TYPE_SNIPETED_MESSAGES)) {
            return Type.LOCAL_COLLECTION;
        } else {
            return Type.STREAM_COLLECTION;
        }
    }
}
