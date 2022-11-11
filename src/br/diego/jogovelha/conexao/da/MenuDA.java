package br.diego.jogovelha.conexao.da;

import java.io.IOException;
import javax.microedition.lcdui.*;

import br.diego.jogovelha.Menu;
import br.diego.jogovelha.PrincipalVelha;

/**
 * Menu do Dente azul.
 * 
 * @author Antônio Diego.
 *
 */
public class MenuDA extends List implements CommandListener {
	private final PrincipalVelha vmid;

	public MenuDA(PrincipalVelha vmid) {
		super("Novo Jogo", List.IMPLICIT);
		this.vmid = vmid;
		// Fazer: verificar direitos das imagens.

		try {
			append("Criar jogo", Image.createImage("/imagens/novo.png"));
			append("Procurar jogo", Image.createImage("/imagens/porta b.png"));
		} catch (IOException ex) {
		}

		addCommand(new Command("Voltar", Command.BACK, 0));
		setCommandListener(this);
	}

	public void commandAction(Command c, Displayable d) {
		if ("Voltar".equals(c.getLabel())) {
			vmid.getDisplay().setCurrent(new Menu(vmid));
		} else if (c == SELECT_COMMAND) {
			switch (getSelectedIndex()) {
			case 0:
				IUServidor servidor = new IUServidor(vmid);
				servidor.start();
				break;
			case 1:
				IUCliente cliente = new IUCliente(vmid);
				cliente.inicia();
				break;
			}
		}
	}
}
