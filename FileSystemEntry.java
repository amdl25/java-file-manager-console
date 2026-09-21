/**
 /**
 * Reprezinta o entitate de baza,care este extinsa de clasele FileEntry si Directory
 * Ofera funcționalitati comune, precum gestionarea caii unui document
 * si accesul la informatiile esentiale ale acestuia
 */
public abstract class FileSystemEntry {

    private String cale;

    public FileSystemEntry(String cale) {
        this.cale = cale;
    }

    public String getCale() {
        return cale;
    }

    public String getNume() {
        return cale.substring(cale.lastIndexOf('\\') + 1);
    }

    public String getExtensie() {
        return cale.substring(cale.lastIndexOf('.') + 1);
    }
}
