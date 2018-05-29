package kr.co.moda.sample;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ModaChain {

	public static List<Block> blockChain = new ArrayList<Block>();
	public static int difficulty = 5;

	public static void main(String[] args) throws JsonProcessingException {

		ModaChain sample = new ModaChain();

		// sample.printHashBlock();

		// sample.printBlocktoJson();

		sample.printMineBlockAndValid();

	}

	public void printHash() {
		Block genesisBlock = new Block("Hi im the first block", "0");
		System.out.println("Hash for block 1 : " + genesisBlock.hash);

		Block secondBlock = new Block("Yo im the second block", genesisBlock.hash);
		System.out.println("Hash for block 2 : " + secondBlock.hash);

		Block thirdBlock = new Block("Hey im the third block", secondBlock.hash);
		System.out.println("Hash for block 3 : " + thirdBlock.hash);
	}

	public void printBlocktoJson() throws JsonProcessingException {
		blockChain.add(new Block("Hi im the first block", "0"));
		blockChain.add(new Block("Yo im the second block", blockChain.get(blockChain.size() - 1).hash));
		blockChain.add(new Block("Hey im the third block", blockChain.get(blockChain.size() - 1).hash));

		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(blockChain);

		System.out.println(json);
	}

	public void printMineBlockAndValid() throws JsonProcessingException {
		blockChain.add(new Block("Hi im the first block", "0"));
		System.out.println("Trying to Mine block 1... ");
		blockChain.get(0).mineBlock(difficulty);

		blockChain.add(new Block("Yo im the second block", blockChain.get(blockChain.size() - 1).hash));
		System.out.println("Trying to Mine block 2... ");
		blockChain.get(1).mineBlock(difficulty);

		blockChain.add(new Block("Hey im the third block", blockChain.get(blockChain.size() - 1).hash));
		System.out.println("Trying to Mine block 3... ");
		blockChain.get(2).mineBlock(difficulty);

		System.out.println("\nBlockchain is Valid: " + isChainValid());

		ObjectMapper mapper = new ObjectMapper();

		String blockchainJson = mapper.writeValueAsString(blockChain);
		System.out.println("\nThe block chain: ");
		System.out.println(blockchainJson);
	}

	public static Boolean isChainValid() {
		Block currentBlock;
		Block previousBlock;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');

		for (int i = 1; i < blockChain.size(); i++) {
			currentBlock = blockChain.get(i);
			previousBlock = blockChain.get(i - 1);

			if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
				System.out.println("Current Hashes not equal");
				return false;
			}
			if (!previousBlock.hash.equals(currentBlock.previousHash)) {
				System.out.println("Previous Hashes not equal");
				return false;
			}
			if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
				System.out.println("This block hasn't been mined");
				return false;
			}
		}
		return true;
	}

}
