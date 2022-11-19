package br.diego.jogovelha.conexao.da;

import java.io.IOException;
import java.util.Vector;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;

import br.diego.jogovelha.jogo.Constantes;
import br.diego.jogovelha.jogo.Jogador;
import br.diego.jogovelha.jogo.QuadroVelha;
import br.diego.jogovelha.jogo.Rival;

/**
 * Classe que representa o cliente Dente Azul.
 *
 * @author Ant??nio Diego
 *
 */
public class ClienteDA implements DiscoveryListener {

    public static final boolean DEPURAR = false;
    /**
     * Desceve o servidor velha.
     *
     */
    private static final UUID UUID_SERVIDOR_VELHA = new UUID(
            "F5B0D0E4B0A05A908070605040302010", false);
    /**
     * Engine pronta.
     */
    private static final int PRONTO = 0;
    /**
     * Engine buscando dispositivos
     */
    private static final int BUSCANDO_DISPOSITIVOS = 1;
    /**
     * Engine buscando servicos
     */
    private static final int BUSCANDO_SERVICOS = 2;
    /**
     * Guarda estado da engine.
     */
    private int estado = PRONTO;
    /**
     * Guarda refer??ncia do discovery agent.
     */
    private DiscoveryAgent agenteDescoberta;
    /**
     * Guarda refer??ncia da interface usu??rio.
     */
    private IUCliente pai;
    /**
     * Guarda dispositivos encontrados.
     */
    private Vector dispositivos = new Vector();
    /**
     * Guarda os servicos encontrados.
     */
    private Vector gravacoes = new Vector();
    /**
     * Guarda codigo retornado da busca de dispositivos.
     *
     */
    // private intdiscType;
    /**
     * Guarda ids das buscas de servicos (Para poder cancelar).
     *
     */
    private int[] idsBuscas;
    /**
     * Guarda padr??o de busca de servi??os.
     */
    private UUID[] grupoUUID;
    private boolean isClosed = false;
    private boolean conexaoCancelada;
    private LocalDevice localDevice;

    public ClienteDA(IUCliente pai) {
        this.pai = pai;
        boolean isBTReady = false;
        try {
            // create/get a local device and discovery agent
            localDevice = LocalDevice.getLocalDevice();
            agenteDescoberta = localDevice.getDiscoveryAgent();

            // remember we've reached this point.
            isBTReady = true;
        } catch (Exception e) {
        }

        pai.inicializacaoCompleta(isBTReady);

        // nothing to do if no bluetooth available
        if (!isBTReady) {
            return;
        }

        // initialize some optimization variables
        grupoUUID = new UUID[2];

        // ok, we are interesting in btspp services only
        grupoUUID[0] = new UUID(0x1101);

        // and only known ones, that allows pictures
        grupoUUID[1] = UUID_SERVIDOR_VELHA;
    }

    private synchronized boolean buscaDispositivos() {
        // Certo, iniciar uma nova busca ent??o
        estado = BUSCANDO_DISPOSITIVOS;
        dispositivos.removeAllElements();

        try {
            agenteDescoberta.startInquiry(DiscoveryAgent.GIAC, this);
        } catch (BluetoothStateException e) {
            pai.informSearchError("Problema ao iniciar busca.");

            return true;
        }

        // Aguardar termino
        try {
            wait(); // at?? que dispositivos sejam encontrados.
        } catch (InterruptedException e) {
            return false;
        }

        // Este "wake up" pode ser causado por uma chamada de 'destroy'
        if (isClosed) {
            return false;
        }

        return true;
    }

    public synchronized boolean buscaServicos() {
        estado = BUSCANDO_SERVICOS;
        gravacoes.removeAllElements();
        idsBuscas = new int[dispositivos.size()];

        boolean isSearchStarted = false;

        for (int i = 0; i < dispositivos.size(); i++) {
            RemoteDevice rd = (RemoteDevice) dispositivos.elementAt(i);

            try {
                idsBuscas[i] = agenteDescoberta.searchServices(null, grupoUUID,
                        rd, this);
            } catch (BluetoothStateException e) {
                idsBuscas[i] = -1;

                continue;
            }

            isSearchStarted = true;
        }

        // at least one of the services search should be found
        if (!isSearchStarted) {
            pai.informSearchError("Falha ao buscar jogos.");

            return true;
        }

        // Aguradar termino da busca
        try {
            wait(); // until services are found
        } catch (InterruptedException e) {

            return false;
        }

        // this "wake up" may be caused by 'destroy' call
        if (isClosed) {
            return false;
        }

        // actually, no services were found
        if (gravacoes.isEmpty()) {
            pai.informSearchError("Nenhum jogo encontrado");
        }

        return true;
    }

