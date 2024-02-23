package com.aem.showcase.core.pojos;

public class DeleteMessage {
    boolean removed;
    String message;
    
    public DeleteMessage() {}

    public DeleteMessage(boolean removed, String message) {
        this.removed = removed;
        this.message = message;
    }

    public boolean isRemoved() {
        return removed;
    }
    public void setRemoved(boolean removed) {
        this.removed = removed;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }    
}
