package com.vapp.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import com.vapp.service.VAPPService;

@Controller
public class FileUploadController extends DefaultHandlerExceptionResolver {
	
	@Autowired
	VAPPService vappSrv;
	
	@RequestMapping(value="/fileUpload", method = RequestMethod.POST)
	public ModelAndView fileUploaded(@RequestParam("txnDate") String txnDate,
            @RequestParam("file") MultipartFile file)
	{		
		String message = "File has been uploaded successfully!";
		
		ModelAndView mav=new ModelAndView();
		File serverFile = null;
		
        System.out.println(txnDate+" : "+file.getOriginalFilename());
		if (file.isEmpty()) {
			message = "Uploaded File is empty";
		}else  if(file.getSize() > 2097152){
			message = "File size exceeds the limit";
		}else  if(file.getOriginalFilename().indexOf(".xls")<0){
			message = "Incorrect File cannot be uploaded!";
		}else {
			try {
			serverFile = new File("./" + txnDate+file.getOriginalFilename().substring(file.getOriginalFilename().indexOf(".")));
			BufferedOutputStream stream = new BufferedOutputStream(
				        new FileOutputStream(serverFile));
			
            stream.write(file.getBytes());
            stream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				
			}
			System.out.println(serverFile.getAbsolutePath());
			vappSrv.readAndInsert(txnDate, serverFile.getAbsolutePath());
		}
		mav.setViewName("message.jsp?message="+message);
		return mav;
	}

	
}
