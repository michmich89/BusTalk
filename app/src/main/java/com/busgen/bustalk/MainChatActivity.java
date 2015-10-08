package com.busgen.bustalk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.busgen.bustalk.model.Client;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainChatActivity extends AppCompatActivity {
    private EditText messageInputLine;
    private ListView messageListView;
    private Button sendButton;
    private MessageAdapter messageAdapter;
    private ArrayList<TempMessage> messageHistory;

    /**The Client is gonna have all the information about chatrooms etc. and handle communication
     * with the server
     */
    private Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);
        initViews();

        //getIntent().getExtras();
        //Bundle bundle = getIntent().getExtras();
        //bundle.get
    }

    private void initViews(){
        messageInputLine = (EditText) findViewById(R.id.message_input_line);
        messageListView = (ListView) findViewById(R.id.message_list_view);
        sendButton = (Button) findViewById(R.id.send_button);

        //Initialize adapter and ListView
        messageAdapter = new MessageAdapter(MainChatActivity.this, new ArrayList<TempMessage>());
        messageListView.setAdapter(messageAdapter);

        loadDummyHistory();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageInputLine.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }
                String date = DateFormat.getDateTimeInstance().format(new Date());
                TempMessage message = new TempMessage(true, messageText, date, "Nisse Hult");

                messageInputLine.setText("");
                displayMessage(message);
            }
        });
    }

    public void displayMessage(TempMessage message) {
        messageAdapter.add(message);
        messageAdapter.notifyDataSetChanged();
    }

    //Makes it seem like there has already been messages sent when the app launches
    private void loadDummyHistory(){
        messageHistory = new ArrayList<TempMessage>();

        String messageText1 = "YO! bla bla bla bla bla bla bla bla bla bla blabla bla bla";
        String date1 = DateFormat.getDateTimeInstance().format(new Date());
        TempMessage dummyMessage1 = new TempMessage(false, messageText1, date1, "Börje Plåt");
        messageHistory.add(dummyMessage1);

        String messageText2 = "Wadup bro!";
        String date2 = DateFormat.getDateTimeInstance().format(new Date());
        TempMessage dummyMessage2 = new TempMessage(true, messageText2, date2, "Nisse Hult");
        messageHistory.add(dummyMessage2);

        for(int i=0; i<messageHistory.size(); i++){
            TempMessage message = messageHistory.get(i);
            displayMessage(message);
        }
    }
}
