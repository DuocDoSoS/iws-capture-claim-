package com.rstn.iws.webservice.utils;

import java.util.HashMap;
import java.util.Map;

public class PropertyConfig {
	public static Map<String, String> mapPropertyConfig(){
		Map<String, String> map = new HashMap<String, String>();
		map.put("Application No", "Application No");
		map.put("Channel", "Channel");
		map.put("Company Code", "Company Code");
		map.put("Branch Code", "Branch Code");
		map.put("Insured Name", "Insured Name");
		map.put("Insured ID Type", "Insured ID Type");
		map.put("Insured ID No", "Insured ID No");
		map.put("Owner Name", "Owner Name");
		map.put("Owner ID Type", "Owner ID Type");
		map.put("Owner ID No", "Owner ID No");
		map.put("Product Code", "Product Code");
		map.put("Submission Date", "Submission Date");
		map.put("Agent Code", "Agent Code");
		return map;
	}

}
