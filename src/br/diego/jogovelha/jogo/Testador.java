package br.diego.jogovelha.jogo;

/**
 * Classe que faz as decis??es dos jogos.
 *
 * @author Ant??nio Diego
 *
 */
public class Testador {

    /**
     * Indica que o n??o h?? vit??ria nem empate na jogada.
     */
    public static final int JogoContinua = 0;
    /**
     * Estado em que o jogador da marca X ganha.
     */
    public static final int VITORIA_INICIADOR = 1;
    /**
     * Estado em que o jogador da marca O ganha.
     */
    public static final int VITORIA_CONVIDADO = 2;
    public static final int EMPATE = 3;
    // private final Tabuleiro tabuleiro;

    public Testador() {
        // this.tabuleiro = tabuleiro;
    }

    public int verifica(Tabuleiro tabuleiro) {
        for (int i = 0; i < 8; i++) {
            int resultado = tabuleiro.tira[i].verifica();
            if (resultado == VITORIA_INICIADOR) {
                return VITORIA_INICIADOR;
            } else if (resultado == Testador.VITORIA_CONVIDADO) {
                return VITORIA_CONVIDADO;
            }
        }

        if (tabuleiro.est\u00e1Cheio()) {
            return EMPATE;
        }

        return JogoContinua;
    }

    public static boolean temVencedor(Tabuleiro tabuleiro) {
        for (int i = 0; i < 8; i++) {
            int resultado = tabuleiro.tira[i].verifica();
            if (resultado == VITORIA_INICIADOR || resultado == Testador.VITORIA_CONVIDADO) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica se o jogo acabou.
     *
     * @param jogo
     */
    public void verifica(QuadroVelha jogo) {
        for (int i = 0; i < 8; i++) {
            int resultado = jogo.tabuleiro.tira[i].verifica();
            if (resultado == VITORIA_INICIADOR) {
                jogo.tabuleiro.esvazia();

                if (jogo.jogador.ePrimeiroJogador()) {
                    jogo.ganhou();
                } else {
                    
                    jogo.perdeu();
                }
                return;
            } else if (resultado == Testador.VITORIA_CONVIDADO) {
                jogo.tabuleiro.esvazia();

                if (!jogo.jogador.ePrimeiroJogador()) {
                    jogo.ganhou();
                } else {
                    jogo.perdeu();
                }
                return;
            }
        }

        if (jogo.tabuleiro.est\u00e1Cheio()) {
            jogo.empate();
            jogo.tabuleiro.esvazia();
        }

    }
    
}
