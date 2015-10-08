package com.busgen.bustalk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.busgen.bustalk.model.Client;
import com.busgen.bustalk.model.IUser;
import com.busgen.bustalk.model.User;

public class LoginActivity extends AppCompatActivity {
    private EditText userNameInput;
    private EditText interestInput;
    private Button loginButton;
    private Toast loginToast;
    private Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        client = Client.getInstance();
        initViews();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = userNameInput.getText().toString();
                if (TextUtils.isEmpty(userName)) {
                    loginToast.show();
                    return;
                }
                //TODO
                /**
                 * Here it should be checked with the serve (via the client) if
                 * the userName is available.
                 */
                String interest = interestInput.getText().toString();
                client.setUser(new User(userName, interest));

                Intent intent = new Intent(LoginActivity.this, MainChatActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }
        });
    }

    private void initViews() {
        userNameInput = (EditText) findViewById(R.id.user_name_input);
        interestInput = (EditText) findViewById(R.id.interest_input);
        loginButton = (Button) findViewById(R.id.login_button);
        loginToast = Toast.makeText(LoginActivity.this, "You have to choose a nickname",
                Toast.LENGTH_SHORT);
    }
}
