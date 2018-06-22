package com.younggam.byeon.sample2;

import java.security.*;
import java.util.ArrayList;

/**
 * UTXO (Unspent Transaction Output) : 미사용 거래 출력
 * 
 */
public class Transaction {

	// 트랜잭션 해쉬를 담는 식별 아이디
	public String transactionId;

	// 보내는 사람 주소, 공개키
	// 받는 사람 주소, 공개키
	public PublicKey sender;
	public PublicKey reciepient;

	// 수령자에게 보내는 금액
	public float value;

	// 이것은 누군가가 지갑에 돈을 쓰는 것을 막는 용도
	public byte[] signature;

	public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
	public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

	// 트랜잭션들을 생성할 때 사용되는 카운트
	private static int sequence = 0;

	public Transaction(PublicKey sender, PublicKey reciepient, float value, ArrayList<TransactionInput> inputs) {
		this.sender = sender;
		this.reciepient = reciepient;

		this.value = value;
		this.inputs = inputs;
	}

	public boolean processTransaction() {
		// 트랜잭션 서명 검증
		if (verifySignature() == false) {
			System.out.println("Transaction Signature failed to verify");
			return false;
		}

		// 트랜잭션 입력 수집 (사용되지 않았는지 확인)
		for (TransactionInput input : inputs) {
			input.UTXO = BlockChainMain.mainUTXOs.get(input.transactionOutputId);
		}

		// 유효한 트랜잭션인지 확인
		if (getInputsValue() < BlockChainMain.minimumTransaction) {
			System.out.println("Transaction Inputs too small: " + getInputsValue());
			System.out.println("Please enter the amount greater than " + BlockChainMain.minimumTransaction);
			return false;
		}

		// 트랜잭션 출력 생성
		float leftOver = getInputsValue() - value; // get value of inputs then the left over change:

		// 해쉬 생성
		transactionId = calulateHash();

		outputs.add(new TransactionOutput(this.reciepient, value, transactionId)); // send value to recipient
		outputs.add(new TransactionOutput(this.sender, leftOver, transactionId)); // send the left over 'change' back to
																					// sender

		// Add outputs to Unspent list
		for (TransactionOutput o : outputs) {
			BlockChainMain.mainUTXOs.put(o.id, o);
		}

		// Remove transaction inputs from UTXO lists as spent:
		for (TransactionInput i : inputs) {
			if (i.UTXO == null)
				continue; // if Transaction can't be found skip it
			BlockChainMain.mainUTXOs.remove(i.UTXO.id);
		}

		return true;
	}

	public float getInputsValue() {
		float total = 0;

		for (TransactionInput input : inputs) {

			// 트랜잭션을 찾을 수 없는 경우 이 동작은 최적이 아닐 수 있다.
			if (input.UTXO == null)
				continue;

			total += input.UTXO.value;
		}
		return total;
	}

	public void generateSignature(PrivateKey privateKey) {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient)
				+ Float.toString(value);

		signature = StringUtil.applyECDSASig(privateKey, data);
	}

	public boolean verifySignature() {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient)
				+ Float.toString(value);

		return StringUtil.verifyECDSASig(sender, data, signature);
	}

	public float getOutputsValue() {
		float total = 0;
		for (TransactionOutput o : outputs) {
			total += o.value;
		}
		return total;
	}

	private String calulateHash() {
		// 두 개의 같은 트랜잭션이 같은 해쉬를 갖는 것을 방지하는 목적으로 사용한다.
		sequence++;

		return StringUtil.applySha256(StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient)
				+ Float.toString(value) + sequence);
	}
}
