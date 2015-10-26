package com.busgen.bustalk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
    private String userName;
    private String interest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);
        initVariables();
        initViews();
        //eventBus.register(this);
    }

    private void initVariables(){
        userName = getIntent().getStringExtra("Username");
        interest = getIntent().getStringExtra("Interest");
        myChatroom = (IChatroom) getIntent().getSerializableExtra("Chatroom");
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
        //messageListView.setAdapter(messageAdapter);
        //messageListView.invalidateViews();
        //messageListView.setSelection(messageListView.getCount() - 1);
    }

    @Override
    public void onEvent(Event event) {
        IServerMessage message = event.getMessage();
        if (event instanceof ToActivityEvent) {
            if (message instanceof MsgChatMessage) {
                final MsgChatMessage chatMessage = (MsgChatMessage) message;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displayMessage(chatMessage);
                    }
                });
                myChatroom.addMessage(chatMessage);
            } else if (message instanceof MsgCreateRoom) {
            } else if (message instanceof MsgJoinRoom) {
            } else if (message instanceof MsgLeaveRoom) {
            } else if (message instanceof MsgLostChatRoom) {
            } else if (message instanceof MsgLostUserInChat) {
            } else if (message instanceof MsgNewChatRoom) {
            } else if (message instanceof MsgNewUserInChat) {
            } else if (message instanceof MsgConnectionLost){
                connectionLostAlert();
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
                            setTitle("Buss 55");
                            ((TextView) findViewById(R.id.nextStopLabel)).setText("Nästa hållplats: " + nextStop);
                        }
                    });
                }
            }
        }
    }

    public void connectionLostAlert(){
        AlertDialog alertDialog = new AlertDialog.Builder(MainChatActivity.this).create();
        alertDialog.setTitle("Connection Error");
        alertDialog.setMessage("Your connection to the server has been lost");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(MainChatActivity.this, LoginActivity.class);
                        startActivity(intent);
                        MainChatActivity.this.finish();
                    }
                });
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_chat, menu);
        this.usersPresent = menu.findItem(R.id.action_users);
        usersPresent.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
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
