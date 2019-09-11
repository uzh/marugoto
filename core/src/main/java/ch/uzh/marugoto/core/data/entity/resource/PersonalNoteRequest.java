package ch.uzh.marugoto.core.data.entity.resource;

public class PersonalNoteRequest implements RequestDto {
    private String note;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
