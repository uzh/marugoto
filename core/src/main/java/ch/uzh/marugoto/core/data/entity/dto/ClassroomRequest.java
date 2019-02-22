package ch.uzh.marugoto.core.data.entity.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import ch.uzh.marugoto.core.Constants;

/**
 * Classroom request DTO class
 */
public class ClassroomRequest implements RequestDto {

    private String name;
    private String description;
    private boolean closeRegistrationOnStart;
    private String invitationLinkId;
    private String startClassAt;
    private String endClassAt;

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
    public LocalDate getStartClassAt() {
        if (startClassAt == null) return null;
        return LocalDate.parse(startClassAt, DateTimeFormatter.ofPattern(Constants.DATE_FORMAT));
    }
    public void setStartClassAt(String startClassAt) {
        this.startClassAt = startClassAt;
    }
    public LocalDate getEndClassAt() {
        if (endClassAt == null) return null;
        return LocalDate.parse(endClassAt, DateTimeFormatter.ofPattern(Constants.DATE_FORMAT));
    }
    public void setEndClassAt(String endClassAt) {
        this.endClassAt = endClassAt;
    }
}
