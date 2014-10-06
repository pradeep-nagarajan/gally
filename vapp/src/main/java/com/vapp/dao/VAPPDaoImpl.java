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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.springframework.stereotype.Component;

import com.vapp.model.GroupData;
import com.vapp.model.MasterData;

@Component
public class VAPPDaoImpl implements VAPPDao {

	static final String SEL_GRP_SQL = "SELECT LEDGER, GRP_MST_ID FROM VAPP_GROUP_MASTER";
	static final String SEL_GRP_DET_SQL = "SELECT MAIN_GRP, MIS_GRP, MAIN_GRP||'~`'|| MIS_GRP ||'~`'|| LEDGER "
			+ "FROM VAPP_GROUP_MASTER ORDER BY 3";
	static final String INS_GRP_SQL = "INSERT INTO VAPP_GROUP_MASTER VALUES(GRP_MST_seq.nextval, ?, ?, ?)";
	static final String DEL_GRP_SQL = "DELETE FROM VAPP_GROUP_MASTER WHERE LEDGER=?";
	static final String UPD_GRP_SQL = "UPDATE VAPP_GROUP_MASTER SET MAIN_GRP=?,MIS_GRP=? WHERE LEDGER=?";
	static final String SEL_IGNORE_SQL = "SELECT LEDGER FROM VAPP_IGNORE_LEDGER";
	static final String EXISTS_SQL = "SELECT 1 FROM VAPP_UPLOADED_TEMP WHERE to_char(TXN_DATE,'MM-YYYY')=?";
	static final String SEL_TEMP_SQL = "SELECT DISTINCT LEDGER FROM VAPP_UPLOADED_TEMP WHERE GRP_ID=-1 ORDER BY LEDGER";
	static final String INSERT_TEMP_SQL = "INSERT INTO VAPP_UPLOADED_TEMP VALUES(MST_DATA_seq.nextval, ?, ?, "
			+ "to_date(?,'DD/MM/RRRR'), ?, ?)";
	static final String DELETE_TEMP_SQL = "DELETE FROM VAPP_UPLOADED_TEMP WHERE to_char(TXN_DATE,'MM-YYYY')=?";
	static final String DEL_TEMP_SQL = "DELETE FROM VAPP_UPLOADED_TEMP WHERE LEDGER=?";
	static final String UPD_GRP_TEMP_SQL = "UPDATE VAPP_UPLOADED_TEMP set GRP_ID=-1 WHERE LEDGER=?";
	static final String UPD_TEMP_SQL = "UPDATE VAPP_UPLOADED_TEMP SET GRP_ID=(SELECT GRP_MST_ID FROM "
			+ "VAPP_GROUP_MASTER WHERE LEDGER=?) WHERE GRP_ID=-1 and LEDGER=?";
	static final String UPDATE_IGNORE_SQL = "UPDATE VAPP_IGNORE_LEDGER SET LEDGER=? where LEDGER=?";
	static final String INSERT_IGNORE_SQL = "INSERT INTO VAPP_IGNORE_LEDGER values(?)";
	static final String DEL_IGNORE_SQL = "DELETE FROM VAPP_IGNORE_LEDGER WHERE LEDGER=?";
	static final String MIS_TEMP_SQL = "SELECT temp.ledger||'~`'||master.MIS_GRP misledge, TO_CHAR (txn_date, 'DD/MM/YYYY'), "
			//+ "SUM (DECODE (cr_dr, 'CR', '-' || amount, amount)) "
			+ "SUM (DECODE (master.main_grp,'Revenue', amount, DECODE (cr_dr, 'CR', '-' "
			+ "|| amount, amount))) amount "
			+ "FROM vapp_uploaded_temp temp, vapp_group_master master WHERE TXN_DATE between to_date(?,'DD-MM-YYYY') "
			+ "and to_date(?,'DD-MM-YYYY') "
			+ "and grp_mst_id=grp_id "
			+ "group by temp.ledger||'~`'||master.MIS_GRP, txn_date "
			+ "order by to_char(TXN_DATE,'YYYYMMDD'), misledge";
	static final String PL_TEMP_SQL="SELECT DECODE(main_grp,'Revenue','AAAAAB',main_grp)||'~`'||MIS_GRP,TO_CHAR (txn_date, 'Mon-YY'),"
			//+ "SUM (DECODE (cr_dr, 'CR', '-' || amount, amount)) amount "
			+ "SUM (DECODE (master.main_grp,'Revenue', amount, DECODE (cr_dr, 'CR', '-' "
			+ "|| amount, amount))) amount "
			+"FROM vapp_uploaded_temp, vapp_group_master master "
			+"WHERE grp_mst_id = grp_id AND TXN_DATE between to_date(?,'DD-MM-YYYY') and to_date(?,'DD-MM-YYYY') "
			//+ "AND main_grp = 'Revenue' "
			+"group by DECODE(main_grp,'Revenue','AAAAAB',main_grp)||'~`'||MIS_GRP,TXN_DATE "
			+ "order by to_char(TXN_DATE,'YYYYMMDD'), 1";

