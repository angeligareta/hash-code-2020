import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

class Library {
    private final Integer id;
    private ArrayList<Integer> booksIds;
    private HashMap<Integer, Integer> booksIdScore;
    private final int signupTime;
    private final int booksPerDay;
    private final int totalNumberOfDays;
    private int scoreSum;
    private boolean seen;

    Library(Integer id, String[] books, String[] booksScore, int scanTime, int booksPerDay, int totalNumberOfDays) {
        this.id = id;
        this.signupTime = scanTime;
        this.booksPerDay = booksPerDay;
        this.totalNumberOfDays = totalNumberOfDays;
        this.seen = false;
        this.scoreSum = 0;

        this.booksIds = new ArrayList<>();
        this.booksIdScore = new HashMap<>();
        for (String book : books) {
            int bookId = Integer.parseInt(book);

            if (!this.booksIds.contains(bookId)) {
                int bookScore = Integer.parseInt(booksScore[bookId]);

                this.booksIds.add(bookId);
                this.booksIdScore.put(bookId, bookScore);
                this.scoreSum += bookScore;
            }
        }
    }

    void setSeen() {
        this.seen = true;
    }

    Double getLibraryScore() {
        if (seen) {
            // Put first seen values (omitting them as we are looping)
            return Double.MAX_VALUE;
        } else {
            return this.scoreSum + Math.pow(this.booksPerDay, 1.3) / Math.pow(this.signupTime, 2);
        }
    }

    Integer getId() {
        return id;
    }

    ArrayList<Integer> getBooksIds() {
        return booksIds;
    }

    HashMap<Integer, Integer> getBooksScore() {
        return booksIdScore;
    }

    void removeBooks(List<Integer> booksIdsToRemove) {
        ArrayList<Integer> oldBooksIds = this.getBooksIds();
        for (Integer id : booksIdsToRemove) {
            if (oldBooksIds.contains(id)) {
                this.scoreSum -= this.booksIdScore.get(id);
                this.booksIdScore.remove(id);
                this.booksIds.remove(id);
            }
        }
    }
}

public class HashCode {

    private static void calculateSolution(String inputFileName, String outputFileName) throws IOException {
        int bookNumber = 0;
        int totalNumberOfDays = 0;
        ArrayList<Library> libraries = new ArrayList<>();

        try {
            File dataset = new File(inputFileName);
            Scanner dataset_reader = new Scanner(dataset);

            if (dataset_reader.hasNextLine()) {
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
        libraries.sort(Comparator.comparing(x -> -x.getLibraryScore()));

        try {
            writer.write(libraries.size() + "\n");
            int realLibraryCount = 0;
            int libraryCount = 0;

            while (libraryCount < libraries.size()) {
                Library library = libraries.get(libraryCount);
                libraries.get(libraryCount).setSeen();

                // Omit libraries without books
                if (library.getBooksIds().size() > 0) {
                    // This library we are writing it
                    realLibraryCount += 1;

                    // Sort by books with more score
                    ArrayList<Integer> bookIdsSorted = library.getBooksIds();
                    bookIdsSorted.sort(Comparator.comparing(bookId -> -library.getBooksScore().get(bookId)));

                    // Show library id and number of books
                    try {
                        writer.write(library.getId() + " " + bookIdsSorted.size() + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    // Write books id sorted
                    try {
                        String sortedBookLine = "";
                        for (Integer bookId : bookIdsSorted) {
                            sortedBookLine = sortedBookLine.concat(bookId + " ");
                        }
                        writer.write(sortedBookLine.trim() + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Remove library seen
                    // Remove books previously sent and reorder
                    for (Library newLibrary : libraries.subList(libraryCount + 1, libraries.size())) {
                        newLibrary.removeBooks(bookIdsSorted);
                    }
                    libraries.sort(Comparator.comparing(x -> -x.getLibraryScore()));
                }

                // Increment count
                libraryCount += 1;
            }

            writer.close();
            System.out.println("Put this in first line: " + realLibraryCount);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) throws IOException {
        new Thread(() -> {
            try {
                calculateSolution("./data/a_example.txt", "./data/solution-a.txt");
                System.out.println("A Done !");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                calculateSolution("./data/b_read_on.txt", "./data/solution-b.txt");
                System.out.println("B Done !");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                calculateSolution("./data/c_incunabula.txt", "./data/solution-c.txt");
                System.out.println("C Done !");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                calculateSolution("./data/d_tough_choices.txt", "./data/solution-d.txt");
                System.out.println("D Done !");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                calculateSolution("./data/e_so_many_books.txt", "./data/solution-e.txt");
                System.out.println("E Done !");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                calculateSolution("./data/f_libraries_of_the_world.txt", "./data/solution-f.txt");
                System.out.println("F Done !");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
