package br.diego.jogovelha;

import java.io.IOException;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * Classe que processa a tela inicial.
 *
 * @author Ant??nio Diego
 *
 */
public class TelaInicial extends Canvas {

    private Image imagemInicial;
    private final int largura, altura;

    public TelaInicial() {
        try {
            imagemInicial = Image
                    .createImage("/imagens/tela inicial preta.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.largura = getWidth();
        this.altura = getHeight();
    }

    public void paint(Graphics g) {
        g.setColor(0x000000);
        g.fillRect(0, 0, largura, altura);
        // g.setColor(0x00DD50);
        // int larguraTexto = g.getFont().stringWidth("Jogo da Velha");
        // g.drawString("Jogo da Velha", (getWidth()-larguraTexto)/2,
        // getHeight()/2, 0);

        g.drawImage(imagemInicial, largura / 2, altura / 2,
                Graphics.HCENTER | Graphics.VCENTER);
    }
}
