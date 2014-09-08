package com.vapp.service;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vapp.model.GroupData;

public interface VAPPService {
	public boolean readAndInsert(String fileName,String fileFullPath);
	public void readXLS(Connection conn, Date txnDate, String fileFullPath);
	public Map<String, Object> getIgnoreLedger();
	public Map<String, Object> deleteIgnoreLedger(String ledger);
	public Map<String, Object> insertIgnoreLedger(String ledger);
	public Map<String, Object> updateIgnoreLedger(String ledger, String newLedger);
	public List<String> getTempData();
	public Map<String, Set<String>> insertGroupMasterData(GroupData groupData);
	public String getMISData(String fromDate, String toDate);
	public Map<String, Set<String>> getGroupLIst();
	public Map<String, Set<String>> deleteGroupMasterData(GroupData groupData);
}
