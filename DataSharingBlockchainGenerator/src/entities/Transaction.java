package entities;

/**
 * Transaction of data sharing process.
 * @author lyonwong
 *
 */
public class Transaction {
	/**
	 * HashPointer refers to previous transaction.
	 */
	public HashPointer hp;
	/**
	 * Owner's public key hash string.
	 */
	public String opuk;
	/**
	 * Sharer's public key hash string.
	 */
	public String spuk;
	/**
	 * Unix timestamp at the millisecond level.
	 */
	public long timestamp;
	/**
	 * Constructor
	 * @param hp HashPointer refers to previous transaction.
	 * @param opuk Owner's public key hash string.
	 * @param spuk sharer's public key hash string.
	 * @param timestamp Unix timestamp at the millisecond level (a 13-bit integer).
	 */
	public Transaction(HashPointer hp, String opuk, String spuk, long timestamp) {
		this.hp = hp;
		this.opuk = opuk;
		this.spuk = spuk;
		this.timestamp = timestamp;
	}
	/***
	 * Constructor.
	 */
	public Transaction() {};	
}
