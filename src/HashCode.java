import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

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
    private boolean seen;

    Library(Integer id, String[] books, String[] booksScore, int scanTime, int booksPerDay, int totalNumberOfDays) {
        this.id = id;
        this.signupTime = scanTime;
        this.booksPerDay = booksPerDay;
        this.totalNumberOfDays = totalNumberOfDays;
        this.seen = false;

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

    public void setSeen() {
        this.seen = true;
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

//        return 0.5 * ((getFirstComponent() - Global.averageFirstComponent) / Global.stdFirstComponent) +
//                0.5 * ((getSecondComponent() - Global.averageSecondComponent) / Global.stdSecondComponent); //- Math.pow(2, this.signupTime);
        if (seen) {
            return Double.MAX_VALUE;
        } else {
            return 0.5 * getFirstComponent() + 0.5 * getSecondComponent();
        }
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

    public void removeBooks(List<Integer> booksIdsToRemove) {
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

    public static void calculateSolution(String inputFileName, String outputFileName) throws IOException {
        int bookNumber = 0;
        int totalNumberOfDays = 0;
        ArrayList<Library> libraries = new ArrayList<>();
        int maxNumberOfBooksToTake = 0;

        try {
            File dataset = new File(inputFileName);
            Scanner dataset_reader = new Scanner(dataset);

            if (dataset_reader.hasNextLine()) {
                String nextLine = dataset_reader.nextLine();
                String[] description = nextLine.split(" ");
                bookNumber = Integer.parseInt(description[0]);
                int libraryNumber = Integer.parseInt(description[1]);
                totalNumberOfDays = Integer.parseInt(description[2]);

                //maxNumberOfBooksToTake = (int) (bookNumber * 0.7);

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

//        libraries.forEach(Library::calculateSum);
//        int size = libraries.size();
//        Global.averageFirstComponent = Global.averageFirstComponent / size;
//        Global.averageSecondComponent = Global.averageSecondComponent / size;
//        double firstComponentStd = 0;
//        double secondComponentStd = 0;
//        for (Library library : libraries) {
//            firstComponentStd += Math.pow(library.getFirstComponent() - Global.averageFirstComponent, 2);
//            secondComponentStd += Math.pow(library.getSecondComponent() - Global.averageSecondComponent, 2);
//        }
//        Global.stdFirstComponent = Math.sqrt(firstComponentStd / size);
//        Global.stdSecondComponent = Math.sqrt(secondComponentStd / size);
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

//                    List<Integer> booksToTake = bookIdsSorted.subList(0, bookIdsSorted.size());

//                    if (bookIdsSorted.size() > maxNumberOfBooksToTake) {
//                        booksToTake = booksToTake.subList(0, maxNumberOfBooksToTake);
//                    }

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
        calculateSolution("./data/a_example.txt", "./data/solution-a.txt");
        System.out.println("A Done !");
        calculateSolution("./data/b_read_on.txt", "./data/solution-b.txt");
        System.out.println("B Done !");
        calculateSolution("./data/c_incunabula.txt", "./data/solution-c.txt");
        System.out.println("C Done !");
        //calculateSolution("./data/d_tough_choices.txt", "./data/solution-d.txt");
        System.out.println("D Done !");
        calculateSolution("./data/e_so_many_books.txt", "./data/solution-e.txt");
        System.out.println("E Done !");
        calculateSolution("./data/f_libraries_of_the_world.txt", "./data/solution-f.txt");
        System.out.println("F Done !");
    }
}
