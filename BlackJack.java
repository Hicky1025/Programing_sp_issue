import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class BlackJack {
    public static void main(String[] args) {

        // Deckクラスのインスタンスを作成
        Deck deck = new Deck();

        // Playareaクラスのインスタンスを要素として持つリスト
        ArrayList<Playarea> playarea_list = new ArrayList<>();

        int CPU_NUM = 4;

        // 各プレイヤーの手札の合計値を格納する変数
        int dealer_sum = 0;
        int player_sum = 0;
        int[] cpu_sum = new int[CPU_NUM];
        

        // バーストしたか否かのフラグ
        boolean burst_flag = false;
        boolean dealer_burst_flag = false;
        boolean[] cpu_burst_flag = new boolean[CPU_NUM];

        int split_count = 0;

        // 初期化（山札を作る）
        deck.initialize();

        // Dealer用とPlayer用のPlayareaを2つ作成して、playarea_listに格納。リストの０番目をdealer、1番目をPlayerとして扱う
        // 各手札に2枚づつ格納・表示
        for (int p = 0; p < 2+CPU_NUM; p++) {
            //0:player 1:dealer 2~5:CPU 6~:player(split)
            playarea_list.add(new Playarea());

            // deal_card_list : Cardクラスのインスタンスを要素として持つリスト
            // ここに最初に配る2枚のカードを格納する
            ArrayList<Card> deal_card_list = deck.deal(2);

            // p番目のプレイエリア
            Playarea playarea = playarea_list.get(p);

            // p番目のプレイエリアのカードリスト（手札）にディールした2枚を格納
            for (int m = 0; m < deal_card_list.size(); m++){
                Card init_card = deal_card_list.get(m);
                playarea.card_list.add(init_card);
            }

            // 初期手札の表示
            if (p == 0) {
                System.out.println("Dealerの手札:");

                // 手札の表示
                playarea.show();

                // 手札の合計の計算
                dealer_sum = playarea.sum();

                // 合計値の表示
                System.out.println("合計値:" + dealer_sum);
                System.out.println("");
            } else if (p == 1){
                System.out.println("Player1の手札:");
                playarea.show();
                player_sum = playarea.sum();
                System.out.println("合計値:" + player_sum);
                System.out.println("");
            } else {
                System.out.println("CPU"+ (p-2) +"の手札:");
                playarea.show();
                cpu_sum[p-2] = playarea.sum();
                System.out.println("合計値:" + cpu_sum[p-2]);
                System.out.println("");
            }
        }


        // ゲームの処理
        while (true) {

            //playerの処理
            //playerがバーストしていても処理を継続するため変更
            while(true){

                // PlayerのPlayareaを取得する
                Playarea hit_playarea = playarea_list.get(1);   

                // hitかstandかを選択
                // commandで「commandをcloseしろ」と警告が出てるが、閉じると2回目以降の標準入力ができなくなるから無視(https://kokishi-computing.com/web/2021/04/28/java-scanner-close/)
                
                Scanner command = new Scanner(System.in);
                if(hit_playarea.cansplit_check() == true){
                    System.out.println("hitかstandかsplitを入力してください"+ hit_playarea.sum());
                }else{
                    System.out.println("hitかstandを入力してください"+ hit_playarea.sum());
                }
                String command_res = command.nextLine();
                
             
                
                // hitした時の処理
                // 注意 ： javaは変数ごとに同じ文字列でも異なるメモリを確保するため、文字列が一致しているかではなく、文字列を格納している変数のメモリの場所を比較するため比較演算子"=="で文字列一致ができない。
                if (command_res.equals("hit")) {


                    // deal関数でカードを1枚もらう : [[card1]]みたいな形で値が返ってくる。card1はCardクラスのインスタンス
                    ArrayList<Card> deal_hit_card = deck.deal(1);

                    // [[card1]]の0番目を取得する : [card1]がhit_cardに格納される
                    Card hit_card = deal_hit_card.get(0);

                    // プレイヤーのPlayareaの手札に引いたカードを格納する
                    hit_playarea.card_list.add(hit_card);

                    // 手札の合計値を計算・表示
                    int sum = hit_playarea.sum();
                    System.out.println("Player1の手札:");
                    hit_playarea.show();
                    System.out.println("合計値:" + sum);
                    System.out.println("");

                    // プレイヤーがバーストしていないかの判定
                    player_sum = hit_playarea.sum();
                    burst_flag = hit_playarea.is_burst();
                    // バースト確認
                    if (burst_flag == true) {
                        break;
                    }
                } else if (command_res.equals("stand")) {
                // プレイヤーが　standした時の処理
                    player_sum = hit_playarea.sum();
                    break;
                } else if (command_res.equals("split") && hit_playarea.cansplit_check() == true) {
                // プレイヤーが　splitした時の処理
                        split_count++;

                        player_sum = hit_playarea.sum();
                        break;
                }

            }
            //cpuのターン
            for(int i = 0;i < CPU_NUM; i++){
                Playarea CPU = playarea_list.get(i+2);
                while(true){
                    if (CPU.think().equals("hit")) {
                        ArrayList<Card> dealer_hit = deck.deal(1);
                        Card dealer_hit_card = dealer_hit.get(0);
                        CPU.card_list.add(dealer_hit_card);
                    } else {
                        System.out.println("CPU"+i+"の手札:");
                        CPU.show();
                        System.out.println("合計値:" + CPU.sum());

                        break;
                    }

                    
                    // ディーラーがこのタイミングでバーストしたらプレイヤーの勝ち
                    if (cpu_burst_flag[i] == true) {

                    }
                    // ディーラーがバーストしているかの判定
                cpu_sum[i] = CPU.sum();
                cpu_burst_flag[i] = CPU.is_burst();
                }
            }
            
            
            // ディーラーのターン

            // DealerのPlayareaの作成
            Playarea dealer = playarea_list.get(0);

            // Dealerのhit処理とstand処理の分岐
            while (true){
                if (dealer.think().equals("hit")) {
                    ArrayList<Card> dealer_hit = deck.deal(1);
                    Card dealer_hit_card = dealer_hit.get(0);
                    dealer.card_list.add(dealer_hit_card);
                } else {
                    System.out.println("Dealerの手札:");
                    dealer.show();
                    System.out.println("合計値:" + dealer.sum());

                    break;
                }
            }
            // ディーラーがバーストしているかの判定
            dealer_sum = dealer.sum();
            dealer_burst_flag = dealer.is_burst();

            //プレイヤーとの勝敗判定
            judge(dealer_sum,dealer_burst_flag, player_sum, burst_flag);
            for(int i = 0; i<4; i++){
                judge_cpu(dealer_sum,dealer_burst_flag, cpu_sum[i], cpu_burst_flag[i], i);
            }
            break;
        
        }
    }
    

    // プレイヤーもディーラーもバーストしなかった時に呼ばれる
    public static void judge(int d_sum, boolean d_flag, int p_sum, boolean p_flag){
        
        if(p_flag == true){
            //プレイヤーがバーストしてたらプレイヤーの負け
            System.out.println("You BLose");
        }else if(d_flag == true){
            //ディーラーがバーストしてたらプレイヤーの勝ち
            System.out.println("You BWin");
        }else if (d_sum < p_sum) {
            // ディーラーの合計値よりプレイヤーの方が高かったらプレイヤーの勝ち
            System.out.println("You Win");
        } else if (d_sum > p_sum) {
            // ディーラーの合計値がプレイヤーよりも高かったらプレイヤーの負け
            System.out.println("You Lose");
        } else {
            //　引き分け 
            System.out.println("Draw");
        }
        System.out.println( d_sum + " - " + p_sum);
    }
        // プレイヤーもディーラーもバーストしなかった時に呼ばれる
        public static void judge_cpu(int d_sum, boolean d_flag, int p_sum, boolean p_flag, int cpu_num){
            
            if(p_flag == true){
                //プレイヤーがバーストしてたらプレイヤーの負け
                System.out.println("CPU"+cpu_num+" BLose");
            }else if(d_flag == true){
                //ディーラーがバーストしてたらプレイヤーの勝ち
                System.out.println("CPU"+cpu_num+" BWin");
            }else 
            // ディーラーの合計値よりプレイヤーの方が高かったらプレイヤーの勝ち
            if (d_sum < p_sum) {
                System.out.println("CPU"+cpu_num+" Win");
            
            // ディーラーの合計値がプレイヤーよりも高かったらプレイヤーの負け
            } else if (d_sum > p_sum) {
                System.out.println("CPU"+cpu_num+" Lose");
            
            //　引き分け 
            } else {
                System.out.println("CPU"+cpu_num+" Draw");
            }
            System.out.println( d_sum + " - " + p_sum);
        }
}

