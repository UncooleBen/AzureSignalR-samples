# Build SignalR-based Android Chatting App

This tutorial shows you how to build and modify a SignalR-based Android Chatting App. You'll learn how to:

> **&#x2713;** Configure Your App in Azure Active Directory.
>
> **&#x2713;** Configure Your Local Android Mobile ChatRoom App.
>
> **&#x2713;** Chat With Mobile Chat Room App.
>
> **&#x2713;** Integrate the chat room app with [ReliableChatRoom Server](../ReliableChatRoom/.)
>


## Prerequisites
* Install [.NET Core 3.0 SDK](https://dotnet.microsoft.com/download/dotnet-core/3.0) (Version >= 3.0.100)
* Install [Visual Studio 2019](https://visualstudio.microsoft.com/vs/) (Version >= 16.3)
* Install [Android Studio](https://developer.android.com/studio) (We use ver. 4.1)


## Download Source Code from Repository

1. Download or clone the Android Studio project
   
   ```cmd
   git clone https://github.com/$USERNAME/AzureSignalR-samples.git
   ```

2. Open the directory as project in Android Studio

    Open Android Studio -> Open an Existing Project

    ![open-project](./assets/0-2-1-open-project.png)

    Select the project directory -> OK

    ![open-project](./assets/0-2-2-open-project.png)

3. Let the gradle run for dependency resolving while we move on to the next section.

## Configure Your App in Azure Active Directory

1. Enter `Azure Active Directory` in `Azure Portal`

    Enter [Azure Portal](https://portal.azure.com/) and click on `Azure Active Directory`

    ![azure-portal](./assets/1-1-1-azure-portal.png)

2. Register a new app

    a. Click on `App registrations` and then `New registration`

    ![aad](./assets/1-2-1-aad.png)

    b. Enter app name and chose the 3rd option in `Supported account types` 

    ![app-registration](./assets/1-2-2-app-registration.png)

    c. Click `Register` button at the bottom

3. Copy Client ID in the text editor for later use

    You can find Client ID in the `Overview` tab of your newly registered app.

    ![client-id](./assets/1-3-1-client-id.png)

4. Add an authentication for Android clients

    a. In the `Authentication` tab of your newly registered app, click `Add a platform`.

    ![aad-authentication](./assets/1-4-1-aad-authentication.png)

    b. Enter your Android app's package name (in our case should be `com.signalr.androidchatroom`) and a self-generated signature hash string with the provided command.

    ![config-authentication](./assets/1-4-2-config-authentication.png)

    c. Copy your generated signature hash string in text editor

    d. Click `Configure` button

    e. Click `Save` button at top-left

5. Wire your AAD Android authentication information into local Android App

    a. Click `View` button in your newly added Android authentication method.

    ![view-json](./assets/1-5-1-view-json.png)

    b. Copy the JSON by clicking the button and then paste it into `AzureSignalR-samples\samples\MobileChatRoom\AndroidChatRoomClient\app\src\main\res\raw\auth_config_single_account.json`

    ![copy-json](./assets/1-5-2-copy-json.png)

    There should already be a json file there. You can either replace the fields with ones you copied in your text editor earlier or just overwrite the whole file.

    ![existed-json](./assets/1-5-3-existed-json.png)


## Configure Your Local Android Mobile ChatRoom App

1. Download and place `google-services.json`

    a. In [Firebase Console](https://console.firebase.google.com/) -> Click your project

    b. In `Settings` -> `Project Settings` -> Download `google-services.json` -> Copy it to `AzureSignalR-samples\samples\MobileChatRoom\AndroidChatRoomClient\app\google-services.json`

2. Paste your Azure Notification Hub connection string

    a. We assume you've already created an `Azure Notification Hub` when configuring the app server. If not, please read [ReliableChatRoom](../ReliableChatRoom/README.md)

    b. Copy and paste the `Connection String` and `Hub Name` in `AzureSignalR-samples\samples\MobileChatRoom\AndroidChatRoomClient\app\src\main\res\values\strings_secrets.xml`

    ![not-hub-conn-str-copy](./assets/2-2-1-not-hub-conn-str-copy.png)
    ![not-hub-conn-str-paste](./assets/2-2-2-not-hub-conn-str-paste.png)

    ![not-hub-name-copy](./assets/2-2-3-not-hub-name-copy.png)
    ![not-hub-name-paste](./assets/2-2-4-not-hub-name-paste.png)

2. Paste your Azure App Service Url

    a. We assume you've already created an `Azure App Service` when configuring the app server. If not, please read [ReliableChatRoom](../ReliableChatRoom/README.md)

    b. Copy and paste the `URL` to `AzureSignalR-samples\samples\MobileChatRoom\AndroidChatRoomClient\app\src\main\java\com\signalr\androidchatroom\service\SignalRService.java`

    ![app-service-copy](./assets/2-3-1-app-service-copy.png)
    ![app-service-paste](./assets/2-3-2-app-service-paste.png)

## Chat With Mobile Chat Room App

1. Build and run your app server
    
    You have two options:
    
    1. Run app server locally

        In `AzureSignalR-samples\samples\MobileChatRoom\AndroidChatRoomClient\app\src\main\java\com\signalr\androidchatroom\service\SignalRService.java`

        Set `serverUrl` field to `localDebugUrl`.
    
        In your chat room server project directory
        ```cmd 
        cd AzureSignalR-samples\samples\ReliableChatRoom\ReliableChatRoom
        dotnet run
        ```

    2. Publish and run app server on `Azure App Service`

        In `AzureSignalR-samples\samples\MobileChatRoom\AndroidChatRoomClient\app\src\main\java\com\signalr\androidchatroom\service\SignalRService.java`

        Set `serverUrl` field to `azureAppServiceUrl`.
        
        See [reference](../ReliableChatRoom/README.md) of *Build A SignalR-based Reliable Mobile Chat Room Server*.


2. Launch Android Chat Room Clients

    1. Create two AVD in the Android Emulator

        ![avd](./assets/3-2-1-avd.png)

    2. Run the app on multiple devices

        ![run-app](./assets/3-2-2-run-app.png)

    3. Hit `LET'S CHAT!` button

        ![lets-chat](./assets/3-2-3-lets-chat.png)

    4. Log in with Microsoft/Outlook/Live/Xbox account

        ![signin](./assets/3-2-4-signin.png)

    5. Start chatting
        ![start-chatting](./assets/3-2-5-start-chatting.png)

## How to Send Different Messages

1. Broadcast text message

    1. Leave the receiver field blank
    
    2. Type your message in message field

    3. Hit the `SEND` button

    ![broadcast-text](./assets/4-1-1-broadcast-text.png)

2. Private text message

    1. Type your receiver name in receiver field

    2. Type your message in message field

    3. Hit the `SEND` button

    ![private-text](./assets/4-2-1-private-text.png)

3. Broadcast image message

    1. Hit the `CHOOSE IMAGE` button and select your image

    2. The image you chose will be sent

    ![broadcast-image](./assets/4-3-1-broadcast-image.png)

4. Private image message

    1. Type your receiver name in receiver field

    2. Hit the `CHOOSE IMAGE` button and select your image

    3. The image you chose will be sent

    ![private-image](./assets/4-4-1-private-image.png)

## Message Receiver Side

1. Offline Message Pulling

    If you login with an existing username, all related messages will be pulled from server at the start of your client session.

    Here's an example.

    ![history-message](./assets/5-1-1-history-message.png)

2. Image Message Loading

    If you are a receiver of any image message, you will see the message first as a white square inside the bubble. For example:

    ![white-square](./assets/5-2-1-white-square.png)

    To load the content of the image, just click the white square. There will be a smaller red square inside it indicating the loading of image. For example: 

    ![red-square](./assets/5-2-2-red-square.png)

    After loading is finished, the whole content of image will show.

    ![image-loaded](./assets/5-2-3-image-loaded.png)

## Message Sender Side

1. Resend a message

    If your network status is unstable, your sent message might not be received by the server or your receiver client. To resend, just wait for the `Sending` status shown beside the bubble to turn to `Resend`, and the click the `Resend`.

    ![resend-broadcast](./assets/6-1-1-resend-broadcast.png)

2. Status of private messages

    If you have sent a private message, you'll be able to see the status of your message. Currently, the message status includes: `Sending`, `Sent`, and `Read`.

    `Sending` - Your message is making its way to the chat room app server.

    `Sent` - Your message has arrived at the chat room app server, but hasn't been read by or sent to your receiver client,

    `Read` - Your message has been read by your receiver client.

    For example:

    ![sending-private](./assets/6-2-1-sending-private.png)
    ![sent-private](./assets/6-2-2-sent-private.png)
    ![read-private](./assets/6-2-3-read-private.png)

## Client-side Interface Specification

Overview:

![overview-interface](./assets/7-1-1-overview-interface.png)

Table view:

| Interface Method Signature                                                                                                                                | Return Value | Function                                                              |
|-----------------------------------------------------------------------------------------------------------------------------------------------------------|--------------|-----------------------------------------------------------------------|
| void   receiveSystemMessage (String messageId ,  String payload , long sendTime)                                                                          | N/A          | Client receives the system message from server                        |
| void  receiveBroadcastMessage (String messageId ,  String sender ,  String receiver ,  String payload , boolean  isImage , long  sendTime , String ackId) | N/A          | Client receives the broadcast message from server                     |
| void  receivePrivateMessage (String messageId ,  String sender ,  String receiver ,  String payload , boolean  isImage , long  sendTime , String ackId)   | N/A          | Client receives the private message from server                       |
| void  receiveImageContent (String messageId , String payload)                                                                                             | N/A          | Client receives the image content from server                         |
| void  receiveHistoryMessages(String serializedString)                                                                                                     | N/A          | Client receives the history messages from server                      |
| void  serverAck (String messageId , long receivedTimeInLong)                                                                                              | N/A          | Client receives the server ack from server                            |
| void  clientRead (String messageId , String username)                                                                                                     | N/A          | Client receives the read status update from the receiver (via server) |
| void  expireSession ( boolean showAlert)                                                                                                                  | N/A          | Client receives the force expire session message from server.         |