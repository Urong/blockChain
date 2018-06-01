package com.younggam.byeon.sample;

import java.util.Date;

/**
 * 해시값을 찾는 시간이 항상 10분 정도를 유지하게 한다. 난이도가 높아질수록 블록을 위조하기는 더 힘들어지기 때문에 블록체인은 더
 * 안전해진다.
 * 
 */
public class Block {

	public String hash;
	public String previousHash;
	public String data;
	public long timeStamp;

	// 암호 시스템에서 사용하는 일회용 일련번호
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

	// 0의 개수가 하나씩 늘어날 때마다 확률적으로 16배씩 더 많은 nonce 숫자를 대입해 보아야 한다.
	public void mineBlock(int difficulty) {
		String target = new String(new char[difficulty]).replace('\0', '0');
		while (!hash.substring(0, difficulty).equals(target)) {
			// 1비트씩 변질시켜가면서 Hash값을 구해보는것이다.
			// 그렇게 계속 변질시켜보고 Hash값구하고를 반복하다보면 언젠가 앞쪽에 0이 쌓여있는 Hash값이 발견될테고 그럼 채굴에 성공하는 것이다!
			nonce++;

			hash = calculateHash();
		}

		System.out.println("Block Mined!!! : " + hash);
	}

}
