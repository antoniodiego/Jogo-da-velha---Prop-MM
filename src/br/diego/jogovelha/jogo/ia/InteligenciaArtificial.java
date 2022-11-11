package br.diego.jogovelha.jogo.ia;

import java.util.Vector;

import br.diego.jogovelha.jogo.Constantes;
import br.diego.jogovelha.jogo.Juiz;
import br.diego.jogovelha.jogo.Lugar;
import br.diego.jogovelha.jogo.QuadroVelha;
import br.diego.jogovelha.jogo.Tabuleiro;
import br.diego.jogovelha.util.Verificador;

/**
 * Classe que processa a intelig??ncia artificial da m??quina. Ela ? sempre o
 * jogador convidado.
 *
 * @author Antonio Diego
 */
public class InteligenciaArtificial {

    /**
     * Tabuleiro original.09/09/18 10:00 AS altera??es feitas chegam nessa refe
     * tamb?m.
     */
    private final Tabuleiro tabuleiroOriginal;
    int[][] pontuacoes;
    private final Juiz juiz;
    private boolean depura = true;

    public InteligenciaArtificial(Tabuleiro tabuleiro) {
        this.tabuleiroOriginal = tabuleiro;
        this.juiz = new Juiz();
    }

    /**
     * Escolhe a jogada da m?quina no tabuleiro do jogo.Definamos a m?quina como
     * o jogador maximizador. [Tentando maximizar as chances de ganhar]. Sua
     * letra foi definida como o O
     *
     * @param rodada: o indice da rodada.
     * @param pai
     */
    //FIXME:[ N?o acontece nada ap?s m?quina vencer] 
    //Tempo jogada com m?quina iniciadno : 59613 (Tempo dec deci ms: 59613)
    //TODO: Tentar por titulo
    public void fazJogadaDispositivo(int rodada, QuadroVelha pai) {
        /*
		 * Armazena c??pia do tabuleiro. Ela dever? estar com a jogada do advers??rio.
         */
        //17/05/18 7:34- O n? deve ser max(Em copia tab)
        TabuleiroMatriz copiaTabuleiro = new TabuleiroMatriz();
        Lugar[] lugaresOr = tabuleiroOriginal.getLugares();
        for (int i = 0; i < lugaresOr.length; i++) {
            //Menos 1 pois come 1
            copiaTabuleiro.matriz[lugaresOr[i].coluna - 1][lugaresOr[i].linha - 1] = lugaresOr[i].recebeMarca();
        }
        // Decide o melhor lugar para ser marcado pela m??quina.
        //17/05/18 - Aqui deveria entrar no linha min
        int idx = decideMelhorJogada(copiaTabuleiro, rodada, pai.getTit().toString());
        tabuleiroOriginal.recebeLugar(idx)
                .mudaMarca(Constantes.CARACTER_CONVIDADO);
        pai.getTit().append(pai.getMapaV()[idx]);
    }

