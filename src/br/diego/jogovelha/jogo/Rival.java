package br.diego.jogovelha.jogo;

/**
 * Classe que representa o rival.
 * 
 * @author Antônio Diego
 *
 */
public class Rival {

	private int id;
	private char caracter;
	private int pontos;
	private String nome;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public char getCaracter() {
		return caracter;
	}

	public void setCaracter(char caracter) {
		this.caracter = caracter;
	}

	public int getPontos() {
		return pontos;
	}

	public void setPontos(int pontos) {
		this.pontos = pontos;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void aumentaPontos(int valor) {
		this.pontos += valor;
	}
}
