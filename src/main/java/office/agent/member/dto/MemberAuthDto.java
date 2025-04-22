package office.agent.member.dto;

import lombok.Builder;

@Builder
public record MemberAuthDto(String username, String name, String role) {

}
