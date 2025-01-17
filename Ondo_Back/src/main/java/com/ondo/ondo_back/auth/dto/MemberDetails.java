package com.ondo.ondo_back.auth.dto;

import com.ondo.ondo_back.common.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class MemberDetails implements UserDetails {

    private final Member memberEntity;

    public MemberDetails(Member memberEntity) {

        this.memberEntity = memberEntity;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(() -> memberEntity.getRole());
        return collection;
    }

    @Override
    public String getPassword() {

        return memberEntity.getPassword();
    }

    @Override
    public String getUsername() {

        return memberEntity.getEmail();
    }

    public int getMemberId() {

        return memberEntity.getMemberId();
    }

    public String getNickname() {

        return memberEntity.getNickname();
    }

    @Override
    public boolean isAccountNonExpired() {

        return true;
    }

    @Override
    public boolean isAccountNonLocked() {

        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {

        return true;
    }

    @Override
    public boolean isEnabled() {

        return true;
    }
}
