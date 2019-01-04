import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.BufferedWriter;


class OutputHandler {
    protected static LinkedList<String> WriteOutput(Tree tree, String outPath) {
        LinkedList<String> treeText = new LinkedList<String>();
        try {
            FileWriter fw = new FileWriter(outPath, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);

            tree.traverse(tree.root, 0, out, treeText);

            out.close();
        } catch (Exception e) {
            System.err.print(e);
        }

        return treeText;
    }
}
