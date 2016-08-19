package com.chatsample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chatsample.chat.data.source.ChatRepository;
import com.chatsample.chat.data.source.QBDataSource;
import com.quickblox.auth.model.QBSession;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by waleed on 18/08/2016.
 */
public class LoginSignUp extends AppCompatActivity {

    @BindView(R.id.container)
    LinearLayout container;

    @BindView(R.id.infotext)
    TextView textView;

    @BindView(R.id.username)
    EditText username;

    @BindView(R.id.password)
    EditText password;

    private ChatRepository chatRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.loginsignup);
        ButterKnife.bind(this);
        textView.setMovementMethod(new ScrollingMovementMethod());
        chatRepository = ChatRepository.getInstance();
        createSession();

    }


    public void onLoginClick(View view) {

        QBUser qbUser = new QBUser(getUsername(), getPassword());
        chatRepository.login(qbUser, new QBDataSource.Callback<QBUser, QBResponseException>() {
            @Override
            public void onSuccess(QBUser qbUser) {
                setStatus("Sign In Successful: "+qbUser.toString());
                UsersAndDialogs.start(LoginSignUp.this);
            }

            @Override
            public void onError(QBResponseException e) {
                setStatus("Sign In Successfull: "+e.toString());
            }
        });

    }

    public void onSignUpClick(View view) {

        QBUser qbUser = new QBUser(getUsername(), getPassword());
        chatRepository.signUp(qbUser, new QBDataSource.Callback<QBUser, QBResponseException>() {
            @Override
            public void onSuccess(QBUser qbUser) {
                setStatus("SignUp Successful: "+qbUser.toString());
                UsersAndDialogs.start(LoginSignUp.this);
            }

            @Override
            public void onError(QBResponseException e) {
                setStatus("SignUp Error: "+e.toString());
            }
        });
    }

    public void createSession() {


        chatRepository.createSession(new QBDataSource.Callback<QBSession, QBResponseException>() {
            @Override
            public void onSuccess(QBSession qbSession) {
                container.setVisibility(View.VISIBLE);
                setStatus("Session created successfully");

            }

            @Override
            public void onError(QBResponseException e) {
                setStatus("Error in session creation: " + e.toString());
            }
        });
    }

    private String getUsername() {
        return username.getText().toString();

    }

    private String getPassword() {
        return password.getText().toString();
    }

    private void setStatus(String s){
        String st = textView.getText().toString()+"\n";
        st+=s;
        textView.setText(st);
    }
}
