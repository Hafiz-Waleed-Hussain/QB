package com.chatsample.chat.data.source;

import com.quickblox.chat.model.QBDialogCustomData;

/**
 * Created by waleed on 19/08/2016.
 */
public class ChatInfo {

    public String dealerImageUrl;
    public String dealerSellerId;
    public String listingImageUrl;
    public String listingTitle;
    public String listingId;
    public String profileType;
    public String consumerSellerId;
    public String price;

    public QBDialogCustomData createCustomData() {
        QBDialogCustomData data = new QBDialogCustomData("ChatInfo");
        throwExceptionIfNull(dealerImageUrl);
        data.put("dealerImageUrl", dealerImageUrl);
        throwExceptionIfNull(dealerSellerId);
        data.put("dealerSellerId", dealerSellerId);
        throwExceptionIfNull(listingImageUrl);
        data.put("listingImageUrl", listingImageUrl);
        throwExceptionIfNull(listingTitle);
        data.put("listingTitle", listingTitle);
        throwExceptionIfNull(listingId);
        data.put("listingId", listingId);
        throwExceptionIfNull(profileType);
        data.put("profileType", profileType);
        throwExceptionIfNull(consumerSellerId);
        data.put("consumerSellerId", consumerSellerId);
        throwExceptionIfNull(price);
        data.put("price", price);


        return data;
    }

    private void throwExceptionIfNull(String s) {
        if (s == null || s.isEmpty()) {
            throw new RuntimeException("Fill all values");
        }

    }
}
