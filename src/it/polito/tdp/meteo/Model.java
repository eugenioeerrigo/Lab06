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
	private List<SimpleCity> risultato;
	private double best;
	
	public Model() {
		dao = new MeteoDAO();
		cities = new ArrayList<>();
			
			for(Rilevamento r : dao.getAllRilevamenti()) {               //posso farlo con metodo da dao con query SQL
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

	private void resetCities(int mese) {
		for (Citta c : cities) {
			c.setCounter(0);
			c.setRilevamenti(dao.getAllRilevamentiLocalitaMese(mese, c.getNome()));
		}
	}

	public String trovaSequenza(int mese) {
		String ris = "";
		
		List<SimpleCity> parziale = new ArrayList<>();
		best = Double.MAX_VALUE;
		risultato = null;
		
		this.resetCities(mese);
		
	    this.ricorsiva(1, parziale);
	    
		if(risultato!=null) {
			System.out.println(String.format("DEBUG score: %f", this.punteggioSoluzione(risultato)));
			
			for(SimpleCity sc : risultato)
				ris +=sc.getNome()+"\n";
		}
		
		return ris;
	}

	private Double punteggioSoluzione(List<SimpleCity> soluzioneCandidata) {
		double score = 0.0;
		
		//NO nulla o vuota
		if(soluzioneCandidata == null || soluzioneCandidata.size() == 0)
			return Double.MAX_VALUE;
		
		//controllo che contenga tutte le citta
		for(Citta c: cities) {
			if(!soluzioneCandidata.contains(new SimpleCity(c.getNome())))
				return Double.MAX_VALUE;
		}
		
		
		SimpleCity previous = soluzioneCandidata.get(0);
		
		for(SimpleCity sc : soluzioneCandidata) {
			if(!previous.equals(sc)) {
				score += COST;
			}
			previous = sc;
			score += sc.getCosto();
		}
			
		
//		for(int i=1; i<soluzioneCandidata.size(); i++) {
//			if(!soluzioneCandidata.get(i).equals(soluzioneCandidata.get(i-1)))
//				score += this.COST;
//		}
		
		return score;
	}

	private boolean controllaParziale(List<SimpleCity> parziale) {
//		int c1 = 0 ;
//		int c2 = 0;
//		int c3 = 0;
//		int def = 0;
//		for(SimpleCity s : parziale) {
//			if(s.getNome().compareTo(cities.get(0).getNome())==0)
//				c1++;
//			if(s.getNome().compareTo(cities.get(1).getNome())==0)
//				c2++;
//			if(s.getNome().compareTo(cities.get(2).getNome())==0)
//				c3++;
//			else
//				def++;
//		}
//		if(c1==0 || c2==0 || c3==0)
//			return false;
//		
//		if(c1>NUMERO_GIORNI_CITTA_MAX || c2>NUMERO_GIORNI_CITTA_MAX || c3>NUMERO_GIORNI_CITTA_MAX)
//			return false;
		
		if(parziale == null)
			return false;
		
		if(parziale.size() == 0)
			return true;
		
		// Controllo sui vincoli del numero di giorni massimo in ciascuna citta
				for (Citta citta : cities) {
					if (citta.getCounter() > NUMERO_GIORNI_CITTA_MAX)
						return false;
				}
		
		
//		for(int i=1; i<parziale.size(); i++) {
//			if(!parziale.get(i).equals(parziale.get(i-1)))
//				return false;
//		}
				
				// Controllo sul vincolo del numero minimo di giorni consecutivi
				SimpleCity previous = parziale.get(0);
				int counter = 0;

				for (SimpleCity sc : parziale) {
					if (!previous.equals(sc)) {
						if (counter < NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN) {
							return false;
						}
						counter = 1;
						previous = sc;
					} else {
						counter++;
					}
				}
		return true;
	}

	private void ricorsiva(int livello, List<SimpleCity> parziale) {
		
		//Debug
//		for(SimpleCity e : parziale)
//			System.out.print(e.getNome()+" ");
//		System.out.println("---");
		
		//Condizione di terminazione
//		if(parziale.size() > this.NUMERO_GIORNI_TOTALI)
//			return;
		
		//Controllo se la soluzione parziale è la migliore
		if(livello >= NUMERO_GIORNI_TOTALI) {

				if(this.punteggioSoluzione(parziale)<best) {
					
					System.out.println(parziale.toString());
					System.out.println(String.format("DEBUG score: %f", this.punteggioSoluzione(parziale)));
					
					risultato = new ArrayList<>(parziale);
					best = punteggioSoluzione(parziale);
				}
			System.out.println(parziale.toString());
		}
		
		for(Citta citta : cities) {
			
			SimpleCity sc = new SimpleCity(citta.getNome(), citta.getRilevamenti().get(livello).getUmidita());
			
			parziale.add(sc);
			citta.increaseCounter();
			
			if(controllaParziale(parziale))
				ricorsiva(livello+1, parziale);
			
			parziale.remove(sc);
			citta.decreaseCounter();
		}
		
	}
	
	
}