    /**
     * Decide melhor indice para ser marcado pela m?quina[para um jogador.]
     *
     * @param tab
     * @param rodada
     * @return O indice do tabuleiro que ? melhor op??o jogada
     */
    /*FIXME: [N?o acontece nada ap?s DAAEACA].
   * Ap?s DAAEACA o jogador perde e a m?quina come?a a jogar mais leva um grande quantidade de tempo para ela decidir sua melhor jogada.
   * FIXME: M?quina leva [~6] segundos para marcar na segunda jogada e talvez cerca [de 9*6 = 54 s na primeira] 21503 ap?s decis?o de local previlegiado.
    *Tempo maquina na segundo caiu para 648 ms e primeiro 4071 ms
     */
    //Linha deve ser min pois ? para a m?quina
    //FIXME: Tem partidas onde est? fazendo jog errada e . Corrigido
    //FIXME: Partida tinha duas op??es vit escolheu fechar vit X
    private int decideMelhorJogada(TabuleiroMatriz tab, int rodada, String tit) {
        //M?quina ? definida como maximizador
        if (depura) {
            System.out.println("Rodada: " + rodada + " da m??quina.");
            System.out.println("Tit decideM(): " + tit);
        }
        long tempoIn = System.currentTimeMillis();
        // Procura lugares vazios do tabuleiro. Esses lugares s?o as op??es de jogada para o pr?ximo jogador.
        // Vector vazios = recebeVazios(tab);
        int[] dadosV = recebeVaziosA(tab);
        int[] valores = new int[9];

        ////
        //Calcula possibilidades de vit?ria
        ////
        //Possibilidade v?toria para m?quina ? prioridade
        LocalSimples[] psVM = testaPossibilidadesVitoria(tab, false);
        //Possibi vit 
        LocalSimples[] psVJ = testaPossibilidadesVitoria(tab, true);
        // possi vit 09/09/18 15:56
        if (psVM.length > 0 || psVJ.length > 0) {
            System.out.println("Achou possibi vit?ria");
            if (psVM.length > 0) {
                System.out.println("Possibi vit?ria m?q");
                return Tabuleiro.retornaIndice(psVM[0].linha + 1, psVM[0].coluna + 1);
            } else if (psVJ.length == 1) {
                //Uma possib para adv
                  System.out.println("Possibi vit?ria jog");
                int idCV = Tabuleiro.retornaIndice(psVJ[0].linha + 1, psVJ[0].coluna + 1);
                //Parece bom fazer isso para deixar vazio somente possi
                dadosV = new int[9];
                dadosV[idCV] = 1;
                //vazios.addElement(idCV);
            }
        }

        ////
        // Testa cada uma jogada poss??vel e decide o valor.
        int alfa = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        int mam = Integer.MIN_VALUE;
        TabuleiroMatriz filho;
        String subtit;
        int valor;
        for (int c = 0; c < dadosV.length; c++) {
            //Dve verificar pois vetor contem apenas info
            if (dadosV[c] == 0) {
                continue;
            }
            //  int valorin = ((Integer) vazios.elementAt(c)).intValue();
            filho = tab.recebeCop();
            //Marca com a marca.
            if (depura) {
                System.out.println("Vi: " + c);
                System.out.println("Col: " + Tabuleiro.retornaCol(c));
            }
            filho.matriz[Tabuleiro.retornaCol(c)][Tabuleiro.retornaLin(c)] = Constantes.CARACTER_CONVIDADO;
            //: Constantes.CARACTER_INICIADOR_JOGO);
            // Decidir valor do lugar.
            if (depura) {
                System.out.println("Testando possibil rodada: " + rodada + "" + " idc: " + c);
            }/*
                Decidir valor do lugar. este valor ? o deste filho
             * Calcular valor do filho [sendo max caso for maquina]. O valor deste deve ser o menor que min pode causar.
            *Calcular valor minimo que o adver?rio pode causar
             */
            //System.out.println("Testando possibil rodada: " + rodada + "" + " idc: " + valorin);
            subtit = tit + (c + 1);// String.valueOf(c + 1);
            //TODO: Por verfica??o de possibilidade vit?ria

            //Valor de acordo com min quando ? a m?quina. 17/05 Isto ? se for m est? em min(x), marcou um o e o valor deste ? o minimo entre os filhos. Os filhos dever? estar na linha max.
            valor = minimax(filho, rodada, alfa, beta, false, subtit);
            System.out.println("Valor det: " + valor);
            //    if (paraMaquina) {
            mam = Math.max(valor, mam);
            alfa = Math.max(alfa, mam);
//            } else {
//                mam = Math.max(valor, mam);
//                beta = Math.min(beta, valor);
//            }

            if (alfa >= beta) {
                if (depura) {
                    //System.out.println("Corte alfa beta [em] ap?s avalia??o de: " + subtit);
                   // System.out.println("bre em c=" + c);
                }
                break;
            }
            if (depura) {
              //  System.out.println("Valor rodada: " + rodada + " indice: "
                    //    + c + " " + valor);
            }
            valores[c] = valor;
//        String tit = String.valueOf(c + 1);
//        int valor = minimax(filho, rodada, Integer.MIN_VALUE, Integer.MAX_VALUE, !paraMaquina, tit);//decideValorJogada(filho, paraMaquina, rodada);
//        System.out.println("Valor rodada: " + rodada + " indice: "
//                + valorin + " " + valor);
//        valores.addElement(new Integer(valor));
        }
        // }

        int indiceMelhor = 0;

        //   if (paraMaquina) {
        if (rodada == 0) {
            if (depura) {
                System.out.println("Valores m?quina iniciando. Copie!");
            }
        }
        //if (rodada >= 3) {
        int maiorValor = Integer.MIN_VALUE;
        int valorL;
        for (int c = 0; c < valores.length; c++) {
            if (dadosV[c] == 0) {
                continue;
            }
            valorL = valores[c];
            if (rodada == 0) {
                if (depura) {
                    System.out.println("Espa?o " + (c + 1) + ":" + valorL);
                }
            }
            if (valorL > maiorValor) {
                System.out.println("Novo valor: " + valorL);
                maiorValor = valorL;
                indiceMelhor = c;
            }
        }
//            } else {
//                indiceMelhor = 0;
//            }
        //   }
        if (depura) {
            System.out.println(
                    "Indice melhor: " + indiceMelhor);
        }
        ///  Integer melhorV = (Integer) vazios.elementAt(indiceMelhor);
        if (depura) {
            System.out.println(
                    "Melhor: " + indiceMelhor);
        }
        //  if (depura) {
        System.out.println("Tempo dec deci ms: " + (System.currentTimeMillis() - tempoIn));
        //  }
        return indiceMelhor;//melhorV.intValue();
        // } else {
        //
        // }
    }

