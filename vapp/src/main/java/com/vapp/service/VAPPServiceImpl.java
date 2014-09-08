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

@Service
public class VAPPServiceImpl implements VAPPService
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
		
			int colIndex = data.get("Ledger~`MIS Grouping").size()-1;
			
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
					cell.setCellValue(key.substring(0, key.indexOf("~`")));
					Cell cell2 = row.createCell(1);
					cell2.setCellValue(key.substring(key.indexOf("~`")+2));
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
			int currColIndex=((data.get("Ledger~`MIS Grouping").size())*2)+1;
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
    
}
