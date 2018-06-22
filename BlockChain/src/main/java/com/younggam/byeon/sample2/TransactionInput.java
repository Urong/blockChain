package com.younggam.byeon.sample2;

public class TransactionInput {

	// TransactionOutputs를 참조
	public String transactionOutputId;
	public TransactionOutput UTXO;

	public TransactionInput(String transactionOutputId) {
		this.transactionOutputId = transactionOutputId;
	}
}