	static Map<String, Integer> hm = new HashMap<String, Integer>();
	static List<String> ignoreLedger = new ArrayList<String>();

	public Map<String, Integer> getHm() {
		return hm;
	}

	public List<String> getIgnoreLedger() {
		return ignoreLedger;
	}

	public boolean checkTempData(String txnDate, Connection conn) {
		PreparedStatement stmt = null;
		ResultSet rset = null;
		boolean result = false;
		try {
			stmt = conn.prepareStatement(EXISTS_SQL);
			stmt.setString(1, txnDate);
			rset = stmt.executeQuery();
			if (rset.next()) {
				result = true;
				System.out.println("Records Exists!");
			} else {
				System.out.println("Records not exists!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rset != null)
					rset.close();
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public void getGroupMasterData(Connection conn) {
		Statement stmt = null;
		ResultSet rset = null;
		hm = new HashMap<String, Integer>();
		try {
			stmt = conn.createStatement();
			rset = stmt.executeQuery(SEL_GRP_SQL);
			while (rset.next()) {
				hm.put(rset.getString(1), rset.getInt(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rset != null)
					rset.close();
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void getIgnoreLedgerList(Connection conn) {
		Statement stmt = null;
		ResultSet rset = null;
		ignoreLedger = new ArrayList<String>();
		ignoreLedger.add("");
		ignoreLedger.add(null);
		try {
			stmt = conn.createStatement();
			rset = stmt.executeQuery(SEL_IGNORE_SQL);
			while (rset.next()) {
				ignoreLedger.add(rset.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rset != null)
					rset.close();
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public List<String> deleteIgnoreLedger(String ledger) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getVAPPConnection();
			ps = conn.prepareStatement(DEL_IGNORE_SQL);
			ps.setString(1, ledger);
			int i = ps.executeUpdate();
			if (i >= 1) {
				getIgnoreLedgerList(conn);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return ignoreLedger;
	}

	public List<String> insertIgnoreLedger(String ledger) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getVAPPConnection();
			ps = conn.prepareStatement(INSERT_IGNORE_SQL);
			ps.setString(1, ledger);
			int i = ps.executeUpdate();
			if (i >= 1) {
				getIgnoreLedgerList(conn);
				ps = conn.prepareStatement(DEL_TEMP_SQL);
				ps.setString(1, ledger);
				ps.executeUpdate();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return ignoreLedger;
	}

	public List<String> updateIgnoreLedger(String ledger, String newLedger) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getVAPPConnection();
			ps = conn.prepareStatement(UPDATE_IGNORE_SQL);
			ps.setString(1, newLedger);
			ps.setString(2, ledger);
			int i = ps.executeUpdate();
			if (i >= 1) {
				getIgnoreLedgerList(conn);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return ignoreLedger;
	}

	public int deleteTempData(String txnDate) {
		Connection conn = null;
		PreparedStatement stmt = null;
		int i = 0;
		try {
			conn = getVAPPConnection();
			stmt = conn.prepareStatement(DELETE_TEMP_SQL);
			stmt.setString(1, txnDate);
			i = stmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return i;
	}

	public int insertTempData(Connection conn, MasterData md, Date txnDate) {
		PreparedStatement stmt = null;
		int i = 0;
		try {
			stmt = conn.prepareStatement(INSERT_TEMP_SQL);
			if (hm.containsKey(md.getLedger()))
				stmt.setInt(1, hm.get(md.getLedger()));
			else
				stmt.setInt(1, -1);
			stmt.setString(2, md.getLedger());
			stmt.setDate(3, txnDate);
			stmt.setString(4, md.getCrDr());
			stmt.setBigDecimal(5, md.getAmount());
			i = stmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return i;
	}

	public List<String> getTempData() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rset = null;
		List<String> tempData = new ArrayList<String>();
		try {
			conn = getVAPPConnection();
			stmt = conn.createStatement();
			rset = stmt.executeQuery(SEL_TEMP_SQL);
			while (rset.next()) {
				tempData.add(rset.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rset != null)
					rset.close();
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return tempData;
	}

	public Map<String, Set<String>> getGroupLIst() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rset = null;
		Set<String> mainGrp = new TreeSet<String>();
		Set<String> misGrp = new TreeSet<String>();
		Set<String> allData = new TreeSet<String>();
		Map<String, Set<String>> tsm = new HashMap<String, Set<String>>();
		try {
			conn = getVAPPConnection();
			stmt = conn.createStatement();
			rset = stmt.executeQuery(SEL_GRP_DET_SQL);
			while (rset.next()) {
				mainGrp.add(rset.getString(1));
				misGrp.add(rset.getString(2));
				allData.add(rset.getString(3));
			}
			tsm.put("mainGrp", mainGrp);
			tsm.put("misGrp", misGrp);
			tsm.put("allData", allData);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rset != null)
					rset.close();
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return tsm;
	}

	public int insertGroupMasterData(GroupData groupData) {
		Connection conn = null;
		PreparedStatement stmt = null;
		int i = 0;
		try {
			conn = getVAPPConnection();
			stmt = conn.prepareStatement(INS_GRP_SQL);
			stmt.setString(1, groupData.getMainGroup());
			stmt.setString(2, groupData.getMisGroup());
			stmt.setString(3, groupData.getLedger());
			i = stmt.executeUpdate();
			if (i >= 1) {
				getGroupMasterData(conn);
				if (stmt != null)
					stmt.close();
				stmt = conn.prepareStatement(UPD_TEMP_SQL);
				stmt.setString(1, groupData.getLedger());
				stmt.setString(2, groupData.getLedger());
				stmt.executeUpdate();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return i;
	}

	public int deleteGroupMasterData(GroupData groupData) {
		Connection conn = null;
		PreparedStatement stmt = null;
		int i = 0;
		try {
			conn = getVAPPConnection();
			stmt = conn.prepareStatement(UPD_GRP_TEMP_SQL);
			stmt.setString(1, groupData.getLedger());
			stmt.executeUpdate();

			if (stmt != null)
				stmt.close();
			stmt = conn.prepareStatement(DEL_GRP_SQL);
			stmt.setString(1, groupData.getLedger());
			i = stmt.executeUpdate();
			getGroupMasterData(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return i;
	}

	public int updateGroupMasterData(GroupData groupData) {
		Connection conn = null;
		PreparedStatement stmt = null;
		int i = 0;
		try {
			conn = getVAPPConnection();
			stmt = conn.prepareStatement(UPD_GRP_SQL);
			stmt.setString(1, groupData.getMainGroup());
			stmt.setString(2, groupData.getMisGroup());
			stmt.setString(3, groupData.getLedger());
			i = stmt.executeUpdate();
			getGroupMasterData(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return i;
	}

	public Map<String, List<Object>> getMISData(String fromDate, String toDate) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rset = null;
		Map<String, List<Object>> data = new TreeMap<String, List<Object>>();
		List<Object> headerRowData = new ArrayList<Object>();
		data.put("AAAAAA", headerRowData);
		int colIndex = -1;
		String prevDate = "";

		try {
			conn = getVAPPConnection();
			stmt = conn.prepareStatement(MIS_TEMP_SQL);
			stmt.setString(1, fromDate);
			stmt.setString(2, toDate);
			rset = stmt.executeQuery();

			while (rset.next()) {
				if (!prevDate.equalsIgnoreCase(rset.getString(2))) {
					colIndex++;
					prevDate = rset.getString(2);
					headerRowData.add(prevDate);
					data.put("AAAAAA", headerRowData);
				}
				List<Object> rowData;
				if (data.containsKey(rset.getString(1)))
					rowData = data.get(rset.getString(1));
				else
					rowData = new ArrayList<Object>();
				for (; rowData.size() < colIndex;)
					rowData.add("-");
				rowData.add(rset.getDouble(3));
				data.put(rset.getString(1), rowData);

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rset != null)
					rset.close();
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return data;
	}

	public Map<String, List<Object>> getPLReport(String fromDate, String toDate) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rset = null;
		Map<String, List<Object>> data = new TreeMap<String, List<Object>>();
		List<Object> headerRowData = new ArrayList<Object>();
		data.put("AAAAAA", headerRowData);
		int colIndex = -1;
		String prevDate = "";

		try {
			conn = getVAPPConnection();
			stmt = conn.prepareStatement(PL_TEMP_SQL);
			stmt.setString(1, fromDate);
			stmt.setString(2, toDate);
			rset = stmt.executeQuery();

			while (rset.next()) {
				if (!prevDate.equalsIgnoreCase(rset.getString(2))) {
					colIndex++;
					prevDate = rset.getString(2);
					headerRowData.add(prevDate);
					data.put("AAAAAA", headerRowData);
				}
				List<Object> rowData;
				if (data.containsKey(rset.getString(1)))
					rowData = data.get(rset.getString(1));
				else
					rowData = new ArrayList<Object>();
				for (; rowData.size() < colIndex;)
					rowData.add("-");
				rowData.add(rset.getDouble(3));
				data.put(rset.getString(1), rowData);

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rset != null)
					rset.close();
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return data;
	}
	
	public Connection getConnection(String url, String user, String pwd)
			throws ClassNotFoundException, SQLException {
		Connection conn;
		Class.forName("oracle.jdbc.driver.OracleDriver");

		conn = DriverManager.getConnection(url, user, pwd);
		return conn;
	}

	public Connection getVAPPConnection() throws ClassNotFoundException,
			SQLException {
		Connection conn = null;
		conn = getConnection("jdbc:oracle:thin:@localhost:1521:XE", "techdash",
				"techdash");
		return conn;
	}


}
