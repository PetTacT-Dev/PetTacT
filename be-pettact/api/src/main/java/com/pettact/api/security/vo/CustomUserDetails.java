package com.pettact.api.security.vo;

import com.pettact.api.user.entity.Users;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {
	private static final long serialVersionUID = 5550148879853596026L;
    private final Users user;

    public CustomUserDetails(Users user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // CommonCode에서 꺼낸 권한 코드값 사용
        String roleCode = user.getRoleCode();
        return List.of(new SimpleGrantedAuthority(roleCode));
    }

    @Override
    public String getPassword() {
        return user.getUserPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserEmail(); // 로그인 ID
    }

    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()               { return true; }

    // 추가적으로 Users 전체를 써야 하는 경우 접근 가능
    public Users getUserEntity() {
        return user;
    }
}