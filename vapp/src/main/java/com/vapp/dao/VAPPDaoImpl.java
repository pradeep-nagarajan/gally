package com.vapp.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.vapp.model.MasterData;

@Component
public class VAPPDaoImpl implements VAPPDao {
	
	static final String SEL_GRP_SQL="SELECT LEDGER, GRP_MST_ID FROM VAPP_GROUP_MASTER";
	static final String SEL_IGNORE_SQL="SELECT LEDGER FROM VAPP_IGNORE_LEDGER";
	static final String EXISTS_SQL="SELECT 1 FROM VAPP_UPLOADED_TEMP WHERE to_char(TXN_DATE,'MM-YYYY')=?";
	static final String INSERT_TEMP_SQL="INSERT INTO VAPP_UPLOADED_TEMP VALUES(MST_DATA_seq.nextval, ?, ?, to_date(?,'DD/MM/RRRR'), ?, ?)";
	static final String DELETE_TEMP_SQL="DELETE FROM VAPP_UPLOADED_TEMP WHERE to_char(TXN_DATE,'MM-YYYY')=?";
	static final String UPDATE_IGNORE_SQL="UPDATE VAPP_IGNORE_LEDGER SET LEDGER=? where LEDGER=?";
	static final String INSERT_IGNORE_SQL="INSERT INTO VAPP_IGNORE_LEDGER values(?)";
	static final String DEL_IGNORE_SQL="DELETE FROM VAPP_IGNORE_LEDGER WHERE LEDGER=?";
	
	
	static Map<String, Integer> hm=new HashMap<String, Integer>();
	static List<String> ignoreLedger=new ArrayList<String>();
	
	public Map<String, Integer> getHm() {
		return hm;
	}
	public List<String> getIgnoreLedger() {
		return ignoreLedger;
	}
	
	public boolean checkTempData(String txnDate, Connection conn){
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
	
	public void getGroupMasterData(Connection conn){
		Statement stmt = null;
		ResultSet rset = null;
		hm=new HashMap<String, Integer>();
		try {
			stmt = conn.createStatement();
			rset = stmt.executeQuery(SEL_GRP_SQL);
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
    
    public void getIgnoreLedgerList(Connection conn){
		Statement stmt = null;
		ResultSet rset = null;
		ignoreLedger=new ArrayList<String>();
		ignoreLedger.add("");
		ignoreLedger.add(null);
		try {
			stmt = conn.createStatement();
			rset = stmt.executeQuery(SEL_IGNORE_SQL);
			while(rset.next()){
				ignoreLedger.add(rset.getString(1));
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
	
	public List<String> deleteIgnoreLedger(String ledger){
		Connection conn=null;
		PreparedStatement ps=null;
		boolean result=false;
    	try {
			conn = getVAPPConnection();
			ps=conn.prepareStatement(DEL_IGNORE_SQL);
			ps.setString(1, ledger);
			int i=ps.executeUpdate();
			if(i>=1){
				result=true;
				getIgnoreLedgerList(conn);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				if(conn!=null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
    	return ignoreLedger;
	}
	
	public List<String> insertIgnoreLedger(String ledger){
		Connection conn=null;
		PreparedStatement ps=null;
		boolean result=false;
    	try {
			conn = getVAPPConnection();
			ps=conn.prepareStatement(INSERT_IGNORE_SQL);
			ps.setString(1, ledger);
			int i=ps.executeUpdate();
			if(i>=1){
				result=true;
				getIgnoreLedgerList(conn);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				if(conn!=null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
    	return ignoreLedger;
	}
	
	public List<String> updateIgnoreLedger(String ledger, String newLedger){
		Connection conn=null;
		PreparedStatement ps=null;
		boolean result=false;
    	try {
			conn = getVAPPConnection();
			ps=conn.prepareStatement(UPDATE_IGNORE_SQL);
			ps.setString(1, newLedger);
			ps.setString(2, ledger);
			int i=ps.executeUpdate();
			if(i>=1){
				result=true;
				getIgnoreLedgerList(conn);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				if(conn!=null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
    	return ignoreLedger;
	}
	
	public int deleteTempData(String txnDate){
		Connection conn=null;
    	PreparedStatement stmt=null;
    	int i=0;
    	try {
    		conn = getVAPPConnection();
			stmt=conn.prepareStatement(DELETE_TEMP_SQL);
			stmt.setString(1, txnDate);
			i=stmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
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
    
    public int insertTempData(Connection conn, MasterData md, Date txnDate){
    	PreparedStatement stmt=null;
    	int i=0;
    	try {
			stmt=conn.prepareStatement(INSERT_TEMP_SQL);
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
	
	public Connection getConnection(String url, String user, String pwd)
			throws ClassNotFoundException, SQLException {
		Connection conn;
		Class.forName("oracle.jdbc.driver.OracleDriver");

		conn = DriverManager.getConnection(url, user, pwd);
		return conn;
	}
    
    public Connection getVAPPConnection() throws ClassNotFoundException, SQLException{
    	Connection conn=null;
    	conn = getConnection("jdbc:oracle:thin:@localhost:1521:XE",
				"techdash", "techdash");
    	return conn;
    }
}
