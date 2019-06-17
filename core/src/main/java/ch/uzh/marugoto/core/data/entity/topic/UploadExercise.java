package ch.uzh.marugoto.core.data.entity.topic;

public class UploadExercise extends Exercise {

	private boolean isMandatory;
	
	public boolean isMandatory() {
		return isMandatory;
	}
	
	public void setMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}
}
