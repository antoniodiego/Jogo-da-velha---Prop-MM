package br.diego.jogovelha.jogo;

import javax.microedition.lcdui.Graphics;

/**
 * Representa o tabuleiro do jogo da velha.
 *
 * @author Ant??nio Diego
 *
 */
// TODO: Melhorar apar??ncia.
public class Tabuleiro {

    public int pH;
    public int pV;
    public int largura;
    public int altura;
    public final int larguraCasa;
    public final int alturaCasa;
    private final Lugar[] lugares = new Lugar[9];
    public final Tira[] tira = new Tira[8];
    int corTabuleiro = 0x000000;

    public Tabuleiro(int largura, int altura) {
        this.largura = largura;
        this.altura = altura;

        larguraCasa = largura / 3;
        alturaCasa = altura / 3;

        // Primeira linha
        lugares[0] = new Lugar(1, 1);
        lugares[1] = new Lugar(1, 2);
        lugares[2] = new Lugar(1, 3);

        // Segunda linha
        lugares[3] = new Lugar(2, 1);
        lugares[4] = new Lugar(2, 2);
        lugares[5] = new Lugar(2, 3);

        // Terceira linha
        lugares[6] = new Lugar(3, 1);
        lugares[7] = new Lugar(3, 2);
        lugares[8] = new Lugar(3, 3);

        // Horizontais
        tira[0] = new Tira(lugares[0], lugares[1], lugares[2]);
        tira[1] = new Tira(lugares[3], lugares[4], lugares[5]);
        tira[2] = new Tira(lugares[6], lugares[7], lugares[8]);

        // Verticais
        tira[3] = new Tira(lugares[0], lugares[3], lugares[6]);
        tira[4] = new Tira(lugares[1], lugares[4], lugares[7]);
        tira[5] = new Tira(lugares[2], lugares[5], lugares[8]);

        // Diagonais
        tira[6] = new Tira(lugares[0], lugares[4], lugares[8]);
        tira[7] = new Tira(lugares[2], lugares[4], lugares[6]);
    }

    public void reajustaLugares() {
        for (int i = 0; i < lugares.length; i++) {
            lugares[i].ajusta(this);
        }
    }

    public void desenha(Graphics g) {
        g.setColor(corTabuleiro);

        // Primeira linha horizontal
        g.drawLine(pH, pV + alturaCasa, pH + largura, pV + alturaCasa);
        // Segunda linha horizontal
        g.drawLine(pH, pV + (alturaCasa * 2), pH + largura,
                pV + (alturaCasa * 2));

        // Primeira linha vertical
        g.drawLine(pH + larguraCasa, pV, pH + larguraCasa, pV + altura);
        // Segunda linha vertical
        g.drawLine(pH + (larguraCasa * 2), pV, pH + (larguraCasa * 2),
                pV + altura);

        for (int i = 0; i < lugares.length; i++) {
            lugares[i].desenha(g);
        }
    }

    public void esvazia() {
        for (int i = 0; i < lugares.length; i++) {
            lugares[i].limpa();
        }
    }

    /**
     *
     * @return true se o tabuleiro estiver com todos os lugares marcados, false
     * caso contr??rio.
     */
    public boolean est\u00e1Cheio() {
        for (int i = 0; i < lugares.length; i++) {
            if (lugares[i].recebeMarca() == Constantes.SEM_MARCA) {
                return false;
            }
        }
        return true;
    }

    public Lugar recebeLugar(int indice) {
        if (indice < 0 || indice > 8) {
            throw new IllegalArgumentException();
        }
        return lugares[indice];
    }

    /**
     * Retorna o lugar no tabuleiro [0 a 8] da cordenada
     *
     * @param linha
     * @param coluna
     * @return
     */
    public static int retornaIndice(int linha, int coluna) {
        return (((linha - 1) * 3) + coluna) - 1;
    }

    /**
     * Coluna e linha comec 0
     *
     * @param ind
     * @return
     */
    public static int retornaCol(int ind) {
// Ex: ind 1 - linha 0 coluna 1,
//Parece que coluna ? o resto por 3 e a linha o quociente
        //   int coluna =
        return ind % 3;
        // int linha = ind / 3;
    }

    /**
     * Coluna e linha comec 0
     *
     * @param ind
     * @return
     */
    public static int retornaLin(int ind) {
// Ex: ind 1 - linha 0 coluna 1,
//Parece que coluna ? o resto por 3 e a linha o quociente
        //   int coluna =
        return ind / 3;
        // int linha = ind / 3;
    }

    public Lugar[] getLugares() {
        return lugares;
    }

    public boolean eFinal() {
        return est\u00e1Cheio() || Juiz.temVencedor(this);
    }

    public Tabuleiro recebeCopia() {
        // Armazena c??pia do tabuleiro.
        Tabuleiro copiaTabuleiro = new Tabuleiro(this.largura,
                this.altura);

        // Copia jogadas do tabuleiro original.
        for (int i = 0; i < this.getLugares().length; i++) {
            copiaTabuleiro.lugares[i].mudaMarca(this.lugares[i].recebeMarca());
        }

        return copiaTabuleiro;
    }

    public void imprime() {
        for (int i = 0; i < this.lugares.length; i++) {
            System.out.print(lugares[i].recebeMarca());
            if ((i + 1) % 3 == 0) {
                System.out.println();
            } else {
                System.out.print('|');
            }
        }
    }
}
