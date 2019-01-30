import tools.ECDSA;
import tools.RandomNum;
import tools.SHA256;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

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
//		for(int i=0;i<100;i++) {
//			String date = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date(RandomNum.getRandomInt(1483200000000L, 1546271999999L)));
//			System.out.println(date);
//		}
//		for(int i=0;i<10;i++) {
//			Transaction trans = new Transaction();
//			System.out.println(trans.toString());
//		}
//		Pools p=new Pools();
//		p.generateRandomPublicKeys(100);
//		for(Entry<String, String> entry : p.puks.entrySet()) {
//			System.out.println(entry.getKey()+":"+entry.getValue());
//		}
//		HashPointer hp = new HashPointer();
//		hp.hash = "abc";
//		String kkk = hp.gettest();
//		kkk += "123";
//		System.out.println(hp.hash);

//		Create 100 organizations with public keys.
		pools.generateRandomPublicKeys(100);
//		Generate DAs during the first 3 months beginning at 2017/01/01.(1483200000000L, 1490975999999L)
		generateDAs(1483200000000L, 1490975999999L);
//		Generate blocks from 2017/04/01 to 2018/12/31.(1490976000000L, 1546271999999L)
		generateBlocks(1490976000000L, 1546271999999L);
//		Generate block Merkle tree.
		generateBlockMerkleTree();
//		Print generating results information in report.txt.
		printInfo();
//		Print generating results information in separate txt files.
		exportDataSets();
	}

	/***
	 * Generate data assets during the period between begin and end.
	 * 
	 * @param begin the begin timestamp.
	 * @param end   the end timestamp.
	 */
	private static void generateDAs(long begin, long end) {
		for (Entry<String, String> entry : pools.puks.entrySet()) {
			for (long i = 0; i < RandomNum.getRandomInt(5L, 10L); i++) {
				// Create DR
				HashPointer drid = new HashPointer();
				do {
					String dridHashStr = SHA256.getSHA256Str(String.valueOf(RandomNum.getRandomInt(10000L, 20000L)));
					drid.hash = dridHashStr;
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
	 */
	private static void generateBlocks(long begin, long end) {
		for(Entry<HashPointer, Transaction> entry : pools.das.entrySet()) {
			long bts = begin;
			long ets = end;
			for (long i=0; i<RandomNum.getRandomInt(10L, 20L); i++) {
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
//		Generat Merkle tree.
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
