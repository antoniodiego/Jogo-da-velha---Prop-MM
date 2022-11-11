package br.diego.jogovelha.conexao.da;

import javax.microedition.lcdui.*;

import br.diego.jogovelha.PrincipalVelha;

/**
 * Interface usúario do servidor.
 * 
 * @author Antônio Diego
 *
 */
public class IUServidor implements CommandListener, Runnable {

	private final Command COMANDO_CANCELAR = new Command("Cancelar",
			Command.CANCEL, 0);
	public final Form telaEspera = new Form("Criar jogo");
	private ServidorDA bt_servidor;
	/**
	 * Classe MIDlet.
	 * 
	 */
	public final PrincipalVelha midlet;

	public IUServidor(PrincipalVelha midlet) {
		this.midlet = midlet;

		telaEspera.addCommand(COMANDO_CANCELAR);
		telaEspera.setCommandListener(this);
	}

	public void start() {
		new Thread(this).start();
	}

	public void commandAction(Command c, Displayable d) {
		if (c == COMANDO_CANCELAR) {
			bt_servidor.destroy();
			midlet.getDisplay().setCurrent(new MenuDA(midlet));
		}
	}

	public void completeInitialization(boolean btReady) {
		// bluetooth iniciado
		if (btReady) {
			telaEspera.append("Aguardando outro jogador...");
		} else {
			telaEspera.append("Falha ao iniciar bluetooth");
		}
		midlet.getDisplay().setCurrent(telaEspera);

		// something wrong
		// Alert al = new Alert("Erro", "Falha ao iniciar bluetooth", null,
		// AlertType.INFO);
		// al.setTimeout(Alert);
		// midlet.getDisplay().setCurrent(al);
	}

	public void run() {
		bt_servidor = new ServidorDA(this);
		bt_servidor.inicia();
	}

}
