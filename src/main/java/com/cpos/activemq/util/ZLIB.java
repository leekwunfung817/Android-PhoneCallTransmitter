package com.cpos.activemq.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class ZLIB {


	public static byte[] compress(byte[] data) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    DeflaterOutputStream dout = new DeflaterOutputStream(baos);
	    dout.write(data);
	    dout.close();
	    return baos.toByteArray();
	}

	public static byte[] decompress(byte[] bytes) throws Exception {
		InputStream in = new InflaterInputStream(new ByteArrayInputStream(bytes));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[8192];
		int len;
		while ((len = in.read(buffer)) > 0) {
			baos.write(buffer, 0, len);
		}
		return baos.toByteArray();
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(new String( decompress(Files.readAllBytes(Paths.get("C:\\Users\\Ivan Lee\\Documents\\WeChat Files\\wxid_pf8bxghtzitu22\\FileStorage\\File\\2022-05\\20200903144150.T_CAR_LIST")))));
	}
}
