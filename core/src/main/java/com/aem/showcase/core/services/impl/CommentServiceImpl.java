package com.aem.showcase.core.services.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.serviceusermapping.ServiceUserMapped;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.aem.showcase.core.pojos.CommentPojo;
import com.aem.showcase.core.services.CommentsService;

@Component(
    service = CommentsService.class,
    reference = {
        @Reference(
            name = CommentServiceImpl.SERVICE_ID,
            service = ServiceUserMapped.class,
            target = "(subServiceName=AEMShowCaseUserJCR)"
        )
    },
    immediate = true)
public class CommentServiceImpl implements CommentsService{

    @Reference
    ResourceResolverFactory resourceResolverFactory;
    
    protected static final String SERVICE_ID = "AEMShowCaseUserJCR";

    @Override
    public boolean createComment(CommentPojo commentPojo) {
        try (ResourceResolver resolver = resourceResolverFactory.getResourceResolver(getAdminResourceResolverMap())){
            
            Session session = resolver.adaptTo(Session.class);
            Node commentsNode = null;
            try {
                commentsNode = session.getNode("/content/aem-showcase/comments");    
            } catch (PathNotFoundException e) {
                Node showCaseNode = session.getNode("/content/aem-showcase");
                commentsNode = showCaseNode.addNode("comments", NodeType.NT_FOLDER);
            }

            Node comment = commentsNode.addNode(commentPojo.getId(), NodeType.NT_UNSTRUCTURED);
            comment.setProperty("id", commentPojo.getId());
            comment.setProperty("content", commentPojo.getContent());
            comment.setProperty("created", commentPojo.getCreated());
            comment.setProperty("fullName", commentPojo.getFullname());
            comment.setProperty("upvote", commentPojo.getUpvote_count());

            session.save();
            session.logout();

            return true;
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

    Map<String, Object> getAdminResourceResolverMap() {
        Map<String, Object> authInfo = Collections.singletonMap(
            ResourceResolverFactory.SUBSERVICE, SERVICE_ID);

        return authInfo;
    }
}
