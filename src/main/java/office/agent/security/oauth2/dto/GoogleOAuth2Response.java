package office.agent.security.oauth2.dto;

import java.util.Map;

public class GoogleOAuth2Response implements OAuth2Response{
    private final Map<String, Object> attributes;
    private static final String PROVIDER = "google";

    public GoogleOAuth2Response(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProvider() {
        return PROVIDER;
    }

    @Override
    public String getProviderId() {
        return this.attributes.get("sub").toString();
    }

    @Override
    public String getEmail() {
        return this.attributes.get("email").toString();
    }

    @Override
    public String getName() {
        return this.attributes.get("name").toString();
    }
}
