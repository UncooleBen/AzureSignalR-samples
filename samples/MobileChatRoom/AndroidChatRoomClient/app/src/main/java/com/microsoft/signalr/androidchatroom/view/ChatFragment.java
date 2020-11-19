package com.microsoft.signalr.androidchatroom.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.microsoft.signalr.androidchatroom.R;
import com.microsoft.signalr.androidchatroom.contract.ChatContract;
import com.microsoft.signalr.androidchatroom.presenter.ChatPresenter;
import com.microsoft.signalr.androidchatroom.view.chatrecyclerview.ChatContentAdapter;
import com.microsoft.signalr.androidchatroom.model.entity.Message;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ChatFragment extends BaseFragment implements ChatContract.View {
    private static final String TAG = "ChatFragment";
    public static final int RESULT_LOAD_IMAGE = 1;

    private ChatPresenter mChatPresenter;


    private List<Message> messages = new ArrayList<>();
    private String username;
    private String deviceUuid;

    // View elements and adapters
    private EditText chatBoxReceiverEditText;
    private EditText chatBoxMessageEditText;
    private Button chatBoxSendButton;
    private Button chatBoxImageButton;
    private RecyclerView chatContentRecyclerView;
    private ChatContentAdapter chatContentAdapter;
    private LinearLayoutManager layoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Get passed username
        if ((username = getArguments().getString("username")) == null) {
            username = "EMPTY_PLACEHOLDER";
        }

        if ((deviceUuid = getArguments().getString("deviceUuid")) == null) {
            deviceUuid = "EMPTY_PLACEHOLDER";
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // Get view element references
        this.chatBoxReceiverEditText = view.findViewById(R.id.edit_chat_receiver);
        this.chatBoxMessageEditText = view.findViewById(R.id.edit_chat_message);
        this.chatBoxSendButton = view.findViewById(R.id.button_chatbox_send);
        this.chatBoxImageButton = view.findViewById(R.id.button_chatbox_image);
        this.chatContentRecyclerView = view.findViewById(R.id.recyclerview_chatcontent);

        // Create objects
        mChatPresenter = new ChatPresenter(this, username, deviceUuid);
        this.chatContentAdapter = new ChatContentAdapter(messages, getContext(), this, mChatPresenter);
        this.layoutManager = new LinearLayoutManager(this.getActivity());

        // Configure RecyclerView
        configureRecyclerView();

        return view;
    }

    private void configureRecyclerView() {
        // Add append new messages to end (bottom)
        layoutManager.setStackFromEnd(true);

        chatContentRecyclerView.setLayoutManager(layoutManager);
        chatContentRecyclerView.setAdapter(chatContentAdapter);
    }

    @Override
    public void activateListeners() {
        chatBoxSendButton.setOnClickListener(this::sendButtonOnClickListener);
        chatBoxImageButton.setOnClickListener(this::imageButtonOnClickListener);
        chatContentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(-1)) {
                    Log.d(TAG, "OnScroll cannot scroll vertical -1");
                    mChatPresenter.pullHistoryMessages();
                }
            }
        });
    }

    @Override
    public void deactivateListeners() {
        chatBoxSendButton.setOnClickListener(null);
        chatBoxImageButton.setOnClickListener(null);
        chatContentRecyclerView.addOnScrollListener(null);
    }

    @Override
    public void setMessages(List<Message> messages, int direction) {
        this.messages = messages;

        updateRecyclerView(false, direction);
    }

    @Override
    public void setLogout(boolean isForced) {
        if (isForced) {
            requireActivity().runOnUiThread(() -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(R.string.alert_message)
                        .setTitle(R.string.alert_title)
                        .setCancelable(false);
                builder.setPositiveButton(R.string.alert_ok, (dialog, id) -> {
                    NavHostFragment.findNavController(ChatFragment.this).navigate(R.id.action_ChatFragment_to_LoginFragment);
                    requireActivity().recreate();
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            });
        } else {
            NavHostFragment.findNavController(ChatFragment.this)
                    .navigate(R.id.action_ChatFragment_to_LoginFragment);
        }
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                Uri imageUri = data.getData();
                InputStream imageStream = requireActivity().getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                mChatPresenter.sendImageMessage(username, chatBoxReceiverEditText.getText().toString(), selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Image picking failed.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getContext(), "You haven't picked Image.", Toast.LENGTH_LONG).show();
        }
    }

    private void sendButtonOnClickListener(View view) {
        if (chatBoxMessageEditText.getText().length() > 0) { // Empty message not allowed
            // Create and send message
            mChatPresenter.sendTextMessage(username, chatBoxReceiverEditText.getText().toString(), chatBoxMessageEditText.getText().toString());
            chatBoxMessageEditText.getText().clear();
        }
    }

    private void imageButtonOnClickListener(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, ChatFragment.RESULT_LOAD_IMAGE);
    }

    // TODO
    private void resendButtonOnClickListener(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, ChatFragment.RESULT_LOAD_IMAGE);
    }

    // TODO
    private void contentButtonOnClickListener(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, ChatFragment.RESULT_LOAD_IMAGE);
    }

    public void updateRecyclerView(boolean sortMessageList, int direction) {
        // Sort by send time first
        if (sortMessageList) {
            messages.sort((m1, m2) -> (int) (m1.getTime() - m2.getTime()));
        }

        chatContentAdapter.setMessages(messages);

        // Then refresh the UiThread
        requireActivity().runOnUiThread(() -> {
            chatContentAdapter.notifyDataSetChanged();
            switch (direction) {
                case 1:
                    Log.d(TAG, "Finger swipe up" + (messages.size() - 1));
                    chatContentRecyclerView.scrollToPosition(messages.size() - 1);
                    break;
                case -1:
                    Log.d(TAG, "Finger swipe down");
                    chatContentRecyclerView.scrollToPosition(0);
                    break;
                default:
            }
        });
    }
}
