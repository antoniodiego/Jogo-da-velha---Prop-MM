package br.diego.jogovelha.conexao.internet;

import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;
import javax.microedition.lcdui.*;

import br.diego.jogovelha.PrincipalVelha;
import br.diego.jogovelha.jogo.Constantes;
import br.diego.jogovelha.jogo.Jogador;
import br.diego.jogovelha.jogo.QuadroVelha;
import br.diego.jogovelha.jogo.Rival;

/**
 * Cliente do jogo via Internet.
 * 
 * @author Ant??nio Diego
 *
 */
public class ClienteInternet implements CommandListener, Runnable {

	private final TextBox campoIp = new TextBox("Endere??o PI:", null, 15,
			TextField.ANY);
	private final Command backCommmand = new Command("Voltar", Command.BACK, 0);
	private final Command cancelCommmand = new Command("Cancelar",
			Command.CANCEL, 0);
	private final Command connectCommand = new Command("Conectar", Command.OK,
			1);
	private final PrincipalVelha midlet;
	private Form log = new Form("Conectando");
	private boolean conexaoCancelada;

	public ClienteInternet(PrincipalVelha midlet) {
		this.midlet = midlet;

		campoIp.addCommand(backCommmand);
		campoIp.addCommand(connectCommand);
		campoIp.setCommandListener(this);

		log.addCommand(cancelCommmand);
		log.setCommandListener(this);
	}

	public void exibeIGU() {
		midlet.getDisplay().setCurrent(campoIp);
	}

	public void commandAction(Command c, Displayable d) {
		if (c == backCommmand) {
			conexaoCancelada = true;
			midlet.getDisplay().setCurrent(new MenuInternet(midlet));
		} else if (c == connectCommand) {
			new Thread(this).start();
		} else if (c == cancelCommmand) {
			this.conexaoCancelada = true;
			midlet.getDisplay().setCurrent(campoIp);
		}
	}

	public void run() {
		midlet.getDisplay().setCurrent(log);

		try {
			Thread.sleep(2000);
		} catch (InterruptedException ex) {
			Alert alerta = new Alert("Erro", "Thread interrompida" + ex, null,
					AlertType.ERROR);
			alerta.setTimeout(Alert.FOREVER);
			midlet.getDisplay().setCurrent(alerta);
		}

		if (conexaoCancelada) {
			return;
		}

		try {
			SocketConnection conexao = (SocketConnection) Connector
					.open("socket://" + campoIp.getString() + ":5542");

			if (conexaoCancelada) {
				conexao.close();
				return;
			}

			Jogador jogador = new Jogador();
			jogador.setCaracter(Constantes.CARACTERE_CONVIDADO);
			// jogador.isPlayer1 = false;
			jogador.mudaPrimeiroJogador(false);
			jogador.setPontos(0);

			Rival rival = new Rival();
			rival.setCaracter(Constantes.CARACTER_INICIADOR_JOGO);
			rival.setId(1);
			rival.setPontos(0);

			QuadroVelha jogo = new QuadroVelha(midlet, jogador, rival);

			jogo.entrada = conexao.openDataInputStream();
			jogo.saida = conexao.openDataOutputStream();
			jogo.getLapis().setVis\u00edvel(false);
			jogo.setDoisJogadores(true);

			if (conexaoCancelada) {
				jogo.terminaJogo(true);
			}

			midlet.getDisplay().setCurrent(jogo);

			jogo.iniciaProcessamentoMensagens();
			jogo.exibeMensagem("Vez dele.",null,0x808000,3000);
		} catch (IOException ex) {
			Alert alerta = new Alert("Erro", "Problema ao conectar " + ex, null,
					AlertType.ERROR);
			alerta.setTimeout(Alert.FOREVER);
			midlet.getDisplay().setCurrent(alerta);
		} catch (SecurityException ex) {
			log.append("Voc?? parece n??o ter permitido a conex??o com a Internet.");
		}
	}
}
