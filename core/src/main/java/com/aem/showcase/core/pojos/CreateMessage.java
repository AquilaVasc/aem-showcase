package com.aem.showcase.core.pojos;

public class CreateMessage {
    boolean created;
    String message;
    
    public CreateMessage() {}

    public CreateMessage(boolean created, String message) {
        this.created = created;
        this.message = message;
    }

    public boolean isCreated() {
        return created;
    }
    public void setCreated(boolean removed) {
        this.created = removed;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }    
}
