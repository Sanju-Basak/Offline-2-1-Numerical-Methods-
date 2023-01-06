import java.io.IOException;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        Scanner scanner= new Scanner(System.in);

        while (true){
            System.out.println("1");
            new ClientThread(scanner.nextLine());
        }
    }
}
