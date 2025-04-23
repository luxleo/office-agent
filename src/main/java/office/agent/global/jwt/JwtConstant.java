package office.agent.global.jwt;

public class JwtConstant {
    public static final String ACCESS_TOKEN_NAME = "access-token";
    public static final Long ACCESS_TOKEN_EXPIRATION_MS = 60 * 60 * 1000L; // 1hour
    public static final String REFRESH_TOKEN_NAME = "refresh-token";
    public static final Long REFRESH_TOKEN_EXPIRATION_MS = 24*60 * 60 * 1000L; // 1hour
    public static final String USER_NAME_CLAIM_KEY = "username";
    public static final String NICKNAME_CLAIM_KEY = "nickname";
    public static final String ROLE_CLAIM_KEY = "role";
}
