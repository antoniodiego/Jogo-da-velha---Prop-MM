package br.diego.jogovelha.jogo.botao;


import javax.microedition.lcdui.Graphics;


/**
 *
 * @author Antonio Diego
 */

public class Botao extends Desenhavel {

    public String texto;
    public int largura;
    public int altura;
    private final OuvidorBotao ouvinte;
    public boolean desenhaBorda;
    public int cor;

    public Botao(String text, OuvidorBotao listener, DadosBotao config) {
        this.texto = text;
        this.ouvinte = listener;
        this.largura = config.width;
        this.altura = config.height;
        this.desenhaBorda = true;
        this.cor = 0xffffff;
    }

    public void draw(Graphics g) {
        g.setColor(0);
        if (desenhaBorda) {
            g.drawRect((int) getX(), (int) getY(), largura, altura);
        }
        g.setColor(cor);
        g.drawString(texto, (int) getX() + 1, (int) getY() + 1, 0);
    }

    public boolean clicado(int x, int y) {
        return x >= this.getX() && y >= this.getY() && x <= this.getX() + largura && y <= this.getY() + altura;
    }

    public void clica() {
        EventoClique evt = new EventoClique();
        evt.texto = this.texto;
        ouvinte.cliqueBotao(evt);
    }
}
