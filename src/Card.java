import java.util.*;

public class Card {

    // 変数の宣言
    String expression;
    String suit;
    ArrayList<Integer> number;

    // コンストラクタ（Cardクラスが呼ばれたときに自動的に実行される関数）。クラス名とコンストラクタ名は一致させる
    public Card(String expressions, String suits, ArrayList<Integer> numbers){

        // 各変数に引数として受け取った値を格納
        // これをすることでCardクラスのインスタンスが作成されたときに、表示する文字列とマークと数値が対応する
        expression = expressions;
        suit = suits;
        number = numbers;
    }

    // 画面に表示する文字列を返す関数
    public String get_expression(){
        return expression;
    }

    // マークを返す関数
    public String get_suit(){
        return suit;
    }

    // カードの数値を返す関数
    public ArrayList<Integer> get_number(){
        return number;
    }
}