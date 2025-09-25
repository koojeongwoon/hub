package com.tinyquest.hub.shared.port.auth.command;

import java.util.Set;

public interface UserRoleAssignmentPort {

    void assignDefaultRoles(Long userId);

    void updateRoles(Long userId, Set<String> roleCodes);
}
