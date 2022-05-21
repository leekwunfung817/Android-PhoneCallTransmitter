package com.cpos.activemq.struct;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SoundServerExchange {
	SoundInfo from;
	SoundInfo to;
	byte[] data = new byte[Constant.CPOS_REC_BUF_SIZE];
	int function;
}
