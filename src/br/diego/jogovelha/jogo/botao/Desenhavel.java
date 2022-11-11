package br.diego.jogovelha.jogo.botao;


import javax.microedition.lcdui.Graphics;


public abstract class Desenhavel {
    private float pH;
    private float pV;
    
    public abstract void draw(Graphics g);
    
     public void setPosition(float x, float y) {
        this.pH = x;
        this.pV = y;
    }

    public void move(float incx, float incy) {
        pH += incx;
        pV += incy;
    }

    public float getX() {
        return pH;
    }

    public void setX(float x) {
        this.pH = x;
    }

    public float getY() {
        return pV;
    }

    public void setY(float y) {
        this.pV = y;
    }
}
