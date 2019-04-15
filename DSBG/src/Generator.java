import tools.ECDSA;
import tools.RandomNum;
import tools.SHA256;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Scanner;

import entities.HashPointer;
import entities.Pools;
import entities.Transaction;
import entities.Block;

/**
 * A generator that generates the entire blockchain for data sharing
 * transactions.
 * 
 * @author lyonwong
 *
 */
public class Generator {
	private static Pools pools = new Pools();
	public static void main(String[] str) {
//		Create 100 organizations with public keys.
		pools.generateRandomPublicKeys(100);
		
//		Generate DAs during the first 3 months beginning at 2017/01/01.(1483200000000L, 1490975999999L)
		generateDAs(1483200000000L, 1490975999999L, 80L, 80L);
//		Generate blocks from 2017/04/01 to 2018/12/31.(1490976000000L, 1546271999999L)
		generateBlocks(1490976000000L, 1546271999999L, 80L, 80L);
		
//		Test transaction verification.
		/* Remove this comment when you want to do this test.
		System.out.println("Now testing DB-TV");
		String timeconsume = "";
		for(int k=0; k<10; k++) {
//			Generate DAs during the first 3 months beginning at 2017/01/01.(1483200000000L, 1490975999999L)
			generateDAs(1483200000000L, 1490975999999L, 5L, 5L);
//			Generate blocks from 2017/04/01 to 2018/12/31.(1490976000000L, 1546271999999L)
			generateBlocks(1490976000000L, 1546271999999L, 80L, 80L);
			int count = 0;
			long exceptime = 0;
//			long runningTime = System.currentTimeMillis();
			for(int i=0; i<pools.trans.size(); i++) {
				System.out.println("Round " + (k+1) +" Checking transaction: " + (i+1) + "/" + pools.trans.size() +" ...");
//				Get rid of TDREs.
				if (pools.trans.get(i).hp.prev == null) {
					continue;
				}
//				Get opuk.
				String opuk = pools.trans.get(i).opuk;
//				Get drid.
				String drid = "";
				HashPointer hp = pools.trans.get(i).hp;
				while (hp.prev != null) {
					hp = ((Transaction) hp.prev).hp;
				}
				drid = hp.hash;
				hp = pools.trans.get(i).hp;
//				Do verification.
				ArrayList<String> als = new ArrayList<String>();
				for(int j=pools.blocks.size()-1; j>=0; j--) {
					HashPointer thp = pools.blocks.get(j).thp;
					long exceptimeS = System.nanoTime();
					if(als.contains(thp.hash)) {
						als.remove(als.indexOf(thp.hash));
						continue;
					}
					if(hp.hash.equals(thp.hash)) {
						if(opuk.equals(((Transaction)thp.prev).opuk)){
							count++;
							break; //Pass, but will not be reached in test.
						}
						else {
							count++;
							break; //No pass, but will not be reached in test.
						}
					}
					if(!opuk.equals(((Transaction)thp.prev).opuk)){
//						while(((Transaction)thp.prev).hp.prev != null) {
//							als.add(((Transaction)thp.prev).hp.hash);
//							thp = ((Transaction)thp.prev).hp;
//						}
						continue;
					}
					boolean isChecked = false;
					while(thp.prev != null) {
						if(((Transaction)thp.prev).hp.hash.equals(hp.hash)) {
							isChecked = true;
							break;
						}
						if(((Transaction)thp.prev).hp.hash.equals(drid)) {
							isChecked = true; //Will not be reached in test.
							break;
						}
						if(((Transaction)thp.prev).hp.prev != null) {
							als.add(((Transaction)thp.prev).hp.hash);
						}
						thp = ((Transaction)thp.prev).hp;
					}
					if(isChecked) {
						count++;
						break;
					}
					exceptime += (System.nanoTime() - exceptimeS);
				}
			}
			timeconsume += (exceptime + " ");
			System.out.println(" Checked transactions: "+count);
		}
		System.out.println(timeconsume);
		*/
		
//		Test data assets tracing.
		/* Remove this comment when you want to do this test.
		System.out.println("Now testing DB-DAT");
//		Generate DAs during the first 3 months beginning at 2017/01/01.(1483200000000L, 1490975999999L)
		generateDAs(1483200000000L, 1490975999999L, 80L, 80L);
//		Generate blocks from 2017/04/01 to 2018/12/31.(1490976000000L, 1546271999999L)
		generateBlocks(1490976000000L, 1546271999999L, 5L, 5L);
		for(int k=0; k<20; k++) {
			ArrayList<String> alsT = new ArrayList<String>();
//			ArrayList<String> das =new ArrayList<String>();
			HashMap<HashPointer,Transaction> das = new HashMap<HashPointer, Transaction>();
//			Randomly fetch 10 DAs.
			int[] rand = RandomNum.getRandomIntSet(1, pools.das.size()-1, 10);
			for(int i=0; i<rand.length; i++) {
//				Iterator<HashPointer> iter = pools.das.keySet().iterator();
				Iterator<Entry<HashPointer,Transaction>> iter = pools.das.entrySet().iterator();
				for(int j=0; j<rand[i]; j++) {
					iter.next();
				}
				Entry<HashPointer, Transaction> etemp = iter.next();
				das.put(etemp.getKey(), etemp.getValue());
			}
//			Tracing the fetched 10 DAs.
//			int count1 = 0;
//			int count2 = 0;
			long runningTime = new Date().getTime();
			for(Entry<HashPointer,Transaction> entry: das.entrySet()) {
				boolean isFound = false;
				alsT.clear();
				for(int i=pools.blocks.size()-1; i>=0; i--) {
					if(alsT.contains(pools.blocks.get(i).thp.hash)) {
						alsT.remove(alsT.indexOf(pools.blocks.get(i).thp.hash));
//						count2 ++;
						continue;
					}
					HashPointer hp = pools.blocks.get(i).thp;
					if(!((Transaction)hp.prev).opuk.equals(entry.getValue().opuk)) {
//						count1 ++;
						continue;
					}
					while(hp.prev != null) {
						alsT.add(hp.hash);
						if(((Transaction) hp.prev).hp.hash.equals(entry.getKey().hash)) {
							isFound = true;
							break;
						}
						hp = ((Transaction)hp.prev).hp;
					}
					if (isFound) {
						break;
					}
				}
			}
//			for(int j=0; j<das.size(); j++) {
//				boolean isFound = false;
//				alsT.clear();
//				for(int i=pools.blocks.size()-1; i>=0; i--) {
//					if(alsT.contains(pools.blocks.get(i).thp.hash)) {
//						alsT.remove(alsT.indexOf(pools.blocks.get(i).thp.hash));
//						continue;
//					}
//					HashPointer hp = pools.blocks.get(i).thp;
//					while(hp.prev != null) {
//						alsT.add(hp.hash);
//						if(((Transaction) hp.prev).hp.hash.equals(das.get(j))) {
//							isFound = true;
//							break;
//						}
//						hp = ((Transaction)hp.prev).hp;
//					}
//					if (isFound) {
//						break;
//					}
//				}
//			}
			System.out.print((new Date().getTime() - runningTime) + " ");
//			System.out.println("\n" + count1 + ":" + count2);
		}
		*/
		
//		Generate block Merkle tree. Enable this when you want to do Test transactions detection.
		generateBlockMerkleTree();
		
//		Print generating results information in console.
		System.out.println("\n***************************************************");
		System.out.println(pools.trans.size()+" transactions.");
		System.out.println(pools.das.size()+" data assets.");
		System.out.println(pools.blocks.size()+" blocks.");
		System.out.println(pools.blockMerkleTree.size()+" levels in block Merkle tree.");
		System.out.println((int)(Math.pow(2, pools.blockMerkleTree.size())-1)+" nodes in block Merkle tree.");
		System.out.println("***************************************************");
		
//		Print generating results information in report.txt.
//		printInfo();
		
//		Print generating results information in separate txt files.
//		exportDataSets();		
		
//		Test transactions detection.
		/* Remove this comment when you want to do this test.*/
		System.out.println("\nNow testing DB-TD");
		int perc = 1;
		String fault = SHA256.getSHA256Str("fault");
		Scanner sc = new Scanner(System.in);
		while (true) {
			System.out.print("\nInput the percentage of bad blocks (%): ");
			perc = sc.nextInt();
			if (perc == 0) {
				break;
			}
			for(int k=0; k<20; k++) {
//				Clone a seudo Merkle tree.
				ArrayList<ArrayList<String>> alals = pools.cloneSeudoMerkleTree();
//				Make bad blocks randomly.
				int[] rand = RandomNum.getRandomIntSet(0, pools.blocks.size()-1, 1);
//				int[] rand = RandomNum.getRandomIntSet(0, pools.blocks.size()-1, pools.blocks.size()*perc/10000);
				for (int i=0; i<rand.length; i++) {
					alals.get(0).set(rand[i], fault);
				}
//				Reconstruct the seudo Merkle tree.
				for (int i=0; i<alals.size()-1; i++) {
					for (int j=0; j<alals.get(i).size(); j=j+2) {
						alals.get(i+1).set((j+1)/2, SHA256.getSHA256Str(alals.get(i).get(j)+alals.get(i).get(j+1)));
					}
				}
//				Compare with the original Merkle tree.
				int bads = 0;
				ArrayList<Integer> hashIndex = new ArrayList<Integer>();
				hashIndex.add(new Integer(0));
				long runningTime = System.nanoTime();
				for (int i=alals.size()-1; i>=0; i--) {
					ArrayList<Integer> index = new ArrayList<Integer>();
					for (int j=0; j<hashIndex.size(); j++) {
//						System.out.println(alals.get(i).get(hashIndex.get(j).intValue()));
//						System.out.println(pools.blockMerkleTree.get(i).get(hashIndex.get(j).intValue()).hash);
						if(!alals.get(i).get(hashIndex.get(j).intValue()).equals(pools.blockMerkleTree.get(i).get(hashIndex.get(j).intValue()).hash)) {
							index.add(new Integer(2*hashIndex.get(j).intValue()));
							index.add(new Integer(2*hashIndex.get(j).intValue()+1));
						}
					}
					hashIndex.clear();
					hashIndex = index;
					bads = hashIndex.size() / 2;
					if(hashIndex.size() == 0) {
						break;
					}
				}
				System.out.print((System.nanoTime() - runningTime)+" ");
//				System.out.println("\n"+bads+" of "+rand.length+" bad blocks have been found!");
			}
		}
		sc.close();
		/**/
	}

