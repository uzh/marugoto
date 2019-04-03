package ch.uzh.marugoto.core.data.entity.dto;

public class UpdateMailState implements RequestDto {
    private String replyText;

    public String getReplyText() {
        return replyText;
    }

    public void setReplyText(String replyText) {
        this.replyText = replyText;
    }
}
