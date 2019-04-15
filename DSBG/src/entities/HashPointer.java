package entities;

import tools.SHA256;

/**
 * HashPointer
 * @author lyonwong
 *
 */
public class HashPointer {
	/**
	 * The variable refers to the left child node.
	 */
	public HashPointer leftChild;
	
	/**
	 * The variable refers to the right child node.
	 */
	public HashPointer rightChild;
	
	/**
	 * The variable refers to the previous object.
	 * When use this attribute the exact type of the object should be assigned. 
	 */
	public Object prev;
	
	/**
	 * The string of previous hash in hex form.
	 */
	public String hash;
	
	/***
	 * Constructor.
	 * @param leftChild left child node of this HashPointer.
	 * @param rightChild right child node of this HashPointer.
	 */
	public HashPointer(HashPointer leftChild, HashPointer rightChild) {
		this.leftChild = leftChild;
		this.rightChild = rightChild;
//		this.hash = SHA256.getSHA256Str(SHA256.getSHA256Str(this.leftChild.toString())+SHA256.getSHA256Str(this.rightChild.toString()));
		this.hash = SHA256.getSHA256Str(this.leftChild.hash+this.rightChild.hash);
	}
	/***
	 * Constructor.
	 * @param prev previous object.
	 */
	public HashPointer(Object prev) {
		this.prev = prev;
		this.hash = SHA256.getSHA256Str(this.prev.toString());
	}
	/***
	 * Constructor
	 * @param hash a given hash with no previous object.
	 */
	public HashPointer(String hash) {
		this.hash = hash;
	}
	/***
	 * Constructor.
	 */
	public HashPointer() {}
	
	public static void main(String[] str) {
		System.out.println(System.nanoTime());
		System.out.println(System.currentTimeMillis());
		System.out.println(3/2);
	}
}
