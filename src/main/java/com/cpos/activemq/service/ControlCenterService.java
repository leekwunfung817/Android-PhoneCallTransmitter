package com.cpos.activemq.service;

import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.cpos.activemq.session.ControlCenterSession;

@Service
public class ControlCenterService extends Thread {

	public ArrayList<ControlCenterSession> sessions = new ArrayList<>();
	
	@PostConstruct
	public void init() {
		start();
	}

	@Override
	public void run() {
		
	}
}
