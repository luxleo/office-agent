package office.agent.member.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import office.agent.global.BaseTimeEntity;

@Getter
@NoArgsConstructor
@Entity
public class Member extends BaseTimeEntity {

    @Id
    private Long id;
    private String email;
    private String password; // for non - oauth2 user
    private String nickName;
    @Enumerated(EnumType.STRING)
    private MemberRole role;
    // Oauth
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;
    private String providerId;
    private String oauthId; // oauth로 로그인시 유저 식별하기 위해 사용됨. AuthProvider.toString() + providerId로 구성한다.

    @Builder
    public Member(Long id, String email, String password, String nickName, MemberRole role, AuthProvider provider, String providerId, String oauthId) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nickName = nickName;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
        this.oauthId = oauthId;
    }

    private String allocateOauthId(final AuthProvider provider, final String providerId) {
        return provider.name() + providerId;
    }
}
