
package br.diego.jogovelha.jogo.ia;

import br.diego.jogovelha.jogo.Constantes;

/**
 *
 * @author Ant?nio Diego- Comp:Ant?nio Diego <your.name at your.org>
 */
public class TabuleiroMatriz {

    /**
     * Incia com zeros
     */
    public int[][] matriz = new int[3][3];

    public TabuleiroMatriz recebeCop() {
        TabuleiroMatriz n = new TabuleiroMatriz();
        for (int li = 0; li < 3; li++) {
            for (int col = 0; col < 3; col++) {
                //Perc col por linha
                n.matriz[col][li] = matriz[col][li];
            }
        }
        return n;
    }

    public boolean cheio() {
        for (int li = 0; li < 3; li++) {
            for (int col = 0; col < 3; col++) {
                //Perc col por linha
                if (matriz[col][li] != Constantes.SEM_MARCA) {
                    return false;
                }
            }
        }

        return true;
    }
}