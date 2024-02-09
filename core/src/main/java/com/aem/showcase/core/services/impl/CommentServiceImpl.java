package com.aem.showcase.core.services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.aem.showcase.core.pojos.CommentPojo;
import com.aem.showcase.core.services.CommentsService;

@Component(service = CommentsService.class, immediate = true)
public class CommentServiceImpl implements CommentsService{

    @Reference
    ResourceResolverFactory resourceResolverFactory;
    
    private static String SERVICE_ID = "AEMShowCaseUserJCR";

    @Override
    public boolean createComment(CommentPojo commentPojo) {
        try {
            ResourceResolver resolver = getAdminResourceResolver();
            Session session = resolver.adaptTo(Session.class);
            Node commentsNode = session.getNode("/content/dam/aem-showcase/comments");

            if(Objects.isNull(commentsNode)) {
                commentsNode = session.getNode("/content/dam/aem-showcase/").addNode("comments", NodeType.NT_FOLDER);
            } 

            Node comment = commentsNode.addNode(commentPojo.getId(), NodeType.NT_UNSTRUCTURED);
            comment.setProperty("id", commentPojo.getId());
            comment.setProperty("content", commentPojo.getContent());
            comment.setProperty("created", commentPojo.getCreated());
            comment.setProperty("fullName", commentPojo.getFullname());
            comment.setProperty("upvote", commentPojo.getUpvote_count());

            session.save();
        } catch (Exception e) {
            String test = "Just to see the exception";
            // TODO: handle exception
        }
        return false;
    }

    @Override
    public boolean deleteComment(CommentPojo commentPojo) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<CommentPojo> findAll(String fullname) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CommentPojo getComment(long id) {
        // TODO Auto-generated method stub
        return null;
    }

    ResourceResolver getAdminResourceResolver() throws LoginException{
        Map<String, Object> paramsMap = new HashMap<>();

        paramsMap.put(ResourceResolverFactory.SUBSERVICE, SERVICE_ID);
        try(ResourceResolver resolver = resourceResolverFactory.getServiceResourceResolver(paramsMap)) {
            return resolver;
        }
    }
}
