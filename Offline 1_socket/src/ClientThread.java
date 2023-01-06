import java.io.*;
import java.net.FileNameMap;
import java.net.Socket;
import java.net.URLConnection;

public class ClientThread implements Runnable {
    private Socket socket;

    private Thread thread;
    private File file;

    public ClientThread(String str)  {
        System.out.println(str);
        file= new File("./uploads/"+ str);
        thread= new Thread(this);
        thread.start();

    }

    private String getMimeType(File file){
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String mimeType = fileNameMap.getContentTypeFor(file.getName());
        //System.out.println(mimeType);
        return mimeType;
    }

    @Override
    public void run() {

        try {
            socket= new Socket("localhost", 5064);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter printWriter= null;
        try {
            printWriter= new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        printWriter.write("UPLOAD "+ file.getName()+ "\r\n");
        printWriter.flush();

        if(file.exists()){
            String str= getMimeType(file);
            if(str.equals("image/jpeg") || str.equals("text/plain") || str.equals("image/png") || str.equals("video/mp4")){
                printWriter.write("valid\r\n");
                printWriter.flush();
            }else{
                printWriter.write("invalid\r\n");
                printWriter.flush();
                System.out.println("Error! The file format is invalid");
                printWriter.close();
                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return;
            }

        }else{
            printWriter.write("invalid\r\n");
            printWriter.flush();
            System.out.println("Error! The file name is invalid");
            printWriter.close();
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        int count;
        byte[] buffer= new byte[1024];
        try {
            OutputStream outputStream= socket.getOutputStream();
            BufferedInputStream bis= new BufferedInputStream(new FileInputStream(file));


            while((count=bis.read(buffer)) > 0) {
                outputStream.write(buffer, 0, count);
                outputStream.flush();
            }

            bis.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        printWriter.close();
        try {
            socket.close();
            //System.out.println("Close");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return;
    }
}
