package ch.uzh.marugoto.core.data.entity.topic;

/**
 *
 * Base class for all exercises
 * 
 */
abstract public class Exercise extends Component {
	private String descriptionForNotebook;

	public Exercise() {
		super();
	}

	public Exercise(int numberOfColumns) {
		super(numberOfColumns);
	}

	public Exercise(int numberOfColumns, Page page) {
		super(numberOfColumns, page);
	}

	public String getDescriptionForNotebook() {
		return descriptionForNotebook;
	}

	public void setDescriptionForNotebook(String descriptionForNotebook) {
		this.descriptionForNotebook = descriptionForNotebook;
	}
}
