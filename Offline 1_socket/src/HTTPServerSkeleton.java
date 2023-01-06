import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class HTTPServerSkeleton {
    static final int PORT = 5064;


    
    public static void main(String[] args) throws IOException {
        
        ServerSocket serverConnect = new ServerSocket(PORT);
        System.out.println("Server started.\nListening for connections on port : " + PORT + " ...\n");

//        File file = new File("index.html");
//        FileInputStream fis = new FileInputStream(file);
//        BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
//        StringBuilder sb = new StringBuilder();
//        String line;
//        while(( line = br.readLine()) != null ) {
//            sb.append( line );
//            sb.append( '\n' );
//        }
//
//        String content = sb.toString();

        while(true)
        {
              new ServerThread(serverConnect.accept(), PORT);
//
        }
        
    }
    
}
