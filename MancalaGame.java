package GameSearch;

import java.io.*;
import java.util.*;

import static java.lang.System.exit;


public class MancalaGame extends GameSearch {
    private int maxDepth = 10; // Profondeur par défaut

    public void setDifficulty(String difficulty) {
        switch (difficulty.toLowerCase()) {
            case "amateur":
                this.maxDepth = 2;
                break;
            case "intermediate":
                this.maxDepth = 4;
                break;
            case "expert":
                this.maxDepth = 10;
                break;
            default:
                this.maxDepth = 10; // Valeur par défaut si non défini
        }
    }

    @Override
    public boolean reachedMaxDepth(Position p, int depth) {
        return depth >= maxDepth; // Utiliser la profondeur configurée
    }

    @Override
    public boolean drawnPosition(Position p) {
        MancalaPosition pos = (MancalaPosition) p;
        return Arrays.stream(pos.board).allMatch(pit -> pit == 0); // Toutes les cases vides
    }

    @Override
    public boolean wonPosition(Position p, boolean player) {
        MancalaPosition pos = (MancalaPosition) p;
        return player ? pos.playerStore1 > pos.playerStore2 : pos.playerStore2 > pos.playerStore1;
    }

    @Override
    public float positionEvaluation(Position p, boolean player) {
        MancalaPosition pos = (MancalaPosition) p;
        return player ? pos.playerStore1 - pos.playerStore2 : pos.playerStore2 - pos.playerStore1;
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

    public boolean isGameOver(MancalaPosition pos) {
        return isAllEmpty(pos.board, 0, 5) || isAllEmpty(pos.board, 6, 11);
    }


    public void finDePartieMul(MancalaPosition pos) {
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


    public Boolean finDePartie(Position position) {
        MancalaPosition pos = (MancalaPosition) position;
        // Si toutes les cases de joueur 1 sont vides, joueur 2 récupère toutes les graines restantes
        if (isAllEmpty(pos.board, 0, 5)) {
            pos.playerStore2 += sumRemainingSeeds(pos.board, 6, 11);
            clearRemainingSeeds(pos.board, 6, 11);
            if (wonPosition(pos, true)) {
                System.out.println("le premiere joueure est gangne ");
            } else if (wonPosition(pos, false))
                System.out.println("le deuxieme joueure est gagne ");
            else
                System.out.println("Partie null");
            return true;
        }

        // Si toutes les cases de joueur 2 sont vides, joueur 1 récupère toutes les graines restantes
        if (isAllEmpty(pos.board, 6, 11)) {
            pos.playerStore1 += sumRemainingSeeds(pos.board, 0, 5);
            clearRemainingSeeds(pos.board, 0, 5);
            if (wonPosition(pos, true)) {
                System.out.println("le premiere joueure est gangne ");
            } else if (wonPosition(pos, false))
                System.out.println("le deuxieme joueure est gagne ");
            else
                System.out.println("Partie null");
            return true;

        }
        if (isAllEmpty(pos.board, 0, 5) && isAllEmpty(pos.board, 6, 11)) {
            // Vérification de la victoire ou du match nul
            if (pos.playerStore1 > pos.playerStore2) {
                System.out.println("Joueur 1 a gagné !");
            } else if (pos.playerStore1 < pos.playerStore2) {
                System.out.println("Joueur 2 a gagné !");
            } else {
                System.out.println("Match nul !");
            }
        }

        return false;
    }

    // Utilitaires pour la gestion des graines restantes
    private boolean isAllEmpty(int[] board, int start, int end) {
        for (int i = start; i <= end; i++) {
            if (board[i] != 0)
                return false;
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
        for (int i = start; i <= end; i++) {
            if (pos.board[i] > 0) { // Si la case contient des graines
                MancalaPosition newPos = pos.copy();
                makeMove(newPos, player, new MancalaMove(i), true);
                moves.add(newPos);
            }
        }
        return moves.toArray(new Position[0]);
    }

    @Override
    public Position makeMove(Position p, boolean player, Move move, boolean isSimulation) {
        MancalaPosition pos = (MancalaPosition) p;
        MancalaMove mancalaMove = (MancalaMove) move;
        int selectedPit = mancalaMove.selectedPit;
        int seeds = pos.board[selectedPit];
        pos.board[selectedPit] = 0;
        int index = selectedPit;
        while (seeds > 0) {
            index = (index + 1) % 12; // Incrémenter l'index, en bouclant dans [0, 11]
            // possez des seeds dans le mancala
            if ((index == 6 && player) || (index == 0 && !player)) {
                if (player) {
                    pos.playerStore1++;
                    seeds--;
                    if (seeds == 0 && !isSimulation) {
                        System.out.println("your Next move ");
                        printPosition(pos);
                        Move newMove = createMove(player, pos);
                        makeMove(pos, true, newMove, false);
                    }
                } else {
                    pos.playerStore2++;
                    seeds--;
                    if (seeds == 0) {
                        pos.extraTurn = true;
                    }
                }
                if (seeds != 0) {
                    pos.board[index]++;
                }
                seeds--;
                continue; // Passez à l'élément suivant
            }
            pos.board[index]++;
            seeds--;
        }
        if (pos.board[index] == 1 && isPlayerSide(index, player)) {
            if (player) {
                int oppositeIndex = index + 6;
                if (pos.board[oppositeIndex] > 0) {
                    pos.playerStore1 += pos.board[oppositeIndex] + 1;
                    pos.board[oppositeIndex] = 0; // Vider la case opposée
                    pos.board[index] = 0;
                }
            } else {
                int oppositeIndex = index - 6;
                if (pos.board[oppositeIndex] > 0) {
                    pos.playerStore2 += pos.board[oppositeIndex] + 1;
                    pos.board[oppositeIndex] = 0; // Vider la case opposée
                    pos.board[index] = 0;
                }
            }
        }
        return pos;
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
            finDePartieMul(pos);
            return null; // Signaler que le jeu est terminé
        }


        // Retournez la position mise à jour
        return pos;
    }

    private boolean isPlayerSide(int index, boolean player) {
        if (player) {
            return index >= 0 && index <= 5; // Cases [0-5] pour le joueur
        } else {
            return index >= 6 && index <= 11; // Cases [7-11] pour le computer
        }
    }

    private int getBestPitFromHint(MancalaPosition currentPosition, MancalaPosition hintPosition, boolean player) {
        for (int i = 0; i < currentPosition.board.length; i++) {
            if (currentPosition.board[i] != hintPosition.board[i]) {
                if (player && i >= 0 && i <= 5) return i; // Pits du joueur
                if (!player && i >= 6 && i <= 11) return i; // Pits de l'adversaire
            }
        }
        return -1; // Aucun mouvement trouvé (erreur)
    }
    @Override
    public Move createMove(boolean player, Position pos) {
        MancalaPosition mancalaPosition = (MancalaPosition) pos;
        Scanner scanner = new Scanner(System.in);
        int pit = -1; // Initialisation avec une valeur invalide
        while (true) {
            try {
                System.out.print("Enter the pit number (0-5 for Player 1, 6-11 for Player 2):\n");
                System.out.println("Or enter 13 to get a hint for the best move.");

                pit = Integer.parseInt(scanner.nextLine()); // Lire l'entrée comme chaîne et convertir en entier

                if (pit == 13) {
                    // Générer un hint pour l'utilisateur
                    Vector hintVector = alphaBeta(0, pos, player); // Utilise la méthode alphaBeta pour calculer le meilleur coup
                    if (hintVector.size() > 1 && hintVector.elementAt(1) instanceof Position) {
                        MancalaPosition hintPosition = (MancalaPosition) hintVector.elementAt(1);
                        int bestPit = getBestPitFromHint(mancalaPosition, hintPosition, player);
                        System.out.println("Hint: The best move is pit " + bestPit);
                    } else {
                        System.out.println("No valid moves available for a hint.");
                    }
                    continue; // Redemander l'entrée après avoir donné un hint
                }

                // Vérifier si le numéro de case est dans les limites autorisées
                if ((player && (pit >= 0 && pit <= 5)) || (!player && (pit >= 6 && pit <= 11))) {
                    if (mancalaPosition.board[pit] == 0) {
                        System.out.println("The chosen pit is empty. Please choose a different one.");
                    } else {
                        break; // Entrée valide
                    }
                } else {
                    System.out.println("Invalid input. Please enter a number between 0-5 for Player 1 or 6-11 for Player 2.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }
        return new MancalaMove(pit);
    }


    public static void main(String[] args) {
        MancalaPosition startingPosition = new MancalaPosition();
        MancalaGame game = new MancalaGame();
        Scanner scanner = new Scanner(System.in);

        boolean playerStarts = false; // Définit qui commence dans le mode contre l'ordinateur
        String difficulty = "intermediate"; // Niveau de difficulté par défaut

        while (true) {
            System.out.println("Entrez 1 pour jouer en mode multijoueur, ou 0 pour jouer contre l'ordinateur :");
            int choice = scanner.nextInt();

            if (choice == 1) {
                // Lancer le mode multijoueur
                game.playMultiplayerGame(startingPosition, true);
                break; // Quitter la boucle principale après avoir démarré le jeu
            } else if (choice == 0) {
                // Configuration pour le mode contre l'ordinateur
                System.out.println("Entrez 1 pour que le joueur commence, ou 0 pour que l'ordinateur commence :");
                int choix = scanner.nextInt();

                if (choix == 1) {
                    playerStarts = true;
                } else if (choix == 0) {
                    playerStarts = false;
                } else {
                    System.out.println("Entrée invalide. Veuillez entrer 1 ou 0.");
                    continue; // Redemander une entrée valide
                }

                // Choix du niveau de difficulté
                while (true) {
                    System.out.println("Sélectionnez la complexité de l'ordinateur :");
                    System.out.println("1 - Amateur");
                    System.out.println("2 - Intermédiaire");
                    System.out.println("3 - Expert");
                    int level = scanner.nextInt();

                    if (level == 1) {
                        difficulty = "amateur";
                        break; // Sortir de la boucle de sélection de difficulté
                    } else if (level == 2) {
                        difficulty = "intermediate";
                        break; // Sortir de la boucle de sélection de difficulté
                    } else if (level == 3) {
                        difficulty = "expert";
                        break; // Sortir de la boucle de sélection de difficulté
                    } else {
                        System.out.println("Entrée invalide. Veuillez entrer 1, 2 ou 3.");
                    }
                }

                // Configurer la difficulté et lancer le jeu contre l'ordinateur
                game.setDifficulty(difficulty);
                game.playGame(startingPosition, playerStarts);
                break; // Quitter la boucle principale après avoir démarré le jeu
            } else {
                System.out.println("Entrée invalide. Veuillez entrer 1 ou 0.");
            }
        }

        scanner.close(); // Libérer les ressources du scanner après usage
    }

}


