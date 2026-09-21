import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Menu {
    private DirectoryManager management;

    public Menu(DirectoryManager management) {
        this.management = management;
    }
    String fisierStatistici = "statistics.txt";


    /**
     * Afiseaza structura arborescenta a fisierelor si subdirectoarelor dintr-un director.
     * Parcurge recursiv lista de fisiere si subdirectoare si le afiseaza cu o indentare
     * specifica pentru fiecare nivel al structurii
     *
     * @param continut Lista de {@link FileSystemEntry} care poate contine atat fișiere, cat si subdirectoare
     *                 Este structura care va fi parcursa si afisata
     */
    public void afiseazaStructuraArborescenta(List<FileSystemEntry> continut, String indentare) {
        for (FileSystemEntry g : continut) {
            if (g instanceof FileEntry) {
                System.out.println(indentare + "Fișier: " + g.getNume());
            } else if (g instanceof Directory) {
                System.out.println(indentare + "Subdirector: " + g.getCale());
                afiseazaStructuraArborescenta(((Directory) g).getContinut(), indentare + "  ");
            }
        }
    }


    public void afisareMeniu() {
        int exit = 0;
        while(exit == 0) {
            System.out.println("---------Meniu------------");
            System.out.println("1. Adauga un director nou si afiseaza continutul acestuia");
            System.out.println("2. Sterge un director");
            System.out.println("3. Genereaza statistici despre directoare");
            System.out.println("4. Salveaza si iesi");

            Scanner scanner = new Scanner(System.in);
            try {
                System.out.print("\nIntroduceti optiunea dorita: ");
                int optiune = scanner.nextInt();
                if(optiune <  1 || optiune > 5) {
                    throw new InvalidOptionException("Te rog alege o optiune intre 1 si 5");
                }

                switch (optiune) {
                    case (1): {
                        System.out.println("Introdu calea directorului");
                        scanner.nextLine();
                        String cale = scanner.nextLine();
                        try {
                            if (management.adaugaDirector(cale)) {
                                if (!management.getDirectoare().isEmpty()) {
                                    Directory directorAdaugat = management.getDirectoare().get(management.getDirectoare().size() - 1);
                                    List<FileSystemEntry> continut = directorAdaugat.getContinut();

                                    if (!continut.isEmpty()) {
                                        System.out.println("Continutul directorului adaugat:");
                                        afiseazaStructuraArborescenta(continut, "");
                                    } else {
                                        System.out.println("Directorul este gol.");
                                    }
                                } else {
                                    System.out.println("Nu exista directoare.");
                                }

                            }
                        } catch(DirectoryAlreadyExistsException e) {
                            System.out.println(e.getMessage());
                        }
                            break;
                    }
                    case (2): {
                        List <Directory> directoare = management.getDirectoare();
                        System.out.println("Directoarele monitorizate: ");
                        System.out.println(directoare.size());
                        for(Directory d: directoare) {
                            System.out.println(d.getCale());
                        }
                        System.out.println("Alege directorul pe care vrei sa-l stergi");
                        scanner.nextLine();
                        String cale = scanner.nextLine();
                        management.stergeDirector(cale);
                        break;
                    }
                    case (3): {
                        management.genereazaStatistici(fisierStatistici);
                        System.out.println("Statisticile au fost generate in fisierul text " + fisierStatistici);
                        break;
                    }
                    case (4): {
                        management.scrieDirectoare();
                        exit = 1;
                        break;
                    }
                    default:
                        System.out.println("Optiunea introdusa este invalida");

                }

            } catch(InputMismatchException e) {
                System.out.println("Te rog introdu un numar intreg");
                scanner.next();

            } catch(InvalidOptionException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
