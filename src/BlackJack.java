import java.util.*;

public class BlackJack {
    public float game() {

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

        ///split関連
        int split_count = 0;
        ArrayList<Playarea> split_list = new ArrayList<>();
        boolean playing = false;
        boolean[] split_stand = new boolean[10];
        boolean[] split_burst = new boolean[10];
        int[] split_sum = new int[10];

        // 金額の倍率
        float rate = 0F;

        for(int i = 0; i < 10; i++){
            split_stand[i] = false;
            split_burst[i] = false;
            split_sum[i] = 0;
        }
        
        // 初期化（山札を作る）
        deck.initialize();

        // Dealer用とPlayer用のPlayareaを2つ作成して、playarea_listに格納。リストの０番目をdealer、1番目をPlayerとして扱う
        // 各手札に2枚づつ格納・表示
        for (int p = 0; p < 2+CPU_NUM; p++) {
            //0:dealer 1:player 2~5:CPU 6~:player(split)
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
                if (player_sum == 21) {
                    System.out.println("BLACKJACK!!");
                    rate = rate + 2.5F;
                    return rate;
                }
            } else {
                System.out.println("CPU"+ (p-2) +"の手札:");
                playarea.show();
                cpu_sum[p-2] = playarea.sum();
                System.out.println("合計値:" + cpu_sum[p-2]);
                System.out.println("");
            }
        }

        ///現在操作しているプレイエリア
        int current = 0;

        // hitした後にdoubleが出てこないようにするためのフラグと、doubleを選択した時のフラグ
        // boolean double_flag = false;
        boolean double_use_flag = false;
        
        // ゲームの処理
        while (true) {

            //playerの処理
            //playerがバーストしていても処理を継続するため変更
            while(true){
                //どのプレイエリアでも操作がなければプレイヤーの操作は終了
                playing = false;
                ///split用に外にfor文
                for(int i = 0; i <= split_count; i++){

                    ///どのプレイエリアを操作するのかの指定　間にCPUが挟まっているため生まれた残念なコード
                    if(i == 0)current = 1;
                    else current = 5+i;
                    System.out.println(current);

                    // PlayerのPlayareaを取得する
                    Playarea hit_playarea = playarea_list.get(current);  

                    //繰り返しにより必要になった操作
                    if (hit_playarea.is_burst() == true || split_stand[current] == true)continue;

                    // hitかstandかを選択
                    // commandで「commandをcloseしろ」と警告が出てるが、閉じると2回目以降の標準入力ができなくなるから無視(https://kokishi-computing.com/web/2021/04/28/java-scanner-close/)
                    
                    Scanner command = new Scanner(System.in);
                    if(double_use_flag){
                        if(hit_playarea.cansplit_check() == true){
                            System.out.println("split_" + i + ":hitかstandかsplitを入力してください"+ hit_playarea.sum());
                        }else{
                            System.out.println("split_" + i + ":hitかstandを入力してください"+ hit_playarea.sum());
                        }
                    }
                    else{
                        if(hit_playarea.cansplit_check() == true){
                            System.out.println("split_" + i + ":hitかstandかdoubleかsplitを入力してください"+ hit_playarea.sum());
                        }else{
                            System.out.println("split_" + i + ":hitかstandかdoubleを入力してください"+ hit_playarea.sum());
                        }
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
                        split_sum[current] = hit_playarea.sum();
                        burst_flag = hit_playarea.is_burst();
                        split_burst[current] = hit_playarea.is_burst();
                        // バースト確認
                        if (split_burst[current] == true) {
                            continue;
                            
                            //break;
                        }else{
                            playing = true;
                        }

                        if (split_count == 0) {
                            double_use_flag = true;
                        }
                        
                    } else if (command_res.equals("stand")) {
                    // プレイヤーが　standした時の処理
                        player_sum = hit_playarea.sum();
                        split_stand[current] = true;
                        //break;
                    } else if (command_res.equals("double") && double_use_flag == false){
                        // プレイヤーがdoubleした時の処理
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
                        
                            hit_playarea.double_flag = true;
                            if (split_count == 0) {
                                break;
                            }
                            
                    } else if (command_res.equals("split") && hit_playarea.cansplit_check() == true) {
                    ///プレイヤーが　splitした時の処理
                        ///splitメソッドが返すリスト(プレイエリア)を返す
                        playarea_list.set(current, hit_playarea.split().get(0));
                        playarea_list.add(hit_playarea.split().get(1));

                        ///それぞれに対してドローも行う

                        ArrayList<Card> deal_card_list = deck.deal(1);

                        Playarea playarea = playarea_list.get(current);

                        for (int m = 0; m < deal_card_list.size(); m++){
                            Card init_card = deal_card_list.get(m);
                            playarea.card_list.add(init_card);
                        }

                        deal_card_list = deck.deal(1);
                        ///ここのマジックナンバーはプレイヤーの人数(split除く)
                        playarea = playarea_list.get(6+split_count);

                        for (int m = 0; m < deal_card_list.size(); m++){
                            Card init_card = deal_card_list.get(m);
                            playarea.card_list.add(init_card);
                        }
                        split_sum[current]= playarea_list.get(1).sum();
                        split_sum[6+split_count] = playarea_list.get(6+split_count).sum();
                        //System.out.println(playarea_list.get(1).sum());
                        //System.out.println(playarea_list.get(6+split_count).sum());
                        

                        split_count++;
                        playing = true;
                        break;

                    
                    }
                
                }
                
                // System.out.println(playing);
                
                
                if(playing == false)break;

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
            if(split_count == 0){
                rate = judge(dealer_sum, dealer_burst_flag, player_sum, burst_flag, 1, rate, playarea_list.get(1).double_flag);
            }else{
                for(int i = 0; i <= split_count; i++){
                    ///どのプレイエリアを操作するのかの指定　間にCPUが挟まっているため生まれた残念なコード
                    if(i == 0)current = 1;
                    else current = 5+i;
                    rate = judge(dealer_sum, dealer_burst_flag, split_sum[current], split_burst[current], i, rate, playarea_list.get(current).double_flag);
                
               }
            }


            //CPUとの勝敗判定
            for(int i = 0; i<4; i++){
                                    ///どのプレイエリアを操作するのかの指定　間にCPUが挟まっているため生まれた残念なコード
                    if(i == 0)current = 1;
                    else current = 5+split_count;
                judge_cpu(dealer_sum,dealer_burst_flag, cpu_sum[i], cpu_burst_flag[i], i);
            }
            break;
        
        }

        return rate;
    }
    

