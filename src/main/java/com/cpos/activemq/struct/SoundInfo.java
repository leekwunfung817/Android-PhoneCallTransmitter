package com.cpos.activemq.struct;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SoundInfo {
	String ip;
	short port;
	String str_car_park_id;
	int device_id;
	int device_type;
}
