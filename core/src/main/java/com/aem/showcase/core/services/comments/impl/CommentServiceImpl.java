package com.aem.showcase.core.services.comments.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.nodetype.NodeType;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.crypto.CryptoException;
import com.adobe.granite.crypto.CryptoSupport;
import com.aem.showcase.core.pojos.CreateMessage;
import com.aem.showcase.core.pojos.DeleteMessage;
import com.aem.showcase.core.pojos.comments.CommentPojo;
import com.aem.showcase.core.pojos.users.AEMUserPojo;
import com.aem.showcase.core.services.comments.CommentsService;
import com.aem.showcase.core.services.users.UserService;

@Component(
    service = CommentsService.class,
    immediate = true)
public class CommentServiceImpl implements CommentsService {

    Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

    private final String ID_PROP = "id";
    private final String CONTENT_PROP = "content";
    private final String CREATED_PROP = "created";
    private final String CREATED_BY_PROP = "createdBy";
    private final String FULL_NAME_PROP = "fullName";
    private final String UPVOTE_LIST_PROP = "upvoteList";
    
    @Reference
    ResourceResolverFactory resourceResolverFactory;

    @Reference
    private CryptoSupport cryptoSupport;

    @Reference
    private UserService userService;
    
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

    // ok  
    @Override
    public CreateMessage createComment(CommentPojo commentPojo) {
        CreateMessage message = new CreateMessage();

        try (ResourceResolver resolver = getUserResourceResolver("admin", "admin")){
    
            Session session = resolver.adaptTo(Session.class);

            Node commentsNode = null;
            try {
                commentsNode = session.getNode("/content/aem-showcase/comments");    
            } catch (PathNotFoundException e) {
                message.setMessage("was not able to access comments node");
                Node showCaseNode = session.getNode("/content/aem-showcase");
                commentsNode = showCaseNode.addNode("comments", NodeType.NT_UNSTRUCTURED);
            }

            Node comment = commentsNode.addNode(commentPojo.getId(), NodeType.NT_UNSTRUCTURED);
            
            comment.setProperty(ID_PROP, commentPojo.getId());
            comment.setProperty(CONTENT_PROP, commentPojo.getContent());
            comment.setProperty(CREATED_PROP, commentPojo.getCreated());
            comment.setProperty(CREATED_BY_PROP, commentPojo.getCreatedBy());
            comment.setProperty(FULL_NAME_PROP, commentPojo.getFullname());
            String[] likesArray =  commentPojo.getLikes().stream().toArray(String[]::new);;
            comment.setProperty(UPVOTE_LIST_PROP, likesArray);

            session.save();
            session.logout();
            resolver.close();

            message.setMessage("commentCreated");
            message.setCreated(true);
            return message;
        } catch (Exception e) {
            logger.error("error while trying to create node. this is the following error message: {}", e);
            message.setMessage("something went wrong");
        }

        
        message.setCreated(false);
        return message;
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
    public CommentPojo findById(String id, SlingHttpServletRequest request){
        CommentPojo comment = null;

        try (ResourceResolver resolver = getUserResourceResolver("admin", "admin")){
    
            Session session = resolver.adaptTo(Session.class);     

            try {
                Node commentNode = session.getNode("/content/aem-showcase/comments/" + id);
                
                comment = getCommentByNode(commentNode, request);
                
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
    public List<CommentPojo> findAll(SlingHttpServletRequest request) {
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

                        CommentPojo comment = getCommentByNode(commentNode, request);
                        if(null != comment) {
                            commentsList.add(comment);
                        }
                        
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

    private CommentPojo getCommentByNode(Node commentNode, SlingHttpServletRequest request) throws ValueFormatException, PathNotFoundException, RepositoryException {
        CommentPojo comment = new CommentPojo();
        comment.setId(commentNode.getProperty(ID_PROP).getString());
        comment.setContent(commentNode.getProperty(CONTENT_PROP).getString());
        comment.setCreated(commentNode.getProperty(CREATED_PROP).getString());
        comment.setCreatedBy(commentNode.getProperty(CREATED_BY_PROP).getString());
        comment.setFullname(commentNode.getProperty(FULL_NAME_PROP).getString());
        
        Value[] likesValues = commentNode.getProperty(UPVOTE_LIST_PROP).getValues();
        if(likesValues != null && likesValues.length > 0) {
            List<String> likes = new ArrayList<>();
            for(Value likeValue : likesValues) {
                likes.add(likeValue.getString());
            }

            comment.setLikes(likes);

            AEMUserPojo aemUser;
            try {
                aemUser = userService.getUser(request.getResourceResolver());
                String aemUserId = null != aemUser.getUserId() && !aemUser.getUserId().isEmpty() ? aemUser.getUserId() : "";

                
                if(likes.contains(aemUserId)){
                    comment.setUserHasUpvoted(true);
                }
            } catch (RepositoryException e) {
                logger.error("error while trying to access user, user probably not logged in: {}", e);
            }  
        }

        return comment;
    }

    @Override
    public CommentPojo likeOrUnlinkComment(String commentId, String userId, SlingHttpServletRequest request) {
        CommentPojo comment = this.findById(commentId, request);

        if(comment == null) {
            return null;
        }

        List<String> likes = comment.getLikes();

        String commentOwnerId = comment.getCreatedBy();
        if(likes != null && likes.size() > 0) {
            if(likes.contains(commentOwnerId)) {
                likes.remove(commentOwnerId);
                if(commentOwnerId.equals(commentOwnerId)) {
                    // once update the logic to return comment pojo with setUserHasUpvoted replace 
                    // logic to simply invert value  
                    comment.setUserHasUpvoted(false);
                }
            } else {
                likes.add(commentOwnerId);
                if(commentOwnerId.equals(commentOwnerId)) {
                    // once update the logic to return comment pojo with setUserHasUpvoted replace 
                    // logic to simply invert value  
                    comment.setUserHasUpvoted(true);
                }
            }
        } else {
            likes = new ArrayList<>();
            likes.add(commentOwnerId);

            if(commentOwnerId.equals(comment.getCreatedBy())) {
                comment.setUserHasUpvoted(true);
            }
        }
        
        return null;
    }

    Map<String, Object> getAdminResourceResolverMap() {
        Map<String, Object> authInfo = Collections.singletonMap(
            ResourceResolverFactory.SUBSERVICE, SERVICE_ID);

        return authInfo;
    }
}