    // プレイヤーもディーラーもバーストしなかった時に呼ばれる
    public static float judge(int d_sum, boolean d_flag, int p_sum, boolean p_flag, int i, float rate, boolean double_flag){
        if (double_flag) {
            if(p_flag == true){
                //プレイヤーがバーストしてたらプレイヤーの負け
                System.out.println("You_"+ i +" BLose");
                rate = rate - 2.0F;
            }else if(d_flag == true){
                //ディーラーがバーストしてたらプレイヤーの勝ち
                System.out.println("You_"+ i +" BWin");
                rate = rate + 2.0F;
            }else if (d_sum < p_sum) {
                // ディーラーの合計値よりプレイヤーの方が高かったらプレイヤーの勝ち
                System.out.println("You_"+ i +" Win");
                rate = rate + 2.0F;
            } else if (d_sum > p_sum) {
                // ディーラーの合計値がプレイヤーよりも高かったらプレイヤーの負け
                System.out.println("You_"+ i +" Lose");
                rate = rate - 2.0F;
            } else {
                //　引き分け 
                System.out.println("You_"+ i +" Draw");
            }
        }
        else {
            if(p_flag == true){
                //プレイヤーがバーストしてたらプレイヤーの負け
                System.out.println("You_"+ i +" BLose");
                rate = rate - 1.0F;
            }else if(d_flag == true){
                //ディーラーがバーストしてたらプレイヤーの勝ち
                System.out.println("You_"+ i +" BWin");
                rate = rate + 1.0F;
            }else if (d_sum < p_sum) {
                // ディーラーの合計値よりプレイヤーの方が高かったらプレイヤーの勝ち
                System.out.println("You_"+ i +" Win");
                rate = rate + 1.0F;
            } else if (d_sum > p_sum) {
                // ディーラーの合計値がプレイヤーよりも高かったらプレイヤーの負け
                System.out.println("You_"+ i +" Lose");
                rate = rate - 1.0F;
            } else {
                //　引き分け 
                System.out.println("You_"+ i +" Draw");
            }
        }
        
        System.out.println( p_sum + " - " + d_sum);

        return rate;
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
        System.out.println( p_sum + " - " + d_sum);
    }

}