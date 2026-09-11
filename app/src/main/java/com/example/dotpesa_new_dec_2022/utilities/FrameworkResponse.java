package com.example.dotpesa_new_dec_2022.utilities;

import java.util.ArrayList;

public class FrameworkResponse {

    Boolean actionSuccessfull = false;
    ArrayList<String> responseMessages = new ArrayList();
    Object responseObject = new Object();

    public FrameworkResponse() {
        actionSuccessfull = false;
        responseMessages = new ArrayList();
        responseObject = null;
    }

    public void setResponse(Boolean success, String message, Object obj) {
        actionSuccessfull = success;
        responseMessages.add(message);
        responseObject = obj;
    }

    public Boolean getResponseSuccess() {
        return actionSuccessfull;
    }

    public void addResponseMessage(String msg) {
        responseMessages.add(msg);
    }

    public ArrayList getListResponseMessages() {
        return responseMessages;
    }

    public String getResponseMessages() {
        String msg = "";
        for (int i = 0; i < responseMessages.size(); i++) {
            msg += responseMessages.get(i) + "\n";
        }
        return msg;
    }

    public String getResponseMessage() {
        if (responseMessages.size() > 0) {
            return (String) responseMessages.get(0);
        }
        return "";
    }

    public void getResponseObject(Object obj) {
        responseObject = obj;
    }

    public Object getResponseObject() {
        return responseObject;
    }
}

