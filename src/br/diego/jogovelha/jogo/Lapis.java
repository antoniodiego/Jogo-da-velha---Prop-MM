package br.diego.jogovelha.jogo;

import java.io.IOException;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * Representa o l??pis no tabuleiro.
 * 
 * @author Ant??nio Diego
 *
 */
public class Lapis {
	/**
	 * O lugar onde o l??pis se encontra.
	 */
	private Lugar lugar;
	private QuadroVelha jogo;
	private boolean visivel;
	private Image imgagemLapis;

	public Lapis() {
		this.visivel = false;

		try {
			imgagemLapis = Image.createImage("/imagens/lapis.png");
		} catch (IOException ex) {
		}
	}

	/**
	 * Escreve a figura do jogador no lugar onde se encontra o l??pis.
	 * 
	 * @return true se o lugar era vazio e escreveu, false caso contr??rio.
	 */
	public boolean escreve() {
		if (lugar.recebeMarca() != Constantes.SEM_MARCA || !this.visivel) {
			return false;
		}
		lugar.mudaMarca(jogo.jogador.getLetra());
		this.visivel = false;
		return true;
	}

	public void desenha(Graphics g) {
		if (!visivel) {
			return;
		}
		int pH = lugar.pH + ((lugar.largura - imgagemLapis.getWidth()) / 2);
		int pV = lugar.pV + ((lugar.altura - imgagemLapis.getHeight()) / 2);
		g.drawImage(imgagemLapis, pH, pV, 0);
	}

	public Lugar getLugar() {
		return lugar;
	}

	public void setLugar(Lugar lugar) {
		this.lugar = lugar;
	}

	public boolean isVis\u00edvel() {
		return visivel;
	}

	public void setVis\u00edvel(boolean visivel) {
		this.visivel = visivel;
	}

	public QuadroVelha getJogo() {
		return jogo;
	}

	public void setJogo(QuadroVelha jogo) {
		this.jogo = jogo;
	}

	public Image getImgagemLapis() {
		return imgagemLapis;
	}

	public void setImgagemLapis(Image imgagemLapis) {
		this.imgagemLapis = imgagemLapis;
	}
}
