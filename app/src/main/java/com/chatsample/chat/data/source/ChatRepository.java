package com.chatsample.chat.data.source;

import com.chatsample.chat.data.source.remote.QBRemoteDataSource;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogCustomData;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

/**
 * Created by waleed on 18/08/2016.
 */
public class ChatRepository implements QBDataSource {

    private static ChatRepository INSTANCE = null;
    private QBDataSource remoteDataSource;
    private ChatCached chatCached;

    public static final ChatRepository getInstance() {

        if (INSTANCE == null) INSTANCE = new ChatRepository();
        return INSTANCE;
    }

    private ChatRepository() {
        remoteDataSource = QBRemoteDataSource.getInstance();
        chatCached = new ChatCached();
    }

    @Override
    public void createSession(Callback<QBSession, QBResponseException> createSessionCallback) {
        remoteDataSource.createSession(createSessionCallback);
    }

    @Override
    public void login(final QBUser qb, final Callback<QBUser, QBResponseException> callback) {

        remoteDataSource.login(qb, new Callback<QBUser, QBResponseException>() {
            @Override
            public void onSuccess(QBUser qbUser) {
                qbUser.setPassword(qb.getPassword());
                chatCached.setSignInUser(qbUser);
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
        remoteDataSource.signUp(qbUser, new Callback<QBUser, QBResponseException>() {
            @Override
            public void onSuccess(QBUser qbUser) {
                chatCached.setSignInUser(qbUser);
            }

            @Override
            public void onError(QBResponseException e) {
                callback.onError(e);
            }
        });
    }

    @Override
    public void chatLogin(QBUser qbUser, Callback<QBUser, QBResponseException> callback) {
        remoteDataSource.chatLogin(qbUser, callback);
    }

    @Override
    public void chatLogout(Callback<QBUser, QBResponseException> callback) {
        remoteDataSource.chatLogout(callback);
    }

    @Override
    public void createGroupChatDialog(String dealerUsername, Integer dealerId, Integer qbUserid, QBDialogCustomData qbDialogCustomData, Callback<QBDialog, QBResponseException> callback) {
        remoteDataSource.createGroupChatDialog(dealerUsername, dealerId, qbUserid, qbDialogCustomData, callback);
    }

    @Override
    public void getGroupChatDialog(String consumerSellerId, String dealerSellerId, String listingId, Callback<QBDialog, QBResponseException> callback) {
        remoteDataSource.getGroupChatDialog(consumerSellerId, dealerSellerId, listingId, callback);
    }

    @Override
    public void getGroupChatDialogs(Callback<ArrayList<QBDialog>, QBResponseException> callback) {
        remoteDataSource.getGroupChatDialogs(callback);
    }

    @Override
    public void getUsersFromDialogs(ArrayList<QBDialog> qbDialogs, Callback<ArrayList<QBUser>, QBResponseException> callback) {
        remoteDataSource.getUsersFromDialogs(qbDialogs, callback);
    }

    @Override
    public void getUserbyId(Integer id, Callback<QBUser, QBResponseException> callback) {
        remoteDataSource.getUserbyId(id, callback);
    }

    @Override
    public void deleteDialog(QBDialog qbDialog, Callback<Void, QBResponseException> callback) {
        remoteDataSource.deleteDialog(qbDialog, callback);
    }

    @Override
    public void updateDialog(QBDialog qbDialog, String consumerSellerId, String dealerSellerId, String listingId, QBDialogCustomData qbDialogCustomData, Callback<QBDialog, QBResponseException> callback) {
        remoteDataSource.updateDialog(qbDialog, consumerSellerId, dealerSellerId, listingId, qbDialogCustomData, callback);
    }


    public boolean isSignedIn() {
        return chatCached.isSignedIn();
    }

    public QBUser getUser(){
        return chatCached.signInUser;
    }

    private class ChatCached {

        private QBUser signInUser;


        public void setSignInUser(QBUser signInUser) {
            this.signInUser = signInUser;
        }

        public boolean isSignedIn() {
            return signInUser != null;
        }

    }
}