    /**
     * Retorna valor do tabuleiro de acordo com o tipo
     *
     * @param tabuleiro
     * @param jogador
     * @param rodada a rodada tab
     * @return
     */
    //XXX: Calcula EAAF
    //XXX: Obs: Calcula EAAAB
    //Se for uma decis?o para a maquina, ent?o aqui deve ter um tab com marca o, entrara no max(O)
    //XXX: Observ: os nomes n?o est?o iguais ao do pape
    private int minimax(TabuleiroMatriz tabuleiro, int rodada, int alfa, int beta, boolean emax, String tit) {
        //Testa se ? terminal e arma em res
        int resultado = testaTermi(tabuleiro);
        //Empatou, perdeu, ganhou
        boolean tabF = (resultado != Juiz.JogoContinua);//tabuleiro.eFinal();
        //true se min - X
        int pont = 0;
        if (tabF) {
            //Jogo acabou
            // int res = //juiz.verifica(tabuleiro);
            // System.out.println("tab final: res: "+res);
            // tabuleiro.imprime();

            // Jogo acabou
            switch (resultado) {
                case Juiz.VITORIA_CONVIDADO:
                    if (emax) {
                        pont = 10; //- rodada;
                    } else {
                        pont = -10;
                    }
                    break;
                case Juiz.VITORIA_INICIADOR:
                    if (emax) {
                        pont = -10;//+ rodada;
                    } else {
                        pont = 10;
                    }
                    break;
                case Juiz.EMPATE:
                    pont = 0;
                    break;
            }

            return pont;
        } else if (emax) {
            /*17/05/18 07:46- Pela defini??o este deveria escolher os maiores entre os filhos do tabuleiro atual o qual deveria estar com jogada de X-min 
            
             */
          //  System.out.println("E max e nao terminal");
            // tabuleiro.imprime();
//N?o ? terminal
            LocalSimples[] psV = testaPossibilidadesVitoria(tabuleiro, true);
            int[] vazios = new int[9];
            //posiv
            if (psV.length > 0) {
                if (psV.length == 1) {
                    /*Local previlegiado para max - O
                Obs: [Ele ? importante independente do valor ou se leva a um empate.]
                     */
                    // vazios = new int[1];
                    int idCV = Tabuleiro.retornaIndice(psV[0].linha + 1, psV[0].coluna + 1);
                    vazios[idCV] = 1;

                    //return 0;
                    //N?o ? bom retornar zero pois pode levar a uma vit?rio, isto ? ter valor 10.
                } else if (psV.length >= 2) {
                    //Advers?rio tem pelo menos duas possi vi
                    return -10;
                }
            } else {
                // Retornar o menor do advers?rio
                vazios = recebeVaziosA(tabuleiro);
            }

            //MAx
            //17/05/18 7:51 - Este deveria ser considera linha min
            int melhor = -10;//Integer.MIN_VALUE;
            int tamVazios = vazios.length;
            TabuleiroMatriz filho;
            //Integer indiceV;
            String titB;
            int v;
            for (int c = 0; c < tamVazios; c++) {
                if (vazios[c] == 0) {
                    continue;
                }
                //indiceV = 
                filho = tabuleiro.recebeCop();
                //Fazer jogada advers?rio,isto ?, o iniciador, X.
                filho.matriz[Tabuleiro.retornaCol(c)][Tabuleiro.retornaLin(c)] = Constantes.CARACTER_CONVIDADO;
            //    System.out.println("Filho indice: " + c + " rod filh: " + (rodada + 1));
                titB = tit + (c + 1);
             //   System.out.println("tit: " + titB);
                //        filho.imprime();
                v = minimax(filho, rodada + 1, alfa, beta, false, titB);
              //  System.out.println("result minmax min marc indice " + c + " em rod " + (rodada + 1) + " " + v);
                melhor = Math.max(melhor, v);
                alfa = Math.max(alfa, melhor);
              //  System.out.println("Novo alfa: " + alfa);
                if (beta <= alfa) {
                   // System.out.println("corte alfb max [em] ap?s avaliar: " + titB);
                    break;
                }
            }
            return melhor;
        } else {
            /*
             * Min. Escolher menor dentre os m?ximos 
            *Chega aqui ap?s primeira jogada m?quina(decide melhoJ) com O
             */
            // System.out.println("Caiu em min");
            //Testa se tem possibilidade vit?ria para O
            LocalSimples[] psV = testaPossibilidadesVitoria(tabuleiro, false);
            int[] vazios = new int[9];
            if (psV != null && psV.length > 0) {
                if (psV.length == 1) {
                    //Tem possibilidade de vit?ria para O[advers?rio ()]
                    /*Local previlegiado para X(min)[max - O]
             *   Obs: Ele ? importante independente do valor ou se leva a um empate.
                    Os outros lugares dever?o ter pontua??o -10;
                    Esse precisa ser analizado.
                     */
                    //
                    //  vazios = new int[1];
                    int idCV = Tabuleiro.retornaIndice(psV[0].linha + 1, psV[0].coluna + 1);
                    vazios[idCV] = 1;
                    //  return 0;
                } else if (psV.length >= 2) {
                    /*Duas possibilidades vit para O
                    Esse tabuleiro(filho) tem certamente pontua??o 10(para max -O)
                     */
                    //  System.out.println("2 poss");
                    return 10;
                }
            } else {
                //Retornar o menor do advers?rio
                vazios = recebeVaziosA(tabuleiro);
            }

            // tabuleiro.imprime();
            /**
             * O| | |X| | |
             */
            //  Vector vazios = recebeVazios(tabuleiro);
            int pior = 10;///Integer.MAX_VALUE;
            int tamV = vazios.length;
            TabuleiroMatriz filho;
            String titB;
            int max;
            for (int c = 0; c < tamV; c++) {
                if (vazios[c] == 0) {
                    continue;
                }
                //Integer indiceV = (Integer) vazios.elementAt(c);
                filho = tabuleiro.recebeCop();
                //Convi adv
                filho.matriz[Tabuleiro.retornaCol(c)][Tabuleiro.retornaLin(c)] = Constantes.CARACTER_INICIADOR_JOGO;
                //  System.out.println("Filho indice: " + c + " rod filh: " + (rodada + 1));
                titB = tit + (c + 1);
                //  System.out.println("tit: " + titB);
                //    filho.imprime();
                max = minimax(filho, rodada + 1, alfa, beta, true, titB);
                // System.out.println("result minmax max marc indice " + c + " em rod " + (rodada + 1) + " " + max);
                pior = Math.min(pior, max);
                beta = Math.min(pior, beta);
                // System.out.println("Novo beta: " + beta);
                if (beta <= alfa) {
                    //   System.out.println("corte alfb min [em] ap?s avaliar: " + titB);
                    break;
                }
            }
            return pior;
        }
    }

