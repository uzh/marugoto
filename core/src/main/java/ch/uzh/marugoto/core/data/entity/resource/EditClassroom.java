package ch.uzh.marugoto.core.data.entity.resource;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import ch.uzh.marugoto.core.Constants;
import ch.uzh.marugoto.core.data.validation.DateFormat;

/**
 * Edit existing classroom request class
 * Should contain only validated fields for editing classroom
 */
public class EditClassroom extends ClassroomRequest {

    @DateFormat
    private String startClassAt;

    @DateFormat
    private String endClassAt;

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
