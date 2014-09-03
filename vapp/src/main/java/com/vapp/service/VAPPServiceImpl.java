package com.vapp.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vapp.dao.VAPPDao;
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
    
}
