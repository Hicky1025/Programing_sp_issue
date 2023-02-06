import java.util.*;

public class Playarea {

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
    boolean double_flag = false;

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
    public ArrayList<Playarea> split(){
        Card A = this.get_card_list().get(0);
        Card B = this.get_card_list().get(1);

        Playarea playareaA = new Playarea();
        Playarea playareaB = new Playarea();

        playareaA.card_list.add(A);
        playareaB.card_list.add(B);

        


        ArrayList<Playarea> newlist = new ArrayList<Playarea>();
        newlist.add(playareaA);
        newlist.add(playareaB);



        //int a = A.get_number().get(0);
        //int b = B.get_number().get(0);
        //System.out.println(b);
        //System.out.println(a);
        
        //int a = A.get_number().get(0);
        //int b = B.get_number().get(0);
        //System.out.println(playareaA.sum());
        //System.out.println(playareaB.sum());
        return newlist;
    }
}