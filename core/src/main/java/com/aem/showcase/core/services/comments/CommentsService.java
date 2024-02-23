package com.aem.showcase.core.services.comments;

import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;

import com.aem.showcase.core.pojos.CreateMessage;
import com.aem.showcase.core.pojos.DeleteMessage;
import com.aem.showcase.core.pojos.comments.CommentPojo;

public interface CommentsService {
    
    List<CommentPojo> findAll(SlingHttpServletRequest request);

    CommentPojo findById(String id, SlingHttpServletRequest request);

    CreateMessage createComment(CommentPojo commentPojo);

    CommentPojo likeOrUnlinkComment(String commentId, String userId, SlingHttpServletRequest request);

    DeleteMessage deleteComment(String id);
}
