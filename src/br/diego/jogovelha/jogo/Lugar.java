package br.diego.jogovelha.jogo;

import javax.microedition.lcdui.Graphics;

//TODO: Melhorar apar??ncia.
public class Lugar extends CoordenadaTabuleiro {

	public int pH;
	public int pV;
	public int largura;
	public int altura;
	private char marca;

	public Lugar() {
		this.marca = Constantes.SEM_MARCA;
	}

	public Lugar(int linha, int coluna) {
		this.coluna = coluna;
		this.linha = linha;
		this.marca = Constantes.SEM_MARCA;
	}

	public void ajusta(Tabuleiro tabuleiro) {
		this.pH = tabuleiro.pH + (coluna - 1) * tabuleiro.larguraCasa;
		this.pV = tabuleiro.pV + (linha - 1) * tabuleiro.alturaCasa;
		this.largura = tabuleiro.larguraCasa;
		this.altura = tabuleiro.alturaCasa;
	}

	public void desenha(Graphics g) {
		int charwidth = g.getFont().charWidth(marca);
		int charheight = g.getFont().getHeight();

		int xcenter = pH + ((largura - charwidth) / 2);
		int ycenter = pV + ((altura - charheight) / 2);

		int corMarca = 0x00ff00;

		g.setColor(corMarca);
		g.drawChar(marca, xcenter, ycenter, 0);
	}

	public boolean bounds(int x, int y) {
		return x >= this.pH && y >= this.pV && x <= this.pH + this.largura
				&& y <= this.pV + this.altura;
	}

        /**
         * Lugar [0 a 1] tab 
         * @return 
         */
	public int getIndice() {
		return Tabuleiro.retornaIndice(linha, coluna);
	}

	public void mudaMarca(char marca) {
		// TODO: Verificar
		this.marca = marca;
	}

	public void limpa() {
		this.marca = Constantes.SEM_MARCA;
	}

	public char recebeMarca() {
		return this.marca;
	}
}
