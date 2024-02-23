package com.aem.showcase.core.services.comments.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.crypto.CryptoException;
import com.adobe.granite.crypto.CryptoSupport;
import com.aem.showcase.core.pojos.DeleteMessage;
import com.aem.showcase.core.pojos.comments.CommentPojo;
import com.aem.showcase.core.services.comments.CommentsService;

@Component(
    service = CommentsService.class,
    immediate = true)
public class CommentServiceImpl implements CommentsService {

    Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

    private final String ID_PROP = "id";
    private final String CONTENT_PROP = "content";
    private final String CREATED_PROP = "created";
    private final String FULL_NAME_PROP = "fullName";
    private final String UPVOTE_PROP = "upvote";
    

    @Reference
    ResourceResolverFactory resourceResolverFactory;

    @Reference
    private CryptoSupport cryptoSupport;
    
    protected static final String SERVICE_ID = "AEMShowCaseUserJCR";

    private ResourceResolver getUserResourceResolver(String username, String password) throws org.apache.sling.api.resource.LoginException {
        Map<String,Object> authenticationInfo = new HashMap<>(2);
        authenticationInfo.put(ResourceResolverFactory.USER, username);
        String unprotectedPass;
        try {
            unprotectedPass = cryptoSupport.unprotect(password);
        } catch (CryptoException e) {
            unprotectedPass = password;
        }
        authenticationInfo.put(ResourceResolverFactory.PASSWORD, unprotectedPass.toCharArray());
        return resourceResolverFactory.getResourceResolver(authenticationInfo);
    }

    @Override
    public boolean createComment(CommentPojo commentPojo) {
        try (ResourceResolver resolver = getUserResourceResolver("admin", "admin")){
    
            Session session = resolver.adaptTo(Session.class);

            Node commentsNode = null;
            try {
                commentsNode = session.getNode("/content/aem-showcase/comments");    
            } catch (PathNotFoundException e) {
                Node showCaseNode = session.getNode("/content/aem-showcase");
                commentsNode = showCaseNode.addNode("comments", NodeType.NT_UNSTRUCTURED);
            }

            Node comment = commentsNode.addNode(commentPojo.getId(), NodeType.NT_UNSTRUCTURED);
            
            comment.setProperty(ID_PROP, commentPojo.getId());
            comment.setProperty(CONTENT_PROP, commentPojo.getContent());
            comment.setProperty(CREATED_PROP, commentPojo.getCreated());
            comment.setProperty(FULL_NAME_PROP, commentPojo.getFullname());
            comment.setProperty(UPVOTE_PROP, commentPojo.getUpvote_count());

            session.save();
            session.logout();
            resolver.close();

            return true;
        } catch (Exception e) {
            logger.error("error while trying to create node. this is the following error message: {}", e);
        }

        return false;
    }

    @Override
    public DeleteMessage deleteComment(String id) {

        DeleteMessage deleteMessage = new DeleteMessage();

        try (ResourceResolver resolver = getUserResourceResolver("admin", "admin")){
    
            Session session = resolver.adaptTo(Session.class);     

            try {
                Node commentNode = session.getNode("/content/aem-showcase/comments/" + id);
                
                commentNode.remove();
                deleteMessage.setMessage("item deleted");
                deleteMessage.setRemoved(true);
            } catch (PathNotFoundException e) {
                deleteMessage.setMessage("item not found");
                deleteMessage.setRemoved(false);
                logger.info("there was something wrong during get comments, the error is: {}", e);
            }

            session.save();
            session.logout();
            resolver.close();
        } catch (Exception e) {
            deleteMessage.setMessage("could not remove node");
            deleteMessage.setRemoved(false);
            logger.error("there was something wrong during get comment, the error is: {}", e);
        }

        return deleteMessage;
    }

    @Override
    public CommentPojo findById(String id){
        CommentPojo comment = null;

        try (ResourceResolver resolver = getUserResourceResolver("admin", "admin")){
    
            Session session = resolver.adaptTo(Session.class);     

            try {
                Node commentNode = session.getNode("/content/aem-showcase/comments/" + id);
                
                comment = new CommentPojo();
                comment.setId(commentNode.getProperty(ID_PROP).getString());
                comment.setContent(commentNode.getProperty(CONTENT_PROP).getString());
                comment.setCreated(commentNode.getProperty(CREATED_PROP).getString());
                comment.setFullname(commentNode.getProperty(FULL_NAME_PROP).getString());
                
                long upvoteCount = commentNode.getProperty(UPVOTE_PROP).getLong();
                comment.setUpvote_count(upvoteCount);
                
                comment.setUser_has_upvoted(false);
                
            } catch (PathNotFoundException e) {
                logger.info("there was something wrong during get comments, the error is: {}", e);
            }

            session.logout();
            resolver.close();
        } catch (Exception e) {
            logger.error("there was something wrong during get comment, the error is: {}", e);
        }

        return comment;
    }

    @Override
    public List<CommentPojo> findAll() {
        List<CommentPojo> commentsList; 

        try (ResourceResolver resolver = getUserResourceResolver("admin", "admin")){
    
            Session session = resolver.adaptTo(Session.class);     

            Node commentsNode = null;
            try {
                commentsNode = session.getNode("/content/aem-showcase/comments");
                NodeIterator commentsNodeIterator = commentsNode.getNodes();   

                boolean hasComments = commentsNodeIterator.hasNext();

                if(hasComments) {
                    commentsList = new ArrayList<>();
                    while (hasComments) {
                        Node commentNode = commentsNodeIterator.nextNode();

                        CommentPojo comment = new CommentPojo();
                        comment.setId(commentNode.getProperty(ID_PROP).getString());
                        comment.setContent(commentNode.getProperty(CONTENT_PROP).getString());
                        comment.setCreated(commentNode.getProperty(CREATED_PROP).getString());
                        comment.setFullname(commentNode.getProperty(FULL_NAME_PROP).getString());
                        
                        long upvoteCount = commentNode.getProperty(UPVOTE_PROP).getLong();
                        comment.setUpvote_count(upvoteCount);
                        
                        comment.setUser_has_upvoted(false);
                        commentsList.add(comment);

                        hasComments = commentsNodeIterator.hasNext();
                    }
                } else {
                    commentsList = Collections.emptyList();
                }

                
            } catch (PathNotFoundException e) {
                // there's no comments node
                commentsList = Collections.emptyList();
            }

            session.logout();
            resolver.close();
        } catch (Exception e) {
            // there's no comments node
            logger.error("there was something wrong during get comments, the error is: {}", e);
            commentsList = Collections.emptyList();
        }

        return commentsList;
    }

    @Override
    public CommentPojo likeOrUnlinkComment(String commentId, String userId) {
        CommentPojo comment = this.findById(commentId);

        if(comment == null) {
            return null;
        }

        String commentOwnerId = comment.getCreatedBy();
        
        return null;
    }

    Map<String, Object> getAdminResourceResolverMap() {
        Map<String, Object> authInfo = Collections.singletonMap(
            ResourceResolverFactory.SUBSERVICE, SERVICE_ID);

        return authInfo;
    }
}
