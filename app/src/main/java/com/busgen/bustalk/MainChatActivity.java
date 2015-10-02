package com.busgen.bustalk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainChatActivity extends AppCompatActivity {

    private EditText messageInput;
    private ListView messagesContainer;
    private Button sendButton;
    private MessageAdapter adapter;
    private ArrayList<TempMessage> messageHistory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);
        initViews();
    }

    private void initViews(){
        messageInput = (EditText) findViewById(R.id.message_edit);
        messagesContainer = (ListView) findViewById(R.id.messages_container);
        sendButton = (Button) findViewById(R.id.send_button);

        loadDummyHistory();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageInput.getText().toString();
                if(TextUtils.isEmpty(messageText)){
                    return;
                }

                TempMessage message = new TempMessage();
                message.setId(111);
                message.setMessage(messageText);
                message.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                message.setMe(true);

                messageInput.setText("");
                displayMessage(message);
            }
        });
    }

    public void displayMessage(TempMessage message){
        adapter.add(message);
        adapter.notifyDataSetChanged();

        //Scroll
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    private void loadDummyHistory(){
        messageHistory = new ArrayList<TempMessage>();

        TempMessage dummyMessage1 = new TempMessage();
        dummyMessage1.setId(1);
        dummyMessage1.setMe(false);
        dummyMessage1.setMessage("YO!");
        dummyMessage1.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        messageHistory.add(dummyMessage1);

        TempMessage dummyMessage2 = new TempMessage();
        dummyMessage2.setId(2);
        dummyMessage2.setMe(true);
        dummyMessage2.setMessage("Wadup bro");
        dummyMessage2.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        messageHistory.add(dummyMessage2);

        adapter = new MessageAdapter(MainChatActivity.this, new ArrayList<TempMessage>());
        messagesContainer.setAdapter(adapter);

        for(int i=0; i<messageHistory.size(); i++){
            TempMessage message = messageHistory.get(i);
            displayMessage(message);
        }

    }












}
