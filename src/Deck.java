import java.util.*;

public class Deck {
    //山札用の配列
    ArrayList<Card> cards = new ArrayList<>();
    // expression:読み方(ex.11=J,12=Q),number:数値,suit:記号
    ArrayList<String> expression = new ArrayList<>();
    ArrayList<ArrayList<Integer>> number = new ArrayList<ArrayList<Integer>>();
    ArrayList<String> suit = new ArrayList<>(){
        {
            add("ダイヤ");
            add("ハート");
            add("スペード");
            add("クローバー");
        }
    };

    public void initialize(){
        for (int i = 1; i <= 13; i++){
            if (i == 1) {
                String exp = "エース";
                expression.add(exp);
            }else if (i == 11) {
                String exp = "ジャック";
                expression.add(exp);
            } else if (i == 12) {
                String exp = "クイーン";
                expression.add(exp);
            } else if (i == 13) {
                String exp = "キング";
                expression.add(exp);
            } else {
                Integer k_str = Integer.valueOf(i);
                String exp = k_str.toString();
                expression.add(exp);
            }
        }

        // 13種のマークに対応した数字をリストに格納
        for (int i = 1; i <= 13; i++) {
            ArrayList<Integer> num = new ArrayList<>();
            // A（エース）の時は1と11の二択
            if (i == 1) {
                num.add(1);
                num.add(11);
            } else if (i >= 10) {
                num.add(10);
            } else {
                num.add(i);
            }
            number.add(num);
        }

        // 52(4*13)の二重ループ
        for (int i = 0; i < suit.size(); i ++) {
            // suitの取得
            String card_suit = suit.get(i);
            for (int j = 0; j < number.size(); j++) {
                // expressionの取得
                String card_expression = expression.get(j);
                // カードの数値の取得
                ArrayList<Integer> card_number = number.get(j);
                // cardsへの格納
                cards.add(new Card(card_expression, card_suit, card_number));
            }
        }
    }

    public ArrayList<Card> deal(int x){
        // Cardクラスのインスタンス
        ArrayList<Card> card_list = new ArrayList<>();
        // Randomクラスのインスタンスを作成
        Random rand = new Random();
        // ディールする分ループ
        for (int i = 0; i < x; i++){
            // 0から山札の枚数の値までのランダムなint値をrand_numに格納
            int rand_num = rand.nextInt(cards.size());
            // 山札のrand_num番目のカードをcardに格納
            Card card = cards.get(rand_num);
            // リストにcardを格納
            card_list.add(card);
            // 引くカードの重複を防ぐために、山札から引いたカードを削除
            cards.remove(rand_num);
        }
        return card_list;
    }
}