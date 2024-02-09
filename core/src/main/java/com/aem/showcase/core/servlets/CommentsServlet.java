/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.aem.showcase.core.servlets;

import com.aem.showcase.core.pojos.CommentPojo;
import com.aem.showcase.core.services.CommentsService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
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
     "sling.servlet.methods=GET,POST" 
 }
)
public class CommentsServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;

    @Reference
    CommentsService commentsService;

    @Override
    protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
        throws ServletException, IOException {

        List<CommentPojo> comments = new ArrayList<>();

        JsonNode node = new ObjectMapper().convertValue(comments, JsonNode.class);
        resp.setContentType("application/json");
        resp.getWriter().write(node.toString());
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
        commentPojo.setId(UUID.randomUUID().toString());
        commentsService.createComment(commentPojo);

    }
}
