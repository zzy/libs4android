package net.ouds.android.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class COMMON {
	/**
	 * 产生UUID
	 * 
	 * @author 张忠宇
	 */
	public static String UUID() {
		String uuid = java.util.UUID.randomUUID().toString();
		StringBuilder bf = new StringBuilder(32);
		for (int i = 0; i < uuid.length(); ++i) {
			char c = uuid.charAt(i);
			if (c != '-' && c != '_') {
				bf.append(c);
			}
		}
		return bf.toString();
	}

	/**
	 * 读取属性文件
	 * 
	 * @author 张忠宇
	 */
	public static String property(String key) {
		try {
			Properties props = new Properties();
			InputStream in = COMMON.class.getResourceAsStream("/util.properties");
			props.load(in);
			in.close();

			return props.getProperty(key);
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
			
			return null;
		}
	}

}
