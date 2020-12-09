package com.signalr.androidchatroom.contract;

import android.app.Activity;

import com.signalr.androidchatroom.util.SimpleCallback;

/**
 * Contract for login and start connection functions
 * Defined in MVP (Model-View-Presenter) Pattern
 */
public interface LoginContract {

    interface Presenter {
        /* Called by view */

        void signIn(SimpleCallback<Void> refreshUiCallback);
    }

    interface View {
        /* Called by model */

        /**
         * Sets a login status and navigate to chat fragment when successful.
         *
         */
        void setLogin(String username);
    }

    interface Model {
        /**
         * Send a login request to the SignalR layer.
         *
         * @param callback A callback specifying what to do after the login returns a result.
         */
        void enterChatRoom(String idToken, String username, SimpleCallback<Void> callback);
        void refreshDeviceUuid();
        void createClientApplication(SimpleCallback<Void> callback);
        void signIn(SimpleCallback<String> usernameCallback);
        void acquireIdToken(SimpleCallback<String> idTokenCallback);
    }
}
