package com.noptech.stira.web.websocket.dto;

public class UpdatePayload {

    String message = "Hello";

    public UpdatePayload(){

    }

    public String toString(){
        return this.message;
    }
}
