package net.mizofumi;

import com.pi4j.io.gpio.*;
import net.mizofumi.Modules.DistanceMonitor.DistanceMonitorListener;
import net.mizofumi.Modules.DistanceMonitor.HC_SR04;
import net.mizofumi.Modules.SteppingMoter.Pattern;
import net.mizofumi.Modules.SteppingMoter.ULN2003;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        /*
        ULN2003     モータ制御を行うクラス
        new ULN2003(IN1_PIN, IN2_PIN, IN3_PIN, IN4_PIN);
         */
        ULN2003 motor1 = new ULN2003(RaspiPin.GPIO_00,RaspiPin.GPIO_01,RaspiPin.GPIO_02,RaspiPin.GPIO_03);
        ULN2003 motor2 = new ULN2003(RaspiPin.GPIO_24,RaspiPin.GPIO_27,RaspiPin.GPIO_25,RaspiPin.GPIO_28);

        /*
        //2-2相励磁パターンで動かす場合は、このように設定する。
        ArrayList<Pattern> pattern2 = new ArrayList<>();
        pattern2.add(new Pattern(1,0,0,1));
        pattern2.add(new Pattern(1,0,0,0));
        pattern2.add(new Pattern(1,1,0,0));
        pattern2.add(new Pattern(0,1,0,0));
        pattern2.add(new Pattern(0,1,1,0));
        pattern2.add(new Pattern(0,0,1,0));
        pattern2.add(new Pattern(0,0,1,1));
        pattern2.add(new Pattern(0,0,0,1));

        motor1.setStepPattern(pattern2);
        motor2.setStepPattern(pattern2);
        */

        /*
        HC_SR04     超音波センサの制御を行うクラス
        new HC_SR04(Echo_PIN, Trig_PIN);
         */
        HC_SR04 ultrasonic_sensor = new HC_SR04( RaspiPin.GPIO_26, RaspiPin.GPIO_22 );

        //超音波センサのイベントを設定
        ultrasonic_sensor.setListener(new DistanceMonitorListener() {

            //  センサが値を読んだ時に呼ばれるメソッド
            //  measureDistanceに値がセットされている
            @Override
            public void onListen(float measureDistance) {
                System.out.println(measureDistance); // 距離を画面出力

                //  10cm以下の場合は減速し、それ以外の場合は標準速度。
                if (measureDistance < 10){
                    motor1.setDelay(10);
                    motor2.setDelay(10);
                }else {
                    motor1.setDefaultDelay();
                    motor2.setDefaultDelay();
                }

                //  5cm 以下で停止
                if (measureDistance < 5){
                    System.out.println("STOP!");
                    if (motor1.isRunning())
                        motor1.stop();

                    if (motor2.isRunning())
                        motor2.stop();

                }else {
                    if (!motor1.isRunning())
                        motor1.forward();

                    if (!motor2.isRunning())
                        motor2.forward();
                }

            }
        });

        //モータ前進
        motor1.forward();
        motor2.forward();

        //超音波センサの監視をスタート
        ultrasonic_sensor.startWatch();

        //60Sec間動かす
        Thread.sleep(60000); //60sec wait

        //超音波センサの監視をストップ
        ultrasonic_sensor.stopWatch();

        //モータ停止
        motor1.stop();
        motor2.stop();


    }

    /*

        M   E   M   O

        GPIOのピン指定は、Physical(物理ピン)とNameのGPIO.NのN部分の番号で指定する。

         +-----+-----+---------+------+---+---Pi 3---+---+------+---------+-----+-----+
         | BCM | wPi |   Name  | Mode | V | Physical | V | Mode | Name    | wPi | BCM |
         +-----+-----+---------+------+---+----++----+---+------+---------+-----+-----+
         |     |     |    3.3v |      |   |  1 || 2  |   |      | 5v      |     |     |
         |   2 |   8 |   SDA.1 | ALT0 | 1 |  3 || 4  |   |      | 5V      |     |     |
         |   3 |   9 |   SCL.1 | ALT0 | 1 |  5 || 6  |   |      | 0v      |     |     |
         |   4 |   7 | GPIO. 7 |   IN | 1 |  7 || 8  | 0 | IN   | TxD     | 15  | 14  |
         |     |     |      0v |      |   |  9 || 10 | 1 | IN   | RxD     | 16  | 15  |
         |  17 |   0 | GPIO. 0 |   IN | 0 | 11 || 12 | 0 | IN   | GPIO. 1 | 1   | 18  |
         |  27 |   2 | GPIO. 2 |   IN | 0 | 13 || 14 |   |      | 0v      |     |     |
         |  22 |   3 | GPIO. 3 |   IN | 0 | 15 || 16 | 0 | IN   | GPIO. 4 | 4   | 23  |
         |     |     |    3.3v |      |   | 17 || 18 | 0 | IN   | GPIO. 5 | 5   | 24  |
         |  10 |  12 |    MOSI | ALT0 | 0 | 19 || 20 |   |      | 0v      |     |     |
         |   9 |  13 |    MISO | ALT0 | 0 | 21 || 22 | 0 | IN   | GPIO. 6 | 6   | 25  |
         |  11 |  14 |    SCLK | ALT0 | 0 | 23 || 24 | 1 | OUT  | CE0     | 10  | 8   |
         |     |     |      0v |      |   | 25 || 26 | 1 | OUT  | CE1     | 11  | 7   |
         |   0 |  30 |   SDA.0 |   IN | 1 | 27 || 28 | 1 | IN   | SCL.0   | 31  | 1   |
         |   5 |  21 | GPIO.21 |   IN | 1 | 29 || 30 |   |      | 0v      |     |     |
         |   6 |  22 | GPIO.22 |   IN | 1 | 31 || 32 | 0 | IN   | GPIO.26 | 26  | 12  |
         |  13 |  23 | GPIO.23 |   IN | 0 | 33 || 34 |   |      | 0v      |     |     |
         |  19 |  24 | GPIO.24 |   IN | 0 | 35 || 36 | 0 | IN   | GPIO.27 | 27  | 16  |
         |  26 |  25 | GPIO.25 |   IN | 0 | 37 || 38 | 0 | IN   | GPIO.28 | 28  | 20  |
         |     |     |      0v |      |   | 39 || 40 | 0 | IN   | GPIO.29 | 29  | 21  |
         +-----+-----+---------+------+---+----++----+---+------+---------+-----+-----+
         | BCM | wPi |   Name  | Mode | V | Physical | V | Mode | Name    | wPi | BCM |
         +-----+-----+---------+------+---+---Pi 3---+---+------+---------+-----+-----+
     */
}
