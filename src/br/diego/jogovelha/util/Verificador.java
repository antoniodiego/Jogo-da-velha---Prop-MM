/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.diego.jogovelha.util;

import br.diego.jogovelha.jogo.Constantes;

/**
 *
 * @author Ant?nio Diego- Comp:Ant?nio Diego <your.name at your.org>
 */
public class Verificador {

    /**
     *
     * @param linha
     * @param matriz
     * @param car
     * @return
     */
    public static boolean verificaLinha(int linha, int[][] matriz, char car) {
        //Venceu
        return matriz[0][linha] == car && matriz[1][linha] == car && matriz[2][linha] == car;
    }

    /**
     *
     * @param coluna
     * @param matriz
     * @param car
     * @return
     */
    public static boolean verificaColuna(int coluna, int[][] matriz, char car) {
        //Venceu
        return matriz[coluna][0] == car && matriz[coluna][1] == car && matriz[coluna][2] == car;
    }

    /**
     *
     * @param matriz
     * @param car
     * @return
     */
    public static boolean verificaDiagonal(int[][] matriz, char car) {
        //Venceu
        return (matriz[0][0] == car && matriz[1][1] == car && matriz[2][2] == car) || (matriz[2][0] == car && matriz[1][1] == car && matriz[0][2] == car);
    }

    public static int verificaPossVL(int[][] matriz, char car, int linha) {
        int colunaP = -1;
        //LLV
        if (matriz[0][linha] == car && matriz[1][linha] == car && matriz[2][linha] == Constantes.SEM_MARCA) {
            colunaP = 2;
        } else if (matriz[0][linha] == Constantes.SEM_MARCA && matriz[1][linha] == car && matriz[2][linha] == car) {
            //VLL
            colunaP = 0;
        } else if (matriz[0][linha] == car && matriz[1][linha] == Constantes.SEM_MARCA && matriz[2][linha] == car) {
            //LVL
            colunaP = 1;
        }
        return colunaP;
    }

    public static int verificaPossVC(int[][] matriz, char car, int col) {
        int linP = -1;
        //LLV
        if (matriz[col][0] == car && matriz[col][1] == car && matriz[col][2] == Constantes.SEM_MARCA) {
            linP = 2;
        } else if (matriz[col][0] == Constantes.SEM_MARCA && matriz[col][1] == car && matriz[col][2] == car) {
            //VLL
            linP = 0;
        } else if (matriz[col][0] == car && matriz[col][1] == Constantes.SEM_MARCA && matriz[col][2] == car) {
            //LVL
            linP = 1;
        }
        return linP;
    }

    /**
     * Verifica poss vit diagonal
     *
     * @param matriz
     * @param car
     * @param diag
     * @return A coordenada do local
     */
    public static int[] verificaPossVD(int[][] matriz, char car, byte diag) {
        int[] lc = new int[]{-1, -1};
        //LLV
        if (diag == 0) {
//Noroeste,sudeste
            if (matriz[0][0] == car && matriz[1][1] == car && matriz[2][2] == Constantes.SEM_MARCA) {
                lc[0] = 2;
                lc[1] = 2;
            } else if (matriz[0][0] == car && matriz[1][1] == Constantes.SEM_MARCA && matriz[2][2] == car) {
                lc[0] = 1;
                lc[1] = 1;
            } else if (matriz[0][0] == Constantes.SEM_MARCA && matriz[1][1] == car && matriz[2][2] == car) {
                lc[0] = 0;
                lc[1] = 0;
            }

        } else {
            if (matriz[2][0] == car && matriz[1][1] == car && matriz[0][2] == Constantes.SEM_MARCA) {
                lc[0] = 0;
                lc[1] = 2;
            } else if (matriz[2][0] == car && matriz[1][1] == Constantes.SEM_MARCA && matriz[0][2] == car) {
                lc[0] = 1;
                lc[1] = 1;
            } else if (matriz[2][0] == Constantes.SEM_MARCA && matriz[1][1] == car && matriz[0][2] == car) {
                lc[0] = 2;
                lc[1] = 0;
            }
        }

        return lc;
    }
}
