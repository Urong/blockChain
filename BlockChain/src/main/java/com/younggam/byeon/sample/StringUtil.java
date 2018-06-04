package com.younggam.byeon.sample;

import java.security.MessageDigest;

/**
 * 해시 함수는 단방향 함수라 데이터로부터 해시값을 구할 수는 있지만 해시값으로부터 데이터를 역산하는 것은 이론적으로 불가능하고, 해시값이
 * 같은 임의의 데이터를 만들어 내는 것 역시 매우 어렵다. 특정한 해시값이 나오는 데이터를 찾으려면 2256개(256비트로 나올 수 있는
 * 모든 경우의 수)의 데이터를 일일이 확인해야 한다. 현대의 컴퓨터로는 불가능한 작업이다.
 * 
 */
public class StringUtil {

	// 가장 많이 사용하는 해시 알고리즘
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

}
