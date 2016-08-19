package com.chatsample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.chatsample.chat.data.source.ChatRepository;
import com.chatsample.chat.data.source.CreateDialogActivity;
import com.chatsample.chat.data.source.QBDataSource;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by waleed on 19/08/2016.
 */
public class UsersAndDialogs extends AppCompatActivity {

    @BindView(R.id.users)
    TextView users;
    @BindView(R.id.dialogs)
    TextView dialogs;
    @BindView(R.id.info)
    TextView info;

    private ChatRepository chatRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_and_dialog_activity);
        ButterKnife.bind(this);

        chatRepository = ChatRepository.getInstance();

        users.setMovementMethod(new ScrollingMovementMethod());
        dialogs.setMovementMethod(new ScrollingMovementMethod());


        start();

    }

    private void start() {
        chatRepository.getGroupChatDialogs(new QBDataSource.Callback<ArrayList<QBDialog>, QBResponseException>() {
            @Override
            public void onSuccess(ArrayList<QBDialog> qbDialogs) {
                final StringBuilder stringBuilder = new StringBuilder();
                for (QBDialog qbDialog : qbDialogs) {
                    stringBuilder.append(qbDialog.toString()+"\n");
                }
                dialogs.setText(stringBuilder.toString()+"\nDone");

                chatRepository.getUsersFromDialogs(qbDialogs, new QBDataSource.Callback<ArrayList<QBUser>, QBResponseException>() {
                    @Override
                    public void onSuccess(ArrayList<QBUser> qbUsers) {
                        StringBuilder stringBuilder = new StringBuilder();

                        for (QBUser qbUser : qbUsers) {
                            stringBuilder.append(qbUser.toString()+"\n");
                        }
                        users.setText(stringBuilder.toString()+"\nDone");
                    }

                    @Override
                    public void onError(QBResponseException e) {
                            users.setText(e.toString()+"\n");
                    }
                });

            }

            @Override
            public void onError(QBResponseException e) {
                dialogs.setText(e.toString()+"\n");
            }
        });
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, UsersAndDialogs.class);
        context.startActivity(intent);
    }


    public void onCreateDialog(View view){

        Intent intent = new Intent(this, CreateDialogActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK){
            dialogs.setText("");
            users.setText("");
            start();
        }else{
            info.setText("New dialog not created");
        }
    }
}
