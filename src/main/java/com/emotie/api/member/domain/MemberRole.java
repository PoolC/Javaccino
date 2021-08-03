package com.emotie.api.member.domain;

import java.util.Collections;
import java.util.List;

public enum  MemberRole implements Role{
    ADMIN {
        @Override
        public boolean isAdmin() {
            return true;
        }

        @Override
        public boolean isMember() {
            return true;
        }

        @Override
        public String getDescription() {
            return "관리자";
        }

        @Override
        public List<MemberRole> getRequiredRoles() {
            return Collections.singletonList(MemberRole.MEMBER);
        }

    },
    MEMBER {
        @Override
        public boolean isMember() {
            return true;
        }

        @Override
        public String getDescription() {
            return "일반회원";
        }
    },
    UNACCEPTED {
        @Override
        public String getDescription() {
            return "비인증회원";
        }
    },
    WITHDRAWAL {
        @Override
        public String getDescription() {
            return "자진탈퇴회원";
        }

    },
    EXPELLED {
        @Override
        public String getDescription() {
            return "추방회원";
        }
    },
    PUBLIC {
        @Override
        public String getDescription() {
            return "외부인";
        }
    }
}
