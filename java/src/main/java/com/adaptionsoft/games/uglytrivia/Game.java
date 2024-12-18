package com.adaptionsoft.games.uglytrivia;

import java.util.ArrayList;
import java.util.LinkedList;

public class Game {
    ArrayList<String> players = new ArrayList<String>();

	public static final int NB_MIN_PLAYERS = 2;
	public static final int NB_MAX_PLAYERS = 6;
	public static final int NB_QUESTIONS = 50;
    int[] places = new int[NB_MAX_PLAYERS];
    int[] purses  = new int[NB_MAX_PLAYERS];
    boolean[] inPenaltyBox  = new boolean[6];


	// TODO faire une liste à 2 niveau avec la catégorie en 1er index pour que ça soit généralisable à n catégories... (pas le temps)
    LinkedList<String> popQuestions = new LinkedList<String>();
    LinkedList<String> scienceQuestions = new LinkedList<String>();
    LinkedList<String> sportsQuestions = new LinkedList<String>();
    LinkedList<String> rockQuestions = new LinkedList<String>();
    
    int currentPlayerIndex = 0;
    boolean isGettingOutOfPenaltyBox;
    
    public  Game(){
    	for (int i = 0; i < NB_QUESTIONS; i++) {
			popQuestions.addLast(createQuestion("Pop", i));
			scienceQuestions.addLast(createQuestion("Science", i));
			sportsQuestions.addLast(createQuestion("Sports", i));
			rockQuestions.addLast(createQuestion("Rock", i));
    	}
    }

	public String createQuestion(String category, int index){
		return category + " " + index;
	}

	/**
	 * verify a game has at least 2 players
	 * @return false if not enough players
	 */
	public boolean isPlayable() {
		return (howManyPlayers() >= NB_MIN_PLAYERS);
	}

	/**
	 * add a player to an existing game
	 * @param playerName
	 * @return boolean if the player has been added. false if the max number of players is already at max.
	 */
	public boolean addPlayer(String playerName) {

		int nbPlayers = howManyPlayers();
		if ( nbPlayers >= NB_MAX_PLAYERS) {
			return false;
		}
	    players.add(playerName);
	    places[nbPlayers] = 0;
	    purses[nbPlayers] = 0;
	    inPenaltyBox[nbPlayers] = false;
	    
	    System.out.println(playerName + " was added");
	    System.out.println("They are player number " + players.size());
		return true;
	}

	/**
	 *
	 * @param playerNumber  the index of the player
	 * @return true if the player is in penalty box
	 */
	public boolean playerIsInPenaltyBox(int playerNumber) {
		return inPenaltyBox[playerNumber];
	}

	/**
	 * moves the player in or out of penalty box
	 * @param playerNumber the index of the player
	 * @param insideBox if true, the players gets IN the box, else OUT
	 */
	public void setPlayerInPenaltyBox(int playerNumber, boolean insideBox) {
		inPenaltyBox[playerNumber] = insideBox;
	}

	/**
	 *
	 * @return the actual number of players
	 */
	public int howManyPlayers() {
		if (players != null) {
			return players.size();
		} else {
			throw new RuntimeException("the list of players has not been correctly initialized");
		}
	}

	public void roll(int roll) {
		System.out.println(players.get(currentPlayerIndex) + " is the current player");
		System.out.println("They have rolled a " + roll);
		
		if (playerIsInPenaltyBox(currentPlayerIndex)) {
			if (roll % 2 != 0) {
				setPlayerInPenaltyBox(currentPlayerIndex, false); // le joueur ne sortait jamais de la penalty box?
				System.out.println(players.get(currentPlayerIndex) + " is getting out of the penalty box");
				movePlayerAndAskQuestion(roll);
			} else {
				System.out.println(players.get(currentPlayerIndex) + " is not getting out of the penalty box");
			}
		} else {
			movePlayerAndAskQuestion(roll);
		}
		
	}

	private void movePlayerAndAskQuestion(int roll) {
		places[currentPlayerIndex] = places[currentPlayerIndex] + roll;
		if (places[currentPlayerIndex] > 11) {
			// prevents getting over the 12th place
			places[currentPlayerIndex] = places[currentPlayerIndex] - 12;
		}

		System.out.printf("%s's new location is %s%n", players.get(currentPlayerIndex), places[currentPlayerIndex]);
		System.out.println("The category is " + currentCategory());
		askQuestion();
	}

	private void askQuestion() {
		switch (currentCategory()) {
			case "Pop":
				System.out.println(popQuestions.removeFirst());
				break;
			case "Science":
				System.out.println(scienceQuestions.removeFirst());
				break;
			case "Sports":
				System.out.println(sportsQuestions.removeFirst());
				break;
			case "Rock":
				System.out.println(rockQuestions.removeFirst());
				break;
		}
	}
	
	
	private String currentCategory() {
		if (places[currentPlayerIndex] < 0 || places[currentPlayerIndex]> 11) {
			throw new RuntimeException("Le joueur est dans une position impossible");
		}
        return switch (places[currentPlayerIndex]) {
            case 0, 4, 8 -> "Pop";
            case 1, 5, 9 -> "Science";
            case 2, 6, 10 -> "Sports";
			case 3, 7, 11 -> "Rock";
            default -> "";
        };
	}

	public boolean wasCorrectlyAnswered() {
		if (inPenaltyBox[currentPlayerIndex]){
			if (isGettingOutOfPenaltyBox) {
				System.out.println("Answer was correct!!!!");
				currentPlayerIndex++;
				if (currentPlayerIndex == players.size()) currentPlayerIndex = 0;
				purses[currentPlayerIndex]++;
				System.out.println(players.get(currentPlayerIndex)
						+ " now has "
						+ purses[currentPlayerIndex]
						+ " Gold Coins.");

				boolean winner = didPlayerWin();

				return winner;
			} else {
				currentPlayerIndex++;
				if (currentPlayerIndex == players.size()) currentPlayerIndex = 0;
				return true;
			}
			
			
			
		} else {
		
			System.out.println("Answer was correct!!!!");
			purses[currentPlayerIndex]++;
			System.out.printf("%s now has %s Gold Coins.%n",
					players.get(currentPlayerIndex),
					purses[currentPlayerIndex]);
			
			boolean winner = didPlayerWin();
			currentPlayerIndex++;
			if (currentPlayerIndex == players.size()) currentPlayerIndex = 0;
			
			return winner;
		}
	}
	
	public boolean wrongAnswer(){
		System.out.println("Question was incorrectly answered");
		System.out.println(players.get(currentPlayerIndex)+ " was sent to the penalty box");
		setPlayerInPenaltyBox(currentPlayerIndex, true);
		
		currentPlayerIndex++;
		if (currentPlayerIndex == players.size()) currentPlayerIndex = 0;
		return true;
	}


	private boolean didPlayerWin() {
		return !(purses[currentPlayerIndex] == 6);
	}
}
