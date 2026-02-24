package game.characters;

public class SungJinWoo {
        private String name;
        private int level;
        private int health;
        private int attackPower;

        public SungJinWoo(String name, int level, int health, int attackPower) {
            this.name = name;
            this.level = level;
            this.health = health;
            this.attackPower = attackPower;
        }

        public void attack() {
            System.out.println(name + " attacks with power " + attackPower);
        }

        public void takeDamage(int damage) {
            health -= damage;
            System.out.println(name + " takes " + damage + " damage. Remaining health: " + health);
        }

        public String getName() {
            return name;
        }

        public int getLevel() {
            return level;
        }

        public int getHealth() {
            return health;
        }

        public int getAttackPower() {
            return attackPower;
        }
}
