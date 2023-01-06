import java.io.*;
import java.net.FileNameMap;
import java.net.Socket;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Date;

public class ServerThread implements Runnable{

    private Socket socket;
    private Thread thread;
    private int port;
    public ServerThread(Socket socket, int port) {
        this.socket= socket;
        this.port= port;
        thread= new Thread(this);
        thread.start();
    }

    private void createHeader(StringBuilder stringBuilder){
        stringBuilder.append("<html>\n");
        stringBuilder.append("<head>\n");
        stringBuilder.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
        stringBuilder.append("</head>\n<body>\n");
    }

    private String getMimeType(File file){
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String mimeType = fileNameMap.getContentTypeFor(file.getName());
        //System.out.println(mimeType);
        return mimeType;
    }

    @Override
    public void run() {
        int flag=0;
        BufferedReader bufferedReader= null;
        String in= null;

        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            in= bufferedReader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(in== null || in.length()> 0){
            if(in== null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    return;
                }
            }
//            PrintWriter pr = new PrintWriter(s.getOutputStream());
//            String input = in.readLine();
//            System.out.println("input : "+input);
//
//            // String content = "<html>Hello</html>";
//            if(input == null) continue;
//            if(input.length() > 0) {
//                if(input.startsWith("GET"))
//                {
//                    pr.write("HTTP/1.1 200 OK\r\n");
//                    pr.write("Server: Java HTTP Server: 1.0\r\n");
//                    pr.write("Date: " + new Date() + "\r\n");
//                    pr.write("Content-Type: text/html\r\n");
//                    pr.write("Content-Length: " + content.length() + "\r\n");
//                    pr.write("\r\n");
//                    pr.write(content);
//                    pr.flush();
//                }
//
//                else
//                {
//
//                }
//            }
//
//            s.close();
            //System.out.println(in);
            if(in.startsWith("GET")) {
                String path = "";
                File newFile;
                String[] strings = in.split("/");
                for (int i = 1; i < strings.length - 1; i++) {
                    if (i == strings.length - 2)
                        path = path + strings[i].replace(" HTTP", "");
                    else {
                        path = path + strings[i] + "/";
                        //path= path.replace("%20", " ");
                    }

                }
                path = path.replace("%20", " ");
                System.out.println(path);
                if (path == "")
                    newFile = new File("./root");
                else
                    newFile = new File("./root/" + path);

                PrintWriter pr;
                try {
                    pr = new PrintWriter(socket.getOutputStream());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                StringBuilder stringBuilder = new StringBuilder();
                if (newFile.exists()) {
                    createHeader(stringBuilder);
                    if (newFile.isDirectory()) {
                        File[] listOfFiles = newFile.listFiles();
                        for (int i = 0; i < listOfFiles.length; i++) {
                            if (listOfFiles[i].isDirectory()) {
                                stringBuilder.append("<h2><b><i><a href=\"http://localhost:" + port + "/" + path + "/" + listOfFiles[i].getName() + "\"> " + listOfFiles[i].getName() + " </a></i></b></h2>\n");
                            }
                            if (listOfFiles[i].isFile()) {
                                stringBuilder.append("<h2><a href=\"http://localhost:" + port + "/" + path + "/" + listOfFiles[i].getName() + "\"> " + listOfFiles[i].getName() + " </a></h2>\n");
                            }
                        }
                    }
                    if(newFile.isFile()){
                        if(getMimeType(newFile).equalsIgnoreCase("text/plain")){
                            //System.out.println("Hello");
                            StringBuilder builder = new StringBuilder();

                            // try block to check for exceptions where
                            // object of BufferedReader class us created
                            // to read filepath
                            try (BufferedReader buffer = new BufferedReader(
                                    new FileReader(newFile.getPath()))) {

                                String str;

                                // Condition check via buffer.readLine() method
                                // holding true upto that the while loop runs
                                while ((str = buffer.readLine()) != null) {

                                    builder.append(str).append("\n");
                                }
                            }

                            // Catch block to handle the exceptions
                            catch (IOException e) {

                                // Print the line number here exception occurred
                                // using printStackTrace() method
                                e.printStackTrace();
                            }
                            stringBuilder.append("<p>"+builder.toString()+ "</p>\n");
                        }
                        else if((getMimeType(newFile).equalsIgnoreCase("image/jpeg"))){
                            String str;
                            Path pathToImage = Paths.get(newFile.getPath());
                            // 1. Convert image to an array of bytes
                            byte[] imageBytes = new byte[0];
                            try {
                                imageBytes = Files.readAllBytes(pathToImage);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            // 2. Encode image bytes[] to Base64 encoded String
                            String base64str = Base64.getEncoder().encodeToString(imageBytes);



                            str = "<img src=\"data:" + "image/jpeg"+  ";base64," + base64str + "\">";
                            stringBuilder.append("<p>"+str+ "</p>\n");

                        }else {
                            flag=1;
                        }
                    }
                    stringBuilder.append("</body>\n</html>");
                } else {
                    createHeader(stringBuilder);
                    stringBuilder.append("<h2>404:Content not found</h2>\n");
                    stringBuilder.append("</body>\n</html>");
                }
                String content = stringBuilder.toString();
                if(newFile.exists()){
                    pr.write("HTTP/1.1 200 OK\r\n");
                    pr.write("Server: Java HTTP Server: 1.1\r\n");
                    pr.write("Date: " + new Date() + "\r\n");
                    if(flag==0){

                        pr.write("Content-Type: text/html"  + "\r\n");
                        pr.write("Content-Length: " + content.length() + "\r\n");
                        pr.write("\r\n");
                        pr.write(content);
                        pr.flush();
                    }else{

                        pr.write("Content-Type: application/force-download\r\n");
                        pr.write("Content-Length: " + newFile.length() + "\r\n");
                        pr.write("\r\n");
                        pr.flush();
                        int count;
                        byte[] buffer= new byte[1024];
                        try {
                            OutputStream outputStream= socket.getOutputStream();
                            BufferedInputStream bis= new BufferedInputStream(new FileInputStream(newFile));

                            while((count=bis.read(buffer)) > 0) {
                                outputStream.write(buffer, 0, count);
                                outputStream.flush();
                            }

                            bis.close();
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }else{
                    pr.write("HTTP/1.1 404 NOT FOUND\r\n");
                    pr.write("Server: Java HTTP Server: 1.1\r\n");
                    pr.write("Date: " + new Date() + "\r\n");
                    pr.write("Content-Type:  text/html\r\n");
                    pr.write("Content-Length: " + content.length() + "\r\n");
                    pr.write("\r\n");
                    pr.write(content);
                    pr.flush();

                    System.out.println("404: CONTENT NOT FOUND!");
                }



                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                pr.close();
                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
            if(in.startsWith("UPLOAD")) {
                //System.out.println("ami");
                String valid= null;
                try {
                    valid = bufferedReader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (valid.equalsIgnoreCase("valid")){
                    System.out.println("valid");

                    int count= 0;
                    byte[] buffer = new byte[1024];

                    try {
                        String[] str= in.split(" ");
                        FileOutputStream fileOutputStream = new FileOutputStream(new File("./uploaded/"+ str[1]));
                        InputStream inputStream = socket.getInputStream();

                        while((count=inputStream.read(buffer)) > 0){
                            System.out.println("eikhane");
                            fileOutputStream.write(buffer, 0, count);
                            fileOutputStream.flush();
                        }

                        inputStream.close();
                        fileOutputStream.close();
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Sesh");

//                    long size
//                            = dataInputStream.readLong(); // read file size
//                    byte[] buffer = new byte[4 * 1024];
//                    while (size > 0
//                            && (bytes = dataInputStream.read(
//                            buffer, 0,
//                            (int)Math.min(buffer.length, size)))
//                            != -1) {
//                        // Here we write the file using write method
//                        fileOutputStream.write(buffer, 0, bytes);
//                        size -= bytes; // read upto file size
//                    }

                    try {
                        bufferedReader.close();
                        socket.close();
                       // System.out.println("sdsd");
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }else {
                    System.out.println("Error! File type or format is invalid");
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }
            }
        }




    }
}
