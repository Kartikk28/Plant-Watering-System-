import org.firmata4j.*;
import java.io.IOException;
import org.firmata4j.ssd1306.SSD1306;

public class ButtonListner implements IODeviceEventListener  {
    private final Pin pump;
    private final Pin mybtn;
    private final SSD1306 display1;


    public ButtonListner(Pin pump, Pin mybtn, SSD1306 theOledScrnObject) {
        this.pump = pump;
        this.mybtn = mybtn;
        this.display1=theOledScrnObject;
    }

    @Override
    public void onPinChange(IOEvent event) {
        if (event.getPin().equals(mybtn) && event.getValue() == 1) {
            // Button pressed, toggle pump state
            try {
                pump.setValue(pump.getValue() == 0 ? 1 : 0); // Toggle pump state
                display1.clear();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStart(IOEvent event) {}

    @Override
    public void onStop(IOEvent event) {}

    @Override
    public void onMessageReceive(IOEvent event, String message) {}
}