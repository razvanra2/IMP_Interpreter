import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.BufferedWriter;

public class Node {
    public String data;
    public type nodeType;
    public LinkedList<Node> childNodes;

    public Node(String newData, type newNodeType) {
        this.data = newData;
        this.nodeType = newNodeType;

        childNodes = new LinkedList<Node>();
    }

    // i know, i know, this is not a visitor pattern
    // but it's close enough and it'll have to do.
    public Object accept(TreeVisitor visitor, LinkedList<Variable> vars, PrintWriter out, String[] lines) {
        // MAIN NODE
        if (this.nodeType.equals(type.MainNode)) {
            return this.childNodes.getFirst().accept(visitor, vars, out, lines);
        } else
        //////////////////////////////////////////////

        // SEQUENCE NODE
        if (this.nodeType.equals(type.SequenceNode)) {
            this.childNodes.getFirst().accept(visitor, vars, out, lines);
            this.childNodes.getLast().accept(visitor, vars, out, lines);
        } else 
        /////////////////////////////////////////////

        // BRACKET NODE
        if (this.nodeType.equals(type.BracketNode)) {
            return this.childNodes.getFirst().accept(visitor, vars, out, lines);
        } else
        /////////////////////////////////////////////

        // INT NODE
        if (this.nodeType.equals(type.IntNode)) {
            return Integer.valueOf(data);
        } else 
        /////////////////////////////////////////////

        // BOOL NODE
        if (this.nodeType.equals(type.BoolNode)) {
            return Boolean.valueOf(data);
        } else
        /////////////////////////////////////////////

        // VAR NODE
        if (this.nodeType.equals(type.VarNode)) {
            for (Variable var : vars) {
                if (var.name.equals(this.data)) {
                    return var;
                }
            }

            out.println("UnassignedVar " + findVarLine(lines, this.data));
            out.close();
            System.exit(0);
        } else
        /////////////////////////////////////////////

        // ASSIGNMENT NODE
        if (this.nodeType.equals(type.AssignmentNode)) {
            Variable assignedToVar = 
                (Variable) this.childNodes.getFirst().accept(visitor, vars, out, lines);

            if (this.childNodes.getLast().accept(visitor, vars, out, lines) instanceof Variable) {
                assignedToVar.value = 
                ((Variable) this.childNodes.getLast().accept(visitor, vars, out, lines)).value;
            } else {
                assignedToVar.value = 
                    this.childNodes.getLast().accept(visitor, vars, out, lines);
            }
        } else
        /////////////////////////////////////////////

        // PLUS NODE
        if (this.nodeType.equals(type.PlusNode)) {
            Integer firstValue;
            Integer secondValue;
            if (this.childNodes.getFirst().nodeType.equals(type.VarNode)) {
                firstValue = (Integer)
                    ((Variable) this.childNodes.getFirst().accept(visitor, vars, out, lines)).value;
            } else {
                firstValue = (Integer) this.childNodes.getFirst().accept(visitor, vars, out, lines);
            }

            if (this.childNodes.getLast().nodeType.equals(type.VarNode)) {
                secondValue = (Integer)
                    ((Variable) this.childNodes.getLast().accept(visitor, vars, out, lines)).value;
            } else {
                secondValue = (Integer) this.childNodes.getLast().accept(visitor, vars, out, lines);
            }

            int line = -1;
            if (firstValue == null) {
                line = findNullLine(vars, lines);
            }
            if (secondValue == null) {
                line = findNullLine(vars, lines);
            }
            if (line != -1) {
                out.println("UnassignedVar " + line);
                out.close();
                System.exit(0);
            }

            return firstValue + secondValue;
        } else 
        /////////////////////////////////////////////

        // DIV NODE
        if (this.nodeType.equals(type.DivNode)) {
            Integer firstValue;
            Integer secondValue;
            if (this.childNodes.getFirst().nodeType.equals(type.VarNode)) {
                firstValue = (Integer)
                    ((Variable) this.childNodes.getFirst().accept(visitor, vars, out, lines)).value;
            } else {
                firstValue = (Integer) this.childNodes.getFirst().accept(visitor, vars, out, lines);
            }

            if (this.childNodes.getLast().nodeType.equals(type.VarNode)) {
                secondValue = (Integer)
                    ((Variable) this.childNodes.getLast().accept(visitor, vars, out, lines)).value;
            } else {
                secondValue = (Integer) this.childNodes.getLast().accept(visitor, vars, out, lines);
            }

            int line = -1;
            if (firstValue == null) {
                line = findNullLine(vars, lines);
            }
            if (secondValue == null) {
                line = findNullLine(vars, lines);
            }
            if (line != -1) {
                out.println("UnassignedVar " + line);
                out.close();
                System.exit(0);
            }

            if (secondValue != 0) {
                return firstValue / secondValue;
            } else {
                out.println("DivideByZero " + getLineOfDiv(vars, lines));
                out.close();
                System.exit(0);
            }
        } else
        /////////////////////////////////////////////

        // BLOCK NODE
        if (this.nodeType.equals(type.BlockNode)) {
            for (Node child : this.childNodes) {
                child.accept(visitor, vars, out, lines);
            }
        } else
        /////////////////////////////////////////////

        // NOT NODE
        if (this.nodeType.equals(type.NotNode)) {
            return ! ((Boolean) this.childNodes.getFirst().accept(visitor, vars, out, lines));
        } else
        /////////////////////////////////////////////

        // AND NODE
        if (this.nodeType.equals(type.AndNode)) {
            return ((Boolean) this.childNodes.getFirst().accept(visitor, vars, out, lines)) &&
                ((Boolean) this.childNodes.getLast().accept(visitor, vars, out, lines));
        }
        /////////////////////////////////////////////

        // GREATER NODE
        if (this.nodeType.equals(type.GreaterNode)) {
            Integer firstValue;
            Integer secondValue;
            if (this.childNodes.getFirst().nodeType.equals(type.VarNode)) {
                firstValue = (Integer)
                    ((Variable) this.childNodes.getFirst().accept(visitor, vars, out, lines)).value;
            } else {
                firstValue = (Integer) this.childNodes.getFirst().accept(visitor, vars, out, lines);
            }

            if (this.childNodes.getLast().nodeType.equals(type.VarNode)) {
                secondValue = (Integer)
                    ((Variable) this.childNodes.getLast().accept(visitor, vars, out, lines)).value;
            } else {
                secondValue = (Integer) this.childNodes.getLast().accept(visitor, vars, out, lines);
            }

            return firstValue > secondValue;
        } else
        /////////////////////////////////////////////        

        // IF NODE
        if (this.nodeType.equals(type.IfNode)) {
            if ((Boolean) this.childNodes.get(0).accept(visitor, vars, out, lines)) {
                this.childNodes.get(1).accept(visitor, vars, out, lines);
            } else {
                if (this.childNodes.size() > 2) {
                    this.childNodes.get(2).accept(visitor, vars, out, lines);
                }
            }
        } else
        /////////////////////////////////////////////

        // WHILE NODE
        if (this.nodeType.equals(type.WhileNode)) {
            while ((Boolean) this.childNodes.get(0).accept(visitor, vars, out, lines)) {
                this.childNodes.get(1).accept(visitor, vars, out, lines);
            }
        }
        /////////////////////////////////////////////

        // generic case
        return new Object();
    }
    private int findNullLine(LinkedList<Variable> vars, String[] lines) {
        Variable nullVar = new Variable(Variable.varType.boolvar, "dummy");
        for (Variable var : vars) {
            if (var.value == null) {
                nullVar = var;
                break;
            }
        }

        for (int i = 1; i <= lines.length; i++) {
            if (lines[i].contains(nullVar.name)) {
                return i + 1;
            }
        }
        return -1;
    }
    private int findVarLine(String[] lines, String data) {
        int cnt = 1;

        for (String line : lines) {
            if (line.contains(data)) {
                return cnt;
            }
            cnt++;
        }
        return -1;
    }

    private int getLineOfDiv(LinkedList<Variable> vars, String[] lines) {
        int cnt = 1;
        String var = "0";
        for (Variable variable : vars) {
            if (variable.value instanceof Integer && ((Integer) variable.value).equals(0)) {
                var = variable.name;
            }
        }

        for (String line : lines) {
            if (line.contains("/") && line.contains(var) 
                && line.lastIndexOf(var) > line.lastIndexOf("/")) {
                return cnt;
            }
            cnt++;
        }
        return -1;
    }

    public enum type {
        MainNode,  // done
        IntNode,  // done
        BoolNode,  // done
        VarNode,  // done
        PlusNode,  // done
        DivNode,  // done
        BracketNode,  // done
        AndNode,  // done
        GreaterNode,  // done
        NotNode,  // done
        AssignmentNode,  // done
        BlockNode,  // done
        IfNode,  // done
        WhileNode,  // done
        SequenceNode  // done, ignore these comments, used them to keep track of my progress
    }
}
