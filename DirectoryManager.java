import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class DirectoryManager {
    private List<Directory> directoare = new ArrayList<>();
    private HashMap<String, List<FileEntry>> fisiereDirector = new HashMap<>();
    private final String fisierText = "monitored_directories.txt";

    public DirectoryManager()  {
        //citire din fisier la initializare pt asigurare persistenta directoare
        citireDirectoare();

    }

    public List<Directory> getDirectoare() {
        return directoare;
    }


    /**
     * Este verificata validitatea unui director pe baza unui unei expresii regulate
     * dar si a existentei directorului pe hard disk
     *
     * @param cale calea directorului
     * @return true in cazul in care calea este valida, false altfel
     * @throws IllegalArgumentException in cazul in care calea introdusa este nula sau goala
     */
    private boolean esteCaleaValida(String cale) {
        if (cale == null || cale.isEmpty()) {
            throw new IllegalArgumentException("Calea directorului nu poate fi nula sau goala");
        }

        //sa inceapa cu o litera mare : \ fara <> | ... + oricate repetari ?-optional grupul
        String pattern = "^[A-Z]:\\\\([^<>|:\"?*]+(\\\\[^<>|:\"?*]+)*)?$";
        if (!Pattern.matches(pattern, cale)) {
            System.out.println("Calea trebuie să inceapa cu o litera de unitate urmata de '\\' si sa contina caractere valide");
            return false;
        }

        File folder = new File(cale);
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Calea " + cale + " nu exista pe hard disk");
            return false;
        }
        return true;
    }


    /**
     * Verifica daca un director exista deja în lista de directoare monitorizate
     *
     * @param caleDirector calea directorului
     * @return true daca directorul exista deja, false în caz contrar
     */
    public boolean esteDirectorExistent(String caleDirector) {
        for (Directory director : directoare) {
            if (director.getCale().equals(caleDirector)) {
                return true;
            }
        }
        return false;
    }



    /**
     * Adauga un director in colectia de directoare, in urma validarii acestuia
     *
     * @param cale calea directorului
     * @return true daca directorul a fost adaugat cu succes, false daca nu
     * @throws DirectoryAlreadyExistsException daca directorul exista deja
     */
    public boolean adaugaDirector(String cale) throws DirectoryAlreadyExistsException {
        if(esteCaleaValida(cale)) {
            if(esteDirectorExistent(cale)) {
                throw new DirectoryAlreadyExistsException("Directorul exista deja!");
            }
            directoare.add(new Directory(cale));
            actualizeazaFisiereGrupate();
            return true;
        }
        return false;
    }

    /**
     * Metoda care sterge un director in urma validarii acestuia
     *
     * @param cale calea directorului ce se doreste a fi sters
     */
    public void stergeDirector(String cale) {
        if (esteCaleaValida(cale)) {
            for (Directory d : directoare) {
                if (d.getCale().equals(cale)) {
                    directoare.remove(d);
                    scrieDirectoare();
                    return; // Ieșire din metodă după ștergere
                }
            }
            System.out.println("Directorul cu calea " + cale + " nu a fost găsit.");
        }
    }


    /**
     * Citeste directoarele din fisierul de text dat de variabila {@link #fisierText}
     * Fiecare linie din fisierul text este validata astfel:
     * - Linia trebuie sa fie o cale valida a unui director, respectand formatul specificat în expresia regulata,
     * - Directorul respectiv trebuie să existe pe disc. Dacă nu exista, se sare peste linia curenta
     * - Daca directorul exista deja în colectia de directoare, acesta este ignorat si nu este adaugat din nou
     *
     * Dacă fișierul de text nu exista sau este gol, se va iesi din metoda fara a se efectua vreo operație
     * Dupa citirea fișierului si validarea directoarelor sunt grupate fisierele
     * in functie de extensii folosind metoda {@link #actualizeazaFisiereGrupate}
     *
     * @throws IllegalArgumentException Daca formatul unei linii din fisier nu este valid (calea nu respecta
     *                                   structura) sau daca directorul nu exista pe disc
     */
    public void citireDirectoare() {
        File file = new File(fisierText);

        if (!file.exists()) {
            System.out.println("Nu exista directoare monitorizate precedent");
            return;
        }

        if (file.length() == 0) {
            System.out.println("Nu exista directoare monitorizate precedent");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(fisierText))) {
            String linie;
            while ((linie = reader.readLine()) != null) {
                linie = linie.trim();
                if (!linie.isEmpty()) {
                    //sa inceapa cu o litera mare : \ fara <> | ... + oricate repetari ?-optional grupul
                    String pattern = "^[A-Z]:\\\\([^<>|:\"?*]+(\\\\[^<>|:\"?*]+)*)?$";
                    if (!Pattern.matches(pattern, linie)) {
                        System.out.println("Calea trebuie să inceapa cu o litera de unitate urmata de '\\' si sa contina caractere valide");
                        continue;
                    }

                    File folder = new File(linie);
                    if (!folder.exists() || !folder.isDirectory()) {
                        System.out.println("Calea " + linie + " nu exista pe hard disk");
                        continue;
                    }
                    if (esteDirectorExistent(linie)) {
                        System.out.println("Directorul " + linie + " exista deja în colectie.");
                    } else {
                        Directory director = new Directory(linie);
                        directoare.add(director);
                    }
                }
            }
            actualizeazaFisiereGrupate();
        } catch (IOException e) {
            System.out.println("Eroare la citirea fișierului: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    /**
     * Salveaza directoarele din colectie intr-un fisier text
     */
    public void scrieDirectoare() {
            try(BufferedWriter writer = new BufferedWriter(new FileWriter(fisierText))) {
                for(Directory director: directoare) {
                    writer.write(director.getCale());
                    writer.newLine();
                }

            } catch (IOException e) {
                System.out.println("Eroare! Directoarele nu au putut fi salvate " + e.getMessage());
            }
    }

    /**
     * Grupeaza fișierele din fiecare director în functie de extensia lor.
     * Metoda parcurge fiecare director din lista de directoare, extrage fisierele din fiecare director
     * si le adauga intr-un HashMap, unde cheia este extensia fisierului,
     * iar valoarea este o lista de fisiere care au aceeasi extensie
     */
    public void actualizeazaFisiereGrupate() {
        for(Directory d: directoare) {
            List<FileSystemEntry> continut = d.getContinut();

            if(!continut.isEmpty()) {
                List<FileEntry> fisiere = new ArrayList<>();

                for (FileSystemEntry element : continut) {
                    if (element instanceof FileEntry) {
                        FileEntry fisier = (FileEntry) element;
                        String extensie = fisier.getExtensie();

                        fisiere.add(fisier);
                        fisiereDirector.putIfAbsent(extensie, new ArrayList<>());
                        fisiereDirector.get(extensie).add(fisier);
                    }
                }
            }
        }
    }


    public void genereazaStatistici(String fisierStatistici) {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fisierStatistici))) {
            scrieFisiereExtensii(writer);
            scrieDimensiuneDirector(writer);
            scrieFisiereDuplicate(writer);

        } catch(IOException e) {
            System.out.println("Eroare la generarea statisticilor " + e.getMessage());
        }
    }


    /**
     * Este scrisa lista de fisiere, ce sunt grupate in fuinctie de extensie
     * Fiecare extensie este asociata cu numarul de fisiere care o au si cu numele fisierelor respective,
     * separate prin virgulă
     *
     * @param writer {@link BufferedWriter} pentru scrierea in fisierul text.
     * @throws IOException Daca apare o eroare la scrierea în fisier
     */
    private void scrieFisiereExtensii(BufferedWriter writer) throws IOException {
        writer.write("--------Fisierele in functie de extensia acestora---------------\n");

        //perechi chei-valoare
        for(Map.Entry<String, List<FileEntry>> inreg: fisiereDirector.entrySet()) {
            String cheie = inreg.getKey();
            List<FileEntry> valoare = inreg.getValue();
            int nrFisiere = valoare.size();

            writer.write("\nExtensia: " + cheie);
            writer.write("\nNr fisiere: " + nrFisiere);
            writer.write("\nFisiere: ");
            for (int i = 0; i < valoare.size(); i++) {
                writer.write(valoare.get(i).getNume());
                if (i < valoare.size() - 1) {
                    writer.write(", ");
                }
            }
            writer.write("\n");
        }
    }


    /**
     * Calculeaza dimensiunea totala a fisierelor dintr-un director, incluzand si fisierele din subdirectoare
     * Pentru fiecare fișier, este scrisa dimensiunea acestuia in fisierul text de iesire
     * In cazul subdirectoare, dimensiunea acestora este adaugata recursiv
     *
     * @param director Directorul pentru care se calculează dimensiunea totala
     * @param writer {@link BufferedWriter} pentru operatia de scriere intr-un fisier text
     * @return Dimensiunea totala a fișierelor din directorul respectiv, în bytes
     * @throws IOException Dacă apare o eroare în timpul scrierii in fisierul text
     */
    private long calculeazaDimensiuneDirector(Directory director, BufferedWriter writer) throws IOException {
        long dimensiuneDirector = 0;
        for(FileSystemEntry g: director.getContinut()) {
            if (g instanceof FileEntry) {
                FileEntry fisier = (FileEntry) g;
                File file = new File(fisier.getCale());
                long dimensiuneFisier = file.length();
                writer.write("Fisier: " + fisier.getNume() + " - dimensiune: " + dimensiuneFisier + " bytes\n");

                dimensiuneDirector += dimensiuneFisier;

            } else if (g instanceof Directory) {
                dimensiuneDirector += calculeazaDimensiuneDirector((Directory) g, writer);
            }
        }
        return dimensiuneDirector;
    }

    /**
     * Scrie folosind {@link BufferedWriter} dimensiunea totala a fișierelor din fiecare director
     * aflat în colecția de directoare. Fiecare director este procesat de metoda
     * {@link #calculeazaDimensiuneDirector}, iar rezultatele sunt scrise în fișierul de ieșire statistics.txt
     *
     * @param writer {@link BufferedWriter}  cu ajutorul caruia se scrie in fisierul de iesire
     * @throws IOException Dacă apare o eroare în timpul scrierii datelor
     */
    private void scrieDimensiuneDirector(BufferedWriter writer) throws IOException {
        writer.write("\n-----------Dimensiunea totala a fisierelor din fiecare director---------------\n\n");
        for(Directory d: directoare) {
            long dimensiuneDirector = calculeazaDimensiuneDirector(d, writer);
            writer.write("Director: " + d.getCale() + " - Dimensiune totala fisiere: " + dimensiuneDirector + " bytes\n");
        }
    }

    /**
     * Verifica recursiv existenta fisierelor duplicate in cadrul unui director, luand in considerare si subdirectoarele lui
     * Un fișier este considerat duplicat daca exista alt fișier cu acelasi nume si aceeasi dimensiune
     * Informatiile despre fisierele duplicate sunt adaugate intr-un {@link HashMap},
     * unde cheia este o combinație a numelui fișierului si a dimensiunii sale, iar valoarea este o lista
     * care contine caile fisierelor duplicate
     *
     * @param director Directorul in care se verifica fișierele duplicate
     * @param fisiereDuplicate {@link HashMap} în care se adauga fisierele duplicate
     */
    private void verificaDuplicate(Directory director, HashMap<String, List<String>> fisiereDuplicate) {
        for(FileSystemEntry g: director.getContinut()) {
            if(g instanceof FileEntry) {
                FileEntry fisier = (FileEntry) g;
                String cheie = fisier.getNume() + "--" + new File(fisier.getCale()).length();

                fisiereDuplicate.putIfAbsent(cheie, new ArrayList<>());
                fisiereDuplicate.get(cheie).add(fisier.getCale());
            } else if(g instanceof Directory) {
                verificaDuplicate((Directory) g, fisiereDuplicate);
            }
        }
    }

    /**
     * Scrie intr-un fisier text lista fisierelor duplicate dintr-un director
     *
     * @param writer {@link BufferedWriter} cu ajutorul caruia se scrie in fisierul de iesire
     * @throws IOException Dacă apare o eroare în timpul scrierii datelor
     */
    private void scrieFisiereDuplicate(BufferedWriter writer) throws IOException {
        HashMap<String, List<String>> fisiereDuplicate = new HashMap<>();
        for(Directory d:directoare) {
            verificaDuplicate(d, fisiereDuplicate);
        }

        writer.write("\n-----------------------Fisiere duplicate------------------------------\n");
        for(Map.Entry<String, List<String>> entry: fisiereDuplicate.entrySet()) {
            List<String> cai = entry.getValue();
            if(cai.size() > 1) {
                writer.write("\nFisier duplicat: " + entry.getKey() + "\n");
                for(String cale: cai) {
                    writer.write("Este in " + cale + "\n");
                }
            }
        }
    }
}
