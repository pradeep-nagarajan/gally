package com.vapp.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

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
import com.vapp.model.IgnoreData;
import com.vapp.model.IgnoreList;
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
	 
	 @RequestMapping(value = "/updateignoreledger", method = RequestMethod.POST)
	  public @ResponseBody
	  ResponseEntity updateIgnoreLedgers(
	      @RequestParam(value = "callback", required = true) String callback,
	      @RequestBody IgnoreData ignoreData)
	  {
		if("D".equalsIgnoreCase(ignoreData.getMode()))
			return new ResponseEntity(constructCallback(callback, vappSrv.deleteIgnoreLedger(ignoreData.getLedger())), new HttpHeaders(),
					HttpStatus.OK);
		else if("I".equalsIgnoreCase(ignoreData.getMode()))
			return new ResponseEntity(constructCallback(callback, vappSrv.insertIgnoreLedger(ignoreData.getLedger())), new HttpHeaders(),
					HttpStatus.OK);
		else if("U".equalsIgnoreCase(ignoreData.getMode()))
			return new ResponseEntity(constructCallback(callback, vappSrv.updateIgnoreLedger(ignoreData.getLedger(), ignoreData.getNewLedger())), new HttpHeaders(),
					HttpStatus.OK);
		else
			return new ResponseEntity(constructCallback(callback, "Error"), new HttpHeaders(),
					HttpStatus.OK);
	  }
	 
	 @RequestMapping(value = "/ignoreallledger", method = RequestMethod.POST)
	  public @ResponseBody
	  ResponseEntity updateIgnoreLedgers(
	      @RequestParam(value = "callback", required = true) String callback,
	      @RequestBody IgnoreList ignoreList)
	  {
		 
		 for (String ignore : ignoreList.getIgnoreList()) {
			 vappSrv.insertIgnoreLedger(ignore);
		}
		 
		 return new ResponseEntity(constructCallback(callback, vappSrv.getIgnoreLedger()), new HttpHeaders(),
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
	 
	 @RequestMapping(value = "/updgrpdata", method = RequestMethod.POST)
	  public @ResponseBody
	  ResponseEntity insertGroupData(
	      @RequestParam(value = "callback", required = true) String callback,@RequestBody GroupData groupData)
	  {
		 if("D".equalsIgnoreCase(groupData.getMode()))
			 return new ResponseEntity(constructCallback(callback, vappSrv.deleteGroupMasterData(groupData)), new HttpHeaders(),
						HttpStatus.OK);
		 else if("I".equalsIgnoreCase(groupData.getMode()))
			 return new ResponseEntity(constructCallback(callback, vappSrv.insertGroupMasterData(groupData)), new HttpHeaders(),
					HttpStatus.OK);
		 else
			return new ResponseEntity(constructCallback(callback, "Error"), new HttpHeaders(),
						HttpStatus.OK);
		 
	  }
	 
	 @RequestMapping(value = "/genmisrpt", method = RequestMethod.GET)
	  public void getMISReport(
	      @RequestParam(value = "fromDate", required = true) String fromDate,
	      @RequestParam(value = "toDate", required = true) String toDate, HttpServletResponse response)
	  {
		 String filePath=vappSrv.getMISData(fromDate, toDate);
		 System.out.println(filePath);
		 response.setHeader("Content-Type", "application/xlsx");
		 response.setHeader("Content-Disposition", "attachment; filename="+filePath.substring(filePath.lastIndexOf("/")+1));
		 FileInputStream fis=null;
		 try {
			OutputStream out=response.getOutputStream();
			fis=new FileInputStream(new File(filePath));
			int n=0;
			byte[] buffer=new byte[1024];
			while((n=fis.read(buffer))!=-1)
				out.write(buffer,0,n);
			
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		 
	  }
	 
	 @RequestMapping(value = "/getgrplist", method = RequestMethod.GET)
	  public @ResponseBody
	  ResponseEntity getGroupList(
	      @RequestParam(value = "callback", required = true) String callback)
	  {
		
		 return new ResponseEntity(constructCallback(callback, vappSrv.getGroupLIst()), new HttpHeaders(),
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
