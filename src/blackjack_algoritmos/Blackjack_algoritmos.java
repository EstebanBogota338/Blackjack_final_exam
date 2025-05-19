package blackjack_algoritmos;

import java.util.Scanner;

public class Blackjack_algoritmos {

    static Scanner sc = new Scanner(System.in);

    static String[] cards = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
    static int[] values = {11, 2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10};
    static String[] color = {"♠", "♥", "♦", "♣"};

    static final String RED = "\u001B[31m";
    static final String DARK_BLUE = "\u001B[34m";
    static final String RESET = "\u001B[0m";

    static int stack_player = 1000;
    static int[] chip_values = {1, 5, 10, 20, 50, 100};

    public static void main(String[] args) {
        String play_again;
        System.out.println("\u001B[32mBienvenido a Blackjack\u001B[0m");

        //Verifies if player still have money
        do {
            if (stack_player <= 0) {
                System.out.println("\u001B[31mNo le queda dinero :( Fin del juego\u001B[0m");
                break;
            }

            // Ask for bet
            int bet = ask_for_deal_with_chips(stack_player);

            //Cards and suite
            String[] player_cards = new String[10];
            //Cards value
            int[] player_value = new int[10];
            String[] dealer_cards = new String[10];
            int[] dealer_value = new int[10];
            
            player_cards[0] = "8♠";
            player_value[0] = 8;
            player_cards[1] = "8♦";
            player_value[1] = 8;
            int cards_player = 2;


           /* int cards_player = 0;
            //Adds card 1 & 2
            cards_player += shuffle_card(player_cards, player_value, cards_player);
            System.out.println();
            cards_player += shuffle_card(player_cards, player_value, cards_player);   */
            show_cards(player_cards, player_value, cards_player, "Jugador");

            //Asks for double bet
            boolean doblado = false;
            if (cards_player == 2 && stack_player >= bet * 2) {
                System.out.println("¿Desea doblar? (s/N)");
                String input = sc.nextLine().trim();
                if (input.equalsIgnoreCase("s")) {
                    play_double_hand(player_cards, player_value, dealer_cards, dealer_value, bet);
                    doblado = true;
                }
            }
            
            //Verifies split 
            if (!doblado && cards_player == 2 && get_card_rank(player_cards[0]).equals(get_card_rank(player_cards[1])) && stack_player >= bet * 2) {
                System.out.println("¿Desea hacer split? (s/N)");
                String input = sc.nextLine().trim();
                if (input.equalsIgnoreCase("s")) {
                    play_split_hands(player_cards, player_value, dealer_cards, dealer_value, bet);
                } else {
                    play_normal_hand(player_cards, player_value, dealer_cards, dealer_value, bet, cards_player);
                }
            } else if (!doblado) {
                play_normal_hand(player_cards, player_value, dealer_cards, dealer_value, bet, cards_player);
            }

                // Restart game
            do {
                System.out.println("¿Quieres jugar otra mano? (s/N)");
                play_again = sc.nextLine().trim();
                if (!play_again.equalsIgnoreCase("s") && !play_again.equalsIgnoreCase("n") && !play_again.isEmpty()) {
                    System.out.println("\u001B[31mPor favor, escriba 's' para sí o 'n' para no\u001B[0m");
                }
            } while (!play_again.equalsIgnoreCase("s") && !play_again.equalsIgnoreCase("n") && !play_again.isEmpty());

        } while (play_again.equalsIgnoreCase("s"));

        System.out.println("\u001B[36mTe vas con $" + stack_player + ", ¡gracias por jugar Blackjack! \u001B[0m");
        System.out.println("\u001B[36mGracias por jugar Blackjack, vuelva pronto!\u001B[0m");
    }

