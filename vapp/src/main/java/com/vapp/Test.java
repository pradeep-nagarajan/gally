package com.vapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Test {

	public static void main(String[] args) {

		// Write the workbook in file system
		FileOutputStream out = null;
		try {
			Character[] excelCol = { 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
					'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T' };
			out = new FileOutputStream(new File("D:/VAPP/tmp/data.xlsx"));
			Connection conn = getVAPPConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT LEDGER, to_char(TXN_DATE,'DD/MM/YYYY'), "
							+ "SUM(DECODE (cr_dr, 'CR', '-' || amount, amount)) "
							+ "from VAPP_UPLOADED_TEMP "
							+ "group by LEDGER,TXN_DATE "
							+ "order by to_char(TXN_DATE,'YYYYMMDD'), ledger");
			Map<String, List<Object>> data = new LinkedHashMap<String, List<Object>>();
			List<Object> headerRowData = new ArrayList<Object>();
			data.put("Ledger", headerRowData);
			int colIndex = -1;
			String prevDate = "";
			
			while (rs.next()) {
				if (!prevDate.equalsIgnoreCase(rs.getString(2))) {
					colIndex++;
					prevDate = rs.getString(2);
					headerRowData.add(prevDate);
					data.put("Ledger", headerRowData);
				}
				List<Object> rowData;
				if (data.containsKey(rs.getString(1)))
					rowData = data.get(rs.getString(1));
				else
					rowData = new ArrayList<Object>();
				for (; rowData.size() < colIndex;)
					rowData.add("-");
				rowData.add(rs.getDouble(3));
				data.put(rs.getString(1), rowData);

			}

			System.out.println(data);

			// Blank workbook
			XSSFWorkbook workbook = new XSSFWorkbook();

			// Create a blank sheet
			XSSFSheet sheet = workbook.createSheet("Net TB");
			
			XSSFFont font=workbook.createFont();
			XSSFCellStyle centerCs=workbook.createCellStyle();
			centerCs.setAlignment(CellStyle.ALIGN_CENTER);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			centerCs.setFont(font);
			XSSFDataFormat df =workbook.createDataFormat();
			XSSFCellStyle numberCs=workbook.createCellStyle();
			numberCs.setDataFormat(df.getFormat("_(* #,##0_);_(* (#,##0);_(* \"-\"??_);_(@_)"));
			// Iterate over data and write to sheet
			Set<String> keyset = data.keySet();
			int rownum = 0;
			for (String key : keyset) {
				Row row = sheet.createRow(rownum++);
				List<Object> objArr = data.get(key);
				if (rownum > 0) {
					Cell cell = row.createCell(0);
					cell.setCellValue(key);
				}
				int cellnum = 1;
				for (Object obj : objArr) {
					Cell cell = row.createCell(cellnum++);
					if (obj instanceof String){
						if("-".equals((String)obj) || ((String)obj).indexOf("/")>-1)
							cell.setCellStyle(centerCs);
						cell.setCellValue((String) obj);
					}
					else if (obj instanceof Integer)
						cell.setCellValue((Integer) obj);
					else if (obj instanceof Double){
						cell.setCellValue((Double) obj);
						cell.setCellStyle(numberCs);
					}
				}
				int colSize = objArr.size();
				for (; colSize <= colIndex;) {
					Cell cell = row.createCell(cellnum++);
					cell.setCellValue("-");
					cell.setCellStyle(centerCs);
					colSize++;
				}

				for (int i = 0; i <= colIndex; i++) {
					Cell cell = row.createCell(cellnum++);
					if (rownum > 1){
						cell.setCellFormula("SUM(B" + rownum + ":"
								+ excelCol[i] + rownum + ")");
						cell.setCellStyle(numberCs);
					}
					else{
						cell.setCellValue("YTD AS ON " + objArr.get(i));
						cell.setCellStyle(centerCs);
					}
					
				}
			}

			workbook.write(out);

			System.out.println("data.xlsx written successfully on disk.");

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static Connection getConnection(String url, String user, String pwd)
			throws ClassNotFoundException, SQLException {
		Connection conn;
		Class.forName("oracle.jdbc.driver.OracleDriver");

		conn = DriverManager.getConnection(url, user, pwd);
		return conn;
	}

	public static Connection getVAPPConnection() throws ClassNotFoundException,
			SQLException {
		Connection conn = null;
		conn = getConnection("jdbc:oracle:thin:@localhost:1521:XE", "techdash",
				"techdash");
		return conn;
	}

}