    public void iniciaBusca() {
        Runnable busca = new Runnable() {

            public void run() {
                if (!buscaDispositivos() || dispositivos.isEmpty()) {
                    return;
                }

                // search for services now
                if (!buscaServicos() || gravacoes.isEmpty()) {
                    return;
                }

                pai.exibeJogos(gravacoes);
            }
        };

        new Thread(busca).start();
    }

    public void cancelaBusca() {
        synchronized (this) {
            if (estado == BUSCANDO_DISPOSITIVOS) {
                agenteDescoberta.cancelInquiry(this);
            } else if (estado == BUSCANDO_SERVICOS) {
                for (int i = 0; i < idsBuscas.length; i++) {
                    agenteDescoberta.cancelServiceSearch(idsBuscas[i]);
                }
            }
        }
    }

    public void conecta(final int indice) {

        if (indice < 0) {
            return;
        }
        new Thread(new Runnable() {

            public void run() {
                conexaoCancelada = false;

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    Alert alerta = new Alert("Erro", "Thread interrompida" + ex,
                            null, AlertType.ERROR);
                    alerta.setTimeout(Alert.FOREVER);
                    pai.midlet.getDisplay().setCurrent(alerta);
                }

                if (conexaoCancelada) {
                    return;
                }

                ServiceRecord sr = (ServiceRecord) gravacoes.elementAt(indice);

                // RemoteDevice ele = sr.getHostDevice();
                String url = sr.getConnectionURL(
                        ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);

                try {
                    StreamConnection conexao = (StreamConnection) Connector
                            .open(url);

                    Jogador jogador = new Jogador();
                    jogador.setCaracter(Constantes.CARACTERE_CONVIDADO);
                    //jogador.isPlayer1 = false;
                    jogador.mudaPrimeiroJogador(false);
                    // jogador.nome = localDevice.getFriendlyName();
                    jogador.setPontos(0);

                    Rival rival = new Rival();
                    rival.setCaracter(Constantes.CARACTER_INICIADOR_JOGO);
                    rival.setId(1);
                    // rival.nome = ele.getFriendlyName(true);
                    rival.setPontos(0);

                    QuadroVelha jogo = new QuadroVelha(pai.midlet, jogador,
                            rival);
                    jogo.entrada = conexao.openDataInputStream();
                    jogo.saida = conexao.openDataOutputStream();

                    jogo.getLapis().setVis\u00edvel(false);
                    jogo.setDoisJogadores(true);
                    pai.midlet.getDisplay().setCurrent(jogo);
                    jogo.iniciaProcessamentoMensagens();

                    jogo.exibeMensagem("Vez dele.", null, 0x808000, 3000);
                } catch (IOException ex) {
                    Alert alerta = new Alert("Erro",
                            "Problema durante a conex??o: " + ex, null,
                            AlertType.ERROR);
                    alerta.setTimeout(Alert.FOREVER);
                    pai.midlet.getDisplay().setCurrent(alerta);
                }
            }
        }).start();
    }

    public void cancelaConexao() {
        conexaoCancelada = true;
    }

    public void destroy() {
        synchronized (this) {
            isClosed = true;
            conexaoCancelada = true;
            notify();
        }
    }

    public void deviceDiscovered(RemoteDevice rd, DeviceClass dc) {
        if (DEPURAR) {
            System.out.println("RemoteDevice: " + rd);
        }

        if (dispositivos.indexOf(rd) == -1) {
            dispositivos.addElement(rd);
        }
    }

    public void inquiryCompleted(int discType) {
        // this.discType = discType;

        synchronized (this) {
            notify();
        }
    }

    public void servicesDiscovered(int transID, ServiceRecord[] srs) {

        if (DEPURAR) {
            System.out.println("Services Discovered");
        }

        for (int i = 0; i < srs.length; i++) {
            if (DEPURAR) {
                System.out.println("Service Record: " + srs[i]);
            }
            gravacoes.addElement(srs[i]);
        }
    }

    public void serviceSearchCompleted(int transID, int respCode) {
        // Procurar indice do transID em searchIDs
        int index = -1;

        for (int i = 0; i < idsBuscas.length; i++) {
            if (idsBuscas[i] == transID) {
                index = i;

                break;
            }
        }

        // Erro transID nao consta
        if (index == -1) {
            System.out.println("Busca por servi??o completado n??o foi feita");
        } else {
            // Encontrou -> muda o valor pra -1
            idsBuscas[index] = -1;
        }

        // Verificar se existe algum sem ser -1 se sim retorna, se todos for -1
        // busca de servicos completas
        for (int i = 0; i < idsBuscas.length; i++) {
            if (idsBuscas[i] != -1) {
                return;
            }
        }

        // Todas as transa??oes completas.
        synchronized (this) {
            notify();
        }
    }
}
