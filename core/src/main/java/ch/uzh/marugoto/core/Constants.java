package ch.uzh.marugoto.core;

public class Constants {
    public static final String DATE_FORMAT = "dd.MM.yyyy";
    public static final String DATE_FORMAT_WITH_TIME = DATE_FORMAT.concat(" - hh:mm");
    public static final String GENERATED_UPLOAD_DIRECTORY = "uploads";
	public static final String USER_HOME_DIRECTORY = "user.home";
    public static final String[] RESOURCE_TYPES = new String[]{ "image", "audio", "video", "pdf"};
    public static final String PDF_EXTENSION = ".pdf";
    public static final String ZIP_EXTENSION = ".zip";
	public static final String EMPTY_STRING = "";
	public static final String NOTIFICATION_FIRST_NAME_PLACEHOLDER = "{{user.firstName}}";
    public static final String NOTIFICATION_TITLE_PLACEHOLDER = "{{user.salutation}}";
    public static final String NOTIFICATION_GENDER_PLACEHOLDER = "{{user.gender}}";

    public static final int IMAGE_WIDTH_COLUMN_12 = 1360;
    public static final int IMAGE_WIDTH_COLUMN_10 = 1098;
    public static final int IMAGE_WIDTH_COLUMN_6 = 530;
    public static final int IMAGE_WIDTH_COLUMN_5 = 427;
    public static final int IMAGE_WIDTH_COLUMN_4 = 333;
    public static final int IMAGE_WIDTH_COLUMN_3 = 292;
    public static final int IMAGE_WIDTH_COLUMN_1 = 120;
    public static final int THUMBNAIL_WIDTH = IMAGE_WIDTH_COLUMN_4;

    public static final int TEXT_EXERCISE_PASSED_SCORE = 90;
    public static final int TEXT_EXERCISE_FULLY_MATCHED_SCORE = 0;
}
