package com.vapp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.vapp.model.MasterData;

public class ReadExcelDemo 
{
	static final String INSERT_SQL="INSERT INTO VAPP_UPLOADED_TEMP VALUES(MST_DATA_seq.nextval, ?, ?, to_date(?,'DD/MM/RRRR'), ?, ?)";
	static final String SELECT_SQL="SELECT LEDGER, GRP_MST_ID FROM VAPP_GROUP_MASTER";
	static final String EXISTS_SQL="SELECT 1 FROM VAPP_UPLOADED_TEMP WHERE to_char(TXN_DATE,'MM-YYYY')=?";
	static final String DELETE_SQL="DELETE FROM VAPP_UPLOADED_TEMP WHERE to_char(TXN_DATE,'MM-YYYY')=?";
	static final Map<String, Integer> hm=new HashMap<String, Integer>();
	static final List<String> ignoreLedger=new ArrayList<String>();
	
	static{
		ignoreLedger.add("Sales Accounts");
		ignoreLedger.add("Direct Expenses");
		ignoreLedger.add("Indirect Expenses");
		ignoreLedger.add("");
		ignoreLedger.add("Particulars");
		ignoreLedger.add(null);
		ignoreLedger.add("Grand Total");
	}
	
    public static void main(String[] args) 
    {
    	ReadExcelDemo red=new ReadExcelDemo();
    	red.readAndInsert("01-09-2014", "");
    }
    
    public boolean readAndInsert(String fileName,String fileFullPath){
    	Connection conn=null;
    	boolean result=false;
    	try {
			conn = getConnection("jdbc:oracle:thin:@localhost:1521:XE",
					"techdash", "techdash");
			conn.setAutoCommit(false);
			String[] a=fileName.split("-");
			int[] b={Integer.parseInt(a[0]),(Integer.parseInt(a[1])-1),Integer.parseInt(a[2])};
			Date txnDate=new Date(b[2], b[1], b[0]);
			String txnDateStr=a[1]+"-"+a[2];
			if(!checkData(txnDateStr, conn)){
				getGroupMasterData(conn);
				readXLS(conn,txnDate, fileFullPath);
				result=true;
			}else{
				deleteData(conn, txnDateStr);
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
                    /*switch (cell.getCellType()) 
                    {
                        case Cell.CELL_TYPE_NUMERIC:
                        	//cell.setCellType(Cell.CELL_TYPE_STRING);
                        	BigDecimal ab=new BigDecimal(cell.getNumericCellValue());
                            System.out.print(ab.setScale(2, RoundingMode.HALF_EVEN) + "\t\t");
                            break;
                        case Cell.CELL_TYPE_STRING:
                            System.out.print(cell.getStringCellValue() + "\t\t\t");
                            break;
                    }*/
                    if(colCount==0){
                    	if(ignoreLedger.contains(cell.getStringCellValue().trim()))
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
                	insertData(conn, md, txnDate);
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
    
    public boolean checkData(String txnDate, Connection conn){
    	PreparedStatement stmt = null;
		ResultSet rset = null;
		boolean result=false;
		try {
			stmt = conn.prepareStatement(EXISTS_SQL);
			stmt.setString(1, txnDate);
			rset = stmt.executeQuery();
			if(rset.next()){
				result=true;
				System.out.println("Records Exists!");
			}else{
				System.out.println("Records not exists!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				if(rset!=null)
					rset.close();
				if(stmt!=null)
					stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
    }
    
    public int deleteData(Connection conn, String txnDate){
    	PreparedStatement stmt=null;
    	int i=0;
    	try {
			stmt=conn.prepareStatement(DELETE_SQL);
			stmt.setString(1, txnDate);
			i=stmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try{
				if(stmt!=null)
					stmt.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
    	
    	return i;
    	
    }
    
    public int insertData(Connection conn, MasterData md, Date txnDate){
    	PreparedStatement stmt=null;
    	int i=0;
    	try {
    		System.out.println(md.toString());
			stmt=conn.prepareStatement(INSERT_SQL);
			if(hm.containsKey(md.getLedger()))
				stmt.setInt(1, hm.get(md.getLedger()));
			else
				stmt.setInt(1, -1);
			stmt.setString(2, md.getLedger());
			stmt.setDate(3, txnDate);
			stmt.setString(4, md.getCrDr());
			stmt.setBigDecimal(5, md.getAmount());
			i=stmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try{
				if(stmt!=null)
					stmt.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
    	
    	return i;
    }
    
    private Connection getConnection(String url, String user, String pwd)
			throws ClassNotFoundException, SQLException {
		Connection conn;
		Class.forName("oracle.jdbc.driver.OracleDriver");

		conn = DriverManager.getConnection(url, user, pwd);
		return conn;
	}
    
    public void getGroupMasterData(Connection conn){
		Statement stmt = null;
		ResultSet rset = null;
		
		try {
			stmt = conn.createStatement();
			rset = stmt.executeQuery(SELECT_SQL);
			while(rset.next()){
				hm.put(rset.getString(1), rset.getInt(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				if(rset!=null)
					rset.close();
				if(stmt!=null)
					stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
    }
    
}
