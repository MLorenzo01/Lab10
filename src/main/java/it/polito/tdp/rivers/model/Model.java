package it.polito.tdp.rivers.model;

import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.rivers.db.RiversDAO;

public class Model {
	
	private RiversDAO dao;
	private static final int GIORNI = 30;
	private static final double MIN = 0.8;
	private static final int ELEVATO = 10;
	private static final int SECONDI = 86400;
	private int giorniCritici = 0; 
	private double fMed = 0.0;
	private List<String> info;
	
	
	public Model() {
		this.dao = new RiversDAO();
		this.info = new ArrayList<>();
	}

	public List<River> getAllRiver(){
		return dao.getAllRivers();
	}
	public List<String> getInfo(int id){
		this.info = new ArrayList<>();
		info.addAll(dao.getDates(id));
		info.add(dao.getNumber(id).toString());
		info.add(dao.getAVG(id).toString());
		fMed = dao.getAVG(id);
		return info;
	}
	
	public String Simulazione(int k, int id) {
		this.giorniCritici = 0;
		double Q = fMed * SECONDI * GIORNI *k;
		Double fOutMin;
		List<Double> CMedia = new ArrayList<>();
		Double C = (double) (Q / 2);
		List<Flow> flows = dao.getAllFlows(id);
		for(Flow fo: flows) {
			Double f = fo.getFlow()*SECONDI;
			double r = Math.random();
			if(r > 0.95) {
				fOutMin = (double) (fMed * SECONDI * 10);
			}else {
				fOutMin = fMed * SECONDI *0.8;
			}
			if((f + C) > Q) {
				C = Q;
			}else {
				C += f;
			}
			if(C < fOutMin) {
				C = 0.0;
				this.giorniCritici++;
			}else {
				C -= fOutMin;
			}
			CMedia.add(C);
		}
		double media = 0.0;
		for(Double num: CMedia)
			media += num;
		media = media/CMedia.size();
		String s = "Numero di giorni critici: " + this.giorniCritici + "\nOccupazione media del bacino: " + media + "\nSIMULAZIONE TERMINATA!";
		return s;
	}

}
