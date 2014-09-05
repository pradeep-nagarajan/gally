package com.vapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.vapp.model.GroupData;
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
	 
	 @RequestMapping(value = "/updateignoreledger", method = RequestMethod.GET)
	  public @ResponseBody
	  ResponseEntity deleteIgnoreLedgers(
	      @RequestParam(value = "callback", required = true) String callback,
	      @RequestParam(value = "ledger", required = true) String ledger,
	      @RequestParam(value = "newledger", required = false) String newLedger,
	      @RequestParam(value = "mode", required = true) String mode)
	  {
		if("D".equalsIgnoreCase(mode))
			return new ResponseEntity(constructCallback(callback, vappSrv.deleteIgnoreLedger(ledger)), new HttpHeaders(),
					HttpStatus.OK);
		else if("I".equalsIgnoreCase(mode))
			return new ResponseEntity(constructCallback(callback, vappSrv.insertIgnoreLedger(ledger)), new HttpHeaders(),
					HttpStatus.OK);
		else if("U".equalsIgnoreCase(mode))
			return new ResponseEntity(constructCallback(callback, vappSrv.updateIgnoreLedger(ledger, newLedger)), new HttpHeaders(),
					HttpStatus.OK);
		else
			return new ResponseEntity(constructCallback(callback, "Error"), new HttpHeaders(),
					HttpStatus.OK);
	  }
	 
	 @RequestMapping(value = "/gettempdata", method = RequestMethod.GET)
	  public @ResponseBody
	  ResponseEntity getTempData(
	      @RequestParam(value = "callback", required = true) String callback)
	  {
		
		 return new ResponseEntity(constructCallback(callback, vappSrv.getTempData()), new HttpHeaders(),
					HttpStatus.OK);
	  }
	 
	 @RequestMapping(value = "/insgrpdata", method = RequestMethod.POST)
	  public @ResponseBody
	  ResponseEntity insertGroupData(
	      @RequestParam(value = "callback", required = false) String callback,@RequestBody GroupData groupData)
	  {
		
		 return new ResponseEntity(constructCallback(callback, vappSrv.insertGroupMasterData(groupData)), new HttpHeaders(),
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
