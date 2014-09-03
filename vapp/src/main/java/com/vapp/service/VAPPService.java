package com.vapp.service;

import java.sql.Connection;
import java.sql.Date;
import java.util.Map;

public interface VAPPService {
	public boolean readAndInsert(String fileName,String fileFullPath);
	public void readXLS(Connection conn, Date txnDate, String fileFullPath);
	public Map<String, Object> getIgnoreLedger();
	public boolean deleteIgnoreLedger(String ledger);
}
