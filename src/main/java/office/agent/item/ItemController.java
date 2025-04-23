package office.agent.item;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import office.agent.security.oauth2.CustomOAuth2User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ItemController {
    @GetMapping("/")
    public ResponseEntity<HelloResponse> index(@AuthenticationPrincipal CustomOAuth2User user) {
        log.info("authenticated user : {}",user.getUserName());
        return ResponseEntity.ok(new HelloResponse("hello"));
    }
    @Getter
    static private class HelloResponse {
        private final String data;

        HelloResponse(String data) {
            this.data = data;
        }
    }
}
