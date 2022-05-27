package com.cpos.activemq.struct;

import org.yaml.snakeyaml.constructor.Construct;

public class SoundBytes {

//	public static byte[] callingMobileToCarPark(SoundServerExchange soundServerExchange) {
//		return structMobileToCarPark(soundServerExchange);
//	}
//
//	public static byte[] answerMobileToCarPark(SoundServerExchange soundServerExchange) {
//		return structMobileToCarPark(soundServerExchange);
//	}
//	
//	public static byte[] soundMobileToCarPark(SoundServerExchange soundServerExchange) {
//		return structMobileToCarPark(soundServerExchange, Constant.CPOS_TEL_FUN_DATA);
//	}
//
//	public static byte[] hangUpMobileToCarPark(SoundServerExchange soundServerExchange) {
//		return structMobileToCarPark(soundServerExchange, Constant.CPOS_TEL_FUN_HANG_UP);
//	}
	
	public static byte[] structMobileToCarPark(SoundServerExchange soundServerExchange) {
		SoundInfo from = soundServerExchange.getFrom();
		String[] ipSlicef = from.getIp().split(".");
		int from_ip = 0;
		byte[] from_ip_bytes = new byte[]{
				(byte) Integer.parseInt(ipSlicef[0]), 
				(byte)Integer.parseInt(ipSlicef[1]), 
				(byte)Integer.parseInt(ipSlicef[2]), 
				(byte)Integer.parseInt(ipSlicef[3])
				};
		for (byte b : from_ip_bytes) {
			from_ip = (from_ip << 8) + (b & 0xFF);
		}
		int from_port = from.getPort();
		byte[] from_str_car_park_id; // = new byte[21];
		int from_device_id = from.getDevice_id();
		int from_device_type = from.getDevice_type();

		
		
		SoundInfo to = soundServerExchange.getTo();
		String[] ipSlicet = to.getIp().split(".");
		int to_ip = 0;
		byte[] to_ip_bytes = new byte[]{
				(byte) Integer.parseInt(ipSlicet[0]), 
				(byte)Integer.parseInt(ipSlicet[1]), 
				(byte)Integer.parseInt(ipSlicet[2]), 
				(byte)Integer.parseInt(ipSlicet[3])
				};
		for (byte b : to_ip_bytes) {
			to_ip = (to_ip << 8) + (b & 0xFF);
		}
		int to_port = to.getPort();
		byte[] to_str_car_park_id; // = new byte[21];
		to_str_car_park_id = to.getStr_car_park_id().getBytes();
		int to_device_id = to.getDevice_id();
		int to_device_type = to.getDevice_type();

		
		
//		byte[] sound_data = new byte[600]; // 声音数据

//		int cmd_function = Constant.CPOS_TEL_FUN_CALL; // 呼叫

		int cmd_len = 685;// 整体数据的长度:

		int i = 0;
		int len = 0;

		byte[] send_buf = new byte[cmd_len]; // 创建数据缓冲区
		{
			send_buf[len++] = 'A'; // 1 BYTE 数据头
			send_buf[len++] = 5; // 1 BYTE 数据类型, 5表示无压缩的原始数据
			send_buf[len++] = (byte) (cmd_len & 0x000000ff); // 2BYTE cmd_len
			send_buf[len++] = (byte) ((cmd_len >> 8) & 0x000000ff); // cmd_len

			{
				// struct data
				// 以下开始结构赋值, 要注意4字节对齐
				{
					/////////////////////////////////////
					// SOUND_INFO from

					// ip - 4 BYTES
					send_buf[len++] = (byte) (from_ip & 0x000000ff);
					send_buf[len++] = (byte) ((from_ip >> 8) & 0x000000ff);
					send_buf[len++] = (byte) ((from_ip >> 16) & 0x000000ff);
					send_buf[len++] = (byte) ((from_ip >> 24) & 0x000000ff);

					// port 2 BYTES
					send_buf[len++] = (byte) (from_port & 0x000000ff);
					send_buf[len++] = (byte) ((from_port >> 8) & 0x000000ff);

					for (i = 0; i < 21; i++) {
						send_buf[len + i] = to_str_car_park_id[i]; //
						len++;
					}

					// 特别注意:
					// 因为32位系统的struct需要4BYTES对齐,所以此处要做补齐
					len++; // 补齐到4字节

					// device_id 4 BYTES
					send_buf[len++] = (byte) (from_device_id & 0x000000ff);
					send_buf[len++] = (byte) ((from_device_id >> 8) & 0x000000ff);
					send_buf[len++] = (byte) ((from_device_id >> 16) & 0x000000ff);
					send_buf[len++] = (byte) ((from_device_id >> 24) & 0x000000ff);

					// device_type 4 BYTES
					send_buf[len++] = (byte) (from_device_type & 0x000000ff);
					send_buf[len++] = (byte) ((from_device_type >> 8) & 0x000000ff);
					send_buf[len++] = (byte) ((from_device_type >> 16) & 0x000000ff);
					send_buf[len++] = (byte) ((from_device_type >> 24) & 0x000000ff);
				}
				{
					/////////////////////////////////////
					// SOUND_INFO to

					send_buf[len++] = (byte) (to_ip & 0x000000ff);
					send_buf[len++] = (byte) ((to_ip >> 8) & 0x000000ff);
					send_buf[len++] = (byte) ((to_ip >> 16) & 0x000000ff);
					send_buf[len++] = (byte) ((to_ip >> 24) & 0x000000ff);

					// port 2 BYTES
					send_buf[len++] = (byte) (to_port & 0x000000ff);
					send_buf[len++] = (byte) ((to_port >> 8) & 0x000000ff);

					for (i = 0; i < 21; i++) {
						send_buf[len + i] = to_str_car_park_id[i]; //
						len++;
					}

					len++; // 补齐到4字节

					// device_id 4 BYTES
					send_buf[len++] = (byte) (to_device_id & 0x000000ff);
					send_buf[len++] = (byte) ((to_device_id >> 8) & 0x000000ff);
					send_buf[len++] = (byte) ((to_device_id >> 16) & 0x000000ff);
					send_buf[len++] = (byte) ((to_device_id >> 24) & 0x000000ff);

					// device_type 4 BYTES
					send_buf[len++] = (byte) (to_device_type & 0x000000ff);
					send_buf[len++] = (byte) ((to_device_type >> 8) & 0x000000ff);
					send_buf[len++] = (byte) ((to_device_type >> 16) & 0x000000ff);
					send_buf[len++] = (byte) ((to_device_type >> 24) & 0x000000ff);
				}
				{
					/////////////////////////////////////////////////
					// data
					byte[] sound_data = soundServerExchange.data;
					for (i = 0; i < sound_data.length; i++) {
						send_buf[len + i] = sound_data[i]; //
						len++;
					}
				}
				{
					// function
					int cmd_function = soundServerExchange.function;
					send_buf[len++] = (byte) (cmd_function & 0x000000ff);
					send_buf[len++] = (byte) ((cmd_function >> 8) & 0x000000ff);
					send_buf[len++] = (byte) ((cmd_function >> 16) & 0x000000ff);
					send_buf[len++] = (byte) ((cmd_function >> 24) & 0x000000ff);
				}
				{
					// 计算struct 的checksum

					int checksum = 0;

					// checksum计算不包括自己
					for (i = 4; i < len; i++) {
						checksum += send_buf[i];
					}

					checksum = checksum % 256;
					checksum = (256 - checksum) & 0x000000ff;

					send_buf[len++] = (byte) (checksum);

					// 补齐4字节
					len++;
					len++;
					len++;
				}


				// 到此 SOUND_SERVER_EXCHANGE struct 赋值完成
				// struct数据部分长度为680 BYTES
				/////////////////////////////////
			}
			// 整组数据的checksum
			int checksum2 = 0;
			for (i = 0; i < len; i++) {
				checksum2 += send_buf[i];
			}

			checksum2 = checksum2 % 256;
			checksum2 = (256 - checksum2) & 0x000000ff;

			send_buf[len++] = (byte) (checksum2);
		}
		// 到此结束, len应该==685表示整个数据长度正确
		return send_buf;
//		udpsocket.write(send_buf);

	}
}