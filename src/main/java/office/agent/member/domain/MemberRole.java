package office.agent.member.domain;

public enum MemberRole {
    ADMIN("ROLE_ADMIN"),
    NORMAL("ROLE_NORMAL");

    private final String roleName;
    MemberRole(String roleName) {
        this.roleName = roleName;
    }

    public String role() {
        return this.roleName;
    }
    @Override
    public String toString() {
        return this.roleName;
    }
}
