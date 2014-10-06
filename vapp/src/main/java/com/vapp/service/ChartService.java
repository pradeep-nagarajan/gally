package com.vapp.service;

import java.util.Map;

public interface ChartService {
	public Map<String, Object> getSingleData(String fromDate, String toDate, String type);
	public Map<String, Object> getAllData(String fromDate, String toDate, String type);
}
