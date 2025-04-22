package office.agent.security.oauth2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import office.agent.global.Snowflake;
import office.agent.member.domain.AuthProvider;
import office.agent.member.domain.Member;
import office.agent.member.domain.MemberRole;
import office.agent.member.dto.MemberAuthDto;
import office.agent.member.repository.MemberRepository;
import office.agent.security.oauth2.dto.GoogleOAuth2Response;
import office.agent.security.oauth2.dto.OAuth2Response;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class CustomOAuth2MemberService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;
    private final Snowflake snowflake;
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("oauth user : {}",oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("google")) {
            oAuth2Response = new GoogleOAuth2Response(oAuth2User.getAttributes());
        }
        if (oAuth2Response == null) {
            log.info("[---] oAuth2Response is null");
        }
        return doAuthenticate(oAuth2Response);
    }

    private String generateUserName(final String provider, final String providerId) {
        return provider + " " + providerId;
    }

    /**
     * 회원가입 되어있지 않은 경우 회원가입 처리후 AuthenticationPrincipal 발급한다.
     * @param oAuth2Response
     * @return
     */
    private CustomOAuth2User doAuthenticate(OAuth2Response oAuth2Response) {
        String oauthId = generateUserName(oAuth2Response.getProvider(), oAuth2Response.getProviderId());
        Optional<Member> optionalMember = memberRepository.findMemberByOauthId(oauthId);
        if (optionalMember.isEmpty()) {
            Member newMember = Member.builder()
                    .id(snowflake.nextId())
                    .oauthId(oauthId)
                    .email(oAuth2Response.getEmail())
                    .nickName(oAuth2Response.getName()) // 처음 nickName은 Provider에 등록된 name으로 정한다.
                    .role(MemberRole.NORMAL)
                    .provider(AuthProvider.GOOGLE)
                    .providerId(oAuth2Response.getProviderId())
                    .build();
            memberRepository.save(newMember);
            MemberAuthDto authDto = new MemberAuthDto(newMember.getOauthId(), newMember.getNickName(), newMember.getRole().role());// TODO: 필드명 바꾸어야한다.
            return new CustomOAuth2User(authDto);
        } else {
            Member findMember = optionalMember.get();
            MemberAuthDto authDto=MemberAuthDto.builder()
                    .username(findMember.getOauthId())
                    .name(findMember.getNickName())
                    .role(findMember.getRole().role())
                    .build();
            return new CustomOAuth2User(authDto);
        }
    }
}
