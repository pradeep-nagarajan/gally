package com.vapp.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

	
}
