package main;

import org.firmata4j.I2CDevice;
import org.firmata4j.Pin;
import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.IODevice;
import org.firmata4j.ssd1306.SSD1306;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;

public class MainClass {

    // Method to calculate the moisture level percentage
    public static double calculateMoistureLevel(long rawMoistureValue, int wetThreshold, int dryThreshold) {
        // Ensure rawMoistureValue falls within the range defined by wetThreshold and dryThreshold
        if (rawMoistureValue < wetThreshold) {
            rawMoistureValue = wetThreshold; // Set to wet threshold if below
        } else if (rawMoistureValue > dryThreshold) {
            rawMoistureValue = dryThreshold; // Set to dry threshold if above
        }

        // Calculate the moisture level percentage
        double moistureLevel = (double) (rawMoistureValue - wetThreshold) / (dryThreshold - wetThreshold);
        moistureLevel = (1 - moistureLevel) * 100.0; // Convert  to percentage
        return moistureLevel;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String myUSB = "/dev/cu.usbserial-0001";
        IODevice myGroveBoard = new FirmataDevice(myUSB);
        int dryValue = 700;
        int wetValue = 550;

        try {
            myGroveBoard.start();
            System.out.println("Board started.");
            myGroveBoard.ensureInitializationIsDone();
        } catch (Exception ex) {
            System.out.println("Couldn't connect to board.");
        }

        Pin mybtn = myGroveBoard.getPin(6);
        mybtn.setMode(Pin.Mode.INPUT);
        Pin pump = myGroveBoard.getPin(2);
        pump.setMode(Pin.Mode.OUTPUT);
        Pin mySensor = myGroveBoard.getPin(15);
        mySensor.setMode(Pin.Mode.ANALOG);

        I2CDevice i2cDevice = myGroveBoard.getI2CDevice((byte) 0x3C);
        SSD1306 oledScreen = new SSD1306(i2cDevice, SSD1306.Size.SSD1306_128_64);
        oledScreen.init();

        ButtonListner buttonListener = new ButtonListner(pump, mybtn, oledScreen);
        myGroveBoard.addEventListener(buttonListener);

        boolean systemRunning = false;
        long lastButtonPressTime = 0;
        ArrayList<Double> moistureLevels = new ArrayList<>();

        OLEDSCRN screenUpdater = new OLEDSCRN(systemRunning, oledScreen);
        Timer timer = new Timer();
        timer.schedule(screenUpdater, 500, 1000);

        while (true) {
            long currentTime = System.currentTimeMillis();
            long buttonValue = mybtn.getValue();

            if (buttonValue == 1 && currentTime - lastButtonPressTime > 500) {
                lastButtonPressTime = currentTime;
                systemRunning = !systemRunning;
                System.out.println(systemRunning ? "System started." : "System stopped.");
                if (!systemRunning) {
                    pump.setValue(0);
                    System.out.println("Pump has been stopped manually.");
                }
            }

            if (systemRunning) {
                long rawMoistureValue = mySensor.getValue();
                double moistureLevel = calculateMoistureLevel(rawMoistureValue, wetValue, dryValue);
                moistureLevels.add(moistureLevel);

                plotMoistureData(moistureLevels);
                Thread.sleep(500);

                if (moistureLevel > 50) { // Example condition based on moisture level
                    pump.setValue(1);
                    screenUpdater.setSystemRunning(true);
                    screenUpdater.setValue((float) moistureLevel);
                } else {
                    pump.setValue(0);
                    screenUpdater.setValue((float) moistureLevel);
                    screenUpdater.setSystemRunning(false);
                }

                System.out.println("Current moisture level: " + moistureLevel);
                screenUpdater.rawMoistureValue(moistureLevel);
            }
        }
    }




private static void plotMoistureData(ArrayList<Double> data) {
        long currentTime = System.currentTimeMillis();
        StdDraw.clear();
        StdDraw.line(0, 0, data.size(), 0);
        StdDraw.line(0, 0, 0, 100);
        StdDraw.setXscale(-1, data.size() + 1); // Adjusted x-axis scale
        StdDraw.setYscale(-0.10, 100); // Adjusted y-axis scale
        StdDraw.text(data.size() / 2, -2, "Time"); // Adjusted x-axis label position
        StdDraw.setPenColor(Color.red);
        StdDraw.text(data.size() / 2, 101, "Time vs Moisture Graph"); // Adjusted graph title position
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.text(0.0, 40, "Moisture", 90); // Adjusted y-axis label position

        // Plot data points
        StdDraw.setPenColor(StdDraw.BLACK);
        for (int i = 1; i < data.size(); i++) {
            double x1 = i - 1;
            double y1 = Math.max(0, data.get(i - 1)); // Ensure y1 does not go below 0
            double x2 = i;
            double y2 = Math.max(0, data.get(i)); // Ensure y2 does not go below 0
            StdDraw.line(x1, y1, x2, y2); // Draw lines between points ensuring they don't go below the x-axis
        }
    }
}


