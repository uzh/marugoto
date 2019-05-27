package ch.uzh.marugoto.core.data.entity.resource;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import ch.uzh.marugoto.core.Constants;
import ch.uzh.marugoto.core.data.validation.DateFormat;

/**
 * Create classroom request class
 * Contain only required and validated fields for creating new classroom
 */
public class CreateClassroom extends ClassroomRequest {

    @NotEmpty(message = "{notEmpty}")
    private String name;

    @NotBlank(message = "{notEmpty}")
    @DateFormat
    private String startClassAt;

    @NotBlank(message = "{notEmpty}")
    @DateFormat
    private String endClassAt;

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setStartClassAt(String startClassAt) {
        this.startClassAt = startClassAt;
    }

    public LocalDate getStartClassAt() {
        if (startClassAt == null) return null;
        return LocalDate.parse(startClassAt, DateTimeFormatter.ofPattern(Constants.DATE_FORMAT));
    }

    public void setEndClassAt(String endClassAt) {
        this.endClassAt = endClassAt;
    }

    public LocalDate getEndClassAt() {
        if (endClassAt == null) return null;
        return LocalDate.parse(endClassAt, DateTimeFormatter.ofPattern(Constants.DATE_FORMAT));
    }
}
