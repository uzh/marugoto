package ch.uzh.marugoto.core.data.entity.dto;

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

    @Override
    public void setStartClassAt(String startClassAt) {
        this.startClassAt = startClassAt;
    }

    @Override
    public void setEndClassAt(String endClassAt) {
        this.endClassAt = endClassAt;
    }
}
