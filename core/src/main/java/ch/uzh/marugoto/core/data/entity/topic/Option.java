package ch.uzh.marugoto.core.data.entity.topic;

/**
 * Option entity for Exercises
 */

public class Option {
	
	private String text;
	private boolean correctOption;
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isCorrectOption() {
		return correctOption;
	}

	public void setCorrectOption(boolean correctOption) {
		this.correctOption = correctOption;
	}

	public Option () {
		super();
	}
	public Option(String text) {
		super();
		this.text = text;
	}
	
	public Option(boolean correctOption) {
		super();
		this.correctOption = correctOption;
	}
	
	@Override
    public boolean equals (Object o) {
		boolean equals = false;

		if (o instanceof Option) {
			Option option = (Option) o;
			equals = option.getText().equals(text);
		}

		return equals;
	}
}
