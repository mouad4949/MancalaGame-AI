package GameSearch;

import java.io.*;
import java.util.*;

import static java.lang.System.exit;


public class MancalaGame extends GameSearch {

    @Override
    public boolean drawnPosition(Position p) {
        MancalaPosition pos = (MancalaPosition) p;
        return Arrays.stream(pos.board).allMatch(pit -> pit == 0); // Toutes les cases vides
    }

    @Override
    public boolean wonPosition(Position p, boolean player) {
        MancalaPosition pos = (MancalaPosition) p;
        if (drawnPosition(p)) {
            return player ? pos.playerStore1 > pos.playerStore2 : pos.playerStore2 > pos.playerStore1;
        }
        return false;
    }

    @Override
    public float positionEvaluation(Position p, boolean player) {
        MancalaPosition pos = (MancalaPosition) p;
        return player ? pos.playerStore1 - pos.playerStore2 : pos.playerStore2 - pos.playerStore1;
    }

    @Override
    public void printPosition(Position p) {
        MancalaPosition pos = (MancalaPosition) p;

        System.out.println("Joueur 1 (Mancala): " + pos.playerStore1);

        // Première ligne : indices 0 à 5
        System.out.print("P1: ");
        for (int i = 0; i <= 5; i++) {
            System.out.print(pos.board[i] + " ");
        }
        System.out.println(); // Saut de ligne

        // Deuxième ligne : indices 6 à 11
        System.out.print("P2: ");
        for (int i = 6; i <= 11; i++) {
            System.out.print(pos.board[i] + " ");
        }
        System.out.println(); // Saut de ligne

        System.out.println("Joueur 2 (Mancala): " + pos.playerStore2);
    }


    @Override
    public Position[] possibleMoves(Position p, boolean player) {
        MancalaPosition pos = (MancalaPosition) p;
        Vector<Position> moves = new Vector<>();
        int start = player ? 0 : 6;
        int end = player ? 5 : 11;

        for (int i = start; i <= end; i++){
            if (pos.board[i] > 0) { // Si la case contient des graines
                MancalaPosition newPos = pos.copy();
                makeMove(newPos, player, new MancalaMove(i));
                moves.add(newPos);
            }
        }
        return moves.toArray(new Position[0]);
    }


    @Override
    public Position makemovemulitjoueur(Position p, boolean joueur1, Move move) {
        MancalaGame game = new MancalaGame();
        MancalaPosition pos = (MancalaPosition) p;
        MancalaMove mancalaMove = (MancalaMove) move;

        int selectedPit = mancalaMove.selectedPit;
        int seeds = pos.board[selectedPit];

        // Vérifiez que le joueur ne choisit pas une case vide
        if (seeds == 0) {
            System.out.println("Case vide ! Choisissez une autre case !");
            return null; // Indique qu'aucun mouvement valide n'a été effectué
        }

        // Vérifiez que le joueur joue dans sa zone
        if (joueur1 && (selectedPit < 0 || selectedPit > 5)) {
            System.out.println("Joueur 1 : choisissez une case entre 0 et 5 !");
            return pos;
        }
        if (!joueur1 && (selectedPit < 6 || selectedPit > 11)) {
            System.out.println("Joueur 2 : choisissez une case entre 6 et 11 !");
            return pos;
        }

        // Videz la case sélectionnée
        pos.board[selectedPit] = 0;
        int index = selectedPit;

        // Distribuer les graines
        while (seeds > 0) {
            index = (index + 1) % 12; // Boucle sur le plateau

            // Ajoutez une graine dans la case actuelle sauf si c'est un Mancala adverse
            if (joueur1 && index == 6) {
                // Joueur 1 traverse vers la zone adverse
                pos.playerStore1++; // Incrément du score
                seeds--; // Décrémente les graines
                if(seeds==0 &&  !isAllEmpty(pos.board,0,5)){
                    System.out.println("Autre chance !");
                    game.playMultiplayerGame(pos, joueur1);
                    return makemovemulitjoueur(p,false,null);
                }
                pos.board[index]++;
                seeds--;
            } else if (!joueur1 && index == 0) {
                // Joueur 2 traverse vers la zone adverse
                pos.playerStore2++; // Incrément du score
                seeds--;
                if(seeds==0 && !isAllEmpty(pos.board,6,11)){
                    System.out.println("Autre chance !");
                    game.playMultiplayerGame(pos, joueur1);
                    return makemovemulitjoueur(p,false,null);
                }
                pos.board[index]++;
                seeds--;
                // Décrémente les graines
            } else {
                // Ajouter une graine dans les autres cases
                pos.board[index]++;
                seeds--;
            }


            if(joueur1 && index < 6 && seeds ==0 && pos.board[index]==1 && pos.board[index+6]!=0){
                int collect=pos.board[index+6];
                int collect1=pos.board[index];
                pos.board[index]=0;
                pos.board[index+6]=0;
                pos.playerStore1+=collect+collect1;

            }
            if(!joueur1 && index>=6 && seeds ==0 && pos.board[index]==1 && pos.board[index-6]!=0){
                int collect=pos.board[index-6];
                int collect1=pos.board[index];
                pos.board[index]=0;
                pos.board[index-6]=0;
                pos.playerStore2+=collect+collect1;

            }
        }
        // Retournez la position mise à jour
        // Vérification de la fin de partie
        if (isGameOver(pos)) {
            finDePartie(pos);
            return null; // Signaler que le jeu est terminé
        }


        // Retournez la position mise à jour
        return pos;
    }


