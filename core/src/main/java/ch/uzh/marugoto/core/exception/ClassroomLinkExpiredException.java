package ch.uzh.marugoto.core.exception;

public class ClassroomLinkExpiredException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public ClassroomLinkExpiredException() {
        super();
    }
	
	public ClassroomLinkExpiredException(String message) {
        super(message);
    }

}