class Deck {
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

class Card {

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

class Playarea {

    // 宣言部
    public boolean boolflag;
    ArrayList<Card> card_list = new ArrayList<>();
    Card tmp;
    Card show_tmp;
    String show_expression;
    String show_suit;
    String show_hoge;
    ArrayList<Integer> card_num;
    int max_value = 0;

    // 手札の合計を返す関数
    public Integer sum(){

        int num1 = 0;
        int num2 = 0;
        
        // 手札の枚数分繰り返す
        for (int o = 0; o < card_list.size(); o++){

            // 手札のo番目のカードを取得
            tmp = card_list.get(o);

            // o番目のカードの数値を取得
            card_num = tmp.get_number();

            // 数値のサイズが１より大きいときTRUE
            // 数値は[1,11]とか[2]のようにそれぞれリストで格納されている。このif文では、カードがAだったときにTRUEになる
            if (card_num.size() > 1){

                // 以前に一度もAを引いてないときにTRUE
                if (num1 == num2) {
                    num1 += card_num.get(0);
                    num2 += card_num.get(1);
                
                //　以前にAを引いたことがある場合の処理
                // 11を2回選ぶとバーストするので2回選ばないようにする
                } else {
                    num1 += card_num.get(0);
                    num2 += card_num.get(0);
                }
            
            // Aを引かなかったときの処理
            } else {
                num1 += card_num.get(0);
                num2 += card_num.get(0);
            }
        }

        // num2がnum1より大きくて、２１以下だったときTRUE
        if (num2 > num1 && num2 <= 21) {
            max_value = num2;

        // それ以外の時の処理
        } else {
            max_value = num1;
        }

        return max_value;
    }

