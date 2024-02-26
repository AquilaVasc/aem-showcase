package com.aem.showcase.core.servlets.comments;

import java.util.Date;

import com.aem.showcase.core.pojos.CreateMessage;
import com.aem.showcase.core.pojos.DeleteMessage;
import com.aem.showcase.core.pojos.ErrorMessage;
import com.aem.showcase.core.pojos.comments.CommentPojo;
import com.aem.showcase.core.pojos.users.AEMUserPojo;
import com.aem.showcase.core.services.comments.CommentsService;
import com.aem.showcase.core.services.users.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */

 @Component(service = Servlet.class, 
 property = { 
     "sling.servlet.paths=/bin/showcase/comments", 
     "sling.servlet.methods=GET,POST,DELETE,PUT" 
 }
)
public class CommentsServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;

    Logger logger = LoggerFactory.getLogger(CommentsServlet.class);

    @Reference
    CommentsService commentsService;

    @Reference
    UserService userService;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
        throws ServletException, IOException {

        String id = request.getParameter("id");

        ObjectMapper mapper = new ObjectMapper();
        if(null != id && !id.isEmpty()) {
            CommentPojo comment = commentsService.findById(id, request);
            JsonNode result = mapper.convertValue(comment, JsonNode.class);
            response.setContentType("application/json");
            response.getWriter().write(result.toString());
        } else {
            List<CommentPojo> comments;
            comments = commentsService.findAll(request);
            JsonNode result = mapper.convertValue(comments, JsonNode.class);
            response.setContentType("application/json");
            response.getWriter().write(result.toString());
        }
    }

    @Override
    protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
        throws ServletException, IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        
        String data = sb.toString();

        CommentPojo commentPojo = new ObjectMapper().readValue(data, CommentPojo.class);
        if(null != commentPojo) {
            if(null == commentPojo.getCreated() || (null != commentPojo && commentPojo.getCreated().isEmpty()))
                commentPojo.setCreated(new Date().toString());

            if(null == commentPojo.getFullname() || (null != commentPojo && commentPojo.getFullname().isEmpty())) {
                AEMUserPojo aemUser;
                try {
                    aemUser = userService.getUser(request.getResourceResolver());
                    String aemUserName = null == aemUser ? "" : aemUser.getFirstName() + " " + aemUser.getLastName();
                    String aemUserId = null != aemUser.getUserId() && !aemUser.getUserId().isEmpty() ? aemUser.getUserId() : "";
                    
                    commentPojo.setCreatedBy(aemUserId);
                    commentPojo.setFullname(aemUserName);
                } catch (RepositoryException e) {
                    logger.error("error while trying to retrieve the session", e);
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                }
            }

            commentPojo.setId(UUID.randomUUID().toString());
            CreateMessage message = commentsService.createComment(commentPojo);
            if (message.isCreated()){
                JsonNode result = new ObjectMapper().convertValue(commentPojo, JsonNode.class);
                response.setContentType("application/json");
                response.getWriter().write(result.toString());
            } else {
                JsonNode result = new ObjectMapper().convertValue(message, JsonNode.class);
                response.setContentType("application/json");
                response.getWriter().write(result.toString());
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doDelete(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
        throws ServletException, IOException {
        String id = request.getParameter("id");

        DeleteMessage message = commentsService.deleteComment(id);

        JsonNode result = new ObjectMapper().convertValue(message, JsonNode.class);
        response.setContentType("application/json");
        response.getWriter().write(result.toString());
    }

    @Override
    protected void doPut(final SlingHttpServletRequest request, final SlingHttpServletResponse response) 
        throws ServletException, IOException{

        String commentId = request.getParameter("id");
        if(null != commentId && !commentId.isEmpty()) {
            AEMUserPojo aemUser;

            try {
                aemUser = userService.getUser(request.getResourceResolver());
                String userId = aemUser.getUserId();

                CommentPojo comment = commentsService.likeOrUnlinkComment(commentId, userId, request);
                JsonNode result = new ObjectMapper().convertValue(comment, JsonNode.class);
                response.setContentType("application/json");
                response.getWriter().write(result.toString());
            } catch (RepositoryException e) {
                logger.error("error while trying to retrieve the session", e);
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
            }
        } else {
            ErrorMessage message = new ErrorMessage(HttpServletResponse.SC_BAD_REQUEST, "you must provide an comment id");
            JsonNode result = new ObjectMapper().convertValue(message, JsonNode.class);
            response.setContentType("application/json");
            response.getWriter().write(result.toString());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
