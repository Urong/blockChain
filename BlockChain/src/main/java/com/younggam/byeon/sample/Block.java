package com.younggam.byeon.sample;

import java.util.Date;

/**
 * 해시값을 찾는 시간이 항상 10분 정도를 유지하게 한다. 난이도가 높아질수록 블록을 위조하기는 더 힘들어지기 때문에 블록체인은 더
 * 안전해진다.
 * 
 * 1비트씩 변질 => 앞쪽에 0이 쌓여있는 Hash값이 발견 => 채굴 성공
 */
public class Block {

	public String hash;
	public String previousHash;
	public String data;
	public long timeStamp;
	public int nonce;

	public Block(String data, String previousHash) {
		this.data = data;
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();

		this.hash = calculateHash();
	}

	public String calculateHash() {
		return StringUtil.applySha256(previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + data);
	}

	public void mineBlock(int difficulty) {
		String target = new String(new char[difficulty]).replace('\0', '0');
		while (!hash.substring(0, difficulty).equals(target)) {
			nonce++;

			hash = calculateHash();
		}

		System.out.println("Block Mined!!! : " + hash);
	}

}
