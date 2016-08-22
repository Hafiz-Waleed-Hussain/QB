package com.chatsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chatsample.R;
import com.chatsample.chat.data.source.ChatInfo;
import com.chatsample.chat.data.source.ChatRepository;
import com.chatsample.chat.data.source.QBDataSource;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by waleed on 19/08/2016.
 */
public class CreateDialogActivity extends AppCompatActivity {

    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.consumerSellerId)
    EditText consumerSellerId;
    @BindView(R.id.listingTitle)
    EditText listingTitle;
    @BindView(R.id.profileType)
    EditText profileType;
    @BindView(R.id.url)
    EditText url;
    @BindView(R.id.dealerSellerId)
    EditText dealerSellerId;
    @BindView(R.id.listingid)
    EditText listingId;
    @BindView(R.id.price)
    EditText price;

    @BindView(R.id.container)
    LinearLayout container;
    @BindView(R.id.info)
    TextView info;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_dialog_activity);
        ButterKnife.bind(this);
        info.setMovementMethod(new ScrollingMovementMethod());

        ChatRepository chatRepository = ChatRepository.getInstance();

        chatRepository.chatLogin(chatRepository.getUser(), new QBDataSource.Callback<QBUser, QBResponseException>() {
            @Override
            public void onSuccess(QBUser qbUser) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        info.setText("Sucess");
                        container.setVisibility(View.VISIBLE);

                    }
                });
            }

            @Override
            public void onError(final QBResponseException e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        info.setText(e.toString());
                    }
                });
            }
        });

    }


    public void onCreate(View view) {


        final ChatInfo chatInfo = new ChatInfo();
        chatInfo.consumerSellerId = consumerSellerId.getText().toString();
        chatInfo.dealerImageUrl = url.getText().toString();
        chatInfo.listingImageUrl = url.getText().toString();
        chatInfo.dealerSellerId = dealerSellerId.getText().toString();
        chatInfo.listingId = listingId.getText().toString();
        chatInfo.price = price.getText().toString();
        chatInfo.profileType = profileType.getText().toString();
        chatInfo.listingTitle = listingTitle.getText().toString();


        ChatRepository.getInstance().getGroupChatDialog(chatInfo.consumerSellerId,
                chatInfo.dealerSellerId,
                chatInfo.listingId, new QBDataSource.Callback<QBDialog, QBResponseException>() {
                    @Override
                    public void onSuccess(QBDialog qbDialog) {
                        if (qbDialog == null) {
                            ChatRepository.getInstance().createGroupChatDialog(
                                    name.getText().toString(),
                                    16567158,
                                    ChatRepository.getInstance().getUser().getId(),
                                    chatInfo.createQBDialogCustomData()
                                    , new QBDataSource.Callback<QBDialog, QBResponseException>() {
                                        @Override
                                        public void onSuccess(QBDialog qbDialog) {
                                            setResult(RESULT_OK);
                                            finish();
                                        }

                                        @Override
                                        public void onError(QBResponseException e) {
                                            setResult(RESULT_CANCELED);
                                            finish();

                                        }
                                    }

                            );
                        } else
                            info.setText("Already: " + qbDialog.toString());


                    }

                    @Override
                    public void onError(final QBResponseException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                info.setText(e.toString());
                            }
                        });


                    }
                });

    }
}
