package com.chatsample.chat.data.source.remote;

import android.os.Bundle;

import com.chatsample.chat.data.source.QBDataSource;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBGroupChatManager;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogCustomData;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.core.request.QBRequestUpdateBuilder;
import com.quickblox.core.request.QueryRule;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by waleed on 18/08/2016.
 */
public class QBRemoteDataSource implements QBDataSource {

    private static QBRemoteDataSource INSTANCE = null;

    private QBChatService qbChatService;

    public static final QBRemoteDataSource getInstance() {
        if (INSTANCE == null) INSTANCE = new QBRemoteDataSource();
        return INSTANCE;
    }

    private QBRemoteDataSource() {
        qbChatService = QBChatService.getInstance();
    }

    @Override
    public void createSession(final Callback<QBSession, QBResponseException> createSessionCallback) {

        QBAuth.createSession(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {
                createSessionCallback.onSuccess(qbSession);
            }

            @Override
            public void onError(QBResponseException e) {
                createSessionCallback.onError(e);
            }
        });
    }

    @Override
    public void login(QBUser qbUser, final Callback<QBUser, QBResponseException> callback) {

        QBUsers.signIn(qbUser, new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                callback.onSuccess(qbUser);
            }

            @Override
            public void onError(QBResponseException e) {
                callback.onError(e);
            }
        });
    }

    @Override
    public void signUp(QBUser qbUser, final Callback<QBUser, QBResponseException> callback) {
        QBUsers.signUp(qbUser, new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                callback.onSuccess(qbUser);
            }

            @Override
            public void onError(QBResponseException e) {
                callback.onError(e);
            }
        });

    }

    @Override
    public void chatLogin(final QBUser qbUser, final Callback<QBUser, QBResponseException> callback) {
        if (qbChatService.isLoggedIn()) {
            callback.onSuccess(qbUser);
            return;
        }

        QBAuth.createSession(qbUser, new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {
                qbUser.setId(qbSession.getUserId());
                qbChatService.login(qbUser, new QBEntityCallback() {
                    @Override
                    public void onSuccess(Object o, Bundle bundle) {
                        callback.onSuccess(qbUser);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        callback.onError(e);
                    }
                });

            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }

    @Override
    public void chatLogout(final Callback<QBUser, QBResponseException> callback) {
        qbChatService.logout(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                callback.onSuccess(null);
            }

            @Override
            public void onError(QBResponseException e) {
                callback.onError(e);
            }
        });
    }

    @Override
    public void createGroupChatDialog(String dealerName,
                                      Integer dealerId,
                                      Integer qbUserid,
                                      QBDialogCustomData qbDialogCustomData,
                                      final Callback<QBDialog, QBResponseException> callback) {

        List<Integer> ids = new ArrayList<>(2);
        ids.add(dealerId);
        ids.add(qbUserid);

        QBDialog qbDialog = new QBDialog();
        qbDialog.setName(dealerName);
        qbDialog.setType(QBDialogType.GROUP);
        qbDialog.setOccupantsIds(ids);
        qbDialog.setCustomData(qbDialogCustomData);

        QBGroupChatManager groupChatManager = QBChatService.getInstance().getGroupChatManager();
        groupChatManager.createDialog(qbDialog, new QBEntityCallback<QBDialog>() {
            @Override
            public void onSuccess(QBDialog qbDialog, Bundle bundle) {
                callback.onSuccess(qbDialog);
            }

            @Override
            public void onError(QBResponseException e) {
                callback.onError(e);
            }
        });
    }

    @Override
    public void getGroupChatDialog(String consumerSellerId,
                                   String dealerSellerId,
                                   String listingId,
                                   final Callback<QBDialog, QBResponseException> callback) {
        QBRequestGetBuilder qbRequestGetBuilder = new QBRequestGetBuilder();
        qbRequestGetBuilder.addRule("data[class_name]", QueryRule.EQ, "ChatInfo");
        qbRequestGetBuilder.addRule("data[consumerSellerId]", QueryRule.EQ, consumerSellerId);
        qbRequestGetBuilder.addRule("data[dealerSellerId]", QueryRule.EQ, dealerSellerId);
        qbRequestGetBuilder.addRule("data[listingId]", QueryRule.EQ, listingId);

        QBChatService.getChatDialogs(QBDialogType.GROUP, qbRequestGetBuilder, new QBEntityCallback<ArrayList<QBDialog>>() {
            @Override
            public void onSuccess(ArrayList<QBDialog> qbDialogs, Bundle bundle) {
                if (qbDialogs.size() == 0) {
                    callback.onSuccess(null);
                } else {
                    callback.onSuccess(qbDialogs.get(0));
                }
            }

            @Override
            public void onError(QBResponseException e) {
                callback.onError(e);
            }
        });

    }

    @Override
    public void getGroupChatDialogs(final Callback<ArrayList<QBDialog>, QBResponseException> callback) {

        QBChatService.getChatDialogs(QBDialogType.GROUP, null, new QBEntityCallback<ArrayList<QBDialog>>() {
            @Override
            public void onSuccess(ArrayList<QBDialog> qbDialogs, Bundle bundle) {
                callback.onSuccess(qbDialogs);
            }

            @Override
            public void onError(QBResponseException e) {
                callback.onError(e);
            }
        });
    }

    @Override
    public void getUsersFromDialogs(ArrayList<QBDialog> qbDialogs, final Callback<ArrayList<QBUser>, QBResponseException> callback) {

        List<Integer> userIds = new ArrayList<>();
        for (QBDialog qbDialog : qbDialogs) {
            userIds.addAll(qbDialog.getOccupants());
        }

        QBUsers.getUsersByIDs(userIds, null, new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                callback.onSuccess(qbUsers);
            }

            @Override
            public void onError(QBResponseException e) {
                callback.onError(e);
            }
        });
    }

    @Override
    public void getUserbyId(Integer id, final Callback<QBUser, QBResponseException> callback) {

        QBUsers.getUser(id, new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                callback.onSuccess(qbUser);
            }

            @Override
            public void onError(QBResponseException e) {
                callback.onError(e);
            }
        });
    }

    @Override
    public void deleteDialog(QBDialog qbDialog, final Callback<Void, QBResponseException> callback) {

        qbChatService.getGroupChatManager().deleteDialog(qbDialog.getDialogId(), true, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                callback.onSuccess(aVoid);
            }

            @Override
            public void onError(QBResponseException e) {
                callback.onError(e);
            }
        });
    }


    @Override
    public void updateDialog(QBDialog qbDialog,
                             String consumerSellerId,
                             String dealerSellerId,
                             String listingId,
                             QBDialogCustomData qbDialogCustomData, final Callback<QBDialog, QBResponseException> callback) {

        QBRequestUpdateBuilder qbRequestGetBuilder = new QBRequestUpdateBuilder();
        qbRequestGetBuilder.addRule("data[class_name]", QueryRule.EQ, "ChatInfo");
        qbRequestGetBuilder.addRule("data[consumerSellerId]", QueryRule.EQ, consumerSellerId);
        qbRequestGetBuilder.addRule("data[dealerSellerId]", QueryRule.EQ, dealerSellerId);
        qbRequestGetBuilder.addRule("data[listingId]", QueryRule.EQ, listingId);

        qbDialog.setCustomData(qbDialogCustomData);

        qbChatService.getGroupChatManager().updateDialog(qbDialog, qbRequestGetBuilder, new QBEntityCallback<QBDialog>() {
            @Override
            public void onSuccess(QBDialog qbDialog, Bundle bundle) {
                callback.onSuccess(qbDialog);
            }

            @Override
            public void onError(QBResponseException e) {
                callback.onError(e);
            }
        });

    }
}