package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Inventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryTest {

    Inventory inventory;
    String[] gadgets = new String[4];

    @BeforeEach
    public void setUp() {
        inventory = Inventory.getInstance();

        gadgets[0] = "Sky Hook";
        gadgets[1] = "Geiger counter";
        gadgets[2] = "X-ray glasses";
        gadgets[3] = "Dagger shoe";
    }

    @Test
    public void load() {
        inventory.load(gadgets);
        assertTrue(inventory.getItem("Sky Hook"), "loading gadgets into inventory wasn't made properly, or, getItem() method failed");
        assertTrue(inventory.getItem("Geiger counter"), "loading gadgets into inventory wasn't made properly, or, getItem() method failed");
        assertTrue(inventory.getItem("X-ray glasses"), "loading gadgets into inventory wasn't made properly, or, getItem() method failed");
    }

    @Test
    public void getInstance() {
        assertNotNull(inventory,"Inventory is instance of null");
    }

    @Test
    public void getItem() {
        inventory.load(gadgets);
        assertTrue(inventory.getItem("Sky Hook"), "loading gadgets into inventory wasn't made properly, or, getItem() method failed");
        assertFalse(inventory.getItem("Sky Hook"), "remove() method, which is called by getItem() method failed");
        // Reload gadget list
        inventory.load(gadgets);
        assertFalse(inventory.getItem("Sky Book"), "The requested gadget is'nt exists, so we expect it to be unavailable");
    }

    @Test
    public void printToFile() {
    }
}