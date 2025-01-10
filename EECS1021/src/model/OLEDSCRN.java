package model;

import org.firmata4j.ssd1306.SSD1306;

import java.text.DecimalFormat;
import java.util.TimerTask;

public class OLEDSCRN extends TimerTask {
    private boolean systemRunning;
    private SSD1306 display1;
    private double value;
    private double Gpoint;


    public OLEDSCRN(boolean systemRunning, SSD1306 display ) {
        this.display1 = display;
        this.systemRunning = systemRunning;
    }

    @Override
    public void run() {

        if(systemRunning){
            display1.getCanvas().drawString(5,5,"Pump is ON ");
        }
        if(!systemRunning){
            display1.getCanvas().drawString(5,5,"Pump is off");
        }
        DecimalFormat fomating=  new DecimalFormat("###.##");
        display1.getCanvas().drawString(5,20,"Moisture " + fomating.format(Gpoint));
        display1.display();

    }
    public void setValue(float value) {
        this.value = value;
    }
    public void setSystemRunning(boolean systemRunning) {
        this.systemRunning = systemRunning;
    }


    public void rawMoistureValue(double gpoint) {
        this.Gpoint=gpoint;
    }
}