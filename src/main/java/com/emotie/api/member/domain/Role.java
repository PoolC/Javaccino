package com.emotie.api.member.domain;

import java.util.Collections;
import java.util.List;

public interface Role {
    default boolean isAdmin() {
        return false;
    }

    default boolean isMember() {
        return false;
    }

    String getDescription();

    default List<MemberRole> getRequiredRoles() {
        return Collections.emptyList();
    }
}
