package com.vapp.service;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;

import com.vapp.dao.VAPPDao;

public class VAPPCommonService {
	@Autowired
	VAPPDao vdi;
	public void initiateAll(){
    	Connection conn=null;
    	try {
			conn = vdi.getVAPPConnection();
			vdi.getGroupMasterData(conn);
			vdi.getIgnoreLedgerList(conn);
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
    }
	
}
