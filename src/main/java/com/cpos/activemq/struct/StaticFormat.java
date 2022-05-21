package com.cpos.activemq.struct;

public class StaticFormat {
	public static class Topic {
		public static final String SERVER_RECEIVE = "cpos/carpark/admin/receive";
		public static final String SERVER_BOARDCAST = "cpos/carpark/admin/send";


		public static String RECEIVE_FROM_CARPARK(String car_park_id, String device_id) {
			return "cpos/carpark/" + car_park_id + "/" + device_id + "/send";
		}
	}

	public static class Json {

		public static String LOGIN_RESPONSE = "[{\"result\":\"1\"}]";

		public static String QUERY_DEVICE_LIST(String username, String device_id) {
			return "[{\"username\":\"" + username + "\",\"device_type\":\"6\",\"device_id\":\"" + device_id
					+ "\",\"msg_type\":\"17\"}]";
		}
	}
}
