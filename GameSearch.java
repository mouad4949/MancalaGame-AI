package GameSearch;

import java.util.*;

import static java.lang.System.exit;

public abstract class GameSearch {

    public static final boolean DEBUG = false;

    /*
     * Note: the abstract Position also needs to be
     *       subclassed to write a new game program.
     */
    /*
     * Note: the abstract class Move also needs to be subclassed.
     *       
     */

    public static boolean PROGRAM = false;
    public static boolean HUMAN = true;

    /**
     *  Notes:  PROGRAM false -1,  HUMAN true 1
     */

    /*
     * Abstract methods:
     */

    public abstract boolean drawnPosition(Position p);
    public abstract boolean wonPosition(Position p, boolean player);
    public abstract float positionEvaluation(Position p, boolean player);
    public abstract void printPosition(Position p);
    public abstract Position [] possibleMoves(Position p, boolean player);
    public abstract Position makeMove(Position p, boolean player, Move move);
    public abstract boolean reachedMaxDepth(Position p, int depth);
    public abstract Move createMove();
    public abstract Position makemovemulitjoueur(Position p,boolean joueur,Move move);
    /*
     * Search utility methods:
     */

    protected Vector alphaBeta(int depth, Position p, boolean player) {
        Vector v = alphaBetaHelper(depth, p, player, 1000000.0f, -1000000.0f);
        //System.out.println("^^ v(0): " + v.elementAt(0) + ", v(1): " + v.elementAt(1));
        return v;
    }

    protected Vector alphaBetaHelper(int depth, Position p,
                                     boolean player, float alpha, float beta) {
        if (GameSearch.DEBUG) System.out.println("alphaBetaHelper("+depth+","+p+","+alpha+","+beta+")");
        if (reachedMaxDepth(p, depth)) {
            Vector v = new Vector(2);
            float value = positionEvaluation(p, player);
            v.addElement(new Float(value));
            v.addElement(null);
            if(GameSearch.DEBUG) {
                System.out.println(" alphaBetaHelper: mx depth at " + depth+
                                   ", value="+value);
            }
            return v;
        }
        Vector best = new Vector();
        Position [] moves = possibleMoves(p, player);
        for (int i=0; i<moves.length; i++) {
            Vector v2 = alphaBetaHelper(depth + 1, moves[i], !player, -beta, -alpha);
            //  if (v2 == null || v2.size() < 1) continue;
            float value = -((Float)v2.elementAt(0)).floatValue();
            if (value > beta) {
                if(GameSearch.DEBUG) System.out.println(" ! ! ! value="+value+", beta="+beta);
                beta = value;
                best = new Vector();
                best.addElement(moves[i]);
                Enumeration enum2 = v2.elements();
                enum2.nextElement(); // skip previous value
                while (enum2.hasMoreElements()) {
                    Object o = enum2.nextElement();
                    if (o != null) best.addElement(o);
                }
            }
            /**
             * Use the alpha-beta cutoff test to abort search if we
             * found a move that proves that the previous move in the
             * move chain was dubious
             */
            if (beta >= alpha) {
                break;
            }
        }
        Vector v3 = new Vector();
        v3.addElement(new Float(beta));
        Enumeration enum2 = best.elements();
        while (enum2.hasMoreElements()) {
            v3.addElement(enum2.nextElement());
        }
        return v3;
    }


