package com.microsoft.signalr.androidchatroom.view.chatrecyclerview;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.microsoft.signalr.androidchatroom.R;
import com.microsoft.signalr.androidchatroom.presenter.ChatPresenter;
import com.microsoft.signalr.androidchatroom.util.MessageTypeUtils;
import com.microsoft.signalr.androidchatroom.view.ChatFragment;
import com.microsoft.signalr.androidchatroom.model.entity.Message;
import com.microsoft.signalr.androidchatroom.model.entity.MessageTypeConstant;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatContentAdapter extends RecyclerView.Adapter<ChatContentViewHolder> {

    private static final String TAG = "ChatContentAdapter";

    private static final int READ_IMAGE_PRIVATE_MESSAGE_VIEW = MessageTypeConstant.READ | MessageTypeConstant.IMAGE | MessageTypeConstant.PRIVATE;
    private static final int READ_TEXT_PRIVATE_MESSAGE_VIEW = MessageTypeConstant.READ | MessageTypeConstant.TEXT | MessageTypeConstant.PRIVATE;
    private static final int RECEIVED_IMAGE_BROADCAST_MESSAGE_VIEW = MessageTypeConstant.RECEIVED | MessageTypeConstant.IMAGE | MessageTypeConstant.BROADCAST;
    private static final int RECEIVED_IMAGE_PRIVATE_MESSAGE_VIEW = MessageTypeConstant.RECEIVED | MessageTypeConstant.IMAGE | MessageTypeConstant.PRIVATE;
    private static final int RECEIVED_TEXT_BROADCAST_MESSAGE_VIEW = MessageTypeConstant.RECEIVED | MessageTypeConstant.TEXT | MessageTypeConstant.BROADCAST;
    private static final int RECEIVED_TEXT_PRIVATE_MESSAGE_VIEW = MessageTypeConstant.RECEIVED | MessageTypeConstant.TEXT | MessageTypeConstant.PRIVATE;
    private static final int SYSTEM_MESSAGE_VIEW = MessageTypeConstant.RECEIVED | MessageTypeConstant.TEXT | MessageTypeConstant.SYSTEM;
    private static final int SENDING_IMAGE_BROADCAST_MESSAGE_VIEW = MessageTypeConstant.SENDING | MessageTypeConstant.IMAGE | MessageTypeConstant.BROADCAST;
    private static final int SENDING_IMAGE_PRIVATE_MESSAGE_VIEW = MessageTypeConstant.SENDING | MessageTypeConstant.IMAGE | MessageTypeConstant.PRIVATE;
    private static final int SENDING_TEXT_BROADCAST_MESSAGE_VIEW = MessageTypeConstant.SENDING | MessageTypeConstant.TEXT | MessageTypeConstant.BROADCAST;
    private static final int SENDING_TEXT_PRIVATE_MESSAGE_VIEW = MessageTypeConstant.SENDING | MessageTypeConstant.TEXT | MessageTypeConstant.PRIVATE;
    private static final int SENT_IMAGE_BROADCAST_MESSAGE_VIEW = MessageTypeConstant.SENT | MessageTypeConstant.IMAGE | MessageTypeConstant.BROADCAST;
    private static final int SENT_IMAGE_PRIVATE_MESSAGE_VIEW = MessageTypeConstant.SENT | MessageTypeConstant.IMAGE | MessageTypeConstant.PRIVATE;
    private static final int SENT_TEXT_BROADCAST_MESSAGE_VIEW = MessageTypeConstant.SENT | MessageTypeConstant.TEXT | MessageTypeConstant.BROADCAST;
    private static final int SENT_TEXT_PRIVATE_MESSAGE_VIEW = MessageTypeConstant.SENT | MessageTypeConstant.TEXT | MessageTypeConstant.PRIVATE;

    private final ChatFragment mChatFragment;
    private final ChatPresenter mChatPresenter;


    private final Context context;
    private List<Message> messages;


    // Used for datetime formatting
    private final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd hh:mm:ss", Locale.US);

    public ChatContentAdapter(List<Message> messages, Context context, ChatFragment chatFragment, ChatPresenter chatPresenter) {
        this.messages = messages;
        this.context = context;
        mChatFragment = chatFragment;
        mChatPresenter = chatPresenter;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public ChatContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        switch (viewType) {
            case SENDING_TEXT_BROADCAST_MESSAGE_VIEW:
            case SENT_TEXT_BROADCAST_MESSAGE_VIEW:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_self_text_broadcast_message, parent, false);
                break;
            case SENDING_TEXT_PRIVATE_MESSAGE_VIEW:
            case SENT_TEXT_PRIVATE_MESSAGE_VIEW:
            case READ_TEXT_PRIVATE_MESSAGE_VIEW:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_self_text_private_message, parent, false);
                break;
            case SENDING_IMAGE_BROADCAST_MESSAGE_VIEW:
            case SENT_IMAGE_BROADCAST_MESSAGE_VIEW:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_self_image_broadcast_message, parent, false);
                break;
            case SENDING_IMAGE_PRIVATE_MESSAGE_VIEW:
            case SENT_IMAGE_PRIVATE_MESSAGE_VIEW:
            case READ_IMAGE_PRIVATE_MESSAGE_VIEW:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_self_image_private_message, parent, false);
                break;
            case RECEIVED_TEXT_BROADCAST_MESSAGE_VIEW:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_received_text_broadcast_message, parent, false);
                break;
            case RECEIVED_TEXT_PRIVATE_MESSAGE_VIEW:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_received_text_private_message, parent, false);
                break;
            case RECEIVED_IMAGE_BROADCAST_MESSAGE_VIEW:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_received_image_broadcast_message, parent, false);
                break;
            case RECEIVED_IMAGE_PRIVATE_MESSAGE_VIEW:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_received_image_private_message, parent, false);
                break;
            case SYSTEM_MESSAGE_VIEW:
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_system_message, parent, false);
                break;
        }

        return new ChatContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatContentViewHolder viewHolder, int position) {
        Message message = messages.get(position);

        // System message directly set content and return
        if ((message.getMessageType() & MessageTypeConstant.MESSAGE_TYPE_MASK) == MessageTypeConstant.SYSTEM) {
            viewHolder.systemMessageContent.setText(message.getPayload());
            return;
        }

        // General chat message components
        Log.d(TAG, message.getPayload());
        viewHolder.messageSender.setText(message.getSender());
        viewHolder.messageTime.setText(sdf.format(new Date(message.getTime())));

        // Different types of message content
        if ((message.getMessageType() & MessageTypeConstant.MESSAGE_CONTENT_MASK) == MessageTypeConstant.TEXT) {
            // Normal text content
            viewHolder.messageTextContent.setText(message.getPayload());
        } else if (((message.getMessageType() & MessageTypeConstant.MESSAGE_CONTENT_MASK) == MessageTypeConstant.IMAGE) && message.getBmp() != null) {
            // Loaded image content
            Bitmap bmp = message.getBmp();
            int[] resized = resizeImage(bmp.getWidth(), bmp.getHeight(), 400);
            viewHolder.messageImageContent.setImageBitmap(Bitmap.createScaledBitmap(bmp, resized[0], resized[1], false));
        } else if (((message.getMessageType() & MessageTypeConstant.MESSAGE_CONTENT_MASK) == MessageTypeConstant.TEXT) && message.getBmp() == null) {
            viewHolder.messageImageContent.setImageResource(R.drawable.ic_ready_to_pull);
            // Image content need to load
            if (message.getBmp() == null) {
                viewHolder.bindImageClick(message, v -> {
                    if (message.getBmp() == null) {
                        Log.d(TAG, "Click on image");
                        viewHolder.messageImageContent.setImageResource(R.drawable.ic_pulling);
                        mChatPresenter.pullImageContent(message.getMessageId());
                    }
                });
            }
        }

        // Different types of message status
        switch (message.getMessageType() & MessageTypeConstant.MESSAGE_STATUS_MASK) {
            case MessageTypeConstant.SENDING:
                viewHolder.statusTextView.setText(R.string.message_sending);
                viewHolder.statusTextView.setOnClickListener(null);
                break;
            case MessageTypeConstant.TIMEOUT:
                viewHolder.statusTextView.setText(R.string.message_resend);
                viewHolder.bindStatusClick(message, v -> {
                    if ((message.getMessageType() & MessageTypeConstant.MESSAGE_STATUS_MASK) == MessageTypeConstant.TIMEOUT) {
                        viewHolder.statusTextView.setText(R.string.message_sending);
                        mChatPresenter.resendMessage(message.getMessageId());
                    }
                });
                break;
            case MessageTypeConstant.SENT:
                if ((message.getMessageType() & MessageTypeConstant.MESSAGE_TYPE_MASK) == MessageTypeConstant.PRIVATE) {
                    Log.d("unread sent private message:", message.getPayload());
                    viewHolder.statusTextView.setText(R.string.message_sent);
                } else if ((message.getMessageType() & MessageTypeConstant.MESSAGE_TYPE_MASK) == MessageTypeConstant.BROADCAST) {
                    viewHolder.statusTextView.setText("");
                }
                break;
            case MessageTypeConstant.READ:
                if (true) {//TODO
                    viewHolder.statusTextView.setText(R.string.message_read);
                } else {
                    viewHolder.statusTextView.setText(R.string.message_empty);
                }
                break;
            default:
        }
    }

    @Override
    public int getItemCount() {
        return this.messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getMessageType();
    }

    private int[] resizeImage(int width, int height, int maxValue) {
        if (width > height && width > maxValue) {
            height = height * maxValue / width;
            width = maxValue;
        } else if (width < height && height > maxValue) {
            width = width * maxValue / height;
            height = maxValue;
        }
        return new int[]{width, height};
    }
}
