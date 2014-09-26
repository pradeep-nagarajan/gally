package com.vapp.service;

import java.util.List;
import java.util.Map;

public interface ChartService {
	public Map<String, Object> getRevenue(String fromDate, String toDate);
	public Map<String, List<Object>> getOperatingExp(String fromDate, String toDate);
}
