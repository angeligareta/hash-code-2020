import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;

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
    private final int scanTime;
    private final int booksPerDay;

    Library(Integer id, String[] books, String[] booksScore, int scanTime, int booksPerDay) {
        this.id = id;
        this.scanTime = scanTime;
        this.booksPerDay = booksPerDay;

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

    public Double getLibraryScore() {
        int bookScoreSum = 0;
        for (Integer bookScore : this.booksIdScore.values()) {
            bookScoreSum += bookScore;
        }

        return bookScoreSum + this.booksIds.size() / this.booksPerDay - Math.pow(2, this.scanTime);
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
}

public class HashCode {

    public static void main(String[] args) throws IOException {
        final String inputFileName = "./data/a_example.txt";
        final String outputFileName = "./data/solution-a.txt";

        int bookNumber = 0;
        int maxDays = 0;
        ArrayList<Library> libraries = new ArrayList<>();

        try {
            File dataset = new File(inputFileName);
            Scanner dataset_reader = new Scanner(dataset);

            while (dataset_reader.hasNextLine()) {
                String nextLine = dataset_reader.nextLine();
                String[] description = nextLine.split(" ");
                System.out.println(description.length);
                bookNumber = Integer.parseInt(description[0]);
                int libraryNumber = Integer.parseInt(description[1]);
                maxDays = Integer.parseInt(description[2]);

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

                    Library library = new Library(i, booksIds, bookScore, signupTime, booksPerDay);
                    libraries.add(library);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileWriter writer = new FileWriter(outputFileName);

        libraries.sort(Comparator.comparing(Library::getLibraryScore));

        try {

            System.out.println(libraries.size());
            writer.write(libraries.size() + "\n");
            libraries.forEach(library -> {
                System.out.print(library.getId() + " " + library.getBooksIds().size() + "\n");
                try {
                    writer.write(library.getId() + " " + library.getBooksIds().size() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ArrayList<Integer> bookIdsSorted = library.getBooksIds();
                bookIdsSorted.sort(Comparator.comparing(libraryId -> library.getBooksScore().get(libraryId)));

                bookIdsSorted.forEach(id -> System.out.print(id + " "));
                bookIdsSorted.forEach(id -> {
                    try {
                        writer.write(id + " ");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                System.out.print("\n");
                try {
                    writer.write("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}