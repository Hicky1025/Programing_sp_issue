import java.util.Scanner;

public class Casino {
    public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);
        Player player = new Player();  

        // betする金額の入力
        System.out.println("いくらベットしますか？");
        System.out.println("所持金 : " + player.money);
        String num = scan.nextLine();
        int bet = Integer.parseInt(num);

        // playerクラスのbet_money変数にbet金額を代入
        player.bet_money = bet;

        // １回目のBlackjackの開始
        BlackJack blackjack = new BlackJack();
        float dividend_rate = blackjack.game();
        // blackjack.rate_flagに倍率が格納されている
        // それをplayerクラスのresult関数に与えて配当を所持金に加算
        player.result(dividend_rate);

        while (true) {
            System.out.println("continue?(y or n)");
            String str = scan.nextLine();

            if (str.equals("y")){
                System.out.println("continue."); 
                System.out.println("いくらベットしますか？");
                System.out.println("所持金 : " + player.money);
                num = scan.nextLine();
                bet = Integer.parseInt(num);
                player.bet_money = bet;

                blackjack.game();
                dividend_rate = blackjack.game();
                player.result(dividend_rate);
            }
            else{
                System.out.println("exit.");
                break;
            }
        }
        scan.close();
    }
}