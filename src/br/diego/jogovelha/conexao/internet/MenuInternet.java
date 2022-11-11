package br.diego.jogovelha.conexao.internet;

import java.io.IOException;
import javax.microedition.lcdui.*;

import br.diego.jogovelha.Menu;
import br.diego.jogovelha.PrincipalVelha;

public class MenuInternet extends List implements CommandListener {

    private final PrincipalVelha vmid;

    public MenuInternet(PrincipalVelha vmid) {
        super("Online", List.IMPLICIT);

        this.vmid = vmid;

        try {
            append("Iniciar jogo", Image.createImage("/imagens/novo.png"));
            append("Entrar em jogo", Image.createImage("/imagens/porta b.png"));
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
                    ServidorInternet servidor = new ServidorInternet(vmid);
                    servidor.exibeIU();
                    new Thread(servidor).start();
                    break;
                case 1:
                    ClienteInternet client = new ClienteInternet(vmid);
                    client.exibeIGU();
                    break;
            }
        }
    }
}
