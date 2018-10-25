package ch.uzh.marugoto.core.data.entity;

/**
 * Option entity for Exercises
 */

public class Option {
	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Option () {
		super();
	}
	
	public Option(String text) {
		super();
		this.text = text;
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
