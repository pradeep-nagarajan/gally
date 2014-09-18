package com.vapp.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vapp.dao.VAPPDao;
import com.vapp.model.GroupData;
import com.vapp.model.MasterData;


public class VAPPServiceImpl1 implements VAPPService
{
	@Autowired
	private VAPPDao vappDao;
    public boolean readAndInsert(String fileName,String fileFullPath){
    	Connection conn=null;
    	boolean result=false;
    	try {
			conn = vappDao.getVAPPConnection();
			conn.setAutoCommit(false);
			String[] a=fileName.split("-");
			int[] b={Integer.parseInt(a[0]),(Integer.parseInt(a[1])-1),Integer.parseInt(a[2])};
			Date txnDate=new Date(b[2], b[1], b[0]);
			String txnDateStr=a[1]+"-"+a[2];
			if(!vappDao.checkTempData(txnDateStr, conn)){
				readXLS(conn,txnDate, fileFullPath);
				result=true;
			}else{
				vappDao.deleteTempData(txnDateStr);
				conn.commit();
				System.out.println("Data Deleted!");
				readAndInsert(fileName, fileFullPath);
			}
			conn.commit();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}finally{
        	try {
        		conn.setAutoCommit(true);
        		if(conn!=null)
            		conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
        	
        }
    	return result;
    }
    
    public void readXLS(Connection conn, Date txnDate, String fileFullPath){
    	FileInputStream file = null;
        try
        {
            file = new FileInputStream(new File(fileFullPath));
            
            //Create Workbook instance holding reference to .xlsx file
            XSSFWorkbook workbook = new XSSFWorkbook(file);
 
            //Get first/desired sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);
            
            //Iterate through each rows one by one
            Iterator<Row> rowIterator = sheet.iterator();
            
            while (rowIterator.hasNext()) 
            {
                Row row = rowIterator.next();
                //For each row, iterate through all the columns
                Iterator<Cell> cellIterator = row.cellIterator();
                int colCount=0;
                MasterData md=new MasterData();
                while (cellIterator.hasNext()) 
                {
                    Cell cell = cellIterator.next();
                    
                    //Check the cell type and format accordingly
                    if(colCount==0){
                    	if(vappDao.getIgnoreLedger().contains(cell.getStringCellValue().trim()))
                    		break;
                    	md.setLedger(cell.getStringCellValue().trim());
                    }
                    else if(colCount==1 && Cell.CELL_TYPE_BLANK!=cell.getCellType()){
                    	md.setCrDr("DR");
                    	BigDecimal amount=new BigDecimal(cell.getNumericCellValue());
                    	md.setAmount(amount.setScale(2, RoundingMode.HALF_EVEN));
                    } else if(colCount==2 && Cell.CELL_TYPE_BLANK!=cell.getCellType()){
                    	md.setCrDr("CR");
                    	BigDecimal amount=new BigDecimal(cell.getNumericCellValue());
                    	md.setAmount(amount.setScale(2, RoundingMode.HALF_EVEN));
                    }
                    colCount++;
                }
                if(md.getLedger()!=null && md.getLedger().length()>0)
                	vappDao.insertTempData(conn, md, txnDate);
                System.out.println("row: "+row.getRowNum());
            }
            
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }finally{
        	try {
        		if(file!=null)
					file.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	
        }
    }

	public Map<String, Object> getIgnoreLedger() {
		Map<String, Object> hm=new HashMap<String, Object>();
		 
		List<String> ignore = new ArrayList<String>();
		ignore.addAll(vappDao.getIgnoreLedger());
		ignore.remove(null);
		ignore.remove("");
		hm.put("ignoreList", ignore);
		return hm;
	}
	
	public Map<String, Object> deleteIgnoreLedger(String ledger){
		Map<String, Object> hm=new HashMap<String, Object>();
		 
		List<String> ignore = new ArrayList<String>();
		ignore.addAll(vappDao.deleteIgnoreLedger(ledger));
		ignore.remove(null);
		ignore.remove("");
		hm.put("ignoreList", ignore);
		return hm;
	}
	
	public Map<String, Object> insertIgnoreLedger(String ledger){
		Map<String, Object> hm=new HashMap<String, Object>();
		 
		List<String> ignore = new ArrayList<String>();
		ignore.addAll(vappDao.insertIgnoreLedger(ledger));
		ignore.remove(null);
		ignore.remove("");
		hm.put("ignoreList", ignore);
		return hm;
	}
	
	public Map<String, Object> updateIgnoreLedger(String ledger, String newLedger){
		Map<String, Object> hm=new HashMap<String, Object>();
		 
		List<String> ignore = new ArrayList<String>();
		ignore.addAll(vappDao.updateIgnoreLedger(ledger, newLedger));
		ignore.remove(null);
		ignore.remove("");
		hm.put("ignoreList", ignore);
		return hm;
	}

	public List<String> getTempData() {
		
		return vappDao.getTempData();
	}

	public Map<String, Set<String>> insertGroupMasterData(GroupData groupData) {
		int i=vappDao.insertGroupMasterData(groupData);
		Map<String, Set<String>> tsm=vappDao.getGroupLIst();
		Set<String> st=new TreeSet<String>();
		st.add(i>=1?"true":"false");
		tsm.put("result", st);
		return tsm;
	}

	public String getMISData(String fromDate, String toDate) {
		String fileName="D:/VAPP/tmp/"+fromDate+"_to_"+toDate+".xlsx";
		Map<String, List<Object>> data = new LinkedHashMap<String, List<Object>>();
		String prevDate = "";
		Character[] excelCol = { 'C', 'D', 'E', 'F', 'G', 'H', 'I',
				'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T' };
		try {
			FileOutputStream out = new FileOutputStream(new File(fileName));
			data=vappDao.getMISData(fromDate, toDate);
		
			int colIndex = data.get("AAAAAA").size()-1;
			
			// Blank workbook
			XSSFWorkbook workbook = new XSSFWorkbook();

			// Create a blank sheet
			XSSFSheet sheet = workbook.createSheet("Net TB");
			
			XSSFFont font=workbook.createFont();
			XSSFCellStyle centerCs=workbook.createCellStyle();
			centerCs.setAlignment(CellStyle.ALIGN_CENTER);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			centerCs.setFont(font);
			XSSFDataFormat df =workbook.createDataFormat();
			XSSFCellStyle numberCs=workbook.createCellStyle();
			numberCs.setDataFormat(df.getFormat("_(* #,##0_);_(* (#,##0);_(* \"-\"??_);_(@_)"));
			// Iterate over data and write to sheet
			Set<String> keyset = data.keySet();
			int rownum = 0;
			for (String key : keyset) {
				Row row = sheet.createRow(rownum++);
				List<Object> objArr = data.get(key);
				if (rownum > 0) {
					Cell cell = row.createCell(0);
					Cell cell2 = row.createCell(1);
					if("AAAAAA".equalsIgnoreCase(key)){
						cell.setCellValue("Ledger");
						cell2.setCellValue("MIS Grouping");
					}else{
						cell.setCellValue(key.substring(0, key.indexOf("~`")));
						cell2.setCellValue(key.substring(key.indexOf("~`")+2));
					}
				}
				int cellnum = 2;
				for (Object obj : objArr) {
					Cell cell = row.createCell(cellnum++);
					if (obj instanceof String){
						if("-".equals((String)obj) || ((String)obj).indexOf("/")>-1)
							cell.setCellStyle(centerCs);
						cell.setCellValue((String) obj);
					}
					else if (obj instanceof Integer)
						cell.setCellValue((Integer) obj);
					else if (obj instanceof Double){
						cell.setCellValue((Double) obj);
						cell.setCellStyle(numberCs);
					}
				}
				int colSize = objArr.size();
				for (; colSize <= colIndex;) {
					Cell cell = row.createCell(cellnum++);
					cell.setCellValue("-");
					cell.setCellStyle(centerCs);
					colSize++;
				}

				for (int i = 0; i <= colIndex; i++) {
					Cell cell = row.createCell(cellnum++);
					if (rownum > 1){
						cell.setCellFormula("SUM(C" + rownum + ":"
								+ excelCol[i] + rownum + ")");
						cell.setCellStyle(numberCs);
					}
					else{
						cell.setCellValue("YTD AS ON " + objArr.get(i));
						cell.setCellStyle(centerCs);
					}
					
				}
			}
			Row row = sheet.createRow(rownum++);
			int currColIndex=((data.get("AAAAAA").size())*2)+1;
			//Calculate Grant Total
			for (int i = 0; i <= currColIndex; i++) {
				Cell cell = row.createCell(i);
				if(i==1){
					cell.setCellValue("Grant Total ");
					cell.setCellStyle(centerCs);
				}else if (i > 1){
					cell.setCellFormula("SUM("+ excelCol[i-2]+"2" + ":"
							+ excelCol[i-2] + (rownum-1) + ")");
					cell.setCellStyle(numberCs);
				}
			}
			for(int i=0;i<=currColIndex;i++)
				sheet.autoSizeColumn(i);
			
				workbook.write(out);
			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println("data.xlsx written successfully on disk.");
		
		return fileName;
	}

	public Map<String, Set<String>> getGroupLIst() {
		
		return vappDao.getGroupLIst();
	}

	public Map<String, Set<String>> deleteGroupMasterData(GroupData groupData) {
		int i=vappDao.deleteGroupMasterData(groupData);
		Map<String, Set<String>> tsm=vappDao.getGroupLIst();
		Set<String> st=new TreeSet<String>();
		st.add(i>=1?"true":"false");
		tsm.put("result", st);
		return tsm;
	}

	public Map<String, Set<String>> updateGroupMasterData(GroupData groupData) {
		int i=vappDao.updateGroupMasterData(groupData);
		Map<String, Set<String>> tsm=vappDao.getGroupLIst();
		Set<String> st=new TreeSet<String>();
		st.add(i>=1?"true":"false");
		tsm.put("result", st);
		return tsm;
	}

	public String getPLReport(String fromDate, String toDate) {
		String fileName="D:/VAPP/tmp/PL_"+fromDate+"_to_"+toDate+".xlsx";
		Map<String, List<Object>> data = new LinkedHashMap<String, List<Object>>();
		String prevGroup = "";
		int startRow = 0,revenueRow=0,operRow=0;
		
		Character[] excelCol = { 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
				'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T' };
		try {
			FileOutputStream out = new FileOutputStream(new File(fileName));
			data=vappDao.getPLReport(fromDate, toDate);
			
			int colIndex = data.get("AAAAAA").size()-1;
			
			// Blank workbook
			XSSFWorkbook workbook = new XSSFWorkbook();

			// Create a blank sheet
			XSSFSheet sheet = workbook.createSheet("MIS P&L (INR '000s)");
			
			XSSFFont font=workbook.createFont();
			XSSFCellStyle centerCs=workbook.createCellStyle();
			centerCs.setAlignment(CellStyle.ALIGN_CENTER);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			centerCs.setFont(font);
			XSSFCellStyle boldCs=workbook.createCellStyle();
			boldCs.setFont(font);
			
			XSSFDataFormat df =workbook.createDataFormat();
			XSSFCellStyle numberCs=workbook.createCellStyle();
			numberCs.setDataFormat(df.getFormat("_(* #,##0_);_(* (#,##0);_(* \"-\"??_);_(@_)"));
			XSSFCellStyle boldNumberCs=workbook.createCellStyle();
			boldNumberCs.setDataFormat(df.getFormat("_(* #,##0_);_(* (#,##0);_(* \"-\"??_);_(@_)"));
			boldNumberCs.setFont(font);
			
			CellStyle fillStyle = workbook.createCellStyle();
			fillStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
			fillStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
			fillStyle.setDataFormat(df.getFormat("_(* #,##0_);_(* (#,##0);_(* \"-\"??_);_(@_)"));
			fillStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
			fillStyle.setFont(font);
			
			CellStyle fillTotStyle = workbook.createCellStyle();
			fillTotStyle.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
			fillTotStyle.setDataFormat(df.getFormat("_(* #,##0_);_(* (#,##0);_(* \"-\"??_);_(@_)"));
			fillTotStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
			fillTotStyle.setBorderTop(XSSFCellStyle.BORDER_THICK);
			fillTotStyle.setFont(font);
			
			CellStyle fillPerStyle = workbook.createCellStyle();
			fillPerStyle.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
			fillPerStyle.setDataFormat(df.getFormat("0%"));
			fillPerStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
			fillPerStyle.setBorderBottom(XSSFCellStyle.BORDER_DOUBLE);
			fillPerStyle.setFont(font);
			
			XSSFFont whiteFont=workbook.createFont();
			whiteFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			whiteFont.setColor(IndexedColors.WHITE.getIndex());
			CellStyle headerFillStyle = workbook.createCellStyle();
			headerFillStyle.setAlignment(CellStyle.ALIGN_CENTER);
			headerFillStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex());
			headerFillStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
			headerFillStyle.setFont(whiteFont);
			
			// Iterate over data and write to sheet
			Set<String> keyset = data.keySet();
			int rownum = 0;
			for (String key : keyset) {
				Row row = sheet.createRow(rownum++);
				List<Object> objArr = data.get(key);
				
				if (rownum > 0) {
					Cell cell = row.createCell(0);
					if("AAAAAA".equalsIgnoreCase(key))
						cell.setCellValue("Profit & Loss Statement");
					else{
						String[] grps=key.split("~`");
						if("AAAAAB".equalsIgnoreCase(grps[0])){
							grps[0]="Revenue";
							startRow=rownum;
						}
						if(prevGroup.equalsIgnoreCase(grps[0]))
							cell.setCellValue(grps[1]);
						else{
							if(prevGroup.equalsIgnoreCase("Revenue")){
								cell.setCellValue("Total Revenue");
								cell.setCellStyle(fillStyle);
								
								for(int h=0;h<=colIndex+1;h++){
									cell = row.createCell(h+1);
									cell.setCellFormula("SUM("+ excelCol[h] + startRow + ":"
											+ excelCol[h] + (rownum-2) + ")");
									cell.setCellStyle(fillStyle);
								}
								revenueRow=rownum;
								row = sheet.createRow(rownum++);
								cell = row.createCell(0);
								startRow=rownum+1;
							}
							cell.setCellValue(grps[0]);
							cell.setCellStyle(boldCs);
							row = sheet.createRow(rownum++);
							cell = row.createCell(0);
							cell.setCellValue(grps[1]);
							prevGroup=grps[0];
						}
					}
					if(rownum<=1)
						cell.setCellStyle(headerFillStyle);
				}
				
				int cellnum = 1;
				for (Object obj : objArr) {
					Cell cell = row.createCell(cellnum++);
					if (obj instanceof String){
						if(((String)obj).indexOf("-")>-1 && ((String)obj).length()>1)
							cell.setCellStyle(headerFillStyle);
						else if(((String)obj).indexOf("-")>-1)
							cell.setCellStyle(centerCs);
						cell.setCellValue((String) obj);
					}
					else if (obj instanceof Integer)
						cell.setCellValue((Integer) obj);
					else if (obj instanceof Double){
						cell.setCellValue((Double) obj);
						cell.setCellStyle(numberCs);
					}
				}
				int colSize = objArr.size();
				for (; colSize <= colIndex;) {
					Cell cell = row.createCell(cellnum++);
					cell.setCellValue("-");
					cell.setCellStyle(centerCs);
					colSize++;
				}
				
				if(rownum==1){
					Cell cell = row.createCell(cellnum++);
					cell.setCellStyle(headerFillStyle);
					cell.setCellValue("YTD AS ON "+toDate);
				}else{
					Cell cell = row.createCell(cellnum++);
					cell.setCellFormula("SUM(B" + rownum + ":"
							+ excelCol[colIndex] + rownum + ")");
					cell.setCellStyle(boldNumberCs);
				}
				
			}
			Row row = sheet.createRow(rownum++);
			int currColIndex=((data.get("AAAAAA").size())*1)+1;
			//Calculate Grant Total
			for (int i = 0; i <= currColIndex; i++) {
				Cell cell = row.createCell(i);
				if(i==0){
					cell.setCellValue("Operating Expense");
					cell.setCellStyle(fillStyle);
				}else{
					cell.setCellFormula("SUM("+ excelCol[i-1]+startRow + ":"
							+ excelCol[i-1] + (rownum-1) + ")");
					cell.setCellStyle(fillStyle);
				}
			}
			operRow=rownum;
			row = sheet.createRow(rownum++);
			for (int i = 0; i <= currColIndex; i++) {
				Cell cell = row.createCell(i);
				if(i==0){
					cell.setCellValue("Operating Profit/(Loss)");
					cell.setCellStyle(fillTotStyle);
				}else{
					cell.setCellFormula("+"+excelCol[i-1]+revenueRow+"-"+excelCol[i-1]+operRow);
					cell.setCellStyle(fillTotStyle);
				}
			}
			row = sheet.createRow(rownum++);
			for (int i = 0; i <= currColIndex; i++) {
				Cell cell = row.createCell(i);
				if(i==0){
					cell.setCellValue("Margin %");
					cell.setCellStyle(fillPerStyle);
				}else{
					cell.setCellFormula("IFERROR("+excelCol[i-1]+(rownum-1)+"/"+excelCol[i-1]+"$"+revenueRow+",0)");
					cell.setCellStyle(fillPerStyle);
				}
			}
			
			for(int i=0;i<=currColIndex;i++)
				sheet.autoSizeColumn(i);
			
				workbook.write(out);
			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println("data2.xlsx written successfully on disk.");
		return fileName;
	}
    
}
