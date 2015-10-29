package com.busgen.bustalk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.busgen.bustalk.events.Event;
import com.busgen.bustalk.events.ToActivityEvent;
import com.busgen.bustalk.events.ToServerEvent;
import com.busgen.bustalk.model.IChatroom;
import com.busgen.bustalk.model.IServerMessage;
import com.busgen.bustalk.model.ServerMessages.MsgChatMessage;
import com.busgen.bustalk.model.ServerMessages.MsgConnectionLost;
import com.busgen.bustalk.model.ServerMessages.MsgCreateRoom;
import com.busgen.bustalk.model.ServerMessages.MsgJoinRoom;
import com.busgen.bustalk.model.ServerMessages.MsgLeaveRoom;
import com.busgen.bustalk.model.ServerMessages.MsgLostChatRoom;
import com.busgen.bustalk.model.ServerMessages.MsgLostUserInChat;
import com.busgen.bustalk.model.ServerMessages.MsgNewChatRoom;
import com.busgen.bustalk.model.ServerMessages.MsgNewUserInChat;
import com.busgen.bustalk.model.ServerMessages.MsgPlatformData;
import com.busgen.bustalk.model.ServerMessages.MsgUsersInChat;
import com.busgen.bustalk.model.ServerMessages.MsgUsersInChatRequest;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainChatActivity extends BindingActivity {
    private EditText messageInputLine;
    private ListView messageListView;
    private Button sendButton;
    private MessageAdapter messageAdapter;
    private IChatroom myChatroom;
    private MenuItem usersPresent;
    private MenuItem userActivityMenuItem;
    private String userName;
    private String interest;
    private boolean backButtonPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);
        initVariables();
        initViews();
    }

    private void initVariables(){
        backButtonPressed = false;
        userName = getIntent().getStringExtra("Username");
        interest = getIntent().getStringExtra("Interest");
        myChatroom = (IChatroom) getIntent().getSerializableExtra("Chatroom");
        Log.d("MyTag", "myChatroom in mainchat activity has ID " + myChatroom.getChatID());
    }

    private void initViews(){
        messageInputLine = (EditText) findViewById(R.id.message_input_line);
        messageListView = (ListView) findViewById(R.id.message_list_view);
        sendButton = (Button) findViewById(R.id.send_button);
        messageAdapter = new MessageAdapter(MainChatActivity.this, new ArrayList<MsgChatMessage>());
        messageListView.setAdapter(messageAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageInputLine.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }
                String date = DateFormat.getDateTimeInstance().format(new Date());
                MsgChatMessage message = new MsgChatMessage(true, messageText, date, userName, myChatroom.getChatID());

                messageInputLine.setText("");
                displayMessage(message);
                myChatroom.addMessage(message);

                Event event = new ToServerEvent(message);
                eventBus.postEvent(event);
            }
        });
    }

    public void displayMessage(MsgChatMessage message) {
        messageAdapter.add(message);
        messageAdapter.notifyDataSetChanged();
    }

    public void updateRoom(int chatId){
        for (IChatroom c : client.getChatrooms()) {
            if (c.getChatID() == chatId) {
                myChatroom = c;
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        IServerMessage message = event.getMessage();
        if (event instanceof ToActivityEvent) {
            Log.d("MyTag", "MainChat got an event" );
            Log.d("MyTag", message.getClass().getName());
            if (message instanceof MsgChatMessage) {
                Log.d("MyTag", "MainChat got a message!");
                final MsgChatMessage chatMessage = (MsgChatMessage) message;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displayMessage(chatMessage);
                    }
                });
                myChatroom.addMessage(chatMessage);
            } else if (message instanceof MsgUsersInChat) {
                int chatId = ((MsgUsersInChat) message).getChatID();
                updateRoom(chatId);
                updateNumOfUsersMenuItem();
            } else if (message instanceof MsgCreateRoom) {
            } else if (message instanceof MsgJoinRoom) {
            } else if (message instanceof MsgLeaveRoom) {
            } else if (message instanceof MsgLostChatRoom) {
            } else if (message instanceof MsgLostUserInChat) {
                int chatId = ((MsgLostUserInChat) message).getChatID();
                updateRoom(chatId);
                updateNumOfUsersMenuItem();
            } else if (message instanceof MsgNewChatRoom) {
            } else if (message instanceof MsgNewUserInChat) {
                int chatId = ((MsgNewUserInChat) message).getChatID();
                updateRoom(chatId);
                updateNumOfUsersMenuItem();
            } else if (message instanceof MsgConnectionLost){
                if(!backButtonPressed){
                    connectionLostAlert();
                }
            } else if (message instanceof MsgPlatformData) {
                /*skriver ut nästa hållplats i en label*/
                if (((MsgPlatformData) message).getDataType().equals("nextStop")){

                    final MsgPlatformData nextStopMessage = (MsgPlatformData) message;
                    System.out.println("Getting the busstop event");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("Running the run");
                            String nextStop = nextStopMessage.getData();
                            setTitle(R.string.title_activity_main_chat);
                            String nextStop2 = getString(R.string.nextStop2) + " ";
                            ((TextView) findViewById(R.id.nextStopLabel)).setText(nextStop2 + nextStop);
                        }
                    });
                }
            }
        }
    }

    public void connectionLostAlert(){
        System.out.println("Alert was used.");
        //AlertDialog alertDialog = new AlertDialog.Builder(MainChatActivity.this).create();
        Runnable testRun = new Runnable() {
            @Override
            public void run() {

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainChatActivity.this);
                alertBuilder.setTitle("Connection Error");
                alertBuilder.setMessage("Your connection to the server has been lost");
                alertBuilder.setCancelable(false);
                alertBuilder.setNegativeButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(dialog != null){
                                    dialog.dismiss();
                                    dialog = null;
                                }
                                Intent intent = new Intent(MainChatActivity.this, LoginActivity.class);
                                startActivity(intent);
                                MainChatActivity.this.finish();
                            }
                        });
                alertBuilder.show();
            }
        };
        runOnUiThread(testRun);
    }

    @Override
    public void onBackPressed() {
        backButtonPressed = true;
        eventBus.postEvent(new ToServerEvent(new MsgConnectionLost()));
        Intent intent = new Intent(MainChatActivity.this, LoginActivity.class);
        startActivity(intent);
        MainChatActivity.this.finish();
    }

    private void updateNumOfUsersMenuItem(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(usersPresent!=null) {
                    String numOfUsers = Integer.toString(myChatroom.getNbrOfUsers());
                    usersPresent.setTitle(numOfUsers);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_chat, menu);
        this.usersPresent = menu.findItem(R.id.num_of_users_menu_item);

        MsgUsersInChatRequest message = new MsgUsersInChatRequest(myChatroom.getChatID());
        Event event = new ToServerEvent(message);
        eventBus.postEvent(event);

        updateNumOfUsersMenuItem();

        this.userActivityMenuItem = menu.findItem(R.id.user_activity_menu_item);
        userActivityMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(MainChatActivity.this, UserActivity.class);
                intent.putExtra("Chatroom", myChatroom);
                startActivity(intent);
                return true;
            }
        });
        return true;
    }


}
