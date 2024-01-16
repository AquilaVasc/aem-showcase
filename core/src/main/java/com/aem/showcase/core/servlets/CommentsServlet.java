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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
        throws ServletException, IOException {

        List<CommentPojo> comments = new ArrayList<>();
        comments.add(new CommentPojo("Primeiro comentario", "2024-10-01", "Aquila Vasconcelos da Silva", 2, false));
        comments.add(new CommentPojo("Segundo comentario", "2024-10-01", "Aquila Vasconcelos da Silva", 2, false));
        comments.add(new CommentPojo("Terceiro comentario", "2024-10-01", "Aquila Vasconcelos da Silva", 2, false));

        JsonNode node = new ObjectMapper().convertValue(comments, JsonNode.class);
        resp.setContentType("application/json");
        resp.getWriter().write(node.toString());
    }

    @Override
    protected void doPost(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
    throws ServletException, IOException {
    // Handle POST method logic if needed
    }
}
