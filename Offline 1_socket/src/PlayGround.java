import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;

public class PlayGround {

    public static void whenUsingGetFileNameMap_thenSuccess(){
        File file = new File("product.mp4");
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String mimeType = fileNameMap.getContentTypeFor(file.getName());

        System.out.println(mimeType);
    }
    public static void main(String[] args) {
        whenUsingGetFileNameMap_thenSuccess();
    }
}
