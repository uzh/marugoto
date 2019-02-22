package ch.uzh.marugoto.core.data.entity.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

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
    public void setStartClassAt(String startClassAt) {
        this.startClassAt = startClassAt;
    }

    @Override
    public void setEndClassAt(String endClassAt) {
        this.endClassAt = endClassAt;
    }
}
