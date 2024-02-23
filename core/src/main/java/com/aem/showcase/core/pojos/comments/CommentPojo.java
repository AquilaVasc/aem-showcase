package com.aem.showcase.core.pojos.comments;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class CommentPojo {
    String id;
    String content;
    String created;
    String createdBy;
    String fullname;
    long upvoteCount;
    boolean userHasUpvoted;

    @JsonIgnore
    List<String> likes = new ArrayList<>();

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
    
    public long getUpvoteCount() {
        return likes.size();
    }
    
    public boolean isUserHasUpvoted() {
        return userHasUpvoted;
    }
    
    public void setUserHasUpvoted(boolean userHasUpvoted) {
        this.userHasUpvoted = userHasUpvoted;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }  
}
