package jdbc.dao.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import config.ServerInfo;
import jdbc.dao.MemberDAO;
import jdbc.dto.Member;
import jdbc.exception.DuplicateIDException;
import jdbc.exception.RecordNotFoundException;

public class MemberDAOImpl implements MemberDAO{

	private static MemberDAOImpl dao = new MemberDAOImpl();
	
	private MemberDAOImpl() {}
	
	public static MemberDAOImpl getInstance() {
		return dao;
	}
	
	///////// DB Connection

	public Connection getConnect() throws SQLException {
		Connection conn = DriverManager.getConnection(ServerInfo.URL, ServerInfo.USER, ServerInfo.PASSWORD);
		System.out.println("DB Connect....");
		
		return conn;
	}
	
	public void closeAll(Connection conn, PreparedStatement ps) throws SQLException {
		if(ps != null) ps.close();
		if(conn != null) conn.close();
	}
	
	public void closeAll(Connection conn, PreparedStatement ps, ResultSet rs) throws SQLException {
		if(rs != null) rs.close();
		closeAll(conn, ps);
	}

	////////// Business Logic
	public int getId(Connection conn) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT seq_id.nextVal FROM member";
        ps = conn.prepareStatement(query);

        rs = ps.executeQuery();
        int sv = 0;
        if(rs.next()) {
            sv = rs.getInt(1);
        }
        return sv;
    }
	
	
	@Override
	public void insertMember(Member m) throws SQLException, DuplicateIDException {
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			conn = getConnect();
			m.setId(getId(conn));
			
			String query = "INSERT INTO member (id, name, email, phone) VALUES (seq_id.nextVal, ?, ?, ?)";
			
			ps = conn.prepareStatement(query);
			ps.setString(1,m.getName());
			ps.setString(2,m.getEmail());
			ps.setString(3,m.getPhone());
			
			if(ps.executeUpdate() != 1) 
				throw new DuplicateIDException();
			
		}finally {
			closeAll(conn, ps);
		}
		
	}

	@Override
	public void deleteMember(int id) throws SQLException, RecordNotFoundException {	
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			conn = getConnect();
			
			String query = "DELETE member WHERE id=?";
			ps = conn.prepareStatement(query);
			
			ps.setInt(1, id);
			
			if(ps.executeUpdate() != 1) 
				throw new RecordNotFoundException();
			
			
		}finally {
			closeAll(conn, ps);
		}
	}

	@Override
	public void updateMember(Member m) throws SQLException, RecordNotFoundException {
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			conn = getConnect();
			
			String query = "UPDATE member SET name=?, email=?, phone=? WHERE id=?";
			ps = conn.prepareStatement(query);
			
			ps.setString(1, m.getName());
			ps.setString(2, m.getEmail());
			ps.setString(3, m.getEmail());
			ps.setInt(4, m.getId());
			
			if(ps.executeUpdate() != 1) 
				throw new RecordNotFoundException();
			
		}finally {
			closeAll(conn, ps);
		}
		
	}

	@Override
	public Member getMember(int id) throws SQLException, RecordNotFoundException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = getConnect();
			
			String query = "SELECT name, email, phone FROM member WHERE id=?";
			
			ps = conn.prepareStatement(query);
			ps.setInt(1, id);
			
			rs = ps.executeQuery();
			
			if(rs.next())
				return new Member(rs.getString("name"), rs.getString("address"), rs.getString("phone"));
			else
				throw new RecordNotFoundException();
			
		}finally {
			closeAll(conn, ps, rs);
		}
		
	}

	@Override
	public ArrayList<Member> getMember() throws SQLException {
		ArrayList<Member> temp = new ArrayList<>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = getConnect();
			
			String query = "SELECT * FROM member";
			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();
			
			while(rs.next()) 
				temp.add(new Member(rs.getInt("id"),rs.getString("name"),rs.getString("email") ,rs.getString("phone")));
			
			return temp;
		}finally {
			closeAll(conn, ps, rs);
		}
	}

	@Override
	public ArrayList<Member> getMember(String id) throws SQLException {
		ArrayList<Member> temp = new ArrayList<>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = getConnect();
			
			String query = "SELECT * FROM member WHERE id=?";
			ps = conn.prepareStatement(query);
			
			ps.setInt(1, Integer.parseInt(id));
			rs = ps.executeQuery();
			
			while(rs.next()) 
				temp.add(new Member(rs.getInt("id"),rs.getString("name"),rs.getString("email") ,rs.getString("phone")));
			
			return temp;
		}finally {
			closeAll(conn, ps, rs);
		}
	}

}
