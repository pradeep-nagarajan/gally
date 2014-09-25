package com.vapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.vapp.service.ChartService;

@Controller
public class ChartController {

	@Autowired
	ChartService chartService;

	@RequestMapping(value = "/revenue", method = RequestMethod.GET)
	public @ResponseBody
	ResponseEntity getRevenue(
			@RequestParam(value = "callback", required = true) String callback,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "type", required = true) String type) {

		if ("Revenue".equalsIgnoreCase(type))
			return new ResponseEntity(constructCallback(callback,
					chartService.getRevenue(fromDate, toDate)), new HttpHeaders(),
					HttpStatus.OK);
		else if ("Operating Expense".equalsIgnoreCase(type))
			return new ResponseEntity(constructCallback(callback,
					chartService.getRevenue(fromDate, toDate)), new HttpHeaders(),
					HttpStatus.OK);
		else
			return new ResponseEntity(constructCallback(callback,
					chartService.getRevenue(fromDate, toDate)), new HttpHeaders(),
					HttpStatus.OK);
	}

	public String constructCallback(String callback, Object data) {
		StringBuffer theStringBuffer = new StringBuffer();
		Gson gson = new Gson();
		theStringBuffer.append(callback + "(");
		theStringBuffer.append(gson.toJson(data));
		theStringBuffer.append(");");

		return theStringBuffer.toString();
	}
}
