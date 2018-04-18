package it.polito.tdp.meteo;

import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.meteo.bean.Citta;
import it.polito.tdp.meteo.bean.Rilevamento;
import it.polito.tdp.meteo.bean.SimpleCity;
import it.polito.tdp.meteo.db.MeteoDAO;

public class Model {

	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;

	private MeteoDAO dao ;
	private List<Citta> cities;
	private List<SimpleCity> soluzione;
	
	public Model() {
		dao = new MeteoDAO();
		soluzione = new ArrayList<>();
		cities = new ArrayList<>();
			
			for(Rilevamento r : dao.getAllRilevamenti()) {
				Citta cit = new Citta(r.getLocalita());
				if(!cities.contains(cit))
					cities.add(cit);
			}
	}

	public String getUmiditaMedia(int mese) {
		String ris = "";
			
		for(Citta r : cities) {
			ris += r.getNome() + " " +dao.getAvgRilevamentiLocalitaMese(mese, r.getNome()) + "\n";
		}
		return ris;
	}

	public String trovaSequenza(int mese) {
		String ris = "";
		
		List<SimpleCity> parziale = new ArrayList<>();
		List<SimpleCity> risultato = this.ricorsiva(0, mese, parziale);
		
		for(SimpleCity sc : risultato)
			ris +=sc.getNome()+"\n";
		return ris;
	}

	private Double punteggioSoluzione(List<SimpleCity> soluzioneCandidata) {

		double score = 0.0;
		for(SimpleCity sc : soluzioneCandidata)
			score += sc.getCosto();
		return score;
	}

	private boolean controllaParziale(List<SimpleCity> parziale) {
		
		return true;
	}

	private List<SimpleCity> ricorsiva(int livello, int mese, List<SimpleCity> parziale) {
		
		if(livello == NUMERO_GIORNI_TOTALI) {
			return parziale;
		}
		
		for(int i=0; i<cities.size(); i++) {
			SimpleCity sc = new SimpleCity(cities.get(i).getNome());
			parziale.add(sc);
			if(controllaParziale(parziale))
				ricorsiva(livello+1, mese, parziale);
			parziale.remove(sc);
		}
		return null;
	}
}
