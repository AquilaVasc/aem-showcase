package com.aem.showcase.core.services.comments;

import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;

import com.aem.showcase.core.pojos.DeleteMessage;
import com.aem.showcase.core.pojos.comments.CommentPojo;

public interface CommentsService {
    
    List<CommentPojo> findAll();

    CommentPojo findById(String id);

    boolean createComment(CommentPojo commentPojo);

    CommentPojo likeOrUnlinkComment(String commentId, String userId);

    DeleteMessage deleteComment(String id);
}
