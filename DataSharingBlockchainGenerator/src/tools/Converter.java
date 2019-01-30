package tools;

/**
 * Format converter.
 * @author lyonwong
 *
 */
public class Converter {
	/**
	 * Convert byte to Hex.
	 * @param bytes the array of bytes.
	 * @return string in Hex form.
	 */
	public static String byte2Hex(byte[] bytes) {
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			String temp = Integer.toHexString(bytes[i] & 0xFF);
			if (temp.length() == 1) {
				stringBuffer.append("0");
			}
			stringBuffer.append(temp);
		}
		return stringBuffer.toString();
	}
}
