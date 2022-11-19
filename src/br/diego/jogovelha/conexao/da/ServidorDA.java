package br.diego.jogovelha.conexao.da;

import java.io.IOException;
import java.io.InterruptedIOException;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;

import br.diego.jogovelha.jogo.Constantes;
import br.diego.jogovelha.jogo.Jogador;
import br.diego.jogovelha.jogo.QuadroVelha;
import br.diego.jogovelha.jogo.Rival;

/**
 * Classe que representa o servidor do dente azul.
 * 
 * @author Ant??nio Diego
 *
 */
public class ServidorDA implements Runnable {

	/**
	 * Desceve oservidor velha.
	 */
	private static final UUID VELHA_SERVIDOR_UUID = new UUID(
			"F5B0D0E4B0A05A908070605040302010", false);
	/**
	 * Guarda refer??ncia do dispositivo local.
	 */
	private LocalDevice localDevice;
	/**
	 * Aceita conex??es.
	 */
	private StreamConnectionNotifier notifier;
	private boolean isClosed = false;
	private final IUServidor parent;
	private ServiceRecord sr;

	public ServidorDA(IUServidor parent) {
		this.parent = parent;
	}

	public void inicia() {
		boolean isBTReady = false;

		try {
			// create/get a local device
			localDevice = LocalDevice.getLocalDevice();

			// set we are discoverable
			if (!localDevice.setDiscoverable(DiscoveryAgent.GIAC)) {
				isBTReady = false;
			}

			// prepare a URL to create a notifier
			String url = "btspp://localhost:" + VELHA_SERVIDOR_UUID
					+ ";name=Jogo da Velha";

			// create notifier now
			notifier = (StreamConnectionNotifier) Connector.open(url);

			sr = localDevice.getRecord(notifier);
			// remember we've reached this point.
			isBTReady = true;
		} catch (IOException e) {
			isBTReady = false;
		} catch (SecurityException ex) {
			isBTReady = false;
		} catch (IllegalArgumentException ex) {
			isBTReady = false;
		}

		parent.completeInitialization(isBTReady);

		if (isBTReady) {
			iniciaServidor();
		}
	}

	private void iniciaServidor() {
		new Thread(this).start();
	}

	public void run() {
		try {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException ex) {
				Alert alerta = new Alert("Erro", "Thread interrompida: " + ex,
						null, AlertType.ERROR);
				alerta.setTimeout(Alert.FOREVER);
				parent.midlet.getDisplay().setCurrent(alerta);
			}

			if (isClosed) {
				return;
			}

			StreamConnection conn = notifier.acceptAndOpen();

			// RemoteDevice ele = RemoteDevice.getRemoteDevice(conn);
			Jogador jogador = new Jogador();

			// jogador.nome = localDevice.getFriendlyName();
			jogador.setCaracter(Constantes.CARACTER_INICIADOR_JOGO);
			// jogador.isPlayer1 = true;
			jogador.mudaPrimeiroJogador(true);
			jogador.setPontos(0);
			Rival rival = new Rival();

			// RemoteDevice.getRemoteDevice(conn);
			RemoteDevice dispositivo = sr.getHostDevice();

			if (dispositivo != null) {
				rival.setNome(dispositivo.getFriendlyName(true));// sr.getHostDevice().getFriendlyName(true);
			}

			rival.setCaracter(Constantes.CARACTERE_CONVIDADO);
			rival.setId(2);
			rival.setPontos(0);

			QuadroVelha jogo = new QuadroVelha(parent.midlet, jogador, rival);
			jogo.entrada = conn.openDataInputStream();
			jogo.saida = conn.openDataOutputStream();

			jogo.getLapis().setVis\u00edvel(true);
			jogo.setDoisJogadores(true);

			parent.midlet.getDisplay().setCurrent(jogo);

			jogo.iniciaProcessamentoMensagens();
			jogo.exibeMensagem("Tua vez.", null, 0x008000, 3000);
		} catch (InterruptedIOException iioe) {
			Alert alerta = new Alert("Info", "Cancelado", null,
					AlertType.CONFIRMATION);
			parent.midlet.getDisplay().setCurrent(alerta,
					new MenuDA(parent.midlet));
			destroy();
		} catch (IOException ex) {
			Alert alerta = new Alert("Erro",
					"Problema durante a conex??o: " + ex, null, AlertType.ERROR);
			alerta.setTimeout(Alert.FOREVER);
			parent.midlet.getDisplay().setCurrent(alerta,
					new MenuDA(parent.midlet));
			destroy();
		} catch (SecurityException ex) {
			parent.telaEspera
					.append("Parece que tu n??o permitiste o uso de conectividade.");
			parent.midlet.getDisplay().setCurrent(parent.telaEspera);
			destroy();
		}
	}

	public void destroy() {
		isClosed = true;

		try {
			if (notifier != null) {
				notifier.close();
			}
		} catch (IOException ex) {
			Alert alerta = new Alert("Erro", "Ocorreu um problema ao cancelar: " + ex,
					null, AlertType.ERROR);
			alerta.setTimeout(Alert.FOREVER);
			parent.midlet.getDisplay().setCurrent(alerta);
		}
	}
}
