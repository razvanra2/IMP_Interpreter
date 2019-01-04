import java.util.LinkedList;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.BufferedWriter;

public class Tree {
    public Node root;

    public Tree(Node node) {
        root = node;
    }

    public void traverse(Node currentNode, int indentLevel, PrintWriter out, LinkedList<String> treeText) {
        if (currentNode == null) {
            return;
        }

        for(int i = 0; i < indentLevel; i++) {
            out.print("\t");
            treeText.add("\t");
        }

        switch (currentNode.nodeType) {
            case MainNode:
                out.print("<MainNode>");
                treeText.add("<MainNode>");
                break;
            case IntNode:
                out.print("<IntNode>" + " " + currentNode.data);
                treeText.add("<IntNode>" + " " + currentNode.data);
                break;

            case BoolNode:
                out.print("<BoolNode>" + " " + currentNode.data);
                treeText.add("<BoolNode>" + " " + currentNode.data);
                break;

            case VarNode:
                out.print("<VariableNode>" + " " + currentNode.data);
                treeText.add("<VariableNode>" + " " + currentNode.data);
                break;

            case PlusNode:
                out.print("<PlusNode> +");
                treeText.add("<PlusNode> +");
                break;

            case DivNode:
                out.print("<DivNode> /");
                treeText.add("<DivNode> /");
                break;

            case BracketNode:
                out.print("<BracketNode> ()");
                treeText.add("<BracketNode> ()");
                break;

            case AndNode:
                out.print("<AndNode> &&");
                treeText.add("<AndNode> &&");
                break;

            case GreaterNode:
                out.print("<GreaterNode> >");
                treeText.add("<GreaterNode> >");
                break;

            case NotNode:
                out.print("<NotNode> !");
                treeText.add("<NotNode> !");
                break;

            case AssignmentNode:
                out.print("<AssignmentNode> =");
                treeText.add("<AssignmentNode> =");
                break;

            case BlockNode:
                out.print("<BlockNode> {}");
                treeText.add("<BlockNode> {}");
                break;

            case IfNode:
                out.print("<IfNode> if");
                treeText.add("<IfNode> if");
                break;

            case WhileNode:
                out.print("<WhileNode> while");
                treeText.add("<WhileNode> while");
                break;

            case SequenceNode:
                out.print("<SequenceNode>");
                treeText.add("<SequenceNode>");
                break;
        }
        treeText.add("\n");
        out.print("\n");

        for (Node child : currentNode.childNodes) {
            traverse(child, indentLevel + 1, out, treeText);
        }
    }
}
