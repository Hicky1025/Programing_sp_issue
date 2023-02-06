public class Player {

    float money = 1000;
    float bet_money;

    // 倍率 : magnification -> mag
    public void result(float mag) {
        money =  money + (bet_money * mag);
    }
}