    public void playMultiplayerGame(Position startingPosition, boolean joueur1) {
        MancalaGame gameLogic = new MancalaGame();
        boolean gameOver = false; // Drapeau pour arrêter le jeu proprement

        while (!gameOver) {
            System.out.println("\nMenu :");
            System.out.println("1. Jouer un tour");
            System.out.println("2. Sauvegarder la partie");
            System.out.println("3. Charger une partie");
            System.out.println("4. Quitter");
            System.out.print("Choisissez une option : ");
            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();

            switch (choice) {
                case 1: // Jouer un tour
                    gameLogic.printPosition(startingPosition); // Affiche l'état du board avant chaque tour

                    MancalaPosition pos = (MancalaPosition) startingPosition;
                    // Vérifiez si la partie est terminée
                    if (gameLogic.isGameOver(pos)) {
                        gameLogic.finDePartie(pos);
                        gameOver = true; // Arrêtez la boucle
                        continue; // Passez directement à la fin de la boucle
                    }
                    // Demander le tour au joueur
                    boolean mouvementValide = false;
                    while (!mouvementValide) {
                        System.out.print((joueur1 ? "Joueur 1" : "Joueur 2") + ", choisissez votre case : ");
                        Move move = createMove(); // Récupère le mouvement du joueur
                        int selectedPit = ((MancalaMove) move).selectedPit;

                        // Vérifiez que le joueur joue sur ses propres cases
                        if (joueur1 && (selectedPit < 0 || selectedPit > 5)) {
                            System.out.println("Choisissez une case entre 0 et 5.");
                            continue;
                        }
                        if (!joueur1 && (selectedPit < 6 || selectedPit > 11)) {
                            System.out.println("Choisissez une case entre 6 et 11.");
                            continue;
                        }

                        // Tentez le mouvement
                        Position nouvellePosition = gameLogic.makemovemulitjoueur(startingPosition, joueur1, move);
                        if (nouvellePosition != null) {
                            startingPosition = nouvellePosition;
                            mouvementValide = true; // Mouvement accepté
                        }
                    }

                    // Alternez le tour
                    joueur1 = !joueur1;
                    gameLogic.printPosition(startingPosition);
                    break;

                case 2: // Sauvegarder la partie
                    gameLogic.saveGame(new GameSave((MancalaPosition) startingPosition, joueur1));
                    System.out.println("Partie sauvegardée !");
                    break;

                case 3: // Charger une partie
                    GameSave loadedGame = gameLogic.loadGame();
                    if (loadedGame != null) {
                        startingPosition = loadedGame.position;
                        joueur1 = loadedGame.joueur1;
                        System.out.println("Partie chargée !");
                        gameLogic.printPosition(startingPosition); // Affiche l'état du board après chargement
                    } else {
                        System.out.println("Aucune partie sauvegardée trouvée !");
                    }
                    break;

                case 4: // Quitter

                    System.out.println("Quitter le jeu.");
                    exit(-1);
                    break;

                default:
                    System.out.println("Option invalide !");
                    break;
            }
        }
    }

    public void playGame(Position startingPosition, boolean humanPlayFirst) {
        if (humanPlayFirst == false ) {
            Vector v = alphaBeta(0, startingPosition, PROGRAM);
            startingPosition = (Position)v.elementAt(1);
        }
//        if (multijoueur) {
//            System.out.println("Mode multijoueur");
//            playMultiplayerGame(startingPosition);
//            return;
//        }
        while (true) {
            
            printPosition(startingPosition);
            if (wonPosition(startingPosition, PROGRAM)) {
                System.out.println("Program won");
                break;
            }
            if (wonPosition(startingPosition, HUMAN)) {
                System.out.println("Human won");
                break;
            }
            if (drawnPosition(startingPosition)) {
                System.out.println("Drawn game");
                break;
            }
            System.out.print("\nYour move:");
            Move move = createMove();
            startingPosition = makeMove(startingPosition, HUMAN, move);
            
            printPosition(startingPosition);
            
             if (wonPosition(startingPosition, HUMAN)) {
                System.out.println("Human won");
                break;
            }
             
            Vector v = alphaBeta(0, startingPosition, PROGRAM);
            
            Enumeration enum2 = v.elements();

                System.out.println(" ----------------------------------------- " );

                     
            startingPosition = (Position)v.elementAt(1);  
           if(startingPosition ==null){
               System.out.println("Drawn game");
               break;
           }
        }
    }
}
