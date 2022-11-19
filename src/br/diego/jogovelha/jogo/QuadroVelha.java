package br.diego.jogovelha.jogo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import br.diego.jogovelha.Menu;
import br.diego.jogovelha.PrincipalVelha;
import br.diego.jogovelha.jogo.botao.Botao;
import br.diego.jogovelha.jogo.botao.OuvidorBotao;
import br.diego.jogovelha.jogo.botao.DadosBotao;
import br.diego.jogovelha.jogo.botao.EventoClique;
import br.diego.jogovelha.jogo.ia.InteligenciaArtificial;

/**
 * Classe que processa o jogo.
 *
 *
 * @author Ant??nio Diego
 *
 */
public class QuadroVelha extends Canvas
        implements CommandListener, OuvidorBotao {

    public static final boolean DEPURAR = false;
    public static final int TEMPO_EXIBICAO_MENSAGEM_MS_PADRAO = 5000;
    public static final int CODIGO_SAI_DO_JOGO = 10;
    /**
     * Comando sair
     */
    private Command comandoSair;
    private Command comandoMarcar;
    /**
     * Fonte da barra de estado
     */
    private Font fonteEstado;
    private final int corFundoEstado = 0x000000;
    private final int corTextoEstado = 0xFF8000;
    private final int alturaBarraEstado;
    private Image imagemEstado;
    /**
     * Tabuleiro.
     */
    public final Tabuleiro tabuleiro;
    private final Lapis lapis;
    private final Testador juiz;
    public final Jogador jogador;
    private final Rival rival;
    /**
     * Corrego de entrada.
     */
    public DataInputStream entrada;
    public DataOutputStream saida;
    private boolean processamentoMensagensCorrendo;
    private final ProcessaMensagensRival processadorMensagens;
    private String mensagem;
    private final RemovedorMensagem removedor;
    private final PrincipalVelha principal;
    private boolean doisJogadores;
    private final boolean suportaToque;
    private final Botao btMenu;
    private boolean vezJogador;
    private InteligenciaArtificial ia;
    private int corMensagem;
    private String proxima;
    private StringBuffer tit;
    private int[] mapaV = new int[9];

    // Sair est?? no lado incorreto.
    // TODO: Colocar borda na barra de estado e no tabuleiro.
    public QuadroVelha(PrincipalVelha principal, Jogador jogador, Rival rival) {
        setFullScreenMode(true);
        this.suportaToque = hasPointerEvents();
        this.principal = principal;
        this.tit = new StringBuffer();

        fonteEstado = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN,
                Font.SIZE_SMALL);
        alturaBarraEstado = fonteEstado.getHeight() * 4;
        imagemEstado = Image.createImage(getWidth(), alturaBarraEstado);
        processadorMensagens = new ProcessaMensagensRival();
        removedor = new RemovedorMensagem();

        if (DEPURAR) {
            System.out.println(
                    "Largura barra de estado: " + imagemEstado.getWidth());
            System.out.println("Altura :" + imagemEstado.getHeight());
        }

        // Pega maior m??ltiplo de tr??s menor que largura
        int resto3 = getWidth() % 3;
        int larguraTabuleiro = getWidth() - resto3;

        if (DEPURAR) {
            System.out.println("Largura Tabuleiro:" + larguraTabuleiro);
        }

        // Maior m??ltiplo de tr??s menor que altura restante
        int alturaRestante = getHeight() - alturaBarraEstado;
        int alturaresto3 = alturaRestante % 3;
        int alturaTabuleiro = alturaRestante - alturaresto3;

        if (DEPURAR) {
            System.out.println("Altura do tabuleiro:" + alturaTabuleiro);
        }

        this.tabuleiro = new Tabuleiro(larguraTabuleiro, alturaTabuleiro);
        this.tabuleiro.pH = 0;
        this.tabuleiro.pV = alturaBarraEstado + 1;

        if (DEPURAR) {
            System.out.println(
                    "PH do tabuleiro:" + tabuleiro.pH + " PV :" + tabuleiro.pV);
        }

        this.tabuleiro.reajustaLugares();
        constroiMapV();
        this.lapis = new Lapis();
        this.lapis.setJogo(this);
        this.lapis.setLugar(
                tabuleiro.getLugares()[Tabuleiro.retornaIndice(2, 2)]);
        this.juiz = new Testador();
        this.jogador = jogador;
        this.rival = rival;
        this.ia = new InteligenciaArtificial(tabuleiro);

        if (jogador.ePrimeiroJogador()) {
            vezJogador = true;
        }

        comandoSair = new Command("Sair", Command.BACK, 0);
        comandoMarcar = new Command("Marcar", Command.OK, 1);

        // addCommand(comandoSair);
        if (!suportaToque) {
            addCommand(comandoSair);
            addCommand(comandoMarcar);
        }

        setCommandListener(this);

        DadosBotao config = new DadosBotao();
        btMenu = new Botao("Sair", this, config);
        btMenu.largura = fonteEstado.stringWidth(btMenu.texto) + 2;
        btMenu.altura = fonteEstado.getHeight();
        btMenu.desenhaBorda = false;
        btMenu.cor = 0xff8000;
        btMenu.setPosition(getWidth() - btMenu.largura - 1,
                getHeight() - btMenu.altura - 1);

        corMensagem = corTextoEstado;
        geraBarraEstado();
    }

    public boolean eDoisJogadores() {
        return doisJogadores;
    }

    public void setDoisJogadores(boolean doisJogadores) {
        this.doisJogadores = doisJogadores;
    }

    protected void paint(Graphics g) {
        g.setColor(0xFFFFFF);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.drawImage(imagemEstado, 0, 0, 0);
        tabuleiro.desenha(g);

        if (!suportaToque) {
            lapis.desenha(g);
        } else {
            btMenu.draw(g);
        }
    }

    /**
     * Gera a barra de estado.
     */
    private void geraBarraEstado() {
        // Prepara conte??do.
        // Fazer: O placar jogador est?? feio.
        // Pega refer??ncia de Graphics.
        Graphics g = imagemEstado.getGraphics();

        // Pinta toda a ??rea com a cor de fundo.
        g.setColor(corFundoEstado);
        g.fillRect(0, 0, imagemEstado.getWidth(), imagemEstado.getHeight());

        // Desenha textos.
        String estadoJogador = "Tu: " + jogador.getPontos() + " pontos";
        // String meuEstado = (jogador.isPlayer1 ? "1??" : "2??") + '-'
        // + jogador.caracter + " venceu: " + ' ' + jogador.pontos + "x";
        String estadoRival = "Ele: " + rival.getPontos() + " pontos";

        g.setColor(corTextoEstado);
        // Estado jogador.
        g.drawString(estadoJogador, 1, 0, 0);
        // Desenha estado dele na proxima linha.
        g.drawString(estadoRival, 1, fonteEstado.getHeight(), 0);

        // Desenha mensagem se houver.
        if (mensagem != null) {
            g.setColor(corMensagem);
            g.drawString(mensagem, getWidth() / 2, fonteEstado.getHeight() * 2,
                    Graphics.HCENTER | Graphics.TOP);
        }
    }

    public void iniciaProcessamentoMensagens() {
        Thread processMessagesThread = new Thread(processadorMensagens);
        processMessagesThread.start();
    }

    public void exibeMensagem(String mensagem, String proxima, int cor,
            int tempo) {
        this.corMensagem = cor;
        this.mensagem = mensagem;
        this.proxima = proxima;
        geraBarraEstado();
        repaint();
        removedor.setTempoRemover(tempo);
        Thread removerThread = new Thread(removedor);
        removerThread.start();
    }

    public void mensagemRemovida() {
        if (proxima != null) {
            exibeMensagem(proxima, null, corMensagem, 3000);
        }
    }

    public void cliqueBotao(EventoClique evt) {
        if (evt.texto.equals("Sair")) {
            processamentoMensagensCorrendo = false;
            terminaJogo(true);
            principal.getDisplay().setCurrent(new Menu(principal));
        }
    }

    private class RemovedorMensagem implements Runnable {

        private int tempoRemover;

        public void run() {
            try {
                Thread.sleep(tempoRemover);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            mensagem = null;
            geraBarraEstado();
            repaint();
            mensagemRemovida();
        }

        public void setTempoRemover(int tempoRemover) {
            this.tempoRemover = tempoRemover;
        }
    }

    private class ProcessaMensagensRival implements Runnable {

        public void run() {
            processamentoMensagensCorrendo = true;
            while (processamentoMensagensCorrendo) {
                try {
                    int numero = entrada.readInt();

                    // Casa marcada.
                    if (numero < 10) {
                        Lugar lugarTemporario = tabuleiro.getLugares()[numero];
                        lugarTemporario.mudaMarca(rival.getCaracter());
                        lapis.setVis\u00edvel(true);
                        vezJogador = true;
                        juiz.verifica(QuadroVelha.this);
                        repaint();
                    }

                    // Ele saiu.
                    if (numero == 10) {
                        processamentoMensagensCorrendo = false;
                        terminaJogo(false);
                        Alert alerta = new Alert("Fim", "Ele saiu.", null,
                                AlertType.ALARM);
                        alerta.setTimeout(Alert.FOREVER);
                        principal.getDisplay().setCurrent(alerta,
                                new Menu(principal));
                    }

                    if (numero == 11) {
                        // Conex??o caiu.
                        processamentoMensagensCorrendo = false;
                        terminaJogo(false);
                        Alert alerta = new Alert("Fim", "Problema na conex??o.",
                                null, AlertType.ALARM);
                        alerta.setTimeout(Alert.FOREVER);
                        principal.getDisplay().setCurrent(alerta,
                                new Menu(principal));
                    }
                } catch (IOException ex) {
                    if (processamentoMensagensCorrendo == false) {
                        // Jogo foi parado
                    } else {
                        processamentoMensagensCorrendo = false;
                        envia(11);
                        terminaJogo(false);
                        // Avisa que houve erro.

                        Alert alerta = new Alert("Fim", "Ouve um problema.",
                                null, AlertType.ALARM);
                        alerta.setTimeout(Alert.FOREVER);
                        principal.getDisplay().setCurrent(alerta,
                                new Menu(principal));
                    }
                }
            }
        }
    }

    public void keyPressed(int keyCode) {
        if (suportaToque) {
            return;
        }

        if (!lapis.isVis\u00edvel()) {
            return;
        }

        Lugar lugarLapis = lapis.getLugar();
        int novaLinha;
        int novaColuna;

        switch (getGameAction(keyCode)) {
            case UP:
                novaLinha = lugarLapis.linha - 1;
                if (novaLinha < 1) {
                    novaLinha = 3;
                }
                lapis.setLugar(tabuleiro.getLugares()[Tabuleiro
                        .retornaIndice(novaLinha, lugarLapis.coluna)]);
                repaint();
                break;
            case DOWN:
                novaLinha = lugarLapis.linha + 1;
                if (novaLinha > 3) {
                    novaLinha = 1;
                }
                lapis.setLugar(tabuleiro.getLugares()[Tabuleiro
                        .retornaIndice(novaLinha, lugarLapis.coluna)]);
                repaint();
                break;
            case LEFT:
                novaColuna = lugarLapis.coluna - 1;
                if (novaColuna < 1) {
                    novaColuna = 3;
                }
                lapis.setLugar(tabuleiro.getLugares()[Tabuleiro
                        .retornaIndice(lugarLapis.linha, novaColuna)]);
                repaint();
                break;
            case RIGHT:
                novaColuna = lugarLapis.coluna + 1;
                if (novaColuna > 3) {
                    novaColuna = 1;
                }
                lapis.setLugar(tabuleiro.getLugares()[Tabuleiro
                        .retornaIndice(lugarLapis.linha, novaColuna)]);
                repaint();
                break;
            case FIRE:
                confirma();
                break;
        }
    }

    protected void pointerPressed(int x, int y) {
        if (btMenu.clicado(x, y)) {
            btMenu.clica();
            return;
        }
        confirmaMarcacaoPonteiro(x, y);
    }

    /**
     * FIXME: parece haver erros.
     *
     * @param x
     * @param y
     */
    private void confirmaMarcacaoPonteiro(int x, int y) {
        if (!lapis.isVis\u00edvel() || !vezJogador) {
            return;
        }

        Lugar[] lugares = tabuleiro.getLugares();
        Lugar lugar;
        for (int i = 0; i < lugares.length; i++) {
            lugar = lugares[i];
            if (lugar.bounds(x, y)
                    && lugar.recebeMarca() == Constantes.SEM_MARCA) {
                lugar.mudaMarca(jogador.getLetra());
                rodada++;

                if (doisJogadores) {
                    envia(i);
                }

                vezJogador = false;
                juiz.verifica(this);
                // lapis.visivel = false;
                // vezJogador = false;

                if (!doisJogadores && !vezJogador) {
                    // FIXME: Repetido.
                    System.out.println("Jogada maq: " + rodada);
                    fazJogadaArtificial();
                    juiz.verifica(this);
                    // vezJogador = true;
                    // repaint();
                }
                repaint();
                break;
            }
        }
    }

    /**
     * Chamado qunao o usu??rio clica em um bot??o que confirma a jogada.
     */
    private void confirma() {
        if (lapis.escreve()) {
            //FIXME: Come? com prim vazio
            constroiMapV();
            tit.append(mapaV[lapis.getLugar().getIndice()]);
            tabuleiro.id.append(lapis.getLugar().getIndice()+1);
            rodada++;
            if (doisJogadores) {
                envia(lapis.getLugar().getIndice());
            }
            vezJogador = false;
            System.out.println("Jogador jogou. Verficando se jogo acabou...");
            juiz.verifica(this);
            repaint();
            serviceRepaints();

            // Contra m??quina
            if (!doisJogadores && !vezJogador) {
                // FIXME: Repetido.
                // A jogada final em que a m?quina ganha pode estar aqui
                System.out.println("Jogada da m?quina na rodada " + rodada + 
                        "em confirma)");
                fazJogadaArtificial();
                System.out.println("M?quina jogou rodada: " + rodada);
                System.out.println("Repintando");
                repaint();

                System.out.println("Antes verifica");

                serviceRepaints();

                serviceRepaints();

                juiz.verifica(this);
                System.out.println("juiz verfic");
            }

            repaint();
        }
    }

    int rodada = 0;

    private void constroiMapV() {
        Lugar[] l = tabuleiro.getLugares();
        int con = 0;
        for (int c = 0; c < l.length; c++) {
            if (l[c].recebeMarca() == Constantes.SEM_MARCA) {
                con++;
                mapaV[c] = con;
            }
        }
    }

    /**
     * Faz jogada da m??quina.
     */
    private void fazJogadaArtificial() {
        /*
		 * int[][] tab = new int[3][3];
		 * 
		 * for (int i = 0; i < this.tabuleiro.lugares.length; i++) { tab[i /
		 * 3][i % 3] = this.tabuleiro.lugares[i].marca; //System.out.println(
		 * "l " + i / 3 + " c " + i % 3 + " v " + tabuleiro.lugar[i].marca); }
		 * 
		 * tab = minimax.decisao_minimax(tab);
		 * 
		 * int indice; for (int i = 0; i < 3; i++) { for (int j = 0; j < 3; j++)
		 * { //x / 3 = i3+j indice = 3 * i + j; // System.out.println(indice);
		 * this.tabuleiro.lugares[indice].marca = (char) tab[i][j]; } }
         */

        ia.fazJogadaDispositivo(rodada, this);
        constroiMapV();
        rodada++;
        // ia.fazJogadaMaquina(tabuleiro);
        lapis.setVis\u00edvel(true);
        vezJogador = true;
    }

    private void envia(int numero) {
        try {
            saida.writeInt(numero);
            saida.flush();
        } catch (IOException ex) {
        }
    }

    public void commandAction(Command c, Displayable d) {
        if (c == comandoSair) {
            processamentoMensagensCorrendo = false;
            terminaJogo(true);
            principal.getDisplay().setCurrent(new Menu(principal));
        }

        if (c == comandoMarcar) {
            confirma();
        }

    }

    /**
     * Chamado quando o jogador ganha a partida.
     */
    public void ganhou() {
        jogador.adicionaPontos(1);
        geraBarraEstado();
        lapis.setVis\u00edvel(true);
        // jogador.mudaPrimeiroJogador(true);
        vezJogador = true;
        // exibeMensagem("Voc?? venceu!");
        exibeMensagem("Tu venceste!.", "Tua vez!", 0x008000, 5000);
        // TODO: Exibir tua vez.
        rodada = 0;
    }

    /**
     * M??todo que ?? chamado quando o jogador perde.
     */
    public void perdeu() {
        System.out.println("Perdeu");

        rival.aumentaPontos(1);
        geraBarraEstado();
        lapis.setVis\u00edvel(false);
        // jogador.mudaPrimeiroJogador(false);
        vezJogador = false;
        exibeMensagem("Tu perdeste!.", "Vez dele!", 0x800000, 5000);
        rodada = 0;
        //  System.out.println("Perdeu");

        //FIXMER: parece que n?o funciona
        System.out.println("repintando tela em perdeu");
        repaint();
        serviceRepaints();

        if (!doisJogadores) {
            System.out.println("Jogada maq: " + rodada + " em perdeu()");

            //FIXME: [Loop [infinito] ] Tempo muito grande ao m?quina ser prim
            //FIXME: Jogo fica travado
            fazJogadaArtificial();
        }
    }

    public void empate() {
        rodada = 0;

        if (jogador.ePrimeiroJogador()) {
            lapis.setVis\u00edvel(true);
            vezJogador = true;
        } else {
            lapis.setVis\u00edvel(false);
            vezJogador = false;
            if (!doisJogadores) {
                fazJogadaArtificial();
            }
        }

        exibeMensagem("Empate !", vezJogador ? "Tua vez!" : "Vez dele!",
                0x808000, 5000);

    }

    public void terminaJogo(boolean emiteAviso) {
        if (!doisJogadores) {
            return;
        }

        if (emiteAviso) {
            try {
                // Fazer: verificar
                saida.writeInt(10);
                saida.flush();
            } catch (IOException ex) {
            }
        }

        try {
            saida.close();
        } catch (IOException ex) {
        }
        try {
            entrada.close();
        } catch (IOException ex) {
        }
    }

    public int[] getMapaV() {
        return mapaV;
    }

    public Lapis getLapis() {
        return lapis;
    }

    public StringBuffer getTit() {
        return tit;
    }

}
