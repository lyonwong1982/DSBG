package tools;

import java.security.MessageDigest;

/**
 * Tool for creating SHA256 digest.
 * 
 * @author lyonwong
 *
 */
public class SHA256 {
	/**
	 * Return a digest of a string.
	 * 
	 * @param str the original.
	 * @return cipher of the original.
	 */
	public static String getSHA256Str(String str) {
		MessageDigest messageDigest;
		String encodeStr = "";
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(str.getBytes("UTF-8"));
			encodeStr = Converter.byte2Hex(messageDigest.digest());
		} 
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return encodeStr;
	}
	
	public static String getSHA256Bytes(byte[] b) {
		MessageDigest messageDigest;
		String encodeStr = "";
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(b);
			encodeStr = Converter.byte2Hex(messageDigest.digest());
		} 
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return encodeStr;
	}
}
