package it.openprojects.teamslittlehelper;

import java.io.Serializable;


public class Player implements Serializable {

	private static final long serialVersionUID = 1L;
	static final int PRESENTE = 0;
    static final int RITARDO = 1;
    static final int ASSENTE = 2;
	private String nome;
	private String telefono;
	private Integer presenza = 0;

	public Player() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Player(String nome, String telefono, Integer presenza) {
		super();
		this.nome = nome;
		this.telefono = telefono;
		this.presenza = presenza;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setPresenza(Integer presenza) {
		this.presenza = presenza;
	}

	public Integer getPresenza() {
		return presenza;
	}
	
	public Integer getPresenzaIcon() {
		switch (this.presenza) {
			case 0:
				return R.drawable.present;
			case 1:
				return R.drawable.late;
			case 2:
				return R.drawable.absent;
			default:
				return R.drawable.present;
		}
	}

}
