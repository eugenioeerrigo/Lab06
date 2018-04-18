package it.polito.tdp.meteo.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.meteo.bean.Rilevamento;

public class MeteoDAO {

	public List<Rilevamento> getAllRilevamenti() {

		final String sql = "SELECT Localita, Data, Umidita FROM situazione ORDER BY data ASC";

		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>();

		try {
			Connection conn = DBConnect.getInstance().getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				rilevamenti.add(r);
			}

			conn.close();
			return rilevamenti;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public List<Rilevamento> getAllRilevamentiLocalitaMese(int mese, String localita) {
		
		List<Rilevamento> rilev = new ArrayList<>();
		for(Rilevamento r : this.getAllRilevamenti()) {
			if(r.getLocalita().compareTo(localita)==0 && (r.getData().getMonth()+1)==mese) {
				rilev.add(r);
			}
		}
		
		return rilev;
	}

	public Double getAvgRilevamentiLocalitaMese(int mese, String localita) {

		int um = 0;
		int i = 0;
		for(Rilevamento r: this.getAllRilevamentiLocalitaMese(mese, localita)) {
			um += r.getUmidita();
			i++;
		}
		if(i==0)
			return 0.0;
		return (double)(um/i);
	}

}
