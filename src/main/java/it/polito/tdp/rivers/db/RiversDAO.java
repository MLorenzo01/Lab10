package it.polito.tdp.rivers.db;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import it.polito.tdp.rivers.model.Flow;
import it.polito.tdp.rivers.model.River;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RiversDAO {
	private TreeMap<Integer, River> idMap;
	

	public RiversDAO() {
		this.idMap = new TreeMap<>();
	}
	public List<River> getAllRivers() {

		final String sql = "SELECT id, name FROM river";

		List<River> rivers = new LinkedList<River>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				River r = new River(res.getInt("id"), res.getString("name"));
				rivers.add(r);
				idMap.put(r.getId(), r);
			}

			conn.close();

		} catch (SQLException e) {
			// e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}

		return rivers;
	}
	public List<Flow> getAllFlows(int river) {

		final String sql = "SELECT * FROM flow where river = ?";

		List<Flow> flows = new LinkedList<>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, river);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				flows.add(new Flow(res.getDate("day").toLocalDate(), res.getDouble("flow"), idMap.get(res.getInt("river"))));
			}
			conn.close();

		} catch (SQLException e) {
			throw new RuntimeException("SQL Error");
		}

		return flows;
	}

	public List<String> getDates(int id) {
		final String sql = "SELECT f.day as d " 
						 + "FROM flow f "
						 + "WHERE f.river = ? AND (f.day = (SELECT MAX(day) FROM flow WHERE river = ?) OR f.day = (SELECT MIN(day) FROM flow WHERE river = ?)) ";

		List<String> dates = new LinkedList<String>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, id);
			st.setInt(2, id);
			st.setInt(3, id);

			ResultSet res = st.executeQuery();
			while (res.next()) {
				dates.add(res.getString("d"));
			}
			conn.close();

		} catch (SQLException e) {
			throw new RuntimeException("SQL Error");
		}
		return dates;
	}

	public Double getAVG(int id) {
		final String sql = "SELECT AVG(f.flow) AS avg "
						 + "FROM flow f "
						 + "WHERE f.river = ? "
						 + "GROUP BY f.river ";

		Double avg;
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, id);
			ResultSet res = st.executeQuery();
			res.next();
			avg = res.getDouble("avg");
			conn.close();

		} catch (SQLException e) {
			throw new RuntimeException("SQL Error");
		}
		return avg;
	}

	public Integer getNumber(int id) {
		final String sql = "SELECT COUNT(*) AS n "
						 + "FROM flow f "
						 + "WHERE f.river = ? "
						 + "GROUP BY f.river ";
		Integer num = 0;
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, id);
			ResultSet res = st.executeQuery();
			res.next();
			num = res.getInt("n");
			conn.close();


		} catch (SQLException e) {
			throw new RuntimeException("SQL Error");
		}
		return num;
	}
}
