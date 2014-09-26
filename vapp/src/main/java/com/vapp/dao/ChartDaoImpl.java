package com.vapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class ChartDaoImpl implements ChartDao {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public Map<String, Double> getRevenue(String fromDate, String toDate) {
		String SQL="SELECT TO_CHAR(txn_date,'Mon-YYYY') TDATE,"+
				"SUM (DECODE (cr_dr, 'CR', '-' "+
				"|| amount, amount)) amount "+
				"FROM vapp_uploaded_temp upload, "+
				"vapp_group_master grp "+
				"WHERE grp.grp_mst_id=upload.grp_id "+
				"AND grp.main_grp    =? "+
				"AND TXN_DATE between to_date(?,'DD-MM-YYYY') "+
				"and to_date(?,'DD-MM-YYYY') "+
				"GROUP BY txn_date "+
				"order by TO_CHAR(txn_date, 'YYYYMMDD')";
		Map<String, Double> listofVal = jdbcTemplate.query(SQL, new Object[] { "Revenue", fromDate, toDate }, new ResultSetExtractor<Map<String, Double>>() {
			public Map<String, Double> extractData(ResultSet rs) throws SQLException {
				Map<String, Double> data = new LinkedHashMap<String, Double>();
				while (rs.next()) {
					data.put(rs.getString("TDATE"),rs.getDouble("amount"));
				}
				return data;
			}
		});
		
		return listofVal;
	}

	public Map<String, List<Object>> getOperatingExp(String fromDate,
			String toDate) {
		
		StringBuffer SQL=new StringBuffer();
		SQL.append("SELECT grp.main_grp, TO_CHAR(txn_date,'Mon-YYYY') TDATE, ")
		.append("SUM (DECODE (cr_dr, 'CR', '-' ")
		.append("|| amount, amount)) amount ")
		.append("FROM vapp_uploaded_temp upload, ")
		.append("vapp_group_master grp ")
		.append("WHERE grp.grp_mst_id=upload.grp_id ")
		.append("AND grp.main_grp != ? ")
		.append("GROUP BY grp.main_grp, txn_date ")
		.append("ORDER BY TO_CHAR(txn_date, 'YYYYMMDD')");

		Map<String, List<Object>> listofVal = jdbcTemplate.query(SQL.toString(), new Object[] { "Revenue"/*, fromDate, toDate*/ }, new ResultSetExtractor<Map<String, List<Object>>>() {
			public Map<String, List<Object>> extractData(ResultSet rs) throws SQLException {
				Map<String, List<Object>> data = new TreeMap<String, List<Object>>();
				List<Object> headerRowData = new ArrayList<Object>();
				data.put("AAAAAA", headerRowData);
				String prevDate = "";

			while (rs.next()) {
				
				if (!prevDate.equalsIgnoreCase(rs.getString(2))) {
					prevDate = rs.getString(2);
					headerRowData.add(prevDate);
					data.put("AAAAAA", headerRowData);
				}
				List<Object> rowData;
				if (data.containsKey(rs.getString(1)))
					rowData = data.get(rs.getString(1));
				else
					rowData = new ArrayList<Object>();
				for (; rowData.size() < headerRowData.size();)
					rowData.add(0);
				rowData.add(rs.getDouble(3));
				data.put(rs.getString(1), rowData);

			}
			return data;
			}
		});
		return listofVal;
	}

	
}
