package com.aem.showcase.core.services;

import java.util.List;

import com.aem.showcase.core.pojos.CommentPojo;

public interface CommentsService {
    
    List<CommentPojo> findAll(String fullname);

    CommentPojo getComment(long id);

    boolean createComment(CommentPojo commentPojo);

    boolean deleteComment(CommentPojo commentPojo);
}
