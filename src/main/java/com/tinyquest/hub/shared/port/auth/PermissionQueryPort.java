package com.tinyquest.hub.shared.port.auth;

import com.tinyquest.hub.shared.permission.ResourceType;
import com.tinyquest.hub.shared.port.auth.query.UserResourceAccessView;

import java.util.Set;

public interface PermissionQueryPort {

    UserResourceAccessView getResourcesForUser(Long userId, Set<ResourceType> scopes);

    void evictAllUserPermissions(Long userId);
}
