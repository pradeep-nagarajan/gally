<%@page import="java.math.RoundingMode"%>
<%@page import="java.math.BigDecimal"%>
<%@page import="org.apache.poi.ss.usermodel.Cell"%>
<%@page import="org.apache.poi.ss.usermodel.Row"%>
<%@page import="org.apache.poi.xssf.usermodel.XSSFSheet"%>
<%@page import="org.apache.poi.xssf.usermodel.XSSFWorkbook"%>
<%@page import="org.apache.commons.fileupload.*,java.util.*,java.io.*"%>

<%
  // JSP to handle  uploading
	String msg="File has been uploaded successfully!";
	// Create a new file upload handler 
	
	DiskFileUpload upload = new DiskFileUpload();
	if (request.getContentType() != null)
	  {
	    // parse request
	    List items = upload.parseRequest(request);

	    // get uploaded file 
	    FileItem file = (FileItem) items.get(0);
	    String source = file.getName().substring(
		file.getName().lastIndexOf(File.separator) + 1);
		File outfile = null;
		if("file".equalsIgnoreCase(file.getFieldName()) && source.indexOf(".xls")>-1){
			
			outfile = new File("/tmp/" + source);
		
			/*if (System.getProperty("os.name").contains("Windows"))
			  {
			    outfile = new File("C:\\DATA\\dwp_Intranet\\" + source);
			  }
			else
			  {
			    outfile = new File(
				"/xjp/tomcat/apache-tomcat-7.0.37/webapps/" + source);
			  }*/
			
			file.write(outfile);
			  
			  try
		        {
		            FileInputStream is = new FileInputStream(outfile);
		 
		            //Create Workbook instance holding reference to .xlsx file
		            XSSFWorkbook workbook = new XSSFWorkbook(is);
		 
		            //Get first/desired sheet from the workbook
		            XSSFSheet sheet = workbook.getSheetAt(0);
		 
		            //Iterate through each rows one by one
		            Iterator<Row> rowIterator = sheet.iterator();
		            while (rowIterator.hasNext()) 
		            {
		                Row row = rowIterator.next();
		                //For each row, iterate through all the columns
		                Iterator<Cell> cellIterator = row.cellIterator();
		                 
		                while (cellIterator.hasNext()) 
		                {
		                    Cell cell = cellIterator.next();
		                    //Check the cell type and format accordingly
		                    switch (cell.getCellType()) 
		                    {
		                        case Cell.CELL_TYPE_NUMERIC:
		                        	//cell.setCellType(Cell.CELL_TYPE_STRING);
		                        	BigDecimal ab=new BigDecimal(cell.getNumericCellValue());
		                            System.out.print(ab.setScale(2, RoundingMode.HALF_EVEN) + "\t\t");
		                            break;
		                        case Cell.CELL_TYPE_STRING:
		                            System.out.print(cell.getStringCellValue() + "\t\t\t");
		                            break;
		                    }
		                }
		                System.out.println("");
		            }
		            is.close();
		        } 
		        catch (Exception e) 
		        {
		            e.printStackTrace();
		        }  

			System.out.println(source + " Upload Is Successful!");
		}else{
		  System.out.println("Sorry Files are only allowed!");
		  msg="Incorrect File!";
		}
	  }
	else{
	  System.out.println("Sorry Invalid Request!");
	  msg="Sorry Invalid Request!";
	}
	/*String site = new String("http://maa1w694bs:8081/UDLoader/uploader.html");
   response.setStatus(response.SC_MOVED_TEMPORARILY);
   response.setHeader("Location", site);*/ 
   
	  
%>
<jsp:forward page="message.html?message=<%=msg%>>"></jsp:forward>
