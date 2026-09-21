import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Directory din sistemul de fisiere, care extinde clasa abstracta {@link FileSystemEntry}
 * Gestioneaza fisierele si subdirectoarele dintr-un director si adauga fisierele multimedia
 */
public class Directory extends FileSystemEntry {
    private List<FileSystemEntry> continut = new ArrayList<>();

    /**
     * Doar fisierele cu aceste extensii sunt adaugate in director
     */
    private String[] extensiiAcceptate = {"mp3", "wav", "jpg", "png"};
    public Directory(String cale) {
        super(cale);
        adaugaContinut();
    }

    /**
     * Adauga fisierele si subdirectoarele din directorul curent la lista continut
     * Metoda verifica daca elementele din directorul curent sunt fisiere sau subdirectoare.
     * Doar fisierele acceptate(cele cu extensia specifica fisierelor multimedia) sunt adaugate in lista
     * alaturi de subdirectoare
     * In cazul in care calea introdusa nu reprezinta un director, se va afisa un mesaj de eroare
     */
    public void adaugaContinut() {
        File folder = new File(getCale());
        if (folder.isDirectory()) {
            File[] elemente = folder.listFiles();
            if (elemente != null) {
                for (File element : elemente) {
                    if (element.isFile()) {
                        FileEntry fisier = new FileEntry(element.getPath());
                        if (esteFisierAcceptat(fisier)) {
                            continut.add(fisier);
                        }
                    } else if (element.isDirectory()) {
                        Directory director = new Directory(element.getPath());
                        continut.add(director);
                    }
                }
            }
        } else {
            System.out.println("Calea " + getCale() + " nu este a unui director valid.");
        }
    }

    /**
     * Verifica daca fisierele au extensiile specifice unui fisier multimedia
     *
     * @param fisier Fisierul care urmeaza sa fie verificat
     * @return true daca extensia fisierului este acceptata, altfel false
     */
    private boolean esteFisierAcceptat(FileEntry fisier) {
        String extensie = fisier.getExtensie().toLowerCase();
        for(String e: extensiiAcceptate) {
            if(e.equals(extensie)) {
                return true;
            }
        }
        return false;
    }

    public List<FileSystemEntry> getContinut() {
        return continut;
    }
}
