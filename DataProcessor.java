import java.util.LinkedList;

public class DataProcessor {
    public static Tree Process(String code) {
        Tree tree = new Tree(new Node(null, Node.type.MainNode));
        Node currentNode = tree.root;

        String[] roughTokens = code.split("\n|;");

        LinkedList<String> processedTokens = new LinkedList<String>();
        LinkedList<Integer> indentSize = new LinkedList<Integer>();

        for (String codeToken : roughTokens) {
            if (codeToken.contains("int ") || codeToken.contains("bool ")) {
                continue;
            }
            int tabs = codeToken.split("\t",-1).length - 1;
            codeToken = codeToken.replaceAll("\t| |\n", "");

            // junk case
            if (codeToken.length() <= 1 && !codeToken.contains("}")) {
                continue;
            }

            processedTokens.add(codeToken);
            indentSize.add(tabs);
        }

        LinkedList<Node> parentStack = new LinkedList<Node>();

        for (int i = 0; i < processedTokens.size();i++) {
            String codeToken = processedTokens.get(i);

            if (processedTokens.get(i) != processedTokens.getLast()) {
                if ((indentSizeMatch(i, processedTokens, indentSize))
                //if (indentSizeMatch(i, processedTokens, indentSize)
                && !(processedTokens.get(i).length() <= 2 && processedTokens.get(i).contains("}"))) {
                    currentNode.childNodes.add(new Node(null, Node.type.SequenceNode));
                    currentNode = currentNode.childNodes.getLast();
                }
            }

            // while loop case

            if (codeToken.startsWith("while")) {
                currentNode.childNodes.add(new Node(null, Node.type.WhileNode));

                int statementStartIndex = codeToken.indexOf("while") + 5;
                int statementLastIndex = codeToken.lastIndexOf(')') + 1;

                String conditionalExpression = codeToken.substring(statementStartIndex, statementLastIndex);

                AttachConditionalStatement(currentNode.childNodes.getLast(), 
                    conditionalExpression);
                
            }

            if (codeToken.startsWith("if")) {
                currentNode.childNodes.add(new Node(null, Node.type.IfNode));

                int statementStartIndex = codeToken.indexOf("if") + 2;
                int statementLastIndex = codeToken.lastIndexOf(')') + 1;

                String conditionalExpression = codeToken.substring(statementStartIndex, statementLastIndex);

                AttachConditionalStatement(currentNode.childNodes.getLast(), 
                    conditionalExpression);
            }

            char[] tokenArray = codeToken.toCharArray();
            for (int j = 0; j < codeToken.length(); j++) {
                if (tokenArray[j] == '{') {
                    parentStack.addFirst(currentNode);
                    currentNode.childNodes.getLast().childNodes.add(new Node(null, Node.type.BlockNode));                    
                    currentNode = currentNode.childNodes.getLast().childNodes.getLast();
                }

                if (tokenArray[j] == '}') {
                    currentNode = parentStack.poll();
                }
            }


            // assignment case
            if (codeToken.contains("=")) { // left side of assignemnt can only be variable
                currentNode.childNodes.add(new Node(null, Node.type.AssignmentNode));
                currentNode.childNodes.getLast().childNodes.add(
                    new Node(codeToken.split("=")[0], Node.type.VarNode)
                );

                // boolean direct assignment case;
                if (codeToken.split("=")[1].equals("true") || codeToken.split("=")[1].equals("false")) {
                    currentNode.childNodes.getLast().childNodes.add(
                        new Node(codeToken.split("=")[1], Node.type.BoolNode)
                    );
                } else if (isNumeric(codeToken.split("=")[1])) {  // direct numeric assignemnt case
                    currentNode.childNodes.getLast().childNodes.add(
                        new Node(codeToken.split("=")[1], Node.type.IntNode)
                    );  // direct variable assignemnt case
                } else if (!(codeToken.split("=")[1]).contains("+") 
                && !(codeToken.split("=")[1]).contains("/") 
                && !(codeToken.split("=")[1]).contains(")")) {
                        currentNode.childNodes.getLast().childNodes.add(
                            new Node(codeToken.split("=")[1], Node.type.VarNode)
                    );
                } else {
                    // mathematical expression assignment case
                    AttachMathematicalExpression(currentNode.childNodes.getLast(), codeToken.split("=")[1]);
                }
            }
        }

        return tree;
    }

