package entities;
/***
 * Block for data sharing process.
 * @author lyonwong
 *
 */
public class Block {
	/***
	 * HashPointer refers to the last block.
	 */
	public HashPointer bhp;
	/***
	 * HashPointer refers to the transaction in this block.
	 */
	public HashPointer thp;
	
	/***
	 * Constructor.
	 * @param bhp HashPointer refers to the last block.
	 * @param thp HashPointer refers to the transaction in this block.
	 */
	public Block(HashPointer bhp, HashPointer thp) {
		this.bhp = bhp;
		this.thp = thp;
	}
	/***
	 * Constructor.
	 */
	public Block() {}
}