    public boolean isGameOver(MancalaPosition pos) {
        return isAllEmpty(pos.board, 0, 5) || isAllEmpty(pos.board, 6, 11);
    }

    public void finDePartie(MancalaPosition pos) {
        // Déterminez le joueur qui peut récupérer les graines restantes
        if (isAllEmpty(pos.board, 0, 5)) {
            pos.playerStore2 += sumRemainingSeeds(pos.board, 6, 11);
            clearRemainingSeeds(pos.board, 6, 11);
        } else if (isAllEmpty(pos.board, 6, 11)) {
            pos.playerStore1 += sumRemainingSeeds(pos.board, 0, 5);
            clearRemainingSeeds(pos.board, 0, 5);
        }

        // Affichez les scores finaux
        System.out.println("Fin de partie !");
        System.out.println("Score Joueur 1 : " + pos.playerStore1 + " | Score Joueur 2 : " + pos.playerStore2);

        // Affichez le gagnant
        if (pos.playerStore1 > pos.playerStore2) {
            System.out.println("Joueur 1 a gagné !");
            exit(-1);
        } else if (pos.playerStore1 < pos.playerStore2) {
            System.out.println("Joueur 2 a gagné !");
            exit(-1);
        } else {
            System.out.println("Match nul !");
            exit(-1);
        }
    }

    private boolean isAllEmpty(int[] board, int start, int end) {
        for (int i = start; i <= end; i++) {
            if (board[i] > 0) {
                return false;
            }
        }
        return true;
    }

    private int sumRemainingSeeds(int[] board, int start, int end) {
        int sum = 0;
        for (int i = start; i <= end; i++) {
            sum += board[i];
        }
        return sum;
    }

    private void clearRemainingSeeds(int[] board, int start, int end) {
        for (int i = start; i <= end; i++) {
            board[i] = 0;
        }
    }

    public void afficherBoard(Position pos) {
        MancalaPosition mancalaPos = (MancalaPosition) pos;
        System.out.println("Joueur 1 (Mancala): " + mancalaPos.playerStore1);
        System.out.println("P1: " + Arrays.toString(Arrays.copyOfRange(mancalaPos.board, 0, 6)));
        System.out.println("P2: " + Arrays.toString(Arrays.copyOfRange(mancalaPos.board, 6, 12)));
        System.out.println("Joueur 2 (Mancala): " + mancalaPos.playerStore2);
    }
    public void saveGame(GameSave gameSave) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("mancala_save.dat"))) {
            oos.writeObject(gameSave);
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde de la partie : " + e.getMessage());
        }
    }

    public GameSave loadGame() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("mancala_save.dat"))) {
            return (GameSave) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erreur lors du chargement de la partie : " + e.getMessage());
            return null;
        }
    }



    @Override
    public Position makeMove(Position p, boolean player, Move move) {
        MancalaPosition pos = (MancalaPosition) p;
        MancalaMove mancalaMove = (MancalaMove) move;
        int selectedPit = mancalaMove.selectedPit;
        int seeds = pos.board[selectedPit];
        pos.board[selectedPit] = 0;
        int index = selectedPit;
        while (seeds > 0) {
            index = (index + 1) % 12; // Incrémenter l'index, en bouclant dans [0, 11]
            // Si c'est le Mancala du joueur actuel
            if ((index == 6 && player) || (index == 0 && !player)) {
                if (player){
                    pos.playerStore1++;
                }else{
                    pos.playerStore2++;
                }
                seeds--;
                if(seeds!=0) {
                    pos.board[index]++;
                }
                seeds--;
                continue; // Passez à l'élément suivant
            }
            pos.board[index]++;
            seeds--;
        }
        // Retournez la position mise à jour
        return pos;
    }

    @Override
    public boolean reachedMaxDepth(Position p, int depth) {
        return depth >= 10; // Profondeur maximale pour l'algorithme
    }

    @Override
    public Move createMove() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the pit number (0-5 for Player 1, 6-11 for Player 2): ");
        int pit = scanner.nextInt();
        return new MancalaMove(pit);
    }

    public static void main(String[] args) {
        MancalaPosition startingPosition = new MancalaPosition();
        MancalaGame game = new MancalaGame();
        Scanner scanner = new Scanner(System.in);

        boolean playerStarts = false;
        boolean multijoueur =false;

            while (true) {
                System.out.println("Entrez 1 pour jouer en mode multijoueur, ou 0 pour jouer avec ordinateur :");
                int choice = scanner.nextInt();
                if (choice == 1) {
                    game.playMultiplayerGame(startingPosition,true);
                    break;
                }else if (choice == 0) {
                    System.out.println("Entrez 1 pour que le joueur commence, ou 0 pour que l'ordinateur commence :");
                    int choix = scanner.nextInt();
                    if (choix == 1) {
                        playerStarts = true;
                        break;
                    } else if (choix == 0) {
                        playerStarts = false;
                        break;
                    } else {
                        System.out.println("Entrée invalide. Veuillez entrer 1 ou 0.");
                    }
                }
               break;
            }
            game.playGame(startingPosition, playerStarts); // Le joueur humain ou l'ordinateur commence selon le choix

    }

}