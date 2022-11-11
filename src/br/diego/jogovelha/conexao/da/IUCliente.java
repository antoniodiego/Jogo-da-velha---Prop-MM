package br.diego.jogovelha.conexao.da;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.microedition.lcdui.*;

import br.diego.jogovelha.PrincipalVelha;

public class IUCliente implements CommandListener, Runnable {

	private final Command COMANDO_BUSCAR = new Command("Buscar", Command.OK, 0);
	private final Command BUSCA_COMANDO_CANCELAR = new Command("Cancelar",
			Command.CANCEL, 1);
	private final Command CONECTANDO_COMANDO_CANCELAR = new Command("Cancelar",
			Command.CANCEL, 1);
	private final Command COMANDO_VOLTAR = new Command("Voltar", Command.BACK,
			2);
	/**
	 * Tela principar para a parte do cliente.
	 */
	private final Form telaPrincipal = new Form("Jogo da Velha");
	/**
	 * Tela com dispositivos encontrados.
	 */
	private final List telaJogos = new List("Jogo da Velha", List.IMPLICIT);
	/**
	 * Objeto que faz o processo real
	 */
	private ClienteDA clienteDa;
	public final PrincipalVelha midlet;

	public IUCliente(PrincipalVelha midlet) {
		this.midlet = midlet;
		telaPrincipal.addCommand(COMANDO_BUSCAR);
		telaPrincipal.addCommand(COMANDO_VOLTAR);
		telaPrincipal.setCommandListener(this);

		telaJogos.addCommand(COMANDO_VOLTAR);
		telaJogos.setCommandListener(this);
	}

	public void inicializacaoCompleta(boolean daPronto) {
		// bluetooth was initialized successfully.

		if (daPronto) {
			StringItem si = new StringItem("Procurar jogos!", null);
			si.setLayout(StringItem.LAYOUT_CENTER | StringItem.LAYOUT_VCENTER);
			telaPrincipal.append(si);
			midlet.getDisplay().setCurrent(telaPrincipal);

			return;
		}

		// something wrong
		Alert al = new Alert("Erro", "Falha ao inicializar o Bluetooth", null,
				AlertType.ERROR);
		al.setTimeout(Alert.FOREVER);
		midlet.getDisplay().setCurrent(al);
	}

	public void commandAction(Command c, Displayable d) {
		if (c == BUSCA_COMANDO_CANCELAR) {
			clienteDa.cancelaBusca();
			midlet.getDisplay().setCurrent(telaPrincipal);

			return;
		}

		if (c == CONECTANDO_COMANDO_CANCELAR) {
			clienteDa.cancelaConexao();
			midlet.getDisplay().setCurrent(telaJogos);
			return;
		}

		if (d == telaPrincipal) {
			if (c == COMANDO_BUSCAR) {
				Form busca = new Form("Buscando...");
				busca.addCommand(BUSCA_COMANDO_CANCELAR);
				busca.setCommandListener(this);
				busca.append(new Gauge("Procurando jogos...", false,
						Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING));

				midlet.getDisplay().setCurrent(busca);

				clienteDa.iniciaBusca();
			} else if (c == COMANDO_VOLTAR) {
				destroy();
				midlet.getDisplay().setCurrent(new MenuDA(midlet));
			}
		} else if (d == telaJogos) {
			if (c == COMANDO_VOLTAR) {
				midlet.getDisplay().setCurrent(telaPrincipal);
			} else if (c == List.SELECT_COMMAND) {
				Form conecta = new Form("Conectando...");
				conecta.addCommand(CONECTANDO_COMANDO_CANCELAR);
				conecta.setCommandListener(this);
				conecta.append(new Gauge("Conectando...", false,
						Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING));
				midlet.getDisplay().setCurrent(conecta);
				clienteDa.conecta(telaJogos.getSelectedIndex());
			}
		}
	}

	private void destroy() {
		clienteDa.destroy();
	}

	public void informSearchError(String cant_start_device_search) {
		Alert alerta = new Alert("Erro", cant_start_device_search, null,
				AlertType.ERROR);
		alerta.setTimeout(Alert.FOREVER);
		midlet.getDisplay().setCurrent(alerta, telaPrincipal);
	}

	public void exibeJogos(Vector records) {
		telaJogos.deleteAll();

		Image phoneImg = null;

		try {
			phoneImg = Image.createImage("/imagens/op????o.png");
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		Enumeration e = records.elements();

		while (e.hasMoreElements()) {
			ServiceRecord sr = (ServiceRecord) e.nextElement();
			RemoteDevice dispoistivo = sr.getHostDevice();

			try {
				telaJogos.append(dispoistivo.getFriendlyName(true), phoneImg);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		midlet.getDisplay().setCurrent(telaJogos);
	}

	public void run() {
		clienteDa = new ClienteDA(this);
	}

	public void inicia() {
		new Thread(this).start();
	}
}
