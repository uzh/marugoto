package ch.uzh.marugoto.core.data.entity;

import java.util.List;

public class UploadExercise extends Exercise {

	private boolean isMandatory;
	private boolean isDelivery;
	private List<String> allowedFileTypes;
	
	public boolean isMandatory() {
		return isMandatory;
	}
	
	public boolean isDelivery() {
		return isDelivery;
	}
	
	public List<String> getAllowedFileTypes() {
		return allowedFileTypes;
	}
	
	public void setMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}
	
	public void setDelivery(boolean isDelivery) {
		this.isDelivery = isDelivery;
	}
	
	public void setAllowedFileTypes(List<String> allowedFileTypes) {
		this.allowedFileTypes = allowedFileTypes;
	}
	
	public UploadExercise (boolean isMandatory, boolean isDelivery, List<String>allowedFileTypes) {
		this.isMandatory = isMandatory;
		this.isDelivery = isDelivery;
		this.allowedFileTypes = allowedFileTypes;
	}
}