    // バーストしたか否かをbool値で返す関数。バーストしてたらTRUE
    public boolean is_burst() {

        if (max_value > 21) {
            boolflag = true;
        } else {
            boolflag = false;
        }

        return boolflag;
    }

    // 手札を表示する関数
    public void show(){

        // 手札の枚数分繰り返す
        for (int q = 0; q < card_list.size(); q++){

            // 手札のp番目の値を取得
            show_tmp = card_list.get(q);

            // p番目の表示する文字列を取得
            show_expression = show_tmp.get_expression();
            show_suit = show_tmp.get_suit();

            show_hoge = show_suit +  "：" + show_expression;
            // 文字列を表示
            System.out.println(show_hoge);
        }
    }

    public String think(){

        while (true) {
            if (sum() <= 17) {

                return "hit";
            } else {

                return "stand";
            }
        }
    }

    public ArrayList<Card> get_card_list(){
        return card_list;
    }

    public Boolean cansplit_check(){
        int A, B;
        A = this.get_card_list().get(0).get_number().get(0);
        //System.out.println(A);
        B = this.get_card_list().get(1).get_number().get(0);
        //System.out.println(B);
        if(A == B){
            return true;
        }
        return false;
    }
    public ArrayList<Card> split(){
        A = this.get_card_list().get(0);
        B = this.get_card_list().get(1);
        System.out.println(A);
        System.out.println(B);
        return card_list;
    }
}

/*
- playarea_listの要素数増やす
- ディーラーの動きを関数にしてCPUに流用
- 勝敗判定をディーラー対各プレイヤーにする
*/