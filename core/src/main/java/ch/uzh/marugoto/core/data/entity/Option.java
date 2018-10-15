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
    	Option option = (Option) o;
        if (option.getText().equals(text)) 
        	return true;
        return false;
    }
}
