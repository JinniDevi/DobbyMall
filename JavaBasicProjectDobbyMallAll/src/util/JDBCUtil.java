package util;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JDBCUtil {
	/*
	 * Map<String, Object> select sqlOne(String sql) : WHERE 절이 없거나 고정되어있고 결과값이 하나로 예상되는 경우 
	 * Map<String, Object> select sqlOne(String sql, List<Object> param) : WHERE절이 있으며, 결과값이 하나로 예상되는 경우
	 * List<Map<String, Object>> selectList(String sql) : WHERE 절이 없거나 고정되어있고 결과값이 한 개 이상으로 예상되는 경우
	 * List<Map<String, Object>> selectList(String sql, List<Object> param) : WHERE 절이 있으며, 결과 값이 한개 이상으로 예상되는 경우
	 * int update(String sql) : INSERT, UPDATE, DELETE를 사용하되 sql에 물음표가 없는 경우
	 * int update(String sql, List<Object> param) : INSERT, UPDATE, DELETE를 사용하되 sql에 물음표가 있는 경우
	 */
	
	
	// 싱글톤 패턴 : 인스턴스(객체)의 생성을 제한하여 하나의 인스턴스(객체)만 사용하는 디자인 패턴
	// 인스턴스를 보관할 변수
	private static JDBCUtil instance = null; //상속받더라도 자손객체가 접근 불가능
	
	// JDBCUtil 객체를 만들수 없게(인스턴스화할 수 없게) private으로 제한함
	private JDBCUtil() {} //생성자 private 으로 하면 객체를 만들수 없음...
	
	public static JDBCUtil getInstance() {
		if(instance == null) instance = new JDBCUtil();
		return instance; // 실제로는 하나만 객체 생성한다.
	}
	
	//이 형태를 외워야 한다.
	
	//배포할 때 url이나 계정 pw 암호화 하기위해 private
	//변경불가 final상수
	//221211 이혜연 home : 172.30.1.80
	//192.168.0.10
	//306호 192.168.36.28
	
	
	/*private final String URL="jdbc:oracle:thin:@192.168.36.28:1521:xe";
	private final String USER ="HYHY";
	private final String PASSWORD ="tiger";
	*/
	
	private final String URL="jdbc:oracle:thin:@localhost:1521:xe";
	private final String USER ="dobbymall";
	private final String PASSWORD ="java";
	
	
	private Connection conn = null;
	private PreparedStatement ps = null;
	private ResultSet rs = null;
	private CallableStatement cstmt = null;
	
	
	//WHERE 절이 없거나 고정되어있고 결과값이 하나로 예상되는 경우 
	public Map<String, Object> selectOne(String sql) {
		// sql => "SELECT * FROM JAVA_BOARD
		//         WHERE BO_NUMBER=(SELECT MAX(BO_NUMBER) FROM JAVA_BOARD)"
		// sql => "SELECT * FROM JAVA_MEMBER WHERE MEM_ID='a001' AND MEM_PASS='1234'"
		Map<String, Object> row = null;
		
		try {
			conn = DriverManager.getConnection(URL, USER, PASSWORD);
			conn.prepareStatement("ALTER SESSION SET NLS_DATE_FORMAT = 'YYYY-MM-DD'").execute();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery(); // SELECT문
			ResultSetMetaData rsmd = rs.getMetaData(); //rs에 들어있는 메타데이터 요청
			int columnCount = rsmd.getColumnCount(); //컬럼의 갯수를 가져옴
			
			while (rs.next()) { // 포인터 한칸 내리기
				row = new HashMap<>(); //null이였던 row에 빈 HashMap넣어줌
				for (int i = 1; i <= columnCount ; i++) {
					String key = rsmd.getColumnName(i); //한 컬럼에 값 하나씩 넣기
					Object value = rs.getObject(i); // 값이 어떤 타입인지 몰라서 Object
					row.put(key, value); //
				}
			}
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) try { rs.close();} catch (Exception e) {}
			if(ps != null) try {ps.close();} catch(Exception e ) {}
			if(conn != null) try { conn.close();} catch (Exception e ) {}
		}
		
		
		
		return row;
	}
	
	// sql = "SELECT * FROM JAVA_BOARD WHERE WRITER LIKE '%' ||?||'%' OR TITLE=? LIKE '%'||?||'%'"
	//=> param =["홍길동". "안녕"]
	
	
	//WHERE절이 있으며, 결과값이 하나로 예상되는 경우
	public Map<String, Object> selectOne(String sql, List<Object> param) {
		
		//sql => "SELECT * FROM JAVA_BOARD WHERE BO_NUMBER=?"
		//=> param =[1]
		// sql => " SELECT * FROM JAVA_MEMBER WHERE MEM_ID=? AND MEM_PASS=? "
		//=> param =["admin", "1234"]
		
		Map<String, Object> row = null;
		
		try {
			conn = DriverManager.getConnection(URL, USER, PASSWORD);
			conn.prepareStatement("ALTER SESSION SET NLS_DATE_FORMAT = 'YYYY-MM-DD'").execute();
			ps = conn.prepareStatement(sql);
			
			for (int i = 0; i < param.size(); i++) { // 0부터 시작하되. 빈공간은 i+1
				ps.setObject(i+1, param.get(i));
			}
			rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			while (rs.next()) {
				row = new HashMap<>();
				for (int i = 1; i <= columnCount; i++) {
					String key = rsmd.getColumnName(i);
					Object value = rs.getObject(i);
					row.put(key, value);
				}
			}
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) try { rs.close();} catch (Exception e) {}
			if(ps != null) try {ps.close();} catch(Exception e ) {}
			if(conn != null) try { conn.close();} catch (Exception e ) {}
		}
		
		return row;
	}
	
	// WHERE 절이 없거나 고정되어있고 결과값이 한 개 이상으로 예상되는 경우
	public List<Map<String, Object>> selectList(String sql) {
		// sql => "SELECT * FROM JAVA_BOARD"
		// sql => "SELECT * FROM JAVA_MEMBER"
		// sql => "SELECT * FROM JAVA_BOARD WHERE BO_NUMBER < 10" //페이징 처리
		
		List<Map<String, Object>> list = null;
		
		try {
			conn = DriverManager.getConnection(URL, USER, PASSWORD);
			conn.prepareStatement("ALTER SESSION SET NLS_DATE_FORMAT = 'YYYY-MM-DD'").execute();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			// 몇행인지 몰라서 메타데이터 불러옴
			ResultSetMetaData rsmd = ps.getMetaData();
			int columnCount = rsmd.getColumnCount();
			while (rs.next()) {
				if(list == null) list = new ArrayList<>();
				Map<String, Object> row = new HashMap<>();
				
				for (int i = 1; i <= columnCount; i++) {
					String key = rsmd.getColumnName(i);
					Object value = rs.getObject(i);
					row.put(key, value);
				}
				
				list.add(row);
			}
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) try { rs.close();} catch (Exception e) {}
			if(ps != null) try {ps.close();} catch(Exception e ) {}
			if(conn != null) try { conn.close();} catch (Exception e ) {}
		}
		
		return list;
	}
	
	//WHERE 절이 있으며, 결과 값이 한개 이상으로 예상되는 경우
	public List<Map<String, Object>> selectList(String sql, List<Object> param){
		// sql = "SELECT * FROM JAVA_BOARD WHERE WRITER LIKE '%' ||?||'%' OR TITLE=? LIKE '%'||?||'%'"
		// param =>["홍","안녕"]
		// param =>["홍","홍"]
		List<Map<String, Object>> list = null;
		
		try {
			conn = DriverManager.getConnection(URL, USER, PASSWORD);
			conn.prepareStatement("ALTER SESSION SET NLS_DATE_FORMAT = 'YYYY-MM-DD'").execute();
			ps = conn.prepareStatement(sql);
			for (int i = 0; i < param.size(); i++) {
				ps.setObject(i+1, param.get(i));
			}
			rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			while (rs.next()) {
				if(list == null) list = new ArrayList<>();
				Map<String, Object> row = new HashMap<>();
				
				for (int i = 1; i <= columnCount; i++) {
					String key = rsmd.getColumnName(i);
					Object value = rs.getObject(i);
					row.put(key, value);
				}
				list.add(row);
			}
			
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) try { rs.close();} catch (Exception e) {}
			if(ps != null) try {ps.close();} catch(Exception e ) {}
			if(conn != null) try { conn.close();} catch (Exception e ) {}
		}
		
		return list;
	}
	
	
	//INSERT, UPDATE, DELETE를 사용하되 sql에 물음표가 없는 경우
	public int update(String sql) {
		// sql => "INSERT INTO JAVA_MEMBER (MEM_ID,MEM_PASS,MEM_NAME)
		//         VALUES ('a001','1234','김은대') 완성된 쿼리가 넘어온 경우
		// 권장 = ? 쓰기
		int result = 0;
		try {
			conn = DriverManager.getConnection(URL, USER, PASSWORD);
			conn.prepareStatement("ALTER SESSION SET NLS_DATE_FORMAT = 'YYYY-MM-DD'").execute();
			ps = conn.prepareStatement(sql);
			result = ps.executeUpdate();
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) try { rs.close();} catch (Exception e) {}
			if(ps != null) try {ps.close();} catch(Exception e ) {}
			if(conn != null) try { conn.close();} catch (Exception e ) {}
		}
		

		return result;
	}
	
	
	
	//INSERT, UPDATE, DELETE를 사용하되 sql에 물음표가(?) 있는 경우
	public int update(String sql, List<Object> param) {
		//sql => "INSERT INTO JAVA_MEMBER (MEM_ID,MEM_PASS,MEM_NAME)  VALUES (?,?,?) "
		//param => ["a001","1234","김은대"] // param.add() 3개
		//sql => " UPDATE JAVA_MEMBER SET MEM_NAME=? WHERE MEM_ID=? "
		//param => ["홍길동","a001"]
		//sql => " DELETE FROM JAVA_BOARD WHERE BO_WRITER=? "
		//param => ["홍길동"]
		
		
		int result = 0;
		
		try {
			conn = DriverManager.getConnection(URL, USER, PASSWORD);
			conn.prepareStatement("ALTER SESSION SET NLS_DATE_FORMAT = 'YYYY-MM-DD'").execute();
			ps = conn.prepareStatement(sql);
			for (int i = 0; i < param.size(); i++) {
				ps.setObject(i+1, param.get(i));
			}
			result = ps.executeUpdate();
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) try { rs.close();} catch (Exception e) {}
			if(ps != null) try {ps.close();} catch(Exception e ) {}
			if(conn != null) try { conn.close();} catch (Exception e ) {}
		}
		return result;
	}

	public int excute(String sql, List<Object> param) {
		
		int result = 1;
		
		try {
			conn = DriverManager.getConnection(URL, USER, PASSWORD);
			conn.prepareStatement("ALTER SESSION SET NLS_DATE_FORMAT = 'YYYY-MM-DD'").execute();
			cstmt = conn.prepareCall(sql);
			for (int i = 0; i < param.size(); i++) {
				cstmt.setObject(i+1, param.get(i));
			}
			cstmt.execute();
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) try { rs.close();} catch (Exception e) {}
			if(cstmt != null) try {cstmt.close();} catch(Exception e ) {}
			if(conn != null) try { conn.close();} catch (Exception e ) {}
		}
		return result;
	}
	
	
}
