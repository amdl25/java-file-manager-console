/**
 * Reprezinta un fisier de pe hard disk si extinde clasa {@link FileSystemEntry}
 * Un obiect de tipul {@link FileEntry} conține informații despre calea fișierului si extensia acestuia
 * Clasa permite obținerea numelui fișierului si a extensiei asociate fisierului respectiv
 */

 public class FileEntry extends FileSystemEntry {
 
    private String extensie;

    public FileEntry(String cale) {
        super(cale);
        this.extensie = super.getExtensie();
    }

    @Override
    public String getExtensie() {
        return extensie;
    }

    public String getNume() {
        return super.getNume();
    }
}
