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
import com.vapp.service.VAPPService;

@Controller
public class VAPPController {
	
	@Autowired
	VAPPService vappSrv;
	
	 @RequestMapping(value = "/ignoreledger", method = RequestMethod.GET)
	  public @ResponseBody
	  ResponseEntity getIgnoreLedgers(
	      @RequestParam(value = "callback", required = true) String callback)
	  {
		
		 return new ResponseEntity(constructCallback(callback, vappSrv.getIgnoreLedger()), new HttpHeaders(),
					HttpStatus.OK);
	  }
	 
	 public String constructCallback(String callback, Object data){
		 StringBuffer theStringBuffer = new StringBuffer();
		 Gson gson = new Gson();
		 theStringBuffer.append(callback + "(");
		 theStringBuffer.append(gson.toJson(data));
		 theStringBuffer.append(");");
		 
		 return theStringBuffer.toString();
	 }
}