    private static boolean indentSizeMatch(int start, LinkedList<String> processedTokens, LinkedList<Integer> indentSize) {
        int counter = 0;
        for (int i = start; i < processedTokens.size(); i++) {
            if(!(processedTokens.get(i).length() <= 2 && processedTokens.get(i).contains("}")) 
            && indentSize.get(i) == indentSize.get(start) 
            && !(processedTokens.get(i).contains("else") && !processedTokens.get(i).contains("if"))
            && !processedTokens.get(start).contains("else")) {
                counter++;
            }
            if (processedTokens.get(i).contains("}") && indentSize.get(i) < indentSize.get(start)) {
                break;
            }

        }
        if (counter > 1) {
            return true;
        }

        return false;
	}

	public static boolean isNumeric(String s) {
        return s != null && s.matches("[-+]?\\d*\\.?\\d+");
    }

    private static void AttachMathematicalExpression(Node attachPoint, String expression) {
        LinkedList<Integer> initParanthesysStack = new LinkedList<Integer>();
        int exprPos = 0;

        Boolean paranthesizedExpr = false;

        for (char character : expression.toCharArray()) {
            if (character == '(')
                initParanthesysStack.addFirst(exprPos);
            if (character == ')' && exprPos != expression.length() - 1)
                initParanthesysStack.poll();

            if (character == ')' && exprPos == expression.length() - 1) {
                int closingParant = initParanthesysStack.poll();
                if (closingParant == 0)
                paranthesizedExpr = true;
            }
            exprPos++;
        }

        if (paranthesizedExpr) {
            attachPoint.childNodes.add(new Node(null, Node.type.BracketNode));
            AttachMathematicalExpression(attachPoint.childNodes.getLast(), expression.substring(1, expression.length() - 1));
        } else {
            LinkedList<Integer> stack = new LinkedList<Integer>();
            int charPos = 0;
            int lastOperandIndex = -1;
            for (char character : expression.toCharArray()) {
                if (character == '(')
                    stack.addFirst(0);
                if (character == ')')
                    stack.poll();
    
                if ((character == '+') && stack.isEmpty()) {
                    lastOperandIndex = charPos;
                }
                charPos++;
            }

            if (lastOperandIndex != -1) {
                String firstHalf = expression.substring(0, lastOperandIndex);
                String secondHalf = expression.substring(lastOperandIndex + 1);

                if (expression.charAt(lastOperandIndex) == '+') {
                    attachPoint.childNodes.add(new Node(null, Node.type.PlusNode));
                    attachPoint = attachPoint.childNodes.getLast();
                } else {
                    attachPoint.childNodes.add(new Node(null, Node.type.DivNode));
                    attachPoint = attachPoint.childNodes.getLast();
                }

                if (firstHalf.contains("+") || firstHalf.contains("/") 
                || (!firstHalf.contains("+") && !firstHalf.contains("/") 
                    && !secondHalf.contains("+") && !secondHalf.contains("/"))) {
                    // eval first half
                    if (firstHalf.equals("true") || firstHalf.equals("false")) {
                        attachPoint.childNodes.add(
                            new Node(firstHalf, Node.type.BoolNode)
                        );
                    } else if (isNumeric(firstHalf)) {  // direct numeric assignemnt case
                        attachPoint.childNodes.add(
                            new Node(firstHalf, Node.type.IntNode)
                        );  // direct variable assignemnt case
                    } else if (!firstHalf.contains("+") 
                    && !firstHalf.contains("/") 
                    && !firstHalf.contains(")")) {
                            attachPoint.childNodes.add(
                                new Node(firstHalf, Node.type.VarNode)
                        );
                    } else {
                        // mathematical expression assignment case
                        AttachMathematicalExpression(attachPoint, firstHalf);
                    }

                    // eval second half
                    if (secondHalf.equals("true") || secondHalf.equals("false")) {
                        attachPoint.childNodes.add(
                            new Node(secondHalf, Node.type.BoolNode)
                        );
                    } else if (isNumeric(secondHalf)) {  // direct numeric assignemnt case
                        attachPoint.childNodes.add(
                            new Node(secondHalf, Node.type.IntNode)
                        );  // direct variable assignemnt case
                    } else if (!secondHalf.contains("+") 
                    && !secondHalf.contains("/") 
                    && !secondHalf.contains(")")) {
                            attachPoint.childNodes.add(
                                new Node(secondHalf, Node.type.VarNode)
                        );
                    } else {
                        // mathematical expression assignment case
                        AttachMathematicalExpression(attachPoint, secondHalf);
                    }
                } else {
                    // eval second half
                    if (secondHalf.equals("true") || secondHalf.equals("false")) {
                        attachPoint.childNodes.add(
                            new Node(secondHalf, Node.type.BoolNode)
                        );
                    } else if (isNumeric(secondHalf)) {  // direct numeric assignemnt case
                        attachPoint.childNodes.add(
                            new Node(secondHalf, Node.type.IntNode)
                        );  // direct variable assignemnt case
                    } else if (!secondHalf.contains("+") 
                    && !secondHalf.contains("/") 
                    && !secondHalf.contains(")")) {
                            attachPoint.childNodes.add(
                                new Node(secondHalf, Node.type.VarNode)
                        );
                    } else {
                        // mathematical expression assignment case
                        AttachMathematicalExpression(attachPoint, secondHalf);
                    }

                    // eval first half
                    if (firstHalf.equals("true") || firstHalf.equals("false")) {
                        attachPoint.childNodes.add(
                            new Node(firstHalf, Node.type.BoolNode)
                        );
                    } else if (isNumeric(firstHalf)) {  // direct numeric assignemnt case
                        attachPoint.childNodes.add(
                            new Node(firstHalf, Node.type.IntNode)
                        );  // direct variable assignemnt case
                    } else if (!firstHalf.contains("+") 
                    && !firstHalf.contains("/") 
                    && !firstHalf.contains(")")) {
                            attachPoint.childNodes.add(
                                new Node(firstHalf, Node.type.VarNode)
                        );
                    } else {
                        // mathematical expression assignment case
                        AttachMathematicalExpression(attachPoint, firstHalf);
                    }
                }
            } else {
                stack = new LinkedList<Integer>();
                charPos = 0;
                lastOperandIndex = -1;
                for (char character : expression.toCharArray()) {
                    if (character == '(')
                        stack.addFirst(0);
                    if (character == ')')
                        stack.poll();
        
                    if ((character == '/') && stack.isEmpty()) {
                        lastOperandIndex = charPos;
                    }
                    charPos++;
                }

                String firstHalf = expression.substring(0, lastOperandIndex);
                String secondHalf = expression.substring(lastOperandIndex + 1);

                if (expression.charAt(lastOperandIndex) == '+') {
                    attachPoint.childNodes.add(new Node(null, Node.type.PlusNode));
                    attachPoint = attachPoint.childNodes.getLast();
                } else {
                    attachPoint.childNodes.add(new Node(null, Node.type.DivNode));
                    attachPoint = attachPoint.childNodes.getLast();
                }
                if (firstHalf.contains("+") || firstHalf.contains("/") 
                || (!firstHalf.contains("+") && !firstHalf.contains("/") 
                    && !secondHalf.contains("+") && !secondHalf.contains("/"))) {
                    // eval first half
                    if (firstHalf.equals("true") || firstHalf.equals("false")) {
                        attachPoint.childNodes.add(
                            new Node(firstHalf, Node.type.BoolNode)
                        );
                    } else if (isNumeric(firstHalf)) {  // direct numeric assignemnt case
                        attachPoint.childNodes.add(
                            new Node(firstHalf, Node.type.IntNode)
                        );  // direct variable assignemnt case
                    } else if (!firstHalf.contains("+") 
                    && !firstHalf.contains("/") 
                    && !firstHalf.contains(")")) {
                            attachPoint.childNodes.add(
                                new Node(firstHalf, Node.type.VarNode)
                        );
                    } else {
                        // mathematical expression assignment case
                        AttachMathematicalExpression(attachPoint, firstHalf);
                    }

                    // eval second half
                    if (secondHalf.equals("true") || secondHalf.equals("false")) {
                        attachPoint.childNodes.add(
                            new Node(secondHalf, Node.type.BoolNode)
                        );
                    } else if (isNumeric(secondHalf)) {  // direct numeric assignemnt case
                        attachPoint.childNodes.add(
                            new Node(secondHalf, Node.type.IntNode)
                        );  // direct variable assignemnt case
                    } else if (!secondHalf.contains("+") 
                    && !secondHalf.contains("/") 
                    && !secondHalf.contains(")")) {
                            attachPoint.childNodes.add(
                                new Node(secondHalf, Node.type.VarNode)
                        );
                    } else {
                        // mathematical expression assignment case
                        AttachMathematicalExpression(attachPoint, secondHalf);
                    }
                } else {
                    // eval second half
                    if (secondHalf.equals("true") || secondHalf.equals("false")) {
                        attachPoint.childNodes.add(
                            new Node(secondHalf, Node.type.BoolNode)
                        );
                    } else if (isNumeric(secondHalf)) {  // direct numeric assignemnt case
                        attachPoint.childNodes.add(
                            new Node(secondHalf, Node.type.IntNode)
                        );  // direct variable assignemnt case
                    } else if (!secondHalf.contains("+") 
                    && !secondHalf.contains("/") 
                    && !secondHalf.contains(")")) {
                            attachPoint.childNodes.add(
                                new Node(secondHalf, Node.type.VarNode)
                        );
                    } else {
                        // mathematical expression assignment case
                        AttachMathematicalExpression(attachPoint, secondHalf);
                    }

                    // eval first half
                    if (firstHalf.equals("true") || firstHalf.equals("false")) {
                        attachPoint.childNodes.add(
                            new Node(firstHalf, Node.type.BoolNode)
                        );
                    } else if (isNumeric(firstHalf)) {  // direct numeric assignemnt case
                        attachPoint.childNodes.add(
                            new Node(firstHalf, Node.type.IntNode)
                        );  // direct variable assignemnt case
                    } else if (!firstHalf.contains("+") 
                    && !firstHalf.contains("/") 
                    && !firstHalf.contains(")")) {
                            attachPoint.childNodes.add(
                                new Node(firstHalf, Node.type.VarNode)
                        );
                    } else {
                        // mathematical expression assignment case
                        AttachMathematicalExpression(attachPoint, firstHalf);
                    }
                }
            }
        }
    }

