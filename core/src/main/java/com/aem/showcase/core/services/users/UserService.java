package com.aem.showcase.core.services.users;

import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.ResourceResolver;

import com.aem.showcase.core.pojos.users.AEMUserPojo;

public interface UserService {
    public AEMUserPojo getUser(ResourceResolver resourceResolver) throws RepositoryException;
    public String getUserId(ResourceResolver resourceResolver);
}
