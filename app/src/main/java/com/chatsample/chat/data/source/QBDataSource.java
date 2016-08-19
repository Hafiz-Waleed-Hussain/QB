package com.chatsample.chat.data.source;

import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogCustomData;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

/**
 * Created by waleed on 18/08/2016.
 */
public interface QBDataSource {


    interface Callback<T, E> {

        void onSuccess(T t);

        void onError(E e);
    }

    void createSession(Callback<QBSession, QBResponseException> createSessionCallback);

    void login(QBUser qbUser, Callback<QBUser, QBResponseException> callback);

    void signUp(QBUser qbUser, Callback<QBUser, QBResponseException> callback);

    void chatLogin(QBUser qbUser, Callback<QBUser, QBResponseException> callback);

    void chatLogout(Callback<QBUser, QBResponseException> callback);

    void createGroupChatDialog(String dealerUsername,
                               Integer dealerId,
                               Integer qbUserid,QBDialogCustomData qbDialogCustomData,
                               Callback<QBDialog, QBResponseException> callback);

    void getGroupChatDialog(String consumerSellerId,
                            String dealerSellerId,
                            String listingId,Callback<QBDialog, QBResponseException> callback);

    void getGroupChatDialogs(Callback<ArrayList<QBDialog>, QBResponseException> callback);

    void getUsersFromDialogs(ArrayList<QBDialog> qbDialogs, Callback<ArrayList<QBUser>, QBResponseException> callback);

    void getUserbyId(Integer id, Callback<QBUser, QBResponseException> callback);

    void deleteDialog(QBDialog qbDialog,Callback<Void, QBResponseException> callback );

    void updateDialog(QBDialog qbDialog,
                      String consumerSellerId,
                      String dealerSellerId,
                      String listingId,
                      QBDialogCustomData qbDialogCustomData, Callback<QBDialog, QBResponseException> callback);
}
