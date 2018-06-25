package com.younggam.byeon.sample2;

import java.util.ArrayList;
import java.util.Date;

/**
 * 머클트리 : 거래가 몇 천개든 뭉쳐서 요약된 용량은 32바이트로 항상 같다. 거래 내역을 위조 방지, 모든 블록체인을 다운받는
 * 풀노드(full node), 데이터 일부만 다운받는 라이트노드(light node)로 쉽고 빠르게 특정 거래를 찾도록 해준다.
 * 
 * 
 */
public class Block {

	public String hash;
	public String previousHash;
	public long timeStamp;
	public int nonce;

	public String merkleRoot;

	// 모든 거래
	public ArrayList<Transaction> transactions = new ArrayList<Transaction>();

	public Block(String previousHash) {
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();

		this.hash = calculateHash();
	}

	public String calculateHash() {
		String calculatedhash = StringUtil
				.applySha256(previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + merkleRoot);

		return calculatedhash;
	}

	public void mineBlock(int difficulty) {
		merkleRoot = StringUtil.getMerkleRoot(transactions);
		String target = StringUtil.getDificultyString(difficulty);

		while (!hash.substring(0, difficulty).equals(target)) {
			nonce++;
			hash = calculateHash();
		}

		System.out.println("Block Mined!!! : " + hash);
	}

	public boolean addTransaction(Transaction transaction) {
		if (transaction == null)
			return false;

		if ((!"0".equals(previousHash))) {
			if (!transaction.processTransaction()) {
				System.out.println("Transaction failed to process.");
				return false;
			}
		}

		transactions.add(transaction);
		System.out.println("Transaction Successfully added to Block");

		return true;
	}

}
