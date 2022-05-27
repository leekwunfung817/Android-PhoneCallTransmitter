package com.cpos.activemq.struct;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SoundServerExchange {
	SoundInfo from;
	SoundInfo to;
	byte[] data;
	int function;
}
