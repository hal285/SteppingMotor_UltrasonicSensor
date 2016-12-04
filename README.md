# SteppingMotor_UltrasonicSensor
大塚研のメンバーのためのコード

## ULN2003
モータドライバULN2003の制御に関するクラスです。

### Usage

```Java
ULN2003 motor1 = new ULN2003(IN1_PIN, IN2_PIN, IN3_PIN, IN4_PIN);
```
コンストラクタはこのような形で引数にそれぞれPinを設定します。
#### 例
```Java
ULN2003 motor1 = new ULN2003(RaspiPin.GPIO_00,RaspiPin.GPIO_01,RaspiPin.GPIO_02,RaspiPin.GPIO_03);
motor1.forward();	//モータの前進
motor1.backward();	//モータの後退
motor1.stop();		//モータの停止
motor1.setDelay(int delay);	//モータの速度変更
motor1.setDefaultDelay();	//モータを標準速度に戻す(int = 5)
motor1.isRunning();	//モータが動作中の場合はTrue,停止中の場合はFalse
motor1.setStepPattern(ArrayList<Pattern> patterns) //モータの動作パターンの変更
```

## HC_SR04
超音波センサHC-SR04の制御に関するクラスです。
```Java
HC_SR04 ultrasonic_sensor = new HC_SR04(Echo_PIN, Trig_PIN);
```
コンストラクタはこのような形で引数にそれぞれPinを設定します。
#### 例
```Java
HC_SR04 ultrasonic_sensor = new HC_SR04( RaspiPin.GPIO_26, RaspiPin.GPIO_22 );
ultrasonic_sensor.setListener(new DistanceMonitorListener() {
    @Override
    public void onListen(float measureDistance) {
        System.out.println(measureDistance); // 距離を出力
    }
});
ultrasonic_sensor.startWatch();
ultrasonic_sensor.stopWatch();
```