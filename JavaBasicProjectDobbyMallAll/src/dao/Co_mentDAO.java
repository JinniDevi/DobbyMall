package dao;

import java.util.List;
import java.util.Map;
import util.JDBCUtil;

public class Co_mentDAO {
	private static Co_mentDAO instance = null;
	private Co_mentDAO () {}
	public static Co_mentDAO getInstance() {
		if(instance == null) instance = new Co_mentDAO();
		return instance;
	}
	
JDBCUtil jdbc = JDBCUtil.getInstance();
	
	public List<Map<String, Object>> getCommentList(int boNumber) {
		String sql = "SELECT * FROM CO_MENT WHERE BO_NO=" + boNumber + " ORDER BY C_NO ASC ";
		return jdbc.selectList(sql);
	}

	public int insertRow(List<Object> param) {
		// C_NO, C_DATETIME, C_CONTENT, BO_NO, MEM_NAME
		String sql = "INSERT INTO CO_MENT "
				+ " (C_NO, C_DATE, C_CONTENT, BO_NO, MEM_NAME) "
				+ " VALUES "
				+ " (C_NO_SEQ.NEXTVAL, TO_CHAR(SYSDATE, 'YYYY-MM-DD'), ?, ?, ?) ";  // TO_CHAR(SYSDATE, 'YYYY-MM-DD')
		return jdbc.update(sql, param);
	}
	
	public int deletRow(int reNumber) {
		String sql = "DELETE FROM CO_MENT WHERE C_NO= " + reNumber;
		return jdbc.update(sql);
	}
	
	
}
