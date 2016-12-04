package net.mizofumi.Modules.SteppingMoter;

/**
 * Created by mizof on 2016/12/04.
 */
public class Pattern {
    Integer[] pins = new Integer[4];

    public Pattern(int IN1, int IN2, int IN3, int IN4) {
        pins[0] = IN1;
        pins[1] = IN2;
        pins[2] = IN3;
        pins[3] = IN4;
    }

    public Integer[] getPins() {
        return pins;
    }
}
