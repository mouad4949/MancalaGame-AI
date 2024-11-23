package GameSearch;

import java.util.*;

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


    public void playMultiplayerGame(Position startingPosition) {
        boolean joueur1 = true; // Joueur 1 commence

        while (true) {
            printPosition(startingPosition);

            // Vérifiez si la partie est gagnée ou nulle
            if (wonPosition(startingPosition, true)) {
                System.out.println("Joueur 1 a gagné !");
                break;
            }
            if (wonPosition(startingPosition, false)) {
                System.out.println("Joueur 2 a gagné !");
                break;
            }
            if (drawnPosition(startingPosition)) {
                System.out.println("Match nul !");
                break;
            }

            boolean mouvementValide = false;
            while (!mouvementValide) {
                System.out.print((joueur1 ? "Joueur 1" : "Joueur 2") + ", choisissez votre case : ");
                Move move = createMove(); // Récupère le mouvement du joueur
                // Vérifiez que le joueur joue sur ses propres cases
                int selectedPit = ((MancalaMove) move).selectedPit;
                if (joueur1 && (selectedPit < 0 || selectedPit > 5)) {
                    System.out.println("Choisissez une case entre 0 et 5.");
                    continue;
                }
                if (!joueur1 && (selectedPit < 6 || selectedPit > 11)) {
                    System.out.println("Choisissez une case entre 6 et 11.");
                    continue;
                }
                Position nouvellePosition = makemovemulitjoueur(startingPosition, joueur1, move);
                if (nouvellePosition != null) {
                    startingPosition = nouvellePosition;
                    mouvementValide = true; // Sort de la boucle si le mouvement est valide
                }
            }


            // Alternez le tour
            joueur1 = !joueur1;
        }
    }



    public void playGame(Position startingPosition, boolean humanPlayFirst,boolean multijoueur) {
        if (humanPlayFirst == false && !multijoueur) {
            Vector v = alphaBeta(0, startingPosition, PROGRAM);
            startingPosition = (Position)v.elementAt(1);
        }
        if (multijoueur) {
            System.out.println("Mode multijoueur");
            playMultiplayerGame(startingPosition);
            return;
        }
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
