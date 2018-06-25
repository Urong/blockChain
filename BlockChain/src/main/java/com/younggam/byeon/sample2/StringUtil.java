package com.younggam.byeon.sample2;

import java.security.*;
import java.util.ArrayList;
import java.util.Base64;
import com.google.gson.GsonBuilder;
import java.util.List;

/**
 * SHA-256
 * 
 * 어떤 입력 값에도 항상 고정된 길이의 해시 값을 출력한다.
 * 
 * 입력 값의 아주 일부만 변경되어도 전혀 다른 결과 값을 출력한다.(눈사태 효과)
 * 
 * 출력된 결과 값을 토대로 입력 값을 유추할 수 없다.
 * 
 * 입력 값은 항상 동일한 해시 값을 출력한다.
 * 
 * ---------------------------------
 * 
 * 타원곡선 암호법(ECDSA)
 * 
 * 타원곡선 이론에 기반한 공개 키 암호 방식이다.
 * 
 * -----------------------------------
 * 
 * base64
 * 
 * 64문자의 영숫자를 이용하여 멀티 바이트 문자열 또는 이진 데이터를 다루기 위한 인코딩 방식.
 * 
 * 
 */
public class StringUtil {

	public static String applySha256(String input) {

		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(input.getBytes("UTF-8"));

			StringBuffer hexString = new StringBuffer();

			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);

				if (hex.length() == 1)
					hexString.append('0');

				hexString.append(hex);
			}

			return hexString.toString();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// 서명 생성 - 타원곡선 암호법
	public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
		Signature dsa;
		byte[] signature = new byte[0];

		try {
			dsa = Signature.getInstance("ECDSA", "BC");
			dsa.initSign(privateKey);

			byte[] strByte = input.getBytes();
			dsa.update(strByte);

			byte[] realSig = dsa.sign();
			signature = realSig;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return signature;
	}

	// 서명 검증 - 타원곡선 암호법
	public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
		try {
			Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");

			ecdsaVerify.initVerify(publicKey);
			ecdsaVerify.update(data.getBytes());

			return ecdsaVerify.verify(signature);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// 키를 스트링으로 변환
	public static String getStringFromKey(Key key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}

	// 거래의 뿌리가 되는 Root, 이진트리
	public static String getMerkleRoot(ArrayList<Transaction> transactions) {
		int count = transactions.size();

		List<String> previousTreeLayer = new ArrayList<String>();

		for (Transaction transaction : transactions) {
			previousTreeLayer.add(transaction.transactionId);
		}

		List<String> treeLayer = previousTreeLayer;

		while (count > 1) {
			treeLayer = new ArrayList<String>();

			for (int i = 1; i < previousTreeLayer.size(); i += 2) {
				treeLayer.add(applySha256(previousTreeLayer.get(i - 1) + previousTreeLayer.get(i)));
			}

			count = treeLayer.size();
			previousTreeLayer = treeLayer;
		}

		String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";

		return merkleRoot;
	}

	public static String getJson(Object o) {
		return new GsonBuilder().setPrettyPrinting().create().toJson(o);
	}

	// 마이닝 조건 레벨 => difficulty : 5 => "00000"
	public static String getDificultyString(int difficulty) {
		return new String(new char[difficulty]).replace('\0', '0');
	}
}
