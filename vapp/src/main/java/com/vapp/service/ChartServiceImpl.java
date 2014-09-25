package com.vapp.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vapp.dao.ChartDao;

@Service
public class ChartServiceImpl implements ChartService {

	@Autowired
	ChartDao chartDao; 
	public Map<String, Object> getRevenue(String fromDate, String toDate) {
		Map<String, Object> result=new LinkedHashMap<String, Object>();
		Map<String, Double> data=chartDao.getRevenue(fromDate, toDate);
		
		if(data!=null && data.size()>0){
			List<String> xAxis=new ArrayList<String>();
			List<Double> yAxis=new ArrayList<Double>();
			result.put("piedata", data);
			Map<String, Object> barData=new HashMap<String, Object>();
			for (String s : data.keySet()) {
				xAxis.add(s);
				yAxis.add(data.get(s));
			}
			barData.put("xaxis", xAxis);
			barData.put("yaxis", yAxis);
			result.put("bardata", barData);
		}
		
		return result;
	}
	public Map<String, Object> getOperatingExp(String fromDate, String toDate) {
		// TODO Auto-generated method stub
		return null;
	}

}
