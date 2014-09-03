package com.vapp.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import com.vapp.model.MasterData;

public interface VAPPDao {
	public boolean checkTempData(String txnDate, Connection conn);
	public void getGroupMasterData(Connection conn);
	public void getIgnoreLedgerList(Connection conn);
	public boolean deleteIgnoreLedger(String ledger);
	public int deleteTempData(String txnDate);
	public int insertTempData(Connection conn, MasterData md, Date txnDate);
	public List<String> getIgnoreLedger();
	public Connection getVAPPConnection() throws ClassNotFoundException, SQLException;
}
