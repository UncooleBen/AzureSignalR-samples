package com.microsoft.signalr.androidchatroom.util;

import com.microsoft.signalr.androidchatroom.model.entity.MessageTypeConstant;

public class MessageTypeUtils {

    public static int setType(int messageType, int type) {
        // Clear type field
        messageType = messageType & (~ MessageTypeConstant.MESSAGE_TYPE_MASK);
        // Set type field
        messageType = messageType | type;
        return messageType;
    }

    public static int setContent(int messageType, int content) {
        // Clear type field
        messageType = messageType & (~ MessageTypeConstant.MESSAGE_CONTENT_MASK);
        // Set type field
        messageType = messageType | content;
        return messageType;
    }

    public static int setStatus(int messageType, int status) {
        // Clear type field
        messageType = messageType & (~ MessageTypeConstant.MESSAGE_STATUS_MASK);
        // Set type field
        messageType = messageType | status;
        return messageType;
    }

    public static int calculateMessageType(int type, int content, int status) {
        return type | content | status;
    }
}
