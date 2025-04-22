package office.agent.security.oauth2;

import lombok.RequiredArgsConstructor;
import office.agent.member.dto.MemberAuthDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {
    private final MemberAuthDto memberDto;

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(memberDto.role()));
        return authorities;
    }

    @Override
    public String getName() {
        return memberDto.name();
    }

    public String getUserName() {
        return memberDto.username();
    }
}
