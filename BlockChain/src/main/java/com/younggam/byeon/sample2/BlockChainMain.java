package com.younggam.byeon.sample2;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 바운시 캐슬 : 방대한 자바 암호화 알고리즘 라이브러리
 * 
 * 개인키 : 무작위 숫자
 * 
 * 공개키 : 개인키 + 타원 곡선 암호법
 * 
 * 비트코인 주소 : 공개키 + SHA-256(16진수로 64바이트)
 * 
 */
public class BlockChainMain {

	public static ArrayList<Block> blockchain = new ArrayList<Block>();

	// 아직 사용하지 않은 아웃풋의 집합
	public static HashMap<String, TransactionOutput> mainUTXOs = new HashMap<String, TransactionOutput>();
	public static int difficulty = 3;

	// 최소 금액
	public static float minimumTransaction = 0.1f;

	// 테스트 지갑
	public static Wallet walletA;
	public static Wallet walletB;
	public static Wallet walletC;
	public static Wallet walletD;
	public static Wallet walletE;

	// 최초의 트랜잭션
	public static Transaction genesisTransaction;

	public static void main(String[] args) {
		// 시큐리티 프로바이더에 바운시 캐슬 암호화 라이브러리를 설정
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		// 지갑들을 생성
		walletA = new Wallet();
		walletB = new Wallet();
		walletC = new Wallet();
		walletD = new Wallet();
		walletE = new Wallet();

		Wallet walletGnesis = new Wallet();

		// 초기 거래를 생성하여, 샘플 코인 100을 A에게 보낸다.
		genesisTransaction = new Transaction(walletGnesis.publicKey, walletA.publicKey, 100f, null);

		// 초기 트랜잭션에 서명
		// 초기 트랜잭션 아이디 설정
		// 트랜잭션 아웃풋을 추가
		genesisTransaction.generateSignature(walletGnesis.privateKey);
		genesisTransaction.transactionId = "0";
		genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.reciepient, genesisTransaction.value,
				genesisTransaction.transactionId));

		// 매우 중요 => 저장소, 거래내역
		mainUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

		System.out.println("Creating and Mining Genesis block... ");
		Block genesis = new Block("0");
		genesis.addTransaction(genesisTransaction);
		addBlock(genesis);

		/* --------------------------- list --------------------------- */

		// start sample
		Block block1 = startSampleA(genesis);

		// Block block2 = startSampleB(block1);
		//
		// Block block3 = startSampleC(block2);
		//
		// startSampleD(block3);

		System.out.println("\nBlockchain is valid : " + isChainValid());
		System.out.println("\n================================= BLOCK CHAIN ================================= \n");
		System.out.println(StringUtil.getJson(blockchain));
	}

	private static Block startSampleA(Block genesis) {
		Block block1 = new Block(genesis.hash);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("\nWalletA is Attempting to send funds (30) to WalletB...");
		System.out.println("\nWalletA is Attempting to send funds (20) to WalletC...");
		System.out.println("\nWalletA is Attempting to send funds (10) to WalletD...");
		System.out.println("\nWalletA is Attempting to send funds (5) to WalletE...");

		block1.addTransaction(walletA.sendFunds(walletB.publicKey, 30f));
		block1.addTransaction(walletA.sendFunds(walletC.publicKey, 20f));
		block1.addTransaction(walletA.sendFunds(walletD.publicKey, 10f));
		block1.addTransaction(walletA.sendFunds(walletE.publicKey, 5f));
		addBlock(block1);

		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());
		System.out.println("WalletC's balance is: " + walletC.getBalance());
		System.out.println("WalletD's balance is: " + walletD.getBalance());
		System.out.println("WalletE's balance is: " + walletE.getBalance());

		return block1;
	}

	private static Block startSampleB(Block block1) {
		Block block2 = new Block(block1.hash);
		System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
		block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
		addBlock(block2);

		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());

		return block2;
	}

	private static Block startSampleC(Block block2) {
		Block block3 = new Block(block2.hash);
		System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
		block3.addTransaction(walletB.sendFunds(walletA.publicKey, 20f));
		addBlock(block3);

		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());

		return block3;
	}

	private static void startSampleD(Block block3) {
		Block block4 = new Block(block3.hash);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
		block4.addTransaction(walletA.sendFunds(walletB.publicKey, 30f));
		addBlock(block4);

		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());
	}

	public static Boolean isChainValid() {
		Block currentBlock;
		Block previousBlock;
		String hashTarget = StringUtil.getDificultyString(difficulty);

		// 임시
		HashMap<String, TransactionOutput> tempUTXOs = new HashMap<String, TransactionOutput>();
		tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

		for (int i = 1; i < blockchain.size(); i++) {
			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i - 1);

			if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
				System.out.println("#Current Hashes not equal");
				return false;
			}

			if (!previousBlock.hash.equals(currentBlock.previousHash)) {
				System.out.println("#Previous Hashes not equal");
				return false;
			}

			if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
				System.out.println("#This block hasn't been mined");
				return false;
			}

			// ------ transaction valid -------

			TransactionOutput tempOutput;

			for (int j = 0; j < currentBlock.transactions.size(); j++) {
				Transaction currentTransaction = currentBlock.transactions.get(j);

				if (!currentTransaction.verifySignature()) {
					System.out.println("#Signature on Transaction(" + j + ") is Invalid");
					return false;
				}

				if (currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
					System.out.println("#Inputs are note equal to outputs on Transaction(" + j + ")");
					return false;
				}

				for (TransactionInput input : currentTransaction.inputs) {
					tempOutput = tempUTXOs.get(input.transactionOutputId);

					if (tempOutput == null) {
						System.out.println("#Referenced input on Transaction(" + j + ") is Missing");
						return false;
					}

					if (input.UTXO.value != tempOutput.value) {
						System.out.println("#Referenced input Transaction(" + j + ") value is Invalid");
						return false;
					}

					tempUTXOs.remove(input.transactionOutputId);

				} // current transaction input List

				for (TransactionOutput output : currentTransaction.outputs) {
					tempUTXOs.put(output.id, output);

				} // current transaction output List

				if (currentTransaction.outputs.get(0).reciepient != currentTransaction.reciepient) {
					System.out.println("#Transaction(" + j + ") output reciepient is not who it should be");
					return false;
				}

				if (currentTransaction.outputs.get(1).reciepient != currentTransaction.sender) {
					System.out.println("#Transaction(" + j + ") output 'change' is not sender.");
					return false;
				}

			} // current block transaction List

		} // blockChain List

		return true;
	}

	public static void addBlock(Block newBlock) {
		newBlock.mineBlock(difficulty);
		blockchain.add(newBlock);
	}
}
