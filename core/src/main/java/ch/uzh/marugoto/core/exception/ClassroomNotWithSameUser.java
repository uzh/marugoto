package ch.uzh.marugoto.core.exception;

public class ClassroomNotWithSameUser extends Exception{

	private static final long serialVersionUID = 1L;
	
	public ClassroomNotWithSameUser() {
        super();
    }
	
	public ClassroomNotWithSameUser(String message) {
        super(message);
    }

}
