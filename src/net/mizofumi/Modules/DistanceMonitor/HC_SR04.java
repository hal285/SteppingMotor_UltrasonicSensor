package net.mizofumi.Modules.DistanceMonitor;

import com.pi4j.io.gpio.*;

/**
 * Created by mizof on 2016/12/04.
 */
public class HC_SR04 {

    private final static float SOUND_SPEED = 340.29f;  // speed of sound in m/s

    private final static int TRIG_DURATION_IN_MICROS = 10; // trigger duration of 10 micro s
    private final static int WAIT_DURATION_IN_MILLIS = 60; // wait 60 milli s

    private final static int TIMEOUT = 2100;

    private final static GpioController gpio = GpioFactory.getInstance();

    private final GpioPinDigitalInput echoPin;
    private final GpioPinDigitalOutput trigPin;
    private boolean isRunning;
    private Thread mThread;
    private DistanceMonitorListener listener;

    public HC_SR04(Pin echoPin, Pin trigPin) {
        this.echoPin = gpio.provisionDigitalInputPin( echoPin );
        this.trigPin = gpio.provisionDigitalOutputPin( trigPin );
        this.trigPin.low();
    }

    public void setListener(DistanceMonitorListener listener) {
        this.listener = listener;
    }

    public float measureDistance() throws TimeoutException {
        this.triggerSensor();
        this.waitForSignal();
        long duration = this.measureSignal();

        return duration * SOUND_SPEED / ( 2 * 10000 );
    }

    private void triggerSensor() {
        try {
            this.trigPin.high();
            Thread.sleep( 0, TRIG_DURATION_IN_MICROS * 1000 );
            this.trigPin.low();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private void waitForSignal() throws TimeoutException {
        int countdown = TIMEOUT;

        while( this.echoPin.isLow() && countdown > 0 ) {
            countdown--;
        }

        if( countdown <= 0 ) {
            throw new TimeoutException( "Timeout waiting for signal start" );
        }
    }

    private long measureSignal() throws TimeoutException {
        int countdown = TIMEOUT;
        long start = System.nanoTime();
        while( this.echoPin.isHigh() && countdown > 0 ) {
            countdown--;
        }
        long end = System.nanoTime();

        if( countdown <= 0 ) {
            throw new TimeoutException( "Timeout waiting for signal end" );
        }

        return (long)Math.ceil( ( end - start ) / 1000.0 );  // Return micro seconds
    }

    public void startWatch(){

        if (isRunning){
            stopWatch();
        }
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                isRunning = true;
                while (isRunning){
                    try {
                        if (listener!=null){
                            listener.onListen(measureDistance());
                        }
                    } catch (HC_SR04.TimeoutException e) {
                        e.printStackTrace();
                    }

                    try {
                        Thread.sleep(60);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        mThread.start();

    }

    public void stopWatch(){
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
            startWatch();
        }
    }

    public static class TimeoutException extends Exception {

        private final String reason;

        public TimeoutException( String reason ) {
            this.reason = reason;
        }

        @Override
        public String toString() {
            return this.reason;
        }
    }

}