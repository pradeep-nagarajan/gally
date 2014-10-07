package com.vapp.dao;

import java.util.List;
import java.util.Map;

public interface ChartDao {
	public Map<String, Double> getRevenue(String fromDate, String toDate);
	public Map<String, List<Object>> getOperatingExp(String fromDate, String toDate);
	public Map<String, List<Object>> getOperRevenue(String fromDate,
			String toDate);
	public Map<String, Double> getPL(String fromDate, String toDate);
	public Map<String, List<Object>> getOperRevenuePL(String fromDate,
			String toDate);
}