    public static void play_normal_hand(String[] player_cards, int[] player_value, String[] dealer_cards, int[] dealer_value, int bet, int cards_player) {
        stack_player -= bet;
        while (add_cards(player_cards, player_value, cards_player) < 21 && hit()) {
            cards_player += shuffle_card(player_cards, player_value, cards_player);
            show_cards(player_cards, player_value, cards_player, "Jugador");
        }

        int player_total = add_cards(player_cards, player_value, cards_player);

        if (player_total > 21) {
            System.out.println("\u001B[31mSe pasó de 21, perdió :(\u001B[0m");
        } else {
            int dealer_total = play_dealer_hand(dealer_cards, dealer_value);
            evaluate_result(player_total, dealer_total, bet);
        }

        System.out.println("Su dinero actual es: \u001B[36m$" + stack_player + RESET);
    }

    public static void play_double_hand(String[] player_cards, int[] player_value, String[] dealer_cards, int[] dealer_value, int bet) {
        bet *= 2;
        stack_player -= bet;
        
        int cards_player = 2;
        cards_player += shuffle_card(player_cards, player_value, cards_player);
        show_cards(player_cards, player_value, cards_player, "Jugador");

        int player_total = add_cards(player_cards, player_value, cards_player);

        if (player_total > 21) {
            System.out.println("\u001B[31mSe pasó de 21, perdió :(\u001B[0m");
        } else {
            int dealer_total = play_dealer_hand(dealer_cards, dealer_value);
            evaluate_result(player_total, dealer_total, bet);
        }

        System.out.println("Su dinero actual es: \u001B[36m$" + stack_player + RESET);
    }

    public static void play_split_hands(String[] player_cards, int[] player_value, String[] dealer_cards, int[] dealer_value, int bet) {
        stack_player -= bet * 2;

        //Creating an aux array
        String[] player_cards2 = new String[10];
        int[] player_value2 = new int[10];

        //Storing second hand's value and symbol
        player_cards2[0] = player_cards[1];
        player_value2[0] = player_value[1];

        //Removing second's hand value of our original array
        player_cards[1] = null;
        player_value[1] = 0;

        int hand1 = 1, hand2 = 1;
        hand1 += shuffle_card(player_cards, player_value, hand1);
        hand2 += shuffle_card(player_cards2, player_value2, hand2);

        show_cards(player_cards, player_value, hand1, "Jugador Mano 1");
        while (add_cards(player_cards, player_value, hand1) < 21 && hit()) {
            hand1 += shuffle_card(player_cards, player_value, hand1);
            show_cards(player_cards, player_value, hand1, "Jugador Mano 1");
        }

        show_cards(player_cards2, player_value2, hand2, "Jugador Mano 2");
        while (add_cards(player_cards2, player_value2, hand2) < 21 && hit()) {
            hand2 += shuffle_card(player_cards2, player_value2, hand2);
            show_cards(player_cards2, player_value2, hand2, "Jugador Mano 2");
        }

        int total1 = add_cards(player_cards, player_value, hand1);
        int total2 = add_cards(player_cards2, player_value2, hand2);

        int dealer_total = play_dealer_hand(dealer_cards, dealer_value);
        evaluate_result(total1, dealer_total, bet);
        evaluate_result(total2, dealer_total, bet);

        System.out.println("Su dinero actual es: \u001B[36m$" + stack_player + RESET);
    }

    public static int play_dealer_hand(String[] dealer_cards, int[] dealer_value) {
        int cards_dealer = 0;
        System.out.println("\u001B[32mCartas del dealer:\u001B[0m");

        cards_dealer += shuffle_card(dealer_cards, dealer_value, cards_dealer);
        System.out.println();
        cards_dealer += shuffle_card(dealer_cards, dealer_value, cards_dealer);

        while (add_cards(dealer_cards, dealer_value, cards_dealer) < 17) {
            cards_dealer += shuffle_card(dealer_cards, dealer_value, cards_dealer);
        }

        show_cards(dealer_cards, dealer_value, cards_dealer, "Dealer");
        return add_cards(dealer_cards, dealer_value, cards_dealer);
    }

