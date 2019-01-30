package tools;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.ECPublicKey;

/**
 * ECDSA public key generator.
 * 
 * @author lyonwong
 *
 */
public class ECDSA {
	/**
	 * Get a public key of ECDSA.
	 * @return byte array of public key.
	 */
	public static String getECDSAPublicKeyHash() {
		String h = "";
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
			keyPairGenerator.initialize(256);
			KeyPair keyPair = keyPairGenerator.generateKeyPair();
			ECPublicKey ecPublicKey = (ECPublicKey) keyPair.getPublic();
			h = SHA256.getSHA256Bytes(ecPublicKey.getEncoded());
		} 
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return h;
	}
}