    private int testaTermi(TabuleiroMatriz tab) {
        int[][] matriz = tab.matriz;
        if (testaV(Constantes.CARACTER_INICIADOR_JOGO, matriz)) {
            return Juiz.VITORIA_INICIADOR;
        } else if (testaV(Constantes.CARACTER_CONVIDADO, matriz)) {
            return Juiz.VITORIA_CONVIDADO;
        }

        //Ning?em venceu
        if (tab.cheio()) {
            return Juiz.EMPATE;
        } else {
            return Juiz.JogoContinua;
        }

        //Linha sup
//        if (matriz[0][0] == Constantes.CARACTER_CONVIDADO && matriz[1][0] == Constantes.CARACTER_CONVIDADO && matriz[2][0] == Constantes.CARACTER_CONVIDADO) {
//            //convidado venceu
//        }
    }

    /**
     * Testa se c venceu no tabuleiro atual.
     *
     * @param c
     * @param matriz
     * @return true se ele venceu
     */
    private boolean testaV(char c, int[][] matriz) {
        for (int i = 0; i < 3; i++) {
            return Verificador.verificaLinha(i, matriz, c);
        }
//Colunas
        for (int i = 0; i < 3; i++) {
            return Verificador.verificaColuna(i, matriz, c);
        }

        return Verificador.verificaDiagonal(matriz, c);
    }