    public static void evaluate_result(int player, int dealer, int bet) {
        System.out.println();
        if (player > 21) {
            System.out.println("\u001B[31mTe pasaste, pierdes esta mano\u001B[0m");
        } else if (dealer > 21 || player > dealer) {
            System.out.println("\u001B[32mGanaste esta mano!\u001B[0m");
            stack_player += bet * 2;
        } else if (player < dealer) {
            System.out.println("\u001B[31mPerdiste esta mano\u001B[0m");
        } else {
            System.out.println("\u001B[33mEmpate en esta mano\u001B[0m");
            stack_player += bet;
        }
    }

    public static int shuffle_card(String[] suit, int[] card_values, int index) {
        int card = (int)(Math.random() * cards.length);
        int color_s = ((int)(Math.random() * color.length));
        suit[index] = cards[card] + color[color_s];
        card_values[index] = values[card];
        return 1;
    }

    public static String get_card_rank(String card) {
        return card.substring(0, card.length() - 1);
    }

    public static int add_cards(String[] cards, int[] values, int quantity) {
        int add = 0;
        int ace_count = 0;
        for (int i = 0; i < quantity; i++) {
            add += values[i];
            if (cards[i] != null && cards[i].startsWith("A")) ace_count++;
        }
        while (add > 21 && ace_count > 0) {
            add -= 10;
            ace_count--;
        }
        return add;
    }

    public static boolean hit() {
        while (true) {
            System.out.println("¿Quiere otra carta? (s/N)");
            String answer = sc.nextLine();
            if (answer.equalsIgnoreCase("s")) {
                return true;
            } else if (answer.equalsIgnoreCase("n") || answer.isEmpty()) {
                return false;
            } else {
                System.out.println("\u001B[31mPor favor, ingrese 's' para sí o 'n' para no\u001B[0m");
            }
        }
    }

    public static void show_cards(String[] cards, int[] values, int quantity, String who) {
        System.out.println(who + " tiene :");
        for (int i = 0; i < quantity; i++) {
            char symbol = cards[i].charAt(cards[i].length() - 1);
            if (symbol == '♥' || symbol == '♦') {
                System.out.print("[" + RED + cards[i] + RESET + "] ");
            } else {
                System.out.print("[" + DARK_BLUE + cards[i] + RESET + "] ");
            }
        }
        System.out.println("total: \u001B[33m" + add_cards(cards, values, quantity) + RESET);
    }

    public static int ask_for_deal_with_chips(int max_stack) {
        int[] chip_count = new int[chip_values.length];
        int total_bet = 0;

        System.out.println("Tiene $" + max_stack + " arma tu apuesta con fichas");

        for (int i = chip_values.length - 1; i >= 0; i--) {
            while (true) {
                System.out.println("¿Cuantas fichas de $" + chip_values[i] + " quieres?");
                String input = sc.nextLine();
                try {
                    int quantity = Integer.parseInt(input);
                    if (quantity < 0) {
                        System.out.println("\u001B[31mNo puede ser negativa\u001B[0m");
                        continue;
                    }
                    int add_aux = total_bet + quantity * chip_values[i];
                    if (add_aux > max_stack) {
                        System.out.println("\u001B[31mNo tiene dinero suficiente para realizar esta apuesta\u001B[0m");
                        continue;
                    }
                    chip_count[i] = quantity;
                    total_bet = add_aux;
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("\u001B[31mIngrese un número: \n" + e + "\u001B[0m");
                }
            }
        }

        System.out.println("\u001B[33mResumen de la apuesta:");
        for (int i = chip_values.length - 1; i >= 0; i--) {
            if (chip_count[i] > 0) {
                System.out.println("- " + chip_count[i] + " fichas de $" + chip_values[i]);
            }
        }

        System.out.println("\u001B[33mApuesta total: $" + total_bet + "\u001B[0m");
        if (total_bet == 0) {
            System.out.println("\u001B[31mNo puede apostar cero. Intente nuevamente\u001B[0m");
            return ask_for_deal_with_chips(max_stack);
        }
        return total_bet;
    }
}
