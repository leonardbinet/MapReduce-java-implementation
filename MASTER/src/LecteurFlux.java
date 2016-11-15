import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ArrayBlockingQueue;


class LecteurFlux implements Runnable {

    private final InputStream inputStream;
    ArrayBlockingQueue<String> output;


    LecteurFlux(InputStream inputStream, ArrayBlockingQueue<String> output) {
        this.inputStream = inputStream;
        this.output = output;
    }

    private BufferedReader getBufferedReader(InputStream is) {
        return new BufferedReader(new InputStreamReader(is));
    }

    @Override
    public void run() {
        BufferedReader br = getBufferedReader(inputStream);
        String ligne = "";
        try {
            while ((ligne = br.readLine()) != null) {
                output.put(ligne);
            }
            output.put("ENDOFTHREAD");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        
      
    }
}