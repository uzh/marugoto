package ch.uzh.marugoto.core.data.entity.application;

import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.Ref;

import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Classroom information
 */
@Document
public class Classroom {
    @Id
    private String id;
    private String name;
    private String description;
    private String invitationLinkId;
    private LocalDate startClassAt;
    private LocalDate endClassAt;
    private int numberOfStudents;
    @Ref
    private User createdBy;
    private LocalDateTime createdAt;

    public Classroom() {
        super();
        this.createdAt = LocalDateTime.now();
    }

    public Classroom(String name, String description, LocalDate startClassAt, LocalDate endClassAt) {
        this();
        this.name = name;
        this.description = description;
        this.startClassAt = startClassAt;
        this.endClassAt = endClassAt;
    }

    public String getId() {
        return id;
    }

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

    public String getInvitationLinkId() {
        return invitationLinkId;
    }

    public void setInvitationLinkId(String invitationLinkId) {
        this.invitationLinkId = invitationLinkId;
    }

    public LocalDate getStartClassAt() {
        return startClassAt;
    }

    public void setStartClassAt(LocalDate startClassAt) {
        this.startClassAt = startClassAt;
    }

    public LocalDate getEndClassAt() {
        return endClassAt;
    }

    public void setEndClassAt(LocalDate endClassAt) {
        this.endClassAt = endClassAt;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

	public int getNumberOfStudents() {
		return numberOfStudents;
	}

	public void setNumberOfStudents(int numberOfStudents) {
		this.numberOfStudents = numberOfStudents;
	}
}
