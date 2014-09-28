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
		Map<String, Object> result=new LinkedHashMap<String, Object>();
		Map<String, List<Object>> data=chartDao.getOperatingExp(fromDate, toDate);
		
		if(data!=null && data.size()>0){
			Map<String, Double> pieData=new LinkedHashMap<String, Double>();
			//Pie Chart Data
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
								pieData.put(str, d);
							}
							j++;
						}
						i++;
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
