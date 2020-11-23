package com.microsoft.signalr.androidchatroom.model;

import android.graphics.Bitmap;
import android.util.Log;

import com.microsoft.signalr.androidchatroom.contract.ChatContract;
import com.microsoft.signalr.androidchatroom.contract.ServerContract;
import com.microsoft.signalr.androidchatroom.model.entity.Message;
import com.microsoft.signalr.androidchatroom.model.entity.MessageFactory;
import com.microsoft.signalr.androidchatroom.presenter.ChatPresenter;
import com.microsoft.signalr.androidchatroom.service.SignalRService;
import com.microsoft.signalr.androidchatroom.util.SimpleCallback;

import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ChatModel extends BaseModel implements ChatContract.Model, ServerContract {

    private static final String TAG = "ChatModel";

    private ChatPresenter mChatPresenter = null;

    public ChatModel(ChatPresenter chatPresenter) {
        mChatPresenter = chatPresenter;
        registerServerCallbacks();
    }


    @Override
    public void sendPrivateMessage(Message privateMessage) {
        SignalRService.sendPrivateMessage(privateMessage.getMessageId(), privateMessage.getSender(), privateMessage.getReceiver(), privateMessage.getPayload(), privateMessage.isImage());
    }

    @Override
    public void sendBroadcastMessage(Message broadcastMessage) {
        SignalRService.sendBroadcastMessage(broadcastMessage.getMessageId(), broadcastMessage.getSender(), broadcastMessage.getPayload(), broadcastMessage.isImage());
    }

    @Override
    public void sendMessageRead(String messageId) {
        SignalRService.sendMessageRead(messageId);
    }

    @Override
    public void sendAck(String ackId) {
        SignalRService.sendAck(ackId);
    }

    @Override
    public void pullHistoryMessages(long untilTimeInLong) {
        SignalRService.pullHistoryMessages(untilTimeInLong);
    }

    @Override
    public void pullImageContent(String messageId) {
        SignalRService.pullImageContent(messageId);
    }

    @Override
    public void logout() {
        SignalRService.logout()
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe(new SingleObserver<String>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {

                            }

                            @Override
                            public void onSuccess(@NonNull String s) {
                                SignalRService.stopReconnectTimer();
                                SignalRService.stopHubConnection();
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                SignalRService.stopReconnectTimer();
                                SignalRService.stopHubConnection();
                            }
                        });
    }

    private void registerServerCallbacks() {
        SignalRService.registerServerCallback("receiveSystemMessage", this::receiveSystemMessage,
                String.class, String.class, Long.class);
        SignalRService.registerServerCallback("receiveBroadcastMessage", this::receiveBroadcastMessage,
                String.class, String.class, String.class, String.class, Boolean.class, Long.class, String.class);
        SignalRService.registerServerCallback("receivePrivateMessage", this::receivePrivateMessage,
                String.class, String.class, String.class, String.class, Boolean.class, Long.class, String.class);

        SignalRService.registerServerCallback("receiveHistoryMessages", this::receiveHistoryMessages, String.class);
        SignalRService.registerServerCallback("receiveImageContent", this::receiveImageContent, String.class, String.class);

        SignalRService.registerServerCallback("serverAck", this::serverAck, String.class, Long.class);
        SignalRService.registerServerCallback("clientRead", this::clientRead, String.class, String.class);
        SignalRService.registerServerCallback("expireSession", this::expireSession, Boolean.class);
    }

    @Override
    public void receiveSystemMessage(String messageId, String payload, long sendTime) {
        Log.d(TAG, "receiveSystemMessage: " + payload);

        // Create message
        Message systemMessage = MessageFactory.createReceivedSystemMessage(messageId, payload, sendTime);

        // Try to add message to fragment
        mChatPresenter.addMessage(systemMessage);
    }

    @Override
    public void receiveBroadcastMessage(String messageId, String sender, String receiver, String payload, boolean isImage, long sendTime, String ackId) {
        Log.d(TAG, "receiveBroadcastMessage from: " + sender);

        // Create message
        Message chatMessage;
        if (isImage) {
            chatMessage = MessageFactory.createReceivedImageBroadcastMessage(messageId, sender, payload, sendTime);
        } else {
            chatMessage = MessageFactory.createReceivedTextBroadcastMessage(messageId, sender, payload, sendTime);
        }

        // Try to add message to fragment
        mChatPresenter.addMessage(chatMessage, ackId);
    }

    @Override
    public void receivePrivateMessage(String messageId, String sender, String receiver, String payload, boolean isImage, long sendTime, String ackId) {
        Log.d(TAG, "receivePrivateMessage from: " + sender);

        // Create message
        Message chatMessage;
        if (isImage) {
            chatMessage = MessageFactory.createReceivedImagePrivateMessage(messageId, sender, receiver, payload, sendTime);
        } else {
            chatMessage = MessageFactory.createReceivedTextPrivateMessage(messageId, sender, receiver, payload, sendTime);
        }

        // Try to add message to fragment
        mChatPresenter.addMessage(chatMessage, ackId);
    }

    @Override
    public void receiveImageContent(String messageId, String payload) {
        Log.d(TAG, "receiveImageContent");
        Bitmap bmp = MessageFactory.decodeToBitmap(payload);
        mChatPresenter.receiveImageContent(messageId, bmp);
    }

    @Override
    public void receiveHistoryMessages(String serializedString) {
        Log.d(TAG, "receiveHistoryMessages");
        List<Message> historyMessages = MessageFactory.parseHistoryMessages(serializedString, SignalRService.getUsername());
        mChatPresenter.addAllMessages(historyMessages);
    }

    @Override
    public void serverAck(String messageId, long receivedTimeInLong) {
        mChatPresenter.receiveMessageAck(messageId, receivedTimeInLong);
    }

    @Override
    public void clientRead(String messageId, String username) {
        mChatPresenter.receiveMessageRead(messageId);
    }

    @Override
    public void expireSession(boolean isForced) {
        mChatPresenter.confirmLogout(isForced);
    }
}
