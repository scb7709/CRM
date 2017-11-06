package com.yun.ycw.crm.utils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by scb on 2016/2/17.
 */
public class StreamUtils {

	public static String streamToString(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		int len = 0;
		byte[] buffer = new byte[1024];

		while ((len = in.read(buffer)) != -1) {
			out.write(buffer, 0, len);
		}

		String result = out.toString();

		out.close();
		in.close();

		return result;
	}
}
