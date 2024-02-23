package com.aem.showcase.core.pojos.comments;

public class CommentPojo {
    String id;
    String content;
    String created;
    String createdBy;
    String fullname;
    long upvote_count;
    boolean user_has_upvoted;

    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getCreated() {
        return created;
    }
    
    public void setCreated(String created) {
        this.created = created;
    }
    
    public String getFullname() {
        return fullname;
    }
    
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
    
    public long getUpvote_count() {
        return upvote_count;
    }
    
    public void setUpvote_count(long upvote_count) {
        this.upvote_count = upvote_count;
    }
    
    public boolean isUser_has_upvoted() {
        return user_has_upvoted;
    }
    
    public void setUser_has_upvoted(boolean user_has_upvoted) {
        this.user_has_upvoted = user_has_upvoted;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }  
}
