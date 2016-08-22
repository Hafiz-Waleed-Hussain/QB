package com.chatsample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
public class UpdateDialogActivity extends AppCompatActivity {


    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.consumerSellerId)
    TextView consumerSellerId;
    @BindView(R.id.listingTitle)
    EditText listingTitle;
    @BindView(R.id.profileType)
    EditText profileType;
    @BindView(R.id.url)
    EditText url;
    @BindView(R.id.dealerSellerId)
    TextView dealerSellerId;
    @BindView(R.id.listingid)
    TextView listingId;
    @BindView(R.id.price)
    EditText price;

    @BindView(R.id.container)
    LinearLayout container;
    @BindView(R.id.info)
    TextView info;
    private QBDialog qbDialog;
    private ChatInfo chatInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_dialog_activity);

        ButterKnife.bind(this);

        qbDialog = (QBDialog) getIntent().getSerializableExtra("Dialog");
        chatInfo = new ChatInfo();
        chatInfo.createChatInfo(qbDialog.getCustomData());

        name.setText(qbDialog.getName());
        consumerSellerId.setText(chatInfo.consumerSellerId);
        listingTitle.setText(chatInfo.listingTitle);
        profileType.setText(chatInfo.profileType);
        url.setText(chatInfo.dealerImageUrl);
        dealerSellerId.setText(chatInfo.dealerSellerId);
        listingId.setText(chatInfo.listingId);
        price.setText(chatInfo.price);

        ChatRepository chatRepository = ChatRepository.getInstance();
        chatRepository.getInstance().chatLogin(chatRepository.getUser(), new QBDataSource.Callback<QBUser, QBResponseException>() {
            @Override
            public void onSuccess(QBUser qbUser) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        info.setText("Success");
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


    public void onUpdateClick(View view){

        chatInfo.price = price.getText().toString();

        ChatRepository.getInstance().updateDialog(qbDialog,
                chatInfo.consumerSellerId,
                chatInfo.dealerSellerId,
                chatInfo.listingId,
                chatInfo.createQBDialogCustomData(),
                new QBDataSource.Callback<QBDialog, QBResponseException>() {
                    @Override
                    public void onSuccess(QBDialog qbDialog) {
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        setResult(RESULT_CANCELED);
                    }
                });
    }

}
