package ch.heigvd.wem.tools;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Tools {

	/**
	 * Method used to compute the hash (SHA-1) of a String
	 * @param content The content to hash (String)
	 * @return The SHA-1 hash of the content, hexadecimal String
	 * @throws Exception
	 */
	public static String hash(String content) {
		return hashToString(computeHash(content));
	}

	private static byte[] computeHash(String x) {
		try {
			MessageDigest d = MessageDigest.getInstance("SHA-1");
			d.update(x.getBytes(Charset.forName("UTF8")));
			return  d.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace(); //SHOULD NOT HAPPEN...
			return null;
		}
	}

	private static String hashToString(byte[] hash) {  
		StringBuilder sb = new StringBuilder(); 
		for (int i = 0; i < hash.length; ++i) {  
			int v = hash[i] & 0xFF; 
			if(v < 16) {
				sb.append("0"); 
			}
			sb.append(Integer.toString(v, 16)); 
		}  
		return sb.toString(); 
	}
	
}
