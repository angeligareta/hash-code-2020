import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;

class Global {
    static int averageFirstComponent = 0;
    static double averageSecondComponent = 0;
    static double stdFirstComponent = 0;
    static double stdSecondComponent = 0;
}


class Book {
    private final int id;
    private final int score;

    Book(int id, int score) {
        this.id = id;
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public int getId() {
        return id;
    }
}

class Library {
    private final Integer id;
    private ArrayList<Integer> booksIds;
    private HashMap<Integer, Integer> booksIdScore;
    private final int signupTime;
    private final int booksPerDay;
    private final int totalNumberOfDays;

    Library(Integer id, String[] books, String[] booksScore, int scanTime, int booksPerDay, int totalNumberOfDays) {
        this.id = id;
        this.signupTime = scanTime;
        this.booksPerDay = booksPerDay;
        this.totalNumberOfDays = totalNumberOfDays;

        this.booksIds = new ArrayList<Integer>();
        this.booksIdScore = new HashMap<Integer, Integer>();
        for (int i = 0; i < books.length; i++) {
            int bookId = Integer.parseInt(books[i]);

            if (!this.booksIds.contains(bookId)) {
                int bookScore = Integer.parseInt(booksScore[bookId]);

                this.booksIds.add(bookId);
                this.booksIdScore.put(bookId, bookScore);
            }
        }
    }

    public int getFirstComponent() {
        int bookScoreSum = 0;
        for (Integer bookScore : this.booksIdScore.values()) {
            bookScoreSum += bookScore;
        }
        return bookScoreSum;
    }

    public Double getSecondComponent() {
        return (double) this.totalNumberOfDays / (double) this.signupTime;
    }

    public void calculateSum() {
        Global.averageFirstComponent += getFirstComponent();
        Global.averageSecondComponent += getSecondComponent();
    }

    public Double getLibraryScore() {
//        System.out.println("Book Score Sum");
//        System.out.println(firstComponent);
//        System.out.println("Second component");
//        System.out.println(secondComponent);

        return 0.5 * ((getFirstComponent() - Global.averageFirstComponent) / Global.stdFirstComponent) +
                0.5 * ((getSecondComponent() - Global.averageSecondComponent) / Global.stdSecondComponent); //- Math.pow(2, this.signupTime);
    }

    public Integer getId() {
        return id;
    }

    public ArrayList<Integer> getBooksIds() {
        return booksIds;
    }

    public HashMap<Integer, Integer> getBooksScore() {
        return booksIdScore;
    }

    public void removeBooks(ArrayList<Integer> booksIdsToRemove) {
        ArrayList<Integer> oldBooksIds = this.getBooksIds();
        for (Integer id : booksIdsToRemove) {
            if (oldBooksIds.contains(id)) {
                this.booksIds.remove(id);
                this.booksIdScore.remove(id);
            }
        }
    }
}

public class HashCode {

    public static void main(String[] args) throws IOException {
        final String inputFileName = "./data/c_incunabula.txt";
        final String outputFileName = "./data/solution-c.txt";

        int bookNumber = 0;
        int totalNumberOfDays = 0;
        ArrayList<Library> libraries = new ArrayList<>();

        try {
            File dataset = new File(inputFileName);
            Scanner dataset_reader = new Scanner(dataset);

            while (dataset_reader.hasNextLine()) {
                String nextLine = dataset_reader.nextLine();
                String[] description = nextLine.split(" ");
                bookNumber = Integer.parseInt(description[0]);
                int libraryNumber = Integer.parseInt(description[1]);
                totalNumberOfDays = Integer.parseInt(description[2]);

                nextLine = dataset_reader.nextLine();
                String[] bookScore = nextLine.split(" ");

                for (int i = 0; i < libraryNumber; i++) {
                    nextLine = dataset_reader.nextLine();
                    String[] libraryDescription = nextLine.split(" ");

                    int libraryBookNumber = Integer.parseInt(libraryDescription[0]);
                    int signupTime = Integer.parseInt(libraryDescription[1]);
                    int booksPerDay = Integer.parseInt(libraryDescription[2]);

                    nextLine = dataset_reader.nextLine();
                    String[] booksIds = nextLine.split(" ");

                    Library library = new Library(i, booksIds, bookScore, signupTime, booksPerDay, totalNumberOfDays);
                    libraries.add(library);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileWriter writer = new FileWriter(outputFileName);

        libraries.forEach(Library::calculateSum);
        int size = libraries.size();
        Global.averageFirstComponent = Global.averageFirstComponent / size;
        Global.averageSecondComponent = Global.averageSecondComponent / size;
        double firstComponentStd = 0;
        double secondComponentStd = 0;
        for (Library library : libraries) {
            firstComponentStd += Math.pow(library.getFirstComponent() - Global.averageFirstComponent, 2);
            secondComponentStd += Math.pow(library.getSecondComponent() - Global.averageSecondComponent, 2);
        }
        Global.stdFirstComponent = Math.sqrt(firstComponentStd / size);
        Global.stdSecondComponent = Math.sqrt(secondComponentStd / size);
        libraries.sort(Comparator.comparing(Library::getLibraryScore));

        try {
            writer.write(libraries.size() + "\n");
            int realLibraryCount = 0;
            int libraryCount = 0;

            while (libraries.size() > 0) {
                Library library = libraries.get(libraryCount);

                libraryCount += 1;
                if (library.getBooksIds().size() > 0) {
                    realLibraryCount += 1;

                    try {
                        writer.write(library.getId() + " " + library.getBooksIds().size() + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ArrayList<Integer> bookIdsSorted = library.getBooksIds();
                    bookIdsSorted.sort(Comparator.comparing(libraryId -> library.getBooksScore().get(libraryId)));

                    String sortedBookLine = "";
                    for (Integer bookId : bookIdsSorted) {
                        sortedBookLine = sortedBookLine.concat(bookId + " ");
                    }

                    try {
                        writer.write(sortedBookLine.trim() + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // remove books previously sent
                    libraries = (ArrayList<Library>) libraries.subList(1, libraries.size());
                    libraries.forEach(library1 -> {
                        library1.removeBooks(library.getBooksIds());
                    });
                    libraries.forEach(Library::calculateSum);
                    int size1 = libraries.size();
                    Global.averageFirstComponent = Global.averageFirstComponent / size1;
                    Global.averageSecondComponent = Global.averageSecondComponent / size1;
                    double firstComponentStd1 = 0;
                    double secondComponentStd1 = 0;
                    for (Library library1 : libraries) {
                        firstComponentStd1 += Math.pow(library1.getFirstComponent() - Global.averageFirstComponent, 2);
                        secondComponentStd1 += Math.pow(library1.getSecondComponent() - Global.averageSecondComponent, 2);
                    }
                    Global.stdFirstComponent = Math.sqrt(firstComponentStd1 / size1);
                    Global.stdSecondComponent = Math.sqrt(secondComponentStd1 / size1);
                    libraries.sort(Comparator.comparing(Library::getLibraryScore));
                }
            }

            writer.close();
            System.out.println(realLibraryCount);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
