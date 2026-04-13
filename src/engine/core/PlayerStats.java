package engine.core;

public class PlayerStats {
    private int level = 1;
    private int experience = 0;
    private int expToNextLevel = 100;

    private int maxHealth = 100;
    private int currentHealth = 100;
    private int attackPower = 35; // starting strength
    private final Inventory inventory = new Inventory();

    public void addExperience(int amount) {
        experience += amount;
        System.out.println("SYSTEM: Consumed! Gained " + amount + " EXP. (" + experience + "/" + expToNextLevel + ")");

        while (experience >= expToNextLevel) {
            levelUp();
        }
    }

    private void levelUp() {
        experience -= expToNextLevel;
        level++;

        expToNextLevel = (int)(expToNextLevel * 1.5);

        maxHealth += 20;
        currentHealth = maxHealth;
        attackPower += 15;

        System.out.println("==================================================");
        System.out.println(">>> LEVEL UP! Level: " + level + " <<<");
        System.out.println(">>> Healed up! New damage cap: " + attackPower);
        System.out.println("==================================================");
    }

    public int getAttackPower() { return attackPower; }
    public int getCurrentHealth() { return currentHealth; }
    public void takeDamage(int amount) { currentHealth -= amount; }

    public Inventory getInventory() {
        return inventory;
    }
}