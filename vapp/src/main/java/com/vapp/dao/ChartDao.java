package com.vapp.dao;

import java.util.Map;

public interface ChartDao {
	public Map<String, Double> getRevenue(String fromDate, String toDate);
}