	/***
	 * Generate data assets during the period between begin and end.
	 * 
	 * @param begin the begin timestamp.
	 * @param end   the end timestamp.
	 * @param minDRs the minimum of data resources.
	 * @param maxDRs the maximum of data resources.
	 */
	private static void generateDAs(long begin, long end, long minDRs, long maxDRs) {
		//Clear all pools except puks.
		pools.clearAllExPuk();
		int count = 0;
		for (Entry<String, String> entry : pools.puks.entrySet()) {
			System.out.println("generating DAs: " + ++count + "/" + pools.puks.entrySet().size());
			for (long i = 0; i < RandomNum.getRandomInt(minDRs, maxDRs); i++) {//
				// Create DR
				HashPointer drid = new HashPointer();
				do {
					drid.hash = SHA256.getSHA256Str(String.valueOf(RandomNum.getRandomInt(10000L, 20000L)));
				} while (pools.checkRepetitionDR(drid.hash));
				// Get timestamp
				long timestamp;
				do {
					timestamp = RandomNum.getRandomInt(begin, end);
				} while (pools.checkRepetitionTimestamp(timestamp));
				// Create TDRE
				Transaction tran = new Transaction(drid, entry.getValue(), "1", timestamp);
				pools.trans.add(tran);
				// Create DA
				pools.das.put(drid, tran);
				// Add timestamp
				pools.timestamp.add(timestamp);
				// Create block
				Block block = new Block();
				block.bhp = new HashPointer();
				block.thp = new HashPointer();
				if (pools.blocks.size() == 0) {
					block.bhp.hash = SHA256.getSHA256Str("Genesis");
				} else {
					Block prev = pools.blocks.get(pools.blocks.size() - 1);
					block.bhp.hash = SHA256.getSHA256Str(prev.toString());
					block.bhp.prev = prev;
				}
				block.thp.hash = SHA256.getSHA256Str(tran.toString());
				block.thp.prev = tran;
				pools.blocks.add(block);
			}
		}
	}
	/***
	 * Generate blocks during the period between begin and end.
	 * 
	 * @param begin the begin timestamp.
	 * @param end   the end timestamp.
	 * @param minOrgs the minimum of organizations.
	 * @param maxOrgs the maximum of organizations.
	 */
	private static void generateBlocks(long begin, long end, long minOrgs, long maxOrgs) {
		int count = 0;
		for(Entry<HashPointer, Transaction> entry : pools.das.entrySet()) {
			System.out.println("generating Blocks: " + ++count + "/" + pools.das.entrySet().size());
			long bts = begin;
			long ets = end;
			for (long i=0; i<RandomNum.getRandomInt(minOrgs, maxOrgs); i++) {
//				Randomly get a spuk which must not be the opuk and not in the spuks of the DA.
				String spuk = "";
				while(true) {
					long rand = RandomNum.getRandomInt(1, pools.puks.size());
					spuk = pools.puks.get("org"+rand);
					Transaction tran = entry.getValue();
					boolean exist = false;
					while(!tran.spuk.equals("1")) {
						if(tran.spuk.equals(spuk)) {
							exist =true;
							break;
						}
						tran = (Transaction) tran.hp.prev;
					}
					if(exist) {
						continue;
					}
					if(!spuk.equals(entry.getValue().opuk)) {
						break;
					}
				}
//				Randomly generate a timestamp which must grater than current transaction and not in pool.
				long timestamp = 0;
				bts = entry.getValue().timestamp;
				if (bts == ets) {
					break;
				}
				long useoff = 0;
				while(useoff < pools.timestamp.size()) {
					timestamp = RandomNum.getRandomInt(bts+1, ets);
					if(pools.checkRepetitionTimestamp(timestamp)) {
						useoff++;
						continue;
					}
					if(timestamp <= (bts + 864000000L)) {
						break;
					}
				}
				if (useoff == pools.timestamp.size()) {
					break;
				}
//				Add timestamp.
				pools.timestamp.add(timestamp);
//				Generate a new transaction.
				HashPointer prev = new HashPointer();
				prev.hash = SHA256.getSHA256Str(entry.getValue().toString());
				prev.prev = entry.getValue();
				Transaction tran = new Transaction(prev, entry.getValue().opuk, spuk, timestamp);
				pools.trans.add(tran);
//				Update DAs pool.
				pools.das.put(entry.getKey(), tran);
//				Generate a new block.
				Block block = new Block();
				block.bhp = new HashPointer();
				block.thp = new HashPointer();
				block.bhp.prev = pools.blocks.get(pools.blocks.size()-1);
				block.bhp.hash = SHA256.getSHA256Str(block.bhp.prev.toString());
				block.thp.prev = tran;
				block.thp.hash = SHA256.getSHA256Str(tran.toString());
				pools.blocks.add(block);
			}
		}
	}
	
