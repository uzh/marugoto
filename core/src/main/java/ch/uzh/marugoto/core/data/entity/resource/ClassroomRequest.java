package ch.uzh.marugoto.core.data.entity.resource;

/**
 * Classroom request DTO class
 */
public class ClassroomRequest implements RequestDto {

    private String name;
    private String description;
    private boolean closeRegistrationOnStart;
    private String invitationLinkId;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public boolean isCloseRegistrationOnStart() {
        return closeRegistrationOnStart;
    }
    public void setCloseRegistrationOnStart(boolean closeRegistrationOnStart) {
        this.closeRegistrationOnStart = closeRegistrationOnStart;
    }
    public String getInvitationLinkId() {
        return invitationLinkId;
    }
    public void setInvitationLinkId(String invitationLinkId) {
        this.invitationLinkId = invitationLinkId;
    }
}
