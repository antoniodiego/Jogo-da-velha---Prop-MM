package br.diego.jogovelha;

import br.diego.jogovelha.jogo.Tabuleiro;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Screen;
import javax.microedition.midlet.MIDlet;

/**
 * Classe MIDlet.
 *
 * @author Ant??nio Diego
 *
 */
public class PrincipalVelha extends MIDlet {

    private final Display tela;
    private TelaInicial telaInicial;
    private Menu menu;

    public PrincipalVelha() {
        tela = Display.getDisplay(this);
        telaInicial = new TelaInicial();
        menu = new Menu(this);
    }

    public void startApp() {
      //  testeAlocMat();
        //Exibe tela apresenta????o.    
        tela.setCurrent(telaInicial);

        try {
            Thread.sleep(1500);
        } catch (InterruptedException ex) {
        }

        tela.setCurrent(menu);
    }
//Tempo 1000 tab: 30ms
    //8! ~40000 -> 40*30 =1200ms
    //Obs: Regra de 3
//real 771

    /*
    1000 apenas aloc em 1 v - 70ms
     1 segeundo  s? de aloca??o
    9! -> ~362000-> 362*30 = 10.860ms = 10s!
    real aloc em 1 v 362880 vez- 10991
     */
 /*
    Jogada normal: maq seg: 4280 
    maq prim: 36471ms
     */
    private void testaAlocT() {
        int nt = 0;
        long inic = System.currentTimeMillis();
        // Tabuleiro[] tabs = new Tabuleiro[40320];
        Tabuleiro t;
        while (nt < 362880) {
            t = new Tabuleiro(128, 160);
            nt++;
        }
        long fin = System.currentTimeMillis();
        System.out.println("Tempo : " + (fin - inic));
    }

    /*
     * 8! matrizes 3x3 -50ms
     */
    private void testeAlocMat() {
        int nt = 0;
        long inic = System.currentTimeMillis();
        int[][] tabM;
        while (nt < 40320) {
            tabM = new int[3][3];
            nt++;
        }
        long fin = System.currentTimeMillis();
        System.out.println("Tempo matzs: " + (fin - inic));
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
        notifyDestroyed();
    }

    public Display getDisplay() {
        return tela;
    }
}
