package entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import tools.ECDSA;

/***
 * Pools for storing data sets.
 * @author lyonwong
 *
 */
public class Pools {
	/***
	 * Pool for public keys.
	 */
	public HashMap<String, String> puks;
	/***
	 * Pool for transactions.
	 */
	public ArrayList<Transaction> trans;
	/***
	 * Pool for blocks.
	 */
	public ArrayList<Block> blocks;
	/***
	 * Pool for data assets.
	 */
	public HashMap<HashPointer, Transaction> das;
	/***
	 * Pool for timestamp.
	 */
	public ArrayList<Long> timestamp;
	/***
	 * Pool for block Merkle tree.
	 */
	public ArrayList<ArrayList<HashPointer>> blockMerkleTree;
	/***
	 * Constructor.
	 */
	public Pools() {
		this.puks = new HashMap<String, String>();
		this.trans = new ArrayList<Transaction>();
		this.blocks = new ArrayList<Block>();
		this.das = new HashMap<HashPointer, Transaction>();
		this.timestamp = new ArrayList<Long>();
		this.blockMerkleTree = new ArrayList<ArrayList<HashPointer>>();
	}
	/***
	 * Generate public keys in amount times randomly.
	 * @param amount the amount of public keys.
	 */
	public void generateRandomPublicKeys(int amount) {
		this.puks.clear();
		for(int i=0; i<amount; i++) {
			String puk = ECDSA.getECDSAPublicKeyHash();
			if(puks.containsValue(puk)) {
				i--;
				continue;
			}
			puks.put("org"+(i+1), puk);
		}
	}
	/***
	 * Check repetition for a DR.
	 * @param drid a given drid' hash.
	 * @return true if there is already a same drid.
	 */
	public boolean checkRepetitionDR(String drid) {
		Iterator<HashPointer> iter = this.das.keySet().iterator();
		while(iter.hasNext()) {
			if(drid.equals(iter.next().hash)) {
				return true;
			}
		}
		return false;
	}
	/***
	 * Check repetition for a timestamp.
	 * @param timestamp a given timestamp.
	 * @return true if there is already a same timestamp.
	 */
	public boolean checkRepetitionTimestamp(long timestamp) {
		for(int i=0; i<this.timestamp.size(); i++) {
			if(timestamp == this.timestamp.get(i).longValue()) {
				return true;
			}
		}
		return false;
	}
}
