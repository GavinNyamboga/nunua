package com.dev.nunua.Model;

import android.provider.ContactsContract;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MpesaResponse {
    @SerializedName("data")
    @Expose
    private ContactsContract.Contacts.Data data;
    @SerializedName("message")
    @Expose
    private String message;

    public ContactsContract.Contacts.Data getData() {
        return data;
    }

    public void setData(ContactsContract.Contacts.Data data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
