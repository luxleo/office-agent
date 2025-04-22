package office.agent;

import lombok.extern.slf4j.Slf4j;
import office.agent.member.domain.MemberRole;
import org.junit.jupiter.api.Test;

@Slf4j
public class EnumTest {
    @Test
    void enumTest(){
        log.info(String.valueOf(MemberRole.ADMIN));
    }
}
