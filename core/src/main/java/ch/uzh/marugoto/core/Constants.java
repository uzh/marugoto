package ch.uzh.marugoto.core;

public class Constants {
    public static final String DATE_FORMAT = "d.M.yyyy";
    public static final String DATE_FORMAT_WITH_TIME = DATE_FORMAT.concat(" - HH:mm");
    public static final String GENERATED_UPLOAD_DIRECTORY = "uploads";
	public static final String USER_HOME_DIRECTORY = "user.home";
    public static final String PDF_EXTENSION = ".pdf";
    public static final String ZIP_EXTENSION = ".zip";
	public static final String EMPTY_STRING = "";
	public static final String CONTENT_FIRST_NAME_PLACEHOLDER = "{{user.firstName}}";
	public static final String CONTENT_LAST_NAME_PLACEHOLDER = "{{user.lastName}}";
	public static final String SALUTATION_FOR_MALE_GENDER = "Mr.";
	public static final String SALUTATION_FOR_FEMALE_GENDER = "Mrs.";
	
    public static final String INVITATION_LINK_PREFIX = "m-";
    public static final String NOTEBOOK_FILE_NAME_PREFIX = "notebook-";
    public static final String UPLOAD_FILE_NAME_PREFIX = "upload-";

    public static final int INVITATION_LINK_LENGTH = 10;
    public static final int IMAGE_WIDTH_COLUMN_12 = 1360;
    public static final int IMAGE_WIDTH_COLUMN_10 = 1360;
    public static final int IMAGE_WIDTH_COLUMN_6 = 1360;
    public static final int IMAGE_WIDTH_COLUMN_5 = 1360;
    public static final int IMAGE_WIDTH_COLUMN_4 = 1360;
    public static final int IMAGE_WIDTH_COLUMN_3 = 680;
    public static final int IMAGE_WIDTH_COLUMN_1 = 680;
    public static final int THUMBNAIL_WIDTH = IMAGE_WIDTH_COLUMN_4;
    public static final int IMAGE_MAX_COLUMN_WIDTH = 12;

    public static final int TEXT_EXERCISE_PASSED_SCORE = 90;
    public static final int TEXT_EXERCISE_FULLY_MATCHED_SCORE = 0;
}
