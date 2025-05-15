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
 
    static int stack_player = 10000;
    
    static int[] chip_values = {1, 5, 10, 20, 50, 100};    
    
    public static void main(String[] args) { 
        String play_again = "";
        int bet = 0;
        
        System.out.println("\u001B[32mBienvenido a Blackjack\u001B[0m"); 
       
        do {
            
            if (stack_player <= 0) {
                System.out.println("\u001B[31mNo le queda dinero :( Fin del juego\u001B[0m");
                break;
            }
            
            bet = ask_for_deal_with_chips(stack_player);      
            
             int cards_player = 0;
             int cards_dealer = 0;

             String [] player_cards = new String[10];
             int [] player_value = new int[10];
             String [] dealer_cards = new String[10];
             int [] dealer_value = new int[10];
             

            //  Shuffle 
            cards_player += shuffle_card(player_cards, player_value, cards_player);
            System.out.println();
            cards_player += shuffle_card(player_cards, player_value, cards_player);  
            show_cards(player_cards, player_value, cards_player, "Jugador");

            // Player's turn
            while(add_cards(player_cards, player_value, cards_player) < 21 && hit()){
                cards_player += shuffle_card(player_cards, player_value, cards_player);  
                show_cards(player_cards, player_value, cards_player, "Jugador");
            }

            int add_player = add_cards(player_cards, player_value, cards_player);

            if(add_player > 21){
                System.out.println("\u001B[31mSe pasó de 21, perdió :(\u001B[0m");
                stack_player -= bet;
                System.out.println("Su dinero actual es: \u001B[36m$" + stack_player + RESET);
                continue;
            }

            // Dealer's turn
            System.out.println("\u001B[32mCartas del dealer: \u001B[0m");
            cards_dealer += shuffle_card(dealer_cards, dealer_value, cards_dealer);
            System.out.println();
            cards_dealer += shuffle_card(dealer_cards, dealer_value, cards_dealer);

            while (add_cards(dealer_cards, dealer_value, cards_dealer) < 17){
                cards_dealer += shuffle_card(dealer_cards, dealer_value, cards_dealer); 
            }

            show_cards(dealer_cards, dealer_value, cards_dealer, "Dealer");
            int add_dealer = add_cards(dealer_cards, dealer_value, cards_dealer);

            // Winner
            System.out.println();
            if (add_dealer > 21 || add_player > add_dealer){
                System.out.println("\u001B[32mGanó!!!\u001B[0m");
                stack_player += bet;
            } else if (add_player < add_dealer) {
                System.out.println("\u001B[31mPerdió :(\u001B[0m");
                stack_player -= bet;
            } else {
                System.out.println("\u001B[33mEmpate\u001B[0m");
            }   
            
            System.out.println("Su dinero actual es: " + stack_player);

           do {
               System.out.println("¿Quieres jugar otra mano? (s/N)");
               play_again = sc.nextLine().trim();
               
               if(!play_again.equalsIgnoreCase("s") && ! play_again.equalsIgnoreCase("n") && !play_again.isEmpty()) {
                   System.out.println("\u001B[31mPor favor, escriba 's' para sí o 'n' para no\u001B[0m");
               }
           }while(!play_again.equalsIgnoreCase("s") && !play_again.equalsIgnoreCase("n") && !play_again.isEmpty());
            
        } while(play_again.equalsIgnoreCase("s"));
        System.out.println("\u001B[36mTe vas con $" + stack_player + ", ¡gracias por jugar Blackjack! \u001B[0m");
        System.out.println("\u001B[36mGracias por jugar Blackjack, vuelva pronto!\u001B[0m");
    }
    
    public static int ask_for_deal_with_chips(int max_stack) {
        int [] chip_count = new int [chip_values.length];
        int total_bet = 0;
        
        System.out.println("Tiene $" + max_stack + " arma tu apuesta con fichas");
        
        for (int i = chip_values.length - 1; i >= 0; i--){
            while(true) {
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
                }catch(NumberFormatException e) {
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
    
    public static int shuffle_card(String[] suit, int[] card_values, int index){
        int card = (int)(Math.random() * cards.length);     
        int color_s = ((int)(Math.random() * color.length));
        suit[index] = cards[card] + color[color_s];
        card_values[index] = values[card];
        return 1;
    }
    
    public static int add_cards(String[] cards, int[] values, int quantity) {
        int add = 0;
        int ace_count = 0;
        
        for(int i = 0; i < quantity; i++){
            add += values[i];
            if (cards[i].startsWith("A")) {  
                ace_count++;
            }
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
            }else if (answer.equalsIgnoreCase("n") || answer.isEmpty()){
                return false;
            }else {
                System.out.println("\u001B[31mPor favor, ingrese 's' para sí o 'n' para no\u001B[0m");
            }
        }         
    }
    
   public static void show_cards(String[] cards, int []values, int quantity, String who) {
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
}

        