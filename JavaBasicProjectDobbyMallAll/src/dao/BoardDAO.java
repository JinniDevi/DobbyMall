package dao;

import java.util.List;
import java.util.Map;

import util.JDBCUtil;

public class BoardDAO {
	private static BoardDAO instance = null;
	private BoardDAO () {}
	public static BoardDAO getInstance() {
		if(instance == null) instance = new BoardDAO();
		return instance;
	}
	

	JDBCUtil jdbc = JDBCUtil.getInstance();
	

	public List<Map<String, Object>> getListAll() {  // 게시판 리스트 불러오기
		String sql = "SELECT * FROM BOARD ORDER BY BO_NO DESC ";
		return jdbc.selectList(sql);
	}
	
	public int insertRow(List<Object> param) {
		// BO_NO, MEM_NO, MEM_NAME, BO_TITLE, BO_CONTENT, BO_DATETIME, BO_CNT
		String sql = " INSERT INTO BOARD "
				+ " (BO_NO, MEM_NO, MEM_NAME, BO_TITLE, BO_CONTENT, BO_DATE, BO_CNT) "
				+ " VALUES "
				+ " (BO_NO_SEQ.NEXTVAL, ?, ?, ?, ?, TO_CHAR(SYSDATE, 'YYYY-MM-DD'), 0) ";
		
		return jdbc.update(sql, param);		
	}
	
	public void increaseCount(int boNumber) {
		String sql = "UPDATE BOARD SET BO_CNT=BO_CNT+1 WHERE BO_NO= " + boNumber;
		jdbc.update(sql);
	}
	
	public Map<String, Object> getPost(int boNumber) { 
		String sql = "SELECT * FROM BOARD WHERE BO_NO= " + boNumber;
		return jdbc.selectOne(sql);
	}
	
	public int updateRow(String columnName, List<Object> param) {
		String sql = "UPDATE BOARD SET " + columnName + "=?, BO_DATE=? WHERE BO_NO=? ";
		return jdbc.update(sql, param);
	}
	
	public int deleteRow(int boNumber) {
		String sql = "DELETE FROM BOARD WHERE BO_NO= " + boNumber;
		return jdbc.update(sql);
	}
	
	public List<Map<String, Object>> getBoardList(List<Object> param) {
		String sql = " SELECT B.* FROM(SELECT ROWNUM AS RN, A.* FROM "
				+ " (SELECT * FROM BOARD ORDER BY BO_NO DESC) A) B " 
				+ " WHERE RN >= (1 + (? * (? - 1))) "
				+ " AND RN  < (1 + (? * ?)) ";
		return jdbc.selectList(sql, param);
	}
	
	
	
	
}
