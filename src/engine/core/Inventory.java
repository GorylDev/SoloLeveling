package engine.core;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private final int capacity = 16;
    private final List<Item> items = new ArrayList<>();

    public boolean addItem(Item item) {
        if (items.size() < capacity) {
            items.add(item);
            System.out.println("SYSTEM: Picked up -> [" + item.getName() + "]");
            return true;
        }
        System.out.println("SYSTEM: Inventory is full! Can't get: " + item.getName());
        return false;
    }

    public void printInventory() {
        System.out.println("\n=== Your backpack (" + items.size() + "/" + capacity + ") ===");
        if (items.isEmpty()) {
            System.out.println(" [ Empty... ]");
        } else {
            for (int i = 0; i < items.size(); i++) {
                System.out.println(" Slot " + (i + 1) + ": " + items.get(i).getName() + " - " + items.get(i).getDescription());
            }
        }
        System.out.println("==================================");
    }

    public List<Item> getItems() {
        return items;
    }
}