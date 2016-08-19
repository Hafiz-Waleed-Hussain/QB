package com.chatsample;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBGroupChatManager;
import com.quickblox.chat.QBPrivateChat;
import com.quickblox.chat.QBPrivateChatManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBMessageListener;
import com.quickblox.chat.listeners.QBPrivateChatManagerListener;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogCustomData;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.core.request.QueryRule;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ChatQUICKBLOX";
    private TextView textView;

    private ProgressDialog progressDialog;

    private StringBuilder stringBuilder;

    private QBPagedRequestBuilder qbPagedRequestBuilder;

    private int page = 0;
    private QBUser qbUser;
    private QBChatService chatService;
    private ArrayList<QBDialog> qbDialogs;

    @BindView(R.id.opponentId)
    EditText opponentIdEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        stringBuilder = new StringBuilder();
        qbDialogs = new ArrayList<>();

        progressDialog = new ProgressDialog(this);
        textView = (TextView) findViewById(R.id.infotext);

        textView.setMovementMethod(new ScrollingMovementMethod());
        QBAuth.createSession(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {

                set("Session created\n" + qbSession.toString());
                setUpQBPageRequestBuilder();
            }

            @Override
            public void onError(QBResponseException e) {

                set("\nSessionError\n" + e.toString());
            }
        });


    }

    private void setUpQBPageRequestBuilder() {
        qbPagedRequestBuilder = new QBPagedRequestBuilder();

        qbPagedRequestBuilder.setPerPage(10);
    }


    public void onLogin(View view) {

        qbUser = new QBUser();
        qbUser.setLogin("test@y.com");
        qbUser.setPassword("12345678");

        QBUsers.signIn(qbUser, new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                progressDialog.cancel();
                MainActivity.this.qbUser = qbUser;
                set("\nLogin\n" + qbUser.toString());
            }

            @Override
            public void onError(QBResponseException e) {
                progressDialog.cancel();
                set("\nLoginError\n" + e.toString());

            }
        });

    }

    public void onSignUp(View view) {
        progressDialog.show();
        QBUser qbUser = new QBUser();

        qbUser.setLogin("chat@y.com");
        qbUser.setPassword("12345678");


        QBUsers.signUp(qbUser, new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {

                progressDialog.cancel();

                set("\nSignUp\n" + qbUser.toString());


            }

            @Override
            public void onError(QBResponseException e) {
                progressDialog.cancel();
                set("\nSignUpError\n" + e.toString());

            }
        });

    }


    public void onGetUsers(View view) {


        QBRequestGetBuilder qbRequestGetBuilder = new QBRequestGetBuilder();
        QBChatService.getChatDialogs(QBDialogType.GROUP,
                qbRequestGetBuilder, new QBEntityCallback<ArrayList<QBDialog>>() {
                    @Override
                    public void onSuccess(ArrayList<QBDialog> qbDialogs, Bundle bundle) {
                        set("\nTotal Chat Dialog\n" + qbDialogs.size());
                        MainActivity.this.qbDialogs = qbDialogs;
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        set("\nChatDialogError\n" + e.toString());
                    }
                });

//        qbPagedRequestBuilder.setPage(++page);
//        List<String> strings = new ArrayList<>();
//        strings.add("dealer");
//        QBUsers.getUsersByTags(strings, qbPagedRequestBuilder, new QBEntityCallback<ArrayList<QBUser>>() {
//            @Override
//            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
//
//                String s = "\nGetUsers\n";
//                for (int i = 0; i < qbUsers.size(); i++) {
//
//                    s += i + "\n" + qbUsers.get(i).toString() + "\n";
//                }
//                set(s);
//            }
//
//            @Override
//            public void onError(QBResponseException e) {
//                set("\nGetUserError\n" + e.toString());
//            }
//        });

    }


    public void onChat(View view) {


        qbUser = new QBUser();
//        qbUser.setLogin("test@y.com");
//        qbUser.setPassword("12345678");
        qbUser.setLogin("chat@y.com");
        qbUser.setPassword("12345678");


        chatService = QBChatService.getInstance();
        chatService.addConnectionListener(connectionListener);
        QBAuth.createSession(qbUser, new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {
                qbUser.setId(qbSession.getUserId());
                if (chatService.isLoggedIn()) {
                    set("\nChatAlreadyLogin\n");
                } else {
                    chatService.login(qbUser, new QBEntityCallback() {
                        @Override
                        public void onSuccess(Object o, Bundle bundle) {

                            set("\nChat Success\n");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    chatDialog();
                                }
                            });
                        }

                        @Override
                        public void onError(QBResponseException e) {
                            set("\nChatError\n" + e.toString());
                        }
                    });
                }
            }

            @Override
            public void onError(QBResponseException e) {
                set("\nChatSessionError\n" + e.toString());

            }
        });


    }


    public void onChatLogout(View view) {

        if (chatService.isLoggedIn()) {
            chatService.logout(new QBEntityCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid, Bundle bundle) {
                    set("\nChatLogout Success\n");
                }

                @Override
                public void onError(QBResponseException e) {
                    set("\nChatLogout Error\n");
                }
            });
        }
    }

    public void onSendMessage(View view) {


        final QBPrivateChatManager qbPrivateChatManager = chatService.getPrivateChatManager();
        qbPrivateChatManager.addPrivateChatManagerListener(qbPrivateChatManagerListener);


        QBChatMessage chatMessage = new QBChatMessage();
        chatMessage.setBody("Second message");
        chatMessage.setProperty("save_to_history", "1");

        QBPrivateChat qbPrivateChat = qbPrivateChatManager.getChat(16468565);
        if (qbPrivateChat == null) {
            qbPrivateChat = qbPrivateChatManager.createChat(16468565, qbPrivateChatQBMessageListener);
        }

        try {
            qbPrivateChat.sendMessage(chatMessage);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }


    }

    private void set(final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stringBuilder.append(s);
                textView.setText(stringBuilder.toString());
                Log.d(TAG, "set: " + stringBuilder.toString());
            }
        });
    }

    private ConnectionListener connectionListener = new ConnectionListener() {
        @Override
        public void connected(XMPPConnection xmppConnection) {
            set("\nconnected\n");
        }

        @Override
        public void authenticated(XMPPConnection xmppConnection, boolean b) {
            set("\nauthenticated \n");
        }

        @Override
        public void connectionClosed() {
            set("\n connectionClosed\n");

        }

        @Override
        public void connectionClosedOnError(Exception e) {
            set("\nconnectionClosedOnError \n");

        }

        @Override
        public void reconnectionSuccessful() {
            set("\nreconnectionSuccessful \n");

        }

        @Override
        public void reconnectingIn(int i) {
            set("\nreconnectingIn \n");

        }

        @Override
        public void reconnectionFailed(Exception e) {
            set("\nreconnectionFailed \n");

        }
    };

    private void chatDialog() {


        QBRequestGetBuilder qbRequestGetBuilder = new QBRequestGetBuilder();
        qbRequestGetBuilder.setLimit(100);
        QBChatService.getChatDialogs(QBDialogType.PRIVATE,
                qbRequestGetBuilder, new QBEntityCallback<ArrayList<QBDialog>>() {
                    @Override
                    public void onSuccess(ArrayList<QBDialog> qbDialogs, Bundle bundle) {
                        set("\nChatDialog\n" + qbDialogs.get(0).toString());
                        MainActivity.this.qbDialogs = qbDialogs;
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        set("\nChatDialogError\n" + e.toString());
                    }
                });
    }


    private QBMessageListener<QBPrivateChat> qbPrivateChatQBMessageListener = new QBMessageListener<QBPrivateChat>() {
        @Override
        public void processMessage(QBPrivateChat qbPrivateChat, final QBChatMessage qbChatMessage) {

            try {
                qbPrivateChat.readMessage(qbChatMessage);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        set("\nMessageReceived\n" + qbChatMessage.getBody());
                    }
                });
            } catch (XMPPException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void processError(QBPrivateChat qbPrivateChat, QBChatException e, QBChatMessage qbChatMessage) {

        }
    };


    private QBPrivateChatManagerListener qbPrivateChatManagerListener = new QBPrivateChatManagerListener() {
        @Override
        public void chatCreated(QBPrivateChat qbPrivateChat, boolean b) {
            if (!b) {
                qbPrivateChat.addMessageListener(qbPrivateChatQBMessageListener);
            }

        }
    };


    public void createGroupChat(View view) {

        QBRequestGetBuilder qbRequestGetBuilder = new QBRequestGetBuilder();
        qbRequestGetBuilder.addRule("data[class_name]", QueryRule.EQ, "ChatInfo");
        qbRequestGetBuilder.addRule("data[consumerSellerId]", QueryRule.EQ, "1");
        qbRequestGetBuilder.addRule("data[dealerSellerId]", QueryRule.EQ, "1");
        qbRequestGetBuilder.addRule("data[listingId]", QueryRule.EQ, "4");


        QBChatService.getChatDialogs(QBDialogType.GROUP, qbRequestGetBuilder, new QBEntityCallback<ArrayList<QBDialog>>() {
            @Override
            public void onSuccess(ArrayList<QBDialog> qbDialogs, Bundle bundle) {

                if (qbDialogs.size() == 0) {
                    createDialog();
                } else {

                    QBDialogCustomData qbDialogCustomData = new QBDialogCustomData("ChatInfo");
                    qbDialogCustomData.put("dealerImageUrl", "https://cdn4.iconfinder.com/data/icons/32x32-free-design-icons/32/Ok.png");
                    qbDialogCustomData.put("listingImageUrl", "https://cdn4.iconfinder.com/data/icons/32x32-free-design-icons/32/Ok.png");
                    qbDialogCustomData.put("listingTitle", "Honda 2016 Lates");
                    qbDialogCustomData.put("listingId", "4");
                    qbDialogCustomData.put("profileType", "Dealer");
                    qbDialogCustomData.put("dealerSellerId", "1");
                    qbDialogCustomData.put("consumerSellerId", "1");
                    qbDialogCustomData.put("price", "5000");


                    qbDialogs.get(0).setCustomData(qbDialogCustomData);
                    QBGroupChatManager groupChatManager = QBChatService.getInstance().getGroupChatManager();
                    groupChatManager.updateDialog(qbDialogs.get(0), null, new QBEntityCallback<QBDialog>() {
                        @Override
                        public void onSuccess(QBDialog qbDialog, Bundle bundle) {
                            set("Dialog updated: " + qbDialog.toString());
                        }

                        @Override
                        public void onError(QBResponseException e) {
                            set("Dialog updated error: " + e.toString());
                        }
                    });


                    set("Dialog Already available: " + qbDialogs.get(0).toString());
                }
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });


    }

    private void createDialog() {
        List<Integer> ids = new ArrayList<>(2);
        ids.add(Integer.parseInt(opponentIdEditText.getText().toString()));
        ids.add(qbUser.getId());


        QBDialogCustomData qbDialogCustomData = new QBDialogCustomData("ChatInfo");
        qbDialogCustomData.put("dealerImageUrl", "https://cdn4.iconfinder.com/data/icons/32x32-free-design-icons/32/Ok.png");
        qbDialogCustomData.put("listingImageUrl", "https://cdn4.iconfinder.com/data/icons/32x32-free-design-icons/32/Ok.png");
        qbDialogCustomData.put("listingTitle", "Honda 2016 Lates");
        qbDialogCustomData.put("listingId", "4");
        qbDialogCustomData.put("profileType", "Dealer");
        qbDialogCustomData.put("dealerSellerId", "1");
        qbDialogCustomData.put("consumerSellerId", "1");
        qbDialogCustomData.put("price", "1000");


        QBDialog qbDialog = new QBDialog();
        qbDialog.setName("456");
        qbDialog.setType(QBDialogType.GROUP);
        qbDialog.setOccupantsIds(ids);
        qbDialog.setCustomData(qbDialogCustomData);


        QBGroupChatManager groupChatManager = QBChatService.getInstance().getGroupChatManager();
        groupChatManager.createDialog(qbDialog, new QBEntityCallback<QBDialog>() {
            @Override
            public void onSuccess(QBDialog qbDialog, Bundle bundle) {
                set("Groupd Chat Dialog Created: " + qbDialog.toString());
            }

            @Override
            public void onError(QBResponseException e) {
                set("Groupd Chat Dialog Error: " + e.toString());

            }
        });

    }


}
