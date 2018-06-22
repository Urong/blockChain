package com.younggam.byeon.sample;

import java.security.MessageDigest;

/**
 * SHA(Secure Hash Algorithm, 안전한 해시 알고리즘) 해시값의 크기 : 256, 특징 : 메시지가 조금만 바뀌어도 눈사태
 * 효과로 완전히 바뀔 수 있다.
 * 
 * 해시 함수는 단방향 함수라 데이터로부터 해시값을 구할 수는 있지만 해시값으로부터 데이터를 역산하는 것은 이론적으로 불가능하고, 해시값이
 * 같은 임의의 데이터를 만들어 내는 것 역시 매우 어렵다. 특정한 해시값이 나오는 데이터를 찾으려면 2256개(256비트로 나올 수 있는
 * 모든 경우의 수)의 데이터를 일일이 확인해야 한다. 현대의 컴퓨터로는 불가능한 작업이다.
 * 
 * = 해시함수 =
 * 
 * 어떤 입력 값에도 항상 고정된 길이의 해시 값을 출력한다.
 * 
 * 입력 값의 아주 일부만 변경되어도 전혀 다른 결과 값을 출력한다.(눈사태 효과)
 * 
 * 출력된 결과 값을 토대로 입력 값을 유추할 수 없다.
 * 
 * 입력 값은 항상 동일한 해시 값을 출력한다.
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

}
