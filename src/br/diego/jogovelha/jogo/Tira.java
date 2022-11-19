package br.diego.jogovelha.jogo;

/**
 * Representa uma linha no tabuleiro.
 * 
 * @author Ant??nio Diego
 *
 */
public class Tira {

//	private final Lugar lugar1;
//	private final Lugar lugar2;
//	private final Lugar lugar3;

	private Lugar[] lugares;
	
	public Tira(Lugar lugar1, Lugar lugar2, Lugar lugar3) {
//		this.lugar1 = lugar1;
//		this.lugar2 = lugar2;
//		this.lugar3 = lugar3;
		
		this.lugares = new Lugar[]{lugar1,lugar2,lugar3};
	}

	/**
	 * 
	 * @return
	 */
	public int verifica() {
		if (lugares[0].recebeMarca() == lugares[1].recebeMarca()
				&& lugares[1].recebeMarca() == lugares[2].recebeMarca()) {
			char letraVencedor = lugares[0].recebeMarca();
			if (letraVencedor == Constantes.SEM_MARCA) {
				return Testador.JogoContinua;
			}
			if (letraVencedor == Constantes.CARACTER_INICIADOR_JOGO) {
				return Testador.VITORIA_INICIADOR;
			} else if (letraVencedor == Constantes.CARACTERE_CONVIDADO) {
				return Testador.VITORIA_CONVIDADO;
			} else {
				return -1;
			}
		} else {
			return Testador.JogoContinua;
		}
	}
	
	public Lugar recebeLugar(int lugar){
		return lugares[lugar];
	}
}
