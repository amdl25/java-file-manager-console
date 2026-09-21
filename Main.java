public class Main {
    public static void main(String[] args) {
        DirectoryManager management = new DirectoryManager();
        Menu meniu = new Menu(management);
        meniu.afisareMeniu();
    }
}