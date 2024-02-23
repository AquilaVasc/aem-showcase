package com.aem.showcase.core.services.users.impl;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aem.showcase.core.pojos.users.AEMUserPojo;
import com.aem.showcase.core.services.users.UserService;

@Component(service = { UserService.class })
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private Session session;
    private UserManager userManager;
    private User user;
    
    @Override
    public AEMUserPojo getUser(ResourceResolver resourceResolver) {

        try {
            session = resourceResolver.adaptTo(Session.class);
            userManager = resourceResolver.adaptTo(UserManager.class);
            user = (User) userManager.getAuthorizable(session.getUserID());
            
            AEMUserPojo userBean = new AEMUserPojo();
            userBean.setUserId(session.getUserID());
            
            String firstName = user.getProperty("./profile/givenName") == null ? "" : user.getProperty("./profile/givenName")[0].getString(); 
            String lastName = user.getProperty("./profile/familyName") == null ? "" : user.getProperty("./profile/familyName")[0].getString(); 
            String email = user.getProperty("./profile/email") == null ? "" : user.getProperty("./profile/email")[0].getString(); 
            
            userBean.setFirstName(firstName);
            userBean.setLastName(lastName);
            userBean.setEmail(email);
            
            return userBean;

        } catch(RepositoryException e) {
            LOGGER.error("Session error");
        } catch(IllegalStateException e) {
            LOGGER.error("Inapropriate time to invoke method", e);
        }

        return null;
    }

    @Override
    public String getUserId(ResourceResolver resourceResolver) {
        try {
            session = resourceResolver.adaptTo(Session.class);
            return session.getUserID();

        } catch(IllegalStateException e) {
            LOGGER.error("Inapropriate time to invoke method", e);
        }

        return null;
    }
}
