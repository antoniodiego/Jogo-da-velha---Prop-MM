package br.diego.jogovelha.jogo;

/**
 * Classe que representa o jogador.
 * 
 * @author Ant??nio Diego
 *
 */
public class Jogador {

	private char caracter;
	private boolean primeiroJogador;
	// public String nome;
	private int pontos;
	// private boolean primeiro;

	/**
	 * X ou O
	 * 
	 * @return
	 */
	public char getLetra() {
		return caracter;
	}

	public void setCaracter(char caracter) {
		this.caracter = caracter;
	}

	public boolean ePrimeiroJogador() {
		return primeiroJogador;
	}

	public void mudaPrimeiroJogador(boolean primeiroJogador) {
		this.primeiroJogador = primeiroJogador;
	}

	public int getPontos() {
		return pontos;
	}

	public void setPontos(int pontos) {
		this.pontos = pontos;
	}

	public void adicionaPontos(int quantidade) {
		this.pontos += quantidade;
	}

	// public boolean isPrimeiro() {
	// return primeiro;
	// }
	//
	// public void setPrimeiro(boolean isPrimeiro) {
	// this.primeiro = isPrimeiro;
	// }
}
