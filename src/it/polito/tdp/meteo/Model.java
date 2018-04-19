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
	
	public Model() {
		dao = new MeteoDAO();
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
		int c1 = 0 ;
		int c2 = 0;
		int c3 = 0;
		int def = 0;
		for(SimpleCity s : parziale) {
			if(s.getNome().compareTo(cities.get(0).getNome())==0)
				c1++;
			if(s.getNome().compareTo(cities.get(1).getNome())==0)
				c2++;
			if(s.getNome().compareTo(cities.get(2).getNome())==0)
				c3++;
			else
				def++;
		}
		if(c1==0 || c2==0 || c3==0)
			return false;
		if(c1>NUMERO_GIORNI_CITTA_MAX || c2>NUMERO_GIORNI_CITTA_MAX || c3>NUMERO_GIORNI_CITTA_MAX)
			return false;
		
		for(int i=0; i<parziale.size(); i=+NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN) {
			for(int j=0; j<2; j++) {
			if(!parziale.get(i+j).equals(parziale.get(i+j+1)))
				return false;
			if(parziale.get(i+j+1).equals(parziale.get(i+j+2)))
				i++;
			}
		}
		return true;
	}

	private List<SimpleCity> ricorsiva(int livello, int mese, List<SimpleCity> parziale) {
		
		if(livello >= NUMERO_GIORNI_TOTALI) {
			if(controllaParziale(parziale))
				return parziale;
		}
		
		for(int i=0; i<cities.size(); i++) {
			SimpleCity sc = new SimpleCity(cities.get(i).getNome());
			//if(!sc.equals(parziale.get(livello-1)))
				//costo += this.COST;
			parziale.add(sc);
			ricorsiva(livello+1, mese, parziale);
			parziale.remove(sc);
		}
		return null;
	}
}
