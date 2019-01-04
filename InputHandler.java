import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.BufferedWriter;

class InputHandler {
    protected static String ReadInput(String inPath) {
        StringBuilder inputText = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(inPath));
            String line = null;
            while ((line = br.readLine()) != null) {
                inputText.append(line + "\n");
            }
            br.close();
        } catch (Exception ex) {
            System.err.print(ex);
            return null;
        }

        return inputText.toString();
    }
}
