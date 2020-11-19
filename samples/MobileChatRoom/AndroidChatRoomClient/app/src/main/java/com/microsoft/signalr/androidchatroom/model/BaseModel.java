package com.microsoft.signalr.androidchatroom.model;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.androidchatroom.presenter.BasePresenter;
import com.trello.rxlifecycle4.LifecycleProvider;
import com.trello.rxlifecycle4.android.FragmentEvent;

public abstract class BaseModel {

    protected HubConnection mHubConnection;

    public void detach() {
        mHubConnection = null;
    }

}
