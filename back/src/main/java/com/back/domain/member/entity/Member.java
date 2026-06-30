package com.back.domain.member.entity;

import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Member extends BaseEntity {
    @Column(unique = true)
    private String username;
    @Column(unique = true)
    private String githubId;
    @Column(unique = true)
    private String githubLink;
    @Column(unique = true)
    private String widgetLink;
    private String password;
    private String nickname;
    @Column(unique = true)
    private String refreshToken;
    @Setter
    private LocalDateTime deletedDate;

    public Member(long id, String username, String name) {
        setId(id);
        this.username = username;
        setName(name);
        this.deletedDate = null;
    }

    public Member(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.refreshToken = UUID.randomUUID().toString();
        this.deletedDate = null;
    }

    public String getName() {
        return nickname;
    }

    public void setName(String name) {
        this.nickname = name;
    }

    public void modifyRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public boolean isAdmin() {
        if ("system".equals(username)) return true;
        return "admin".equals(username);
    }

    public boolean isDeleted() {
        return deletedDate != null;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getAuthoritiesAsStringList()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    private List<String> getAuthoritiesAsStringList() {
        List<String> authorities = new ArrayList<>();

        if (isAdmin())
            authorities.add("ROLE_ADMIN");

        return authorities;
    }

}
