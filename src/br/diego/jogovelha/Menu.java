package br.diego.jogovelha;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;

import br.diego.jogovelha.conexao.da.MenuDA;
import br.diego.jogovelha.conexao.internet.MenuInternet;
import br.diego.jogovelha.jogo.Constantes;
import br.diego.jogovelha.jogo.Jogador;
import br.diego.jogovelha.jogo.QuadroVelha;
import br.diego.jogovelha.jogo.Rival;

/**
 * Menu do jogo.
 * 
 * @author Ant??nio Diego.
 *
 */
public class Menu extends List implements CommandListener {

	private final Command cmdSair = new Command("Sair", Command.EXIT, 0);
	private final PrincipalVelha midlet;
	private String texto;

	public Menu(PrincipalVelha midlet) {
		super("Jogo da Velha", List.IMPLICIT);
		this.midlet = midlet;
		try {
			// append("Um jogador", Image.createImage("/imagens/lapis.png"));
			// append("Dente azul", Image.createImage("/imagens/lapis.png"));
			// append("Internet", Image.createImage("/imagens/lapis.png"));
			// append("Sobre", Image.createImage("/imagens/sobre.png"));
			// append("Ajuda", Image.createImage("/imagens/ajuda.png"));

			// Fazer: contra dispositivo.
			// Fazer: contra dispositivo est?? f??cil.

			append("Contra o dispositivo",
					Image.createImage("/imagens/dispositivo.png"));
			append("Dente azul", Image.createImage("/imagens/dente azul.png"));
			append("Internet", Image.createImage("/imagens/globo b.png"));
			append("Sobre", Image.createImage("/imagens/sobre.png"));
			append("Ajuda", Image.createImage("/imagens/ajuda.png"));
			texto = leTextoAjuda();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		addCommand(cmdSair);
		setCommandListener(this);
	}

	private String leTextoAjuda() throws IOException {
		InputStream en = getClass()
				.getResourceAsStream("/br/diego/jogovelha/ajuda.txt");

		StringBuffer ajuda = new StringBuffer();
		int quantidade;
		byte[] receptor = new byte[1024];
		while ((quantidade = en.read(receptor)) != -1) {
			ajuda.append(new String(receptor, 0, quantidade, "UTF-8"));
		}
		return ajuda.toString();
	}

	public void commandAction(Command c, Displayable d) {
		if (c == cmdSair) {
			midlet.destroyApp(true);
		} else if (c == SELECT_COMMAND) {
			switch (getSelectedIndex()) {
			case 0:
				iniciaJogo();
				break;
			case 1:
				midlet.getDisplay().setCurrent(new MenuDA(midlet));
				break;
			case 2:
				midlet.getDisplay().setCurrent(new MenuInternet(midlet));
				break;
			case 3:
				Alert alerta = new Alert("Sobre",
						"Jogo da Velha.\n Vers\u00E3o: "
								+ midlet.getAppProperty("MIDlet-Version")
								+ "\nFeito por: Ant\u00F4nio Diego\nSaite: http://antoniodiego.wapka.mobi",
						null, AlertType.INFO);
				alerta.setTimeout(Alert.FOREVER);
				midlet.getDisplay().setCurrent(alerta);
				break;
			case 4:
				Alert ajuda = new Alert("Ajuda", texto, null, AlertType.INFO);
				ajuda.setTimeout(Alert.FOREVER);
				midlet.getDisplay().setCurrent(ajuda);
				break;
			}
		}
	}

	/**
	 * Inicia o jogo contra a m??quina.
	 */
	private void iniciaJogo() {
		Jogador jogador = new Jogador();
		jogador.setCaracter(Constantes.CARACTER_INICIADOR_JOGO);
		jogador.mudaPrimeiroJogador(true);
		// jogador.isPrimeiro = true;
		jogador.setPontos(0);

		Rival rival = new Rival();
		rival.setPontos(0);
		rival.setCaracter(Constantes.CARACTER_CONVIDADO);
		QuadroVelha canvasVelha = new QuadroVelha(midlet, jogador, rival);
		canvasVelha.setDoisJogadores(false);
		canvasVelha.getLapis().setVis\u00edvel(true);

		midlet.getDisplay().setCurrent(canvasVelha);
		canvasVelha.exibeMensagem("Tua vez!", null, 0x00a000, 3000);
	}
}
