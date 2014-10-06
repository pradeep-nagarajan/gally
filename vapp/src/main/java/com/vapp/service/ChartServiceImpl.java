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
	public Map<String, Object> getSingleData(String fromDate, String toDate, String type) {
		Map<String, Object> result=new LinkedHashMap<String, Object>();
		Map<String, Double> data=null;
		if("Revenue".equalsIgnoreCase(type))
			data=chartDao.getRevenue(fromDate, toDate);
		else if("Profit/Loss".equalsIgnoreCase(type))
			data=chartDao.getPL(fromDate, toDate);
			
		if(data!=null && data.size()>0){
			List<String> xAxis=new ArrayList<String>();
			List<Double> yAxis=new ArrayList<Double>();
			//Y Axis Data
			List<Map<String, Object>> yAxisList=new ArrayList<Map<String, Object>>();
			result.put("piedata", data);
			Map<String, Object> barData=new HashMap<String, Object>();
			for (String s : data.keySet()) {
				xAxis.add(s);
				yAxis.add(data.get(s));
			}
			barData.put("xaxis", xAxis);
			Map<String, Object> hm=new HashMap<String, Object>();
			hm.put("name", type);
			hm.put("data", yAxis);
			yAxisList.add(hm);
			barData.put("yaxis", yAxisList);
			result.put("bardata", barData);
		}
		
		return result;
	}
	public Map<String, Object> getAllData(String fromDate, String toDate, String type) {
		Map<String, Object> result=new LinkedHashMap<String, Object>();
		Map<String, List<Object>> data=null;
		if("Operating Expense".equalsIgnoreCase(type))
			data=chartDao.getOperatingExp(fromDate, toDate);
		else if("Operating Expense vs Revenue".equalsIgnoreCase(type))
			data=chartDao.getOperRevenue(fromDate, toDate);
		if(data!=null && data.size()>0){
			Map<String, Double> pieData=new LinkedHashMap<String, Double>();
			//Pie Chart Data
			if("Operating Expense".equalsIgnoreCase(type)){
				for (String key : data.keySet()) {
					if("AAAAAA".equalsIgnoreCase(key)){
						for(Object obj : data.get(key)){
							pieData.put((String)obj, new Double(0.0));
						}
					}else{
						int i=0;
						for(Object obj : data.get(key)){
							int j=0;
							for(String str : pieData.keySet()){
								if(j==i){
									Double d= pieData.get(str);
									d+=(Double)obj;
									pieData.put(str, (Math.round( d * 100.0 ) / 100.0));
								}
								j++;
							}
							i++;
						}
					}
				}
			}else if("Operating Expense vs Revenue".equalsIgnoreCase(type)){
				for (String key : data.keySet()) {
					if(!"AAAAAA".equalsIgnoreCase(key)){
						List<Object> dd=data.get(key);
						Double d=new Double(0);
						for (Object object : dd) {
							d+=(Double)object;
						}
						pieData.put(key, d);
					}
				}
			}
			//Y Axis Data
			List<Map<String, Object>> yAxis=new ArrayList<Map<String, Object>>();
			
			for (String key : data.keySet()) {
				if(!"AAAAAA".equalsIgnoreCase(key)){
					Map<String, Object> hm=new HashMap<String, Object>();
					hm.put("name", key);
					hm.put("data", data.get(key));
					yAxis.add(hm);
				}
			}
			
			Map<String, Object> barData=new HashMap<String, Object>();
			barData.put("xaxis", data.get("AAAAAA"));
			barData.put("yaxis", yAxis);
			result.put("piedata", pieData);
			result.put("bardata", barData);
		}
		
		return result;
	}

}
