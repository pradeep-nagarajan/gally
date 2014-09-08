package com.vapp.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vapp.model.GroupData;
import com.vapp.model.MasterData;

public interface VAPPDao {
	public boolean checkTempData(String txnDate, Connection conn);
	
	public void getIgnoreLedgerList(Connection conn);
	public List<String> deleteIgnoreLedger(String ledger);
	public List<String> insertIgnoreLedger(String ledger);
	public List<String> updateIgnoreLedger(String ledger, String newLedger);
	public int deleteTempData(String txnDate);
	public int insertTempData(Connection conn, MasterData md, Date txnDate);
	public List<String> getIgnoreLedger();
	public Connection getVAPPConnection() throws ClassNotFoundException, SQLException;
	public List<String> getTempData();
	public Map<String, List<Object>> getMISData(String fromDate, String toDate);
	public void getGroupMasterData(Connection conn);
	public int insertGroupMasterData(GroupData groupData);
	public Map<String, Set<String>> getGroupLIst();
	public int deleteGroupMasterData(GroupData groupData);
}

