import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.BufferedWriter;

public class TreeVisitor {
    public void startVisiting(Tree tree, String inData, String outPath) {
        LinkedList<Variable> vars = getVarList(inData);
        


        try {
            FileWriter fw = new FileWriter(outPath, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);

            visit(tree.root, vars, out, inData.split("\n"));
            
            for (Variable var : vars) {
                out.println(var.name + "=" + var.value);
            }

            out.close();
        } catch (Exception e) {
            System.err.print(e);
        }


    }

    public void visit(Node node, LinkedList<Variable> vars, PrintWriter out, String[] lines) {
        node.accept(this, vars, out, lines);
    }

    private LinkedList<Variable> getVarList(String inData) {
        LinkedList<Variable> varList = new LinkedList<Variable>();

        String[] lines = inData.split("\n");

        for (String line : lines) {
            if (line.length() >= 1) {
                line = line.substring(0, line.length() - 1).replaceAll("\\s+","");
                if (line.contains("int")) {
                    line = line.substring(3);
                    if (line.contains(",")) {
                        String[] varNames = line.split(",");
                        for (String varName : varNames) {
                            if (varName.length() >= 1) {
                                varList.add(new Variable(Variable.varType.intvar,
                                varName));
                            }
                        }
                    } else {
                        varList.add(new Variable(Variable.varType.intvar, line));
                    }
                } else if (line.contains("bool")) {
                    line = line.substring(4);

                    if (line.contains(",")) {
                        String[] varNames = line.split(",");
                        for (String varName : varNames) {
                            if (varName.length() >= 1) {
                                varList.add(new Variable(Variable.varType.boolvar,
                                varName));
                            }
                        }
                    } else {
                        varList.add(new Variable(Variable.varType.boolvar, line));
                    }
                }
            }
        }
        return varList;
    }

	public Boolean checkUnnasignedVars(LinkedList<String> treeText, String inData, String outPath) {
        LinkedList<Variable> vars = getVarList(inData);

        for (String treeLine : treeText) {
            if (treeLine.contains("VariableNode")) {
                String varName = treeLine.split(" ")[1];
                Boolean found = false;
                for (Variable var : vars) {
                    if (varName.contains(var.name)) {
                        found = true;
                    }
                }
                if (!found) {
                    try {
                        FileWriter fw = new FileWriter(outPath, true);
                        BufferedWriter bw = new BufferedWriter(fw);
                        PrintWriter out = new PrintWriter(bw);
            
                        out.println("UnassignedVar " + findFirstLineine(varName, inData));

                        out.close();
                        return true;
                    } catch (Exception e) {
                        System.err.print(e);
                    }
                }
            }
        }
        return false;
	}

    private int findFirstLineine(String varName, String inData) {
        int lineNr = 1;

        String[] lines = inData.split("\n");

        for (String line : lines) {
            if (line.contains(varName)) {
                return lineNr;
            }
            lineNr++;
        }
        return -1;
    }
}