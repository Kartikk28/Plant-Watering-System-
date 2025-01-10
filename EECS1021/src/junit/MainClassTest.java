package model;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TestMoistureLevelCalculation {

    @Test
    void testCalculateMoistureLevel() {
        // Example moisture values for testing
        long rawMoistureDry = 800; // Above dry threshold
        long rawMoistureWet = 500; // Below wet threshold
        long rawMoistureNormal = 625; // Normal value between wet and dry thresholds

        int wetThreshold = 550;
        int dryThreshold = 700;

        // Expected results based on the calculation logic
        double expectedDry = 0.0;
        double expectedWet = 100.0;
        double expectedNormal = 50.0; // This expects a linear scale between wet and dry

        // Actual calculations
        double actualDry = MainClass.calculateMoistureLevel(rawMoistureDry, wetThreshold, dryThreshold);
        double actualWet = MainClass.calculateMoistureLevel(rawMoistureWet, wetThreshold, dryThreshold);
        double actualNormal = MainClass.calculateMoistureLevel(rawMoistureNormal, wetThreshold, dryThreshold);

        // Assertions
        assertEquals(expectedDry, actualDry, "The calculation should return 0% for values above the dry threshold.");
        assertEquals(expectedWet, actualWet, "The calculation should return 100% for values below the wet threshold.");
        assertEquals(expectedNormal, actualNormal, "The calculation should return 50% for values midway between thresholds.");
    }
}

