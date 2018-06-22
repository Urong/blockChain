package com.younggam.byeon.sample2;

import java.security.PublicKey;

public class TransactionOutput {

	public String id;

	// 이 동전의 새로운 소유자
	public PublicKey reciepient;

	// 소유한 동전의 양
	public float value;

	// 출력이 작성된 트랜잭션의 ID
	public String parentTransactionId;

	public TransactionOutput(PublicKey reciepient, float value, String parentTransactionId) {
		this.reciepient = reciepient;
		this.value = value;
		this.parentTransactionId = parentTransactionId;

		this.id = StringUtil
				.applySha256(StringUtil.getStringFromKey(reciepient) + Float.toString(value) + parentTransactionId);
	}

	// 나의 동전인지 확인
	public boolean isMine(PublicKey publicKey) {
		return (publicKey == reciepient);
	}

}