	/***
	 * Print generating result information.
	 */
	private static void printInfo() {
		FileWriter fw = null;
		try {
			File f = new File("report.txt");
			if (f.exists()) {
				f.delete();
			}
			f.createNewFile();
			fw = new FileWriter(f, true);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		PrintWriter pw = new PrintWriter(fw);
		pw.println("***************************************************");
		pw.println(pools.trans.size()+" transactions.");
		pw.println(pools.das.size()+" data assets.");
		pw.println(pools.blocks.size()+" blocks.");
		pw.println(pools.blockMerkleTree.size()+" levels in block Merkle tree.");
		pw.println((int)(Math.pow(2, pools.blockMerkleTree.size())-1)+" nodes in block Merkle tree.");
		pw.println("***************************************************");
		pw.println("Transactions:");
		for (int i = 0; i < pools.trans.size(); i++) {
			pw.println("opuk:" + pools.trans.get(i).opuk);
			pw.println("spuk:" + pools.trans.get(i).spuk);
			pw.println("time:" + pools.trans.get(i).timestamp);
			if (pools.trans.get(i).hp.prev != null) {
				pw.println("prev:" + pools.trans.get(i).hp.hash);
			} else {
				pw.println("DRId:" + pools.trans.get(i).hp.hash + "(DR)");
			}
			pw.println("-------------------");
		}
		pw.println("================================");
		pw.println("Data assets:");
		for (Entry<HashPointer, Transaction> entry : pools.das.entrySet()) {
			pw.println("opuk:" + entry.getValue().opuk);
			pw.println("DRId:" + entry.getKey().hash);
			pw.println("-------------------");
		}
		pw.println("================================");
		pw.println("Blocks:");
		for (int i = 0; i < pools.blocks.size(); i++) {
			pw.println("prev:" + pools.blocks.get(i).bhp.hash);
			pw.println("tran:" + pools.blocks.get(i).thp.hash);
			pw.println("-------------------");
		}
		pw.flush();
		try {
			fw.flush();
			pw.close();
			fw.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		System.out.println("Printing generating result information has been done!");
	}
	/***
	 * Export the final data sets.
	 */
	public static void exportDataSets() {
		FileWriter fw = null;
		PrintWriter pw = null;
//		Export transactions to transactions.csv file.
		try {
			File f = new File("transactions.csv");
			if (f.exists()) {
				f.delete();
			}
			f.createNewFile();
			fw = new FileWriter(f, true);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		pw = new PrintWriter(fw);
		pw.println("Timestamp,Owner's public key,Sharer's public key,Hash,Previous Hash");
		for (int i = 0; i < pools.trans.size(); i++) {
			pw.print(pools.trans.get(i).timestamp+",");
			pw.print(pools.trans.get(i).opuk+",");
			pw.print(pools.trans.get(i).spuk+",");
			pw.print(SHA256.getSHA256Str(pools.trans.get(i).toString())+",");
			if (pools.trans.get(i).hp.prev != null) {
				pw.println(pools.trans.get(i).hp.hash);
			} 
			else {
				pw.println(pools.trans.get(i).hp.hash + "(DRId)");
			}
		}
		pw.flush();
		try {
			fw.flush();
			pw.close();
			fw.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
//		Export blocks to blocks.csv file.
		try {
			File f = new File("blocks.csv");
			if (f.exists()) {
				f.delete();
			}
			f.createNewFile();
			fw = new FileWriter(f, true);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		pw = new PrintWriter(fw);
		pw.println("Hash,Previous hash,Transaction hash");
		for (int i = 0; i < pools.blocks.size(); i++) {
			pw.print(SHA256.getSHA256Str(pools.blocks.get(i).toString())+",");
			pw.print(pools.blocks.get(i).bhp.hash+",");
			pw.println(pools.blocks.get(i).thp.hash);
		}
		pw.flush();
		try {
			fw.flush();
			pw.close();
			fw.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		System.out.println("Exporting data sets successfully!");
	}
	/***
	 * Generate block Merkle tree.
	 */
	public static void generateBlockMerkleTree() {
//		Make a copy of block Merkle tree.
		ArrayList<ArrayList<HashPointer>> copyBMT = pools.blockMerkleTree;
		copyBMT.clear();
		ArrayList<HashPointer> alhp = new ArrayList<HashPointer>();
		for (int i=1; i<pools.blocks.size(); i++) {
			alhp.add(pools.blocks.get(i).bhp);
		}
		alhp.add(new HashPointer(pools.blocks.get(pools.blocks.size()-1)));
//		Figure out the amount of 0-blocks.
		double nBlocks = (double) pools.blocks.size();
		int n0Blocks = (int) (Math.pow(2, Math.floor(Math.log(nBlocks) / Math.log(2)) + 1) - nBlocks);
//		Add 0-blocks to copyBMT.
		for (int i=0; i<n0Blocks; i++) {
			alhp.add(new HashPointer(SHA256.getSHA256Str("0")));
		}
		copyBMT.add(alhp);
//		Generate Merkle tree.
		for (int i=0; i<copyBMT.size(); i++) {
			alhp = copyBMT.get(i);
			ArrayList<HashPointer> alhpTemp = new ArrayList<HashPointer>();
			for (int j=0; j<alhp.size(); j=j+2) {
				alhpTemp.add(new HashPointer(alhp.get(j), alhp.get(j+1)));
			}
			if (alhpTemp.size() >= 1) {
				copyBMT.add(alhpTemp);
			}
			if (alhpTemp.size() == 1) {
				break;
			}
		}
	}
}