    /**
     * Testa possibilidade vit??ria advers??rio.
     *
     * @param tab
     * @param jogadorIniciador
     * @return
     */
    //TODO: Duas poss;
//    private Lugar testaPossibilidadeVitoria(Tabuleiro tab, boolean jogadorIniciador) {
//        char letra;
//        if (jogadorIniciador) {
//            letra = Constantes.CARACTER_INICIADOR_JOGO;
//        } else {
//            letra = Constantes.CARACTER_CONVIDADO;
//        }
//
//        Tira[] tiras = tab.tira;
//        for (int c = 0; c < tiras.length; c++) {
//            Tira t = tiras[c];
//            //LLV
//            if (t.recebeLugar(0).recebeMarca() == letra
//                    && t.recebeLugar(1).recebeMarca() == letra
//                    && t.recebeLugar(2).recebeMarca() == Constantes.SEM_MARCA) {
//                return t.recebeLugar(2);
//            } else if (t.recebeLugar(2).recebeMarca() == letra
//                    && t.recebeLugar(1).recebeMarca() == letra
//                    && t.recebeLugar(0).recebeMarca() == Constantes.SEM_MARCA) {
//                return t.recebeLugar(0);
//            } else if (t.recebeLugar(0).recebeMarca() == letra
//                    && t.recebeLugar(1).recebeMarca() == Constantes.SEM_MARCA
//                    && t.recebeLugar(2).recebeMarca() == letra) {
//                return t.recebeLugar(1);
//            }
//        }
//
//        return null;
//    }
    /**
     * Testa possibilidade vitoria de jogador
     *
     * @param tab
     * @param jogadorIniciador
     * @return
     */
    //TODO: Duas poss;
    //FIXM: Parece n?o func
    private LocalSimples[] testaPossibilidadesVitoria(TabuleiroMatriz tab, boolean jogadorIniciador) {
        char letra;
        if (jogadorIniciador) {
            letra = Constantes.CARACTER_INICIADOR_JOGO;
        } else {
            letra = Constantes.CARACTER_CONVIDADO;
        }
        Vector locS = new Vector();
        LocalSimples l;
        for (int i = 0; i < 3; i++) {
            int col = Verificador.verificaPossVL(tab.matriz, letra, i);
            if (col > -1) {
                l = new LocalSimples();
                l.linha = i;
                l.coluna = col;
                locS.addElement(l);
            }
        }
//Colunas
        for (int i = 0; i < 3; i++) {
            int lin = Verificador.verificaPossVC(tab.matriz, letra, i);
            if (lin > -1) {
                l = new LocalSimples();
                l.coluna = i;
                l.linha = lin;
                locS.addElement(l);
            }
        }

        int[] di0 = Verificador.verificaPossVD(tab.matriz, letra, (byte) 0);
        int[] di1 = Verificador.verificaPossVD(tab.matriz, letra, (byte) 1);

        if (di0[0] != -1) {
            l = new LocalSimples();
            l.coluna = di0[0];
            l.linha = di0[1];
            locS.addElement(l);
        }

        if (di1[0] != -1) {
            System.out.println(" poss diaag 2");
            l = new LocalSimples();
            l.coluna = di1[0];
            l.linha = di1[1];
            locS.addElement(l);
        }

        LocalSimples[] ls = new LocalSimples[locS.size()];
        locS.copyInto(ls);
        return ls;
    }

    /**
     * Retorna um array de Integers de ??ndices vazios.
     *
     * @param tab
     * @return
     */
//    private Vector recebeVazios(TabuleiroMatriz tab) {
//        Vector vazios = new Vector();
//        for (int li = 0; li < 3; li++) {
//            for (int col = 0; col < 3; col++) {
//                //Perc col por linha
//                if (tab.matriz[col][li] == Constantes.SEM_MARCA) {
//                    vazios.addElement(new Integer(Tabuleiro.retornaIndice(li + 1, col + 1)));
//                }
//            }
//        }
//        return vazios;
//    }
    /**
     * Retorna um array de 9 posi??es
     *
     * @param tab
     * @return
     */
    private int[] recebeVaziosA(TabuleiroMatriz tab) {
        int[] vazios = new int[9];
        for (int li = 0; li < 3; li++) {
            for (int col = 0; col < 3; col++) {
                //Perc col por linha
                if (tab.matriz[col][li] == Constantes.SEM_MARCA) {
                    vazios[Tabuleiro.retornaIndice(li + 1, col + 1)] = 1;
                }
            }
        }

        return vazios;
    }
}
