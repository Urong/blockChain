package com.younggam.byeon.sample2;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Unspend Transaction Outputs(아직 사용하지 않은 아웃풋들) 과 spent Transaction Outputs(이미
 * 사용된 아웃풋들)
 * 
 * prime192v1: NIST/X9.62/SECG curve over a 192 bit prime field
 * 
 * 개인키 : 무작위 숫자
 * 
 * 공개키 : 개인키 + 타원 곡선 암호법
 * 
 * 비트코인 주소 : 공개키 + SHA-256
 * 
 */
public class Wallet {

	public PrivateKey privateKey;
	public PublicKey publicKey;

	// 아직 사용하지 않은 아웃풋들의 집합
	public HashMap<String, TransactionOutput> walletUTXOs = new HashMap<String, TransactionOutput>();

	public Wallet() {
		generateKeyPair();
	}

	public void generateKeyPair() {
		try {
			// 바운시 캐슬의 타원 곡선 표준 알고리즘(ECDSA)
			// 무작위 숫자
			// 타원 곡선 세부 알고리즘
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");

			// 초기화 및 키 페어 생성
			keyGen.initialize(ecSpec, random);
			KeyPair keyPair = keyGen.generateKeyPair();

			// 키 쌍에서 개인키와 공개키를 추출
			privateKey = keyPair.getPrivate();
			publicKey = keyPair.getPublic();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public float getBalance() {
		float total = 0;

		for (Map.Entry<String, TransactionOutput> item : BlockChainMain.mainUTXOs.entrySet()) {
			// 아직 사용하기 전 잔액
			TransactionOutput mianUTXO = item.getValue();

			// 아웃풋에 사용되지 않은 나의 것이 있는지
			if (mianUTXO.isMine(publicKey)) {

				// 있다면 나의 지갑 사용하지 않은 아웃풋에 추가
				walletUTXOs.put(mianUTXO.id, mianUTXO);
				total += mianUTXO.value;
			}
		}

		return total;
	}

	public Transaction sendFunds(PublicKey _recipient, float value) {

		// 기본금 확인 => 블록체인에 가지고있는 잔액을 개인 지갑으로 옮기는 과정
		if (getBalance() < value) {
			System.out.println("#Not Enough funds to send transaction. Transaction Discarded.");

			return null;
		}

		float total = 0;
		ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();

		for (Map.Entry<String, TransactionOutput> item : walletUTXOs.entrySet()) {
			// 사용하지 않은 개인지갑 아웃풋을 확인
			TransactionOutput walletUTXO = item.getValue();

			total += walletUTXO.value;
			inputs.add(new TransactionInput(walletUTXO.id));

			if (total > value)
				break;
		}

		Transaction newTransaction = new Transaction(publicKey, _recipient, value, inputs);

		// 본인 지갑의 개인키로 서명을 만든다
		newTransaction.generateSignature(privateKey);

		// 사용하지 않은 아웃풋 리스트에서 사용한 아웃풋을 제거
		for (TransactionInput input : inputs) {
			walletUTXOs.remove(input.transactionOutputId);
		}

		return newTransaction;
	}

}
