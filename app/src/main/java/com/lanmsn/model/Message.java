package com.lanmsn.model;

import java.util.Date;


public class Message  {

    private String content;
    private Date date;
    private String sender;

    public Message(String sender,String content,Date date){

        this.sender=sender;
        this.content=content;
        this.date=date;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {

        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {

        return "Sender: "+sender+" Message: "+content+ " Date: "+date.toString();
    }
}
