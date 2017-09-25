package com.rocketchat.common.listener;

import com.rocketchat.common.RocketChatException;

import java.lang.reflect.Type;

public abstract class Callback {
    /**
     * Called when the request could not be executed due to cancellation, a connectivity problem,
     * timeout, parsing errors, authentication error, etc. Because networks can fail during an
     * exchange, it is possible that the remote server accepted the request before the failure.
     */
    public abstract void onError(RocketChatException error);

    public abstract Type getClassType();
}
