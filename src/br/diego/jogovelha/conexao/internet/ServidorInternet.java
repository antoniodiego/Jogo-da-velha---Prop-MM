package br.diego.jogovelha.conexao.internet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.StreamConnection;
import javax.microedition.lcdui.*;

import br.diego.jogovelha.PrincipalVelha;
import br.diego.jogovelha.jogo.Constantes;
import br.diego.jogovelha.jogo.Jogador;
import br.diego.jogovelha.jogo.QuadroVelha;
import br.diego.jogovelha.jogo.Rival;

public class ServidorInternet implements CommandListener, Runnable {

	private final Form iu = new Form("Criar jogo");
	private final Command cmdCancelar = new Command("Cancelar", Command.CANCEL,
			0);
	private boolean cancelado = true;
	private final PrincipalVelha midlet;

	public ServidorInternet(PrincipalVelha midlet) {
		this.midlet = midlet;
		this.iu.addCommand(cmdCancelar);
		this.iu.setCommandListener(this);
		this.iu.append("Preparando...\n");
	}

	public void commandAction(Command c, Displayable d) {
		if (c == cmdCancelar) {
			this.cancelado = true;
			midlet.getDisplay().setCurrent(new MenuInternet(midlet));
		}
	}

	public void exibeIU() {
		midlet.getDisplay().setCurrent(iu);
	}

	public void run() {
		cancelado = false;

		try {
			Thread.sleep(2000);
		} catch (InterruptedException ex) {
			Alert alerta = new Alert("Erro", "Thread interrompida: " + ex, null,
					AlertType.ERROR);
			alerta.setTimeout(Alert.FOREVER);
			midlet.getDisplay().setCurrent(alerta);
		}

		if (cancelado) {
			return;
		}

		try {
			ServerSocketConnection servidor = (ServerSocketConnection) Connector
					.open("socket://:5542");
			this.iu.append("Aguardando no endere??o PI: " + '"'
					+ servidor.getLocalAddress() + '"' + "...");
			StreamConnection conn = servidor.acceptAndOpen();

			if (cancelado) {
				return;
			}

			DataOutputStream out = conn.openDataOutputStream();
			DataInputStream in = conn.openDataInputStream();

			Jogador jogador = new Jogador();
			jogador.setCaracter(Constantes.CARACTER_INICIADOR_JOGO);
			//jogador.isPlayer1 = true;
			jogador.mudaPrimeiroJogador(true);
			jogador.setPontos(0);

			Rival rival = new Rival();
			rival.setCaracter(Constantes.CARACTERE_CONVIDADO);
			rival.setId(2);
			rival.setPontos(0);

			QuadroVelha jogo = new QuadroVelha(midlet, jogador, rival);
			jogo.entrada = in;
			jogo.saida = out;
			jogo.getLapis().setVis\u00edvel(true);
			jogo.setDoisJogadores(true);

			midlet.getDisplay().setCurrent(jogo);
			jogo.iniciaProcessamentoMensagens();
			jogo.exibeMensagem("Tua vez.",null,0x008000,3000);
		} catch (IOException ex) {
			Alert alerta = new Alert("Erro", "Problema ao conectar " + ex, null,
					AlertType.ERROR);
			alerta.setTimeout(Alert.FOREVER);
			midlet.getDisplay().setCurrent(alerta);
		} catch (SecurityException ex) {
			iu.append("Voc?? nao permitiu a conex??o com a Internet");
		}
	}
}
