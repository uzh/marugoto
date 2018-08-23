package ch.uzh.marugoto.core.helper;

public class EntityHelper {
	
	/**
	 * ArangoDB creates IDs including the collection identifier.
	 * 
	 * Example:
	 *     page/393284
	 *     user/893249823
	 *     
	 * For URLs, the slash is problematic, therefore we only use the
	 * numeric part of the ID. This helper function extracts the numeric
	 * part of the ID.
	 */
	public static String getNumericId(String fullId) {
		if (fullId == null)
			return null;
		
		var splitted = fullId.split("/");
		if (splitted.length == 2)
			return splitted[1];
		
		return fullId;
	}
}
