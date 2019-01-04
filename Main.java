import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.BufferedWriter;

public class Main {

    public static void main(String[] args) {
        String inData = InputHandler.ReadInput("input");

        Tree processedData = DataProcessor.Process(inData);

        LinkedList<String> treeText = OutputHandler.WriteOutput(processedData, "arbore");

        Boolean foundBasicError = new TreeVisitor().checkUnnasignedVars(treeText, inData, "output");
        if (!foundBasicError) {
            new TreeVisitor().startVisiting(processedData, inData, "output");
        }
    }
}
