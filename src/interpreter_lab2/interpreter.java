package interpreter_lab2;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.StringTokenizer;

public class interpreter {
	String filename;
	@SuppressWarnings("rawtypes")
	static HashMap<String, variable> hm = new HashMap<String, variable>();  //making a hash map which will store the key i.e. variable name and the generic data type object
	public interpreter(String name){ //constructor
		this.filename = name;
	}
	@SuppressWarnings({ "deprecation", "resource", "rawtypes"})
	public static void main_func(String filename) throws IOException { //take input file name as the argument from testing class
		String buffer, token, value; //variables to be used
		interpreter in = new interpreter(filename);
		File file = new File(in.filename); //open the file (filing process below)
		FileInputStream reader = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(reader);
		DataInputStream dis = new DataInputStream(bis);
		while (dis.available()!=0){
			buffer = dis.readLine();
			StringTokenizer tokens = new StringTokenizer(buffer, " "); //make tokens of the read line
			buffer = tokens.nextToken();
			if (buffer.equalsIgnoreCase("Let")){ //check if declaration statement
					token = tokens.nextToken();
					if (in.isNumeric(token)){ //if variable name is not alphanumeric
						System.out.println("Synthax Error: Variable can only be Alphanumeric.");
						System.exit(0);
					}
					if (tokens.toString().contains("\"")){ //check if value is string
						tokens.nextToken();
						tokens.nextToken();
						value = tokens.nextToken();
						value.replaceAll("\"", "");
						variable<String> vr = new variable<String>(value, "String");
						hm.put(token, vr);
					}
					else {
						tokens.nextToken();
						value = in.expression_Solver(tokens); //solve if value is declared as some expression
						if (value.contains(".")){ //if float then make a float object
							variable<Float> vr = new variable<Float>(Float.parseFloat(value), "Float");
							hm.put(token, vr); //put in hash map
						}
						else { //otherwise make a integer object
							variable<Integer> vr = new variable<Integer>(Integer.parseInt(value), "Integer");
							hm.put(token, vr); //put in hash map
						}
					}
			}
			else if(buffer.equalsIgnoreCase("Print")){ //check if print statement
				if (tokens.countTokens()==1){ //if only variable then below
					token=tokens.nextToken();
					if (hm.containsKey(token)){ //if variable found in hash map then print the value otherwise error
						System.out.println(hm.get(token).value);
					}
					else {
						System.out.println("Variable Error: Variable not found.");
						System.exit(0);
					}
				}
				else {
					System.out.println(in.expression_Solver(tokens)); //if expression after the print statement then solve it and print the answer
				}
			}
			else { //if a simple expression then this portion
				variable vr; 
				tokens.nextToken();
				token = in.expression_Solver(tokens);//solve the expression
				if ((vr = hm.get(buffer)) != null){ //if variable to be assigned is in hash map
					if (vr.type=="Integer"){ //if the assigned variable is float
					variable<Integer> vr1 = new variable<Integer>(Integer.parseInt(token), vr.type);
					hm.remove(buffer); //delete it from hash map and put the new value returned
					hm.put(buffer, vr1);
					}
					else if (vr.type=="Float"){ //same for integer
						variable<Float> vr1 = new variable<Float>(Float.parseFloat(token), vr.type);
						hm.remove(buffer);
						hm.put(buffer, vr1);
					}
				}
				else { //if variable to be assigned not found
					System.out.println("Variable Error: Variable not found.");
					System.exit(0);
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private String expression_Solver(StringTokenizer tokens) {
		String token, term1, term2;
		int i;
		variable vr;
		LinkedList<String> ll = new LinkedList<String>(); //linked list to put each term and operator of expression
		if (tokens.countTokens()==1){
			token = tokens.nextToken();
			if (!isNumeric(token)){
				if ((vr=hm.get(token))!=null){
					return String.valueOf(vr.value); 
				}
				else{
					System.out.println("Variable Error: No Variable found.");
					System.exit(0);
				}
			}
			return token;
		}
		while (tokens.hasMoreElements()){
			token = tokens.nextToken(); //fill the linked list by making tokens of the expression
			ll.add(token);
		}
		while ((i=ll.indexOf("/"))!=-1){ //precedence for /
				term1 = ll.get(i-1); //get term before operator
				term2 = ll.get(i+1); //get term after operator
				operation (term1, term2, "/", ll, i); //apply operation (same for below checks)
		}
		while ((i=ll.indexOf("*"))!=-1){ //precedence for *
			term1 = ll.get(i-1); 
			term2 = ll.get(i+1); 
			operation (term1, term2, "*", ll, i);
		}
		while ((i=ll.indexOf("+"))!=-1){ //precedence for +
			term1 = ll.get(i-1); 
			term2 = ll.get(i+1); 
			operation (term1, term2, "+", ll, i);
		}
		while ((i=ll.indexOf("-"))!=-1){ //precedence for -
			term1 = ll.get(i-1); 
			term2 = ll.get(i+1); 
			operation (term1, term2, "+", ll, i);
		}
		return ll.get(0); //return the result
	}
	@SuppressWarnings({ "rawtypes" })
	private void operation (String term1, String term2, String op, LinkedList<String> ll, int i){
		if (term1.contains(".")&&term2.contains(".")){
			float t1 = Float.parseFloat(term1);
			float t2 = Float.parseFloat(term2);
			ll.remove(i);
			ll.remove(i);
			if (op.equals("/"))
				ll.add(i-1,String.valueOf(t1/t2));
			else if (op.equals("*"))
				ll.add(i-1,String.valueOf(t1*t2));
			else if (op.equals("+"))
				ll.add(i-1,String.valueOf(t1+t2));
			else if (op.equals("-"))
				ll.add(i-1,String.valueOf(t1-t2));
			ll.remove(i);
		}
		else if (term1.matches("[0-9]+")&&term2.contains(".")) {
			int t1 = Integer.parseInt(term1);
			float t2 = Float.parseFloat(term2);
			ll.remove(i);
			ll.remove(i);
			if (op.equals("/"))
				ll.add(i-1,String.valueOf(t1/t2));
			else if (op.equals("*"))
				ll.add(i-1,String.valueOf(t1*t2));
			else if (op.equals("+"))
				ll.add(i-1,String.valueOf(t1+t2));
			else if (op.equals("-"))
				ll.add(i-1,String.valueOf(t1-t2));
			ll.remove(i);
		}
		else if (term2.matches("[0-9]+")&&term1.contains(".")){
			int t2 = Integer.parseInt(term2);
			float t1 = Float.parseFloat(term1);
			ll.remove(i);
			ll.remove(i);
			if (op.equals("/"))
				ll.add(i-1,String.valueOf(t1/t2));
			else if (op.equals("*"))
				ll.add(i-1,String.valueOf(t1*t2));
			else if (op.equals("+"))
				ll.add(i-1,String.valueOf(t1+t2));
			else if (op.equals("-"))
				ll.add(i-1,String.valueOf(t1-t2));
			ll.remove(i);
		}
		else if (term2.matches("[0-9]+")&&term1.matches("[0-9]+")){
			int t2 = Integer.parseInt(term2);
			int t1 = Integer.parseInt(term1);
			ll.remove(i);
			ll.remove(i);
			if (op.equals("/"))
				ll.add(i-1,String.valueOf(t1/t2));
			else if (op.equals("*"))
				ll.add(i-1,String.valueOf(t1*t2));
			else if (op.equals("+"))
				ll.add(i-1,String.valueOf(t1+t2));
			else if (op.equals("-"))
				ll.add(i-1,String.valueOf(t1-t2));
			ll.remove(i);
		}
		else if ((!isNumeric(term2))&&term1.matches("[0-9]+")){
			variable vr1;
			if ((vr1=hm.get(term2)) != null){
				if (vr1.type.equalsIgnoreCase("Float")){
					Float t1 = (Float) vr1.value;
					int t2 = Integer.parseInt(term1);
					ll.remove(i);
					ll.remove(i);
					if (op.equals("/"))
						ll.add(i-1,String.valueOf(t1/t2));
					else if (op.equals("*"))
						ll.add(i-1,String.valueOf(t1*t2));
					else if (op.equals("+"))
						ll.add(i-1,String.valueOf(t1+t2));
					else if (op.equals("-"))
						ll.add(i-1,String.valueOf(t1-t2));
					ll.remove(i);
				}
				else if (vr1.type.equalsIgnoreCase("Integer")){
					int t1 = (Integer) vr1.value;
					int t2 = Integer.parseInt(term1);
					ll.remove(i);
					ll.remove(i);
					if (op.equals("/"))
						ll.add(i-1,String.valueOf(t1/t2));
					else if (op.equals("*"))
						ll.add(i-1,String.valueOf(t1*t2));
					else if (op.equals("+"))
						ll.add(i-1,String.valueOf(t1+t2));
					else if (op.equals("-"))
						ll.add(i-1,String.valueOf(t1-t2));
					ll.remove(i);
				}
			}
			else {
				System.out.println("Variable error: Variable not found.");
				System.exit(0);
			}
		}
		else if ((!isNumeric(term1))&&term2.matches("[0-9]+")){
			variable vr1;
			if ((vr1=hm.get(term1)) != null){
				if (vr1.type.equalsIgnoreCase("Float")){
					Float t1 = (Float) vr1.value;
					int t2 = Integer.parseInt(term2);
					ll.remove(i);
					ll.remove(i);
					if (op.equals("/"))
						ll.add(i-1,String.valueOf(t1/t2));
					else if (op.equals("*"))
						ll.add(i-1,String.valueOf(t1*t2));
					else if (op.equals("+"))
						ll.add(i-1,String.valueOf(t1+t2));
					else if (op.equals("-"))
						ll.add(i-1,String.valueOf(t1-t2));
					ll.remove(i);
				}
				else if (vr1.type.equalsIgnoreCase("Integer")){
					int t1 = (Integer) vr1.value;
					int t2 = Integer.parseInt(term2);
					ll.remove(i);
					ll.remove(i);
					if (op.equals("/"))
						ll.add(i-1,String.valueOf(t1/t2));
					else if (op.equals("*"))
						ll.add(i-1,String.valueOf(t1*t2));
					else if (op.equals("+"))
						ll.add(i-1,String.valueOf(t1+t2));
					else if (op.equals("-"))
						ll.add(i-1,String.valueOf(t1-t2));
					ll.remove(i);
				}
			}
			else {
				System.out.println("Variable error: Variable not found.");
				System.exit(0);
			}
		}
		else if ((!isNumeric(term1))&&(!isNumeric(term2))){
			variable vr1, vr2;
			if ((vr1=hm.get(term1)) != null && (vr2=hm.get(term2)) != null){
				if (vr1.type.equalsIgnoreCase("Float") && vr2.type.equalsIgnoreCase("Float")){
					Float t1 = (Float) vr1.value;
					Float t2 = (Float) vr2.value;
					ll.remove(i);
					ll.remove(i);
					if (op.equals("/"))
						ll.add(i-1,String.valueOf(t1/t2));
					else if (op.equals("*"))
						ll.add(i-1,String.valueOf(t1*t2));
					else if (op.equals("+"))
						ll.add(i-1,String.valueOf(t1+t2));
					else if (op.equals("-"))
						ll.add(i-1,String.valueOf(t1-t2));
					ll.remove(i);
				}
				else if (vr1.type.equalsIgnoreCase("Integer") && vr2.type.equalsIgnoreCase("Float")){
					int t1 = (Integer) vr1.value;
					Float t2 = (Float) vr2.value;
					ll.remove(i);
					ll.remove(i);
					if (op.equals("/"))
						ll.add(i-1,String.valueOf(t1/t2));
					else if (op.equals("*"))
						ll.add(i-1,String.valueOf(t1*t2));
					else if (op.equals("+"))
						ll.add(i-1,String.valueOf(t1+t2));
					else if (op.equals("-"))
						ll.add(i-1,String.valueOf(t1-t2));
					ll.remove(i);
				}
				else if (vr1.type.equalsIgnoreCase("Float") && vr2.type.equalsIgnoreCase("Integer")){
					Float t1 = (Float) vr1.value;
					int t2 = (Integer) vr2.value;
					ll.remove(i);
					ll.remove(i);
					if (op.equals("/"))
						ll.add(i-1,String.valueOf(t1/t2));
					else if (op.equals("*"))
						ll.add(i-1,String.valueOf(t1*t2));
					else if (op.equals("+"))
						ll.add(i-1,String.valueOf(t1+t2));
					else if (op.equals("-"))
						ll.add(i-1,String.valueOf(t1-t2));
					ll.remove(i);
				}
				else if (vr1.type.equalsIgnoreCase("Integer") && vr2.type.equalsIgnoreCase("Integer")){
					int t1 = (Integer) vr1.value;
					int t2 = (Integer) vr2.value;
					ll.remove(i);
					ll.remove(i);
					if (op.equals("/"))
						ll.add(i-1,String.valueOf(t1/t2));
					else if (op.equals("*"))
						ll.add(i-1,String.valueOf(t1*t2));
					else if (op.equals("+"))
						ll.add(i-1,String.valueOf(t1+t2));
					else if (op.equals("-"))
						ll.add(i-1,String.valueOf(t1-t2));
					ll.remove(i);
				}
			}
			else {
				System.out.println("Variable error: Variable not found.");
				System.exit(0);
			}
		}
	}
	@SuppressWarnings("unused")
	private boolean isNumeric(String str) {  //check if string is numeric
		  try {  
		    double num = Double.parseDouble(str);  //if number format exception received the return false
		  }  
		  catch(NumberFormatException nfe) {  
		    return false;  
		  }  
		  return true;  
		}
}
