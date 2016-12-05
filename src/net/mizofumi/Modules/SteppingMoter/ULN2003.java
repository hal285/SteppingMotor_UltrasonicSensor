package net.mizofumi.Modules.SteppingMoter;

import com.pi4j.io.gpio.*;

import java.util.ArrayList;

/**
 * Created by mizof on 2016/12/04.
 */
public class ULN2003 {
    GpioController gpio = GpioFactory.getInstance();
    GpioPinDigitalOutput in1pin;
    GpioPinDigitalOutput in2pin;
    GpioPinDigitalOutput in3pin;
    GpioPinDigitalOutput in4pin;
    int delay = 5;
    ArrayList<Pattern> mStepPattern;
    Thread mThread;
    boolean isRunning = false;

    //ピン設定を定義し、ステップパターンは初期値を使用するコンストラクタ
    public ULN2003(Pin IN1, Pin IN2, Pin IN3, Pin IN4) {
        in1pin  = gpio.provisionDigitalOutputPin(IN1, "IN1", PinState.LOW);
        in2pin  = gpio.provisionDigitalOutputPin(IN2, "IN2", PinState.LOW);
        in3pin  = gpio.provisionDigitalOutputPin(IN3, "IN3", PinState.LOW);
        in4pin  = gpio.provisionDigitalOutputPin(IN4, "IN4", PinState.LOW);
        initPattern();
    }

    //ステップパターンを変更する場合に使用するSetter
    public void setStepPattern(ArrayList<Pattern> mStepPattern) {
        this.mStepPattern = mStepPattern;
    }


    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getDelay() {
        return delay;
    }

    protected void initPattern(){
        mStepPattern = new ArrayList<>();
        mStepPattern.add(new Pattern(1,0,0,0));
        mStepPattern.add(new Pattern(0,1,0,0));
        mStepPattern.add(new Pattern(0,0,1,0));
        mStepPattern.add(new Pattern(0,0,0,1));
    }

    public void forward(){
        //スレッド動作中の場合は一度停止させる
        if (isRunning){
            stop();
        }
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                isRunning = true;
                while (isRunning){
                    for (int i = 0; i < mStepPattern.size(); i++) {
                        writePin(i);
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        mThread.start();
    }

    public void backward(){
        //スレッド動作中の場合は一度停止させる
        if (isRunning){
            stop();
        }
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                isRunning = true;
                while (isRunning){
                    for (int i = mStepPattern.size()-1; i >= 0; i--) {
                        writePin(i);
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        mThread.start();
    }

    public void stop(){
        if (isRunning){
            isRunning = false;
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //スレッドが終了してない場合再度停止させる(再帰処理
        if (mThread.isAlive()){
            stop();
        }
    }

    protected void writePin(int i){
        //IN1
        if (mStepPattern.get(i).getPins()[0] == 0){
            in1pin.low();
        }else {
            in1pin.high();
        }

        //IN2
        if (mStepPattern.get(i).getPins()[1] == 0){
            in2pin.low();
        }else {
            in2pin.high();
        }

        //IN3
        if (mStepPattern.get(i).getPins()[2] == 0){
            in3pin.low();
        }else {
            in3pin.high();
        }

        //IN4
        if (mStepPattern.get(i).getPins()[3] == 0){
            in4pin.low();
        }else {
            in4pin.high();
        }
    }

    public void setDefaultDelay(){
        this.delay = 5;
    }

    public boolean isRunning() {
        return isRunning;
    }
}
