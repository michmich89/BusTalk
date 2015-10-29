package com.busgen.bustalk;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.busgen.bustalk.events.Event;
import com.busgen.bustalk.events.ToActivityEvent;
import com.busgen.bustalk.events.ToServerEvent;
import com.busgen.bustalk.model.IChatroom;
import com.busgen.bustalk.model.IServerMessage;
import com.busgen.bustalk.model.IUser;
import com.busgen.bustalk.model.ServerMessages.MsgAvailableRooms;
import com.busgen.bustalk.model.ServerMessages.MsgAvailableRoomsRequest;
import com.busgen.bustalk.model.ServerMessages.MsgChooseNickname;
import com.busgen.bustalk.model.ServerMessages.MsgConnectToServer;
import com.busgen.bustalk.model.ServerMessages.MsgConnectionLost;
import com.busgen.bustalk.model.ServerMessages.MsgConnectionStatus;
import com.busgen.bustalk.model.ServerMessages.MsgJoinRoom;
import com.busgen.bustalk.model.ServerMessages.MsgNewUserInChat;
import com.busgen.bustalk.model.ServerMessages.MsgNicknameAvailable;
import com.busgen.bustalk.model.ServerMessages.MsgSetGroupId;
import com.busgen.bustalk.model.User;
import com.busgen.bustalk.service.EventBus;
import com.busgen.bustalk.service.MainService;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends BindingActivity {
    private EditText userNameInput;
    private EditText interestInput;
    private Button loginButton;
    private Toast loginToast;
    private Toast noConnectionToast;
    private Toast nameUnavailableToast;
    private String interest;
    private String userName;
    private ProgressDialog progress;
    private boolean hasLoggedIn;
    private boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        startService(new Intent(this, MainService.class));
        hasLoggedIn = false;
        initViews();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //userName = userNameInput.getText().toString();
                if (TextUtils.isEmpty(userNameInput.getText().toString())) {
                    loginToast.show();
                }else{
                    progress = new ProgressDialog(LoginActivity.this);
                    progress.setTitle("Loading");
                    progress.setMessage("Please wait while loading...");
                        /*progress.setButton("Cancel", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                // Use either finish() or return() to either close the activity or just the dialog
                                return;
                            }
                        });*/
                    progress.setCancelable(false);
                    progress.show();
                    eventBus.postEvent(new ToServerEvent(new MsgConnectToServer()));
                }
            }
        });
    }

    private void initViews() {
        userNameInput = (EditText) findViewById(R.id.user_name_input);
        interestInput = (EditText) findViewById(R.id.interest_input);
        loginButton = (Button) findViewById(R.id.login_button);
        loginToast = Toast.makeText(LoginActivity.this, "You have to choose a nickname",
                Toast.LENGTH_SHORT);
        noConnectionToast = Toast.makeText(LoginActivity.this, "Connection to server failed", Toast.LENGTH_SHORT);
        nameUnavailableToast = Toast.makeText(LoginActivity.this, "Name was already taken", Toast.LENGTH_SHORT);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof ToActivityEvent && !hasLoggedIn) {
            IServerMessage message = event.getMessage();
            Log.d("MyTag", "LoginActivity receiving some sort of event, namely");
            Log.d("MyTag", message.getClass().getName());

            if (message instanceof MsgConnectionStatus) {
                MsgConnectionStatus connectionMessage = (MsgConnectionStatus) message;
                System.out.println("Got message status: " + connectionMessage.isConnected());
                if (!connectionMessage.isConnected()) {
                    System.out.println("Wasn't connected");
                    isConnected = false;
                    progress.dismiss();
                    noConnectionToast.show();
                } else {
                    System.out.println("Connected to server! Set username and interests");
                    userName = userNameInput.getText().toString();
                    if (TextUtils.isEmpty(userName)) {  //Onödigt? kollas redan i onClickListener
                        loginToast.show();
                        isConnected = false;
                    }
                    interest = interestInput.getText().toString();
                    IServerMessage serverMessage = new MsgChooseNickname(userName, interest);
                    Event nameEvent = new ToServerEvent(serverMessage);
                    isConnected = true;
                    eventBus.postEvent(nameEvent);
                }
            } else if (message instanceof MsgNicknameAvailable) {
                MsgNicknameAvailable nicknameAvailableMessage = (MsgNicknameAvailable) message;
                Log.d("MyTag", "is nickname is available?...");
                if (nicknameAvailableMessage.getAvailability()) {
                    Log.d("MyTag", "nickname is available, setting user info...");
                    client.setUserName(userName);
                    client.setInterest(interest);

                    IServerMessage serverMessage = new MsgSetGroupId(client.getGroupId());
                    Event setGroupIdEvent = new ToServerEvent(serverMessage);
                    eventBus.postEvent(setGroupIdEvent);

                    serverMessage = new MsgAvailableRoomsRequest();
                    Event requestEvent = new ToServerEvent(serverMessage);
                    eventBus.postEvent(requestEvent);
                } else {
                    Log.d("MyTag", "nickname is NOT available");
                    progress.dismiss();
                    nameUnavailableToast.show();
                    userNameInput.setText("");
                    interestInput.setText("");

                }
            } else if (message instanceof MsgAvailableRooms) {
                MsgAvailableRooms availableRoomsMesssage = (MsgAvailableRooms) message;
                //List<IChatroom> chatrooms = availableRoomsMesssage.getRoomList();
                //IChatroom myChatroom = chatrooms.get(0);
                List<IChatroom> availableChatrooms = availableRoomsMesssage.getRoomList();
                IChatroom tempChatroom = availableChatrooms.get(0);

                Log.d("MyTag", "JOINING CHATROOM " + tempChatroom.getChatID());
                IServerMessage joinMessage = new MsgJoinRoom(tempChatroom);
                Event joinEvent = new ToServerEvent(joinMessage);
                eventBus.postEvent(joinEvent);

//                Intent intent = new Intent(LoginActivity.this, MainChatActivity.class);
//
//                intent.putExtra("Chatroom", client.getChatrooms().get(0));
//                intent.putExtra("Username", client.getUserName());
//                intent.putExtra("Interest", client.getInterest());
//                startActivity(intent);
//                LoginActivity.this.finish();
            } else if (message instanceof MsgNewUserInChat) {
                MsgNewUserInChat msgNewUserInChat = ((MsgNewUserInChat) message);
                IUser newUser = msgNewUserInChat.getUser();
                IChatroom chatroom = client.getChatrooms().get(0);

                Log.d("MyTag", "newUser = " + newUser);
                Log.d("MyTag", "thisUser = " + client.getUser());
                if (newUser.equals(client.getUser())){
                    MsgJoinRoom msgJoinRoom = new MsgJoinRoom(chatroom);
                    Event joinEvent = new ToActivityEvent(msgJoinRoom);
                    eventBus.postEvent(joinEvent);
                }

//                /** special case since it's the login activity*/
//                //todo koll på om det är vi??? wut
//                 Log.d("MyTag", "Telling Login Activity to join the first room");
//                 Intent intent = new Intent(LoginActivity.this, MainChatActivity.class);
//                 Log.d("MyTag", "number of chatrooms in Client: " + client.getChatrooms().size());
//
//                 Log.d("MyTag", "intents put in: " + "chatroom: " + client.getChatrooms().get(0).getChatID());
//                 Log.d("MyTag", "intents put in: " + "Username: " + client.getUserName());
//                 Log.d("MyTag", "intents put in: " + "Interest: " + client.getInterest());
//
//                 intent.putExtra("Chatroom", client.getChatrooms().get(0));
//                 intent.putExtra("Username", client.getUserName());
//                 intent.putExtra("Interest", client.getInterest());
//
//                 startActivity(intent);
//                 hasLoggedIn = true;
//                 LoginActivity.this.finish();
            } else if (message instanceof MsgJoinRoom){

                MsgJoinRoom msgJoinRoom = (MsgJoinRoom) message;
                IChatroom chatroom = msgJoinRoom.getChatroom();
                String userName = client.getUserName();
                String interest = client.getInterest();

                Log.d("MyTag", "Telling Login Activity to join the first room");
                Intent intent = new Intent(LoginActivity.this, MainChatActivity.class);
                Log.d("MyTag", "number of chatrooms in Client: " + client.getChatrooms().size());

                Log.d("MyTag", "intents put in: " + "chatroom: " + client.getChatrooms().get(0).getChatID());
                Log.d("MyTag", "intents put in: " + "Username: " + client.getUserName());
                Log.d("MyTag", "intents put in: " + "Interest: " + client.getInterest());

                intent.putExtra("Chatroom", chatroom);
                intent.putExtra("Username", userName);
                intent.putExtra("Interest", interest);

                startActivity(intent);
                hasLoggedIn = true;
                LoginActivity.this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
    }
}