    private static void AttachConditionalStatement(Node attachPoint, String expression) {
        LinkedList<Integer> initParanthesysStack = new LinkedList<Integer>();
        int exprPos = 0;

        Boolean paranthesizedExpr = false;

        for (char character : expression.toCharArray()) {
            if (character == '(')
                initParanthesysStack.addFirst(0);
            if (character == ')' && exprPos != expression.length() - 1)
                initParanthesysStack.poll();

            if (character == ')' && exprPos == expression.length() - 1 && expression.toCharArray()[0] == '(') {
                initParanthesysStack.poll();
                paranthesizedExpr = true;
            }
            exprPos++;
        }

        if (paranthesizedExpr) {
            attachPoint.childNodes.add(new Node(null, Node.type.BracketNode));
            AttachConditionalStatement(attachPoint.childNodes.getLast(), expression.substring(1, expression.length() - 1));
            return;
        }

        if (expression.contains("&&")) {
            attachPoint.childNodes.add(new Node(null, Node.type.AndNode));
    
            String firstHalf = expression.substring(0, expression.lastIndexOf('&') - 1);
            String secondHalf = expression.substring(expression.lastIndexOf('&') + 1);
            if (firstHalf.contains("&&")) {
                AttachConditionalStatement(attachPoint.childNodes.getLast(), firstHalf);
                AttachConditionalStatement(attachPoint.childNodes.getLast(), secondHalf);
            } else if (secondHalf.contains("&&")) {
                AttachConditionalStatement(attachPoint.childNodes.getLast(), secondHalf);
                AttachConditionalStatement(attachPoint.childNodes.getLast(), firstHalf);
            } else {
                AttachConditionalStatement(attachPoint.childNodes.getLast(), firstHalf);
                AttachConditionalStatement(attachPoint.childNodes.getLast(), secondHalf);
            }
            return;
        }

        if (expression.startsWith("!")) {
            attachPoint.childNodes.add(new Node(null, Node.type.NotNode));
            
            expression = expression.substring(1);
            attachPoint = attachPoint.childNodes.getLast();
            
            AttachConditionalStatement(attachPoint, expression);
            return;
        }

        if (expression.equals("true")) {
            attachPoint.childNodes.add(new Node("true", Node.type.BoolNode));
            return;
        }

        if (expression.equals("false")) {
            attachPoint.childNodes.add(new Node("false", Node.type.BoolNode));
            return;
        }

        // in all other cases, it means we got to the core mathematical condition
        String firstHalf = expression.substring(0, expression.indexOf('>'));
        String secondHalf = expression.substring(expression.indexOf('>') + 1);

        attachPoint.childNodes.add(new Node(null, Node.type.GreaterNode));
        attachPoint = attachPoint.childNodes.getLast();

        if (firstHalf.contains("+") || firstHalf.contains("/")) {
            AttachMathematicalExpression(attachPoint, firstHalf);
        } else {
            if (firstHalf.equals("true") || firstHalf.equals("false")) {
                attachPoint.childNodes.add(new Node(firstHalf, Node.type.BoolNode));
            } else if (isNumeric(firstHalf)) {
                attachPoint.childNodes.add(new Node(firstHalf, Node.type.IntNode));
            } else {
                attachPoint.childNodes.add(new Node(firstHalf, Node.type.VarNode));
            }
        }
        if (secondHalf.contains("+") || secondHalf.contains("/")) {
            AttachMathematicalExpression(attachPoint, secondHalf);
        } else {
            if (secondHalf.equals("true") || secondHalf.equals("false")) {
                attachPoint.childNodes.add(new Node(secondHalf, Node.type.BoolNode));
            } else if (isNumeric(secondHalf)) {
                attachPoint.childNodes.add(new Node(secondHalf, Node.type.IntNode));
            } else {
                attachPoint.childNodes.add(new Node(secondHalf, Node.type.VarNode));
            }
        }
    }
}
