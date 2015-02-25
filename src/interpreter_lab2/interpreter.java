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
	static HashMap<String, variable> hm = new HashMap<String, variable>();
	public interpreter(String name){
		this.filename = name;
	}
	@SuppressWarnings({ "deprecation", "resource", "rawtypes"})
	public static void main_func(String filename) throws IOException {
		String buffer, token, value;
		interpreter in = new interpreter("file.txt");
		File file = new File(in.filename);
		FileInputStream reader = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(reader);
		DataInputStream dis = new DataInputStream(bis);
		while (dis.available()!=0){
			buffer = dis.readLine();
			StringTokenizer tokens = new StringTokenizer(buffer, " ");
			buffer = tokens.nextToken();
			if (buffer.equalsIgnoreCase("Let")){
					token = tokens.nextToken();
					if (token.matches("[0-9]+")){
						System.out.println("Synthax Error: Variable can only be String.");
						System.exit(0);
					}
					if (tokens.toString().contains("\"")){
						tokens.nextToken();
						tokens.nextToken();
						value = tokens.nextToken();
						value.replaceAll("\"", "");
						variable<String> vr = new variable<String>(value, "String");
						hm.put(token, vr);
					}
					else {
						tokens.nextToken();
						value = in.expression_Solver(tokens);
						if (value.contains(".")){
							variable<Float> vr = new variable<Float>(Float.parseFloat(value), "Float");
							hm.put(token, vr);
						}
						else {
							variable<Integer> vr = new variable<Integer>(Integer.parseInt(value), "Integer");
							hm.put(token, vr);
						}
					}
			}
			else if(buffer.equalsIgnoreCase("Print")){
				if (tokens.countTokens()==1){
					token=tokens.nextToken();
					if (hm.containsKey(token)){
						System.out.println(hm.get(token).value);
					}
					else {
						System.out.println("Variable Error: Variable not found.");
						System.exit(0);
					}
				}
				else {
					System.out.println(in.expression_Solver(tokens));
				}
			}
			else {
				variable vr;
				tokens.nextToken();
				token = in.expression_Solver(tokens);
				if ((vr = hm.get(buffer)) != null){
					if (vr.type=="Integer"){
					variable<Integer> vr1 = new variable<Integer>(Integer.parseInt(token), vr.type);
					hm.remove(buffer);
					hm.put(buffer, vr1);
					}
					else if (vr.type=="Float"){
						variable<Float> vr1 = new variable<Float>(Float.parseFloat(token), vr.type);
						hm.remove(buffer);
						hm.put(buffer, vr1);
					}
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private String expression_Solver(StringTokenizer tokens) {
		String token, term1, term2;
		int i;
		variable vr;
		LinkedList<String> ll = new LinkedList<String>();
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
			token = tokens.nextToken();
			ll.add(token);
		}
		while ((i=ll.indexOf("/"))!=-1){
				term1 = ll.get(i-1);
				term2 = ll.get(i+1);
				operation (term1, term2, "/", ll, i);
		}
		while ((i=ll.indexOf("*"))!=-1){
			term1 = ll.get(i-1);
			term2 = ll.get(i+1);
			operation (term1, term2, "*", ll, i);
		}
		while ((i=ll.indexOf("+"))!=-1){
			term1 = ll.get(i-1);
			term2 = ll.get(i+1);
			operation (term1, term2, "+", ll, i);
		}
		while ((i=ll.indexOf("-"))!=-1){
			term1 = ll.get(i-1);
			term2 = ll.get(i+1);
			operation (term1, term2, "+", ll, i);
		}
		return ll.get(0);
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
	private boolean isNumeric(String str) {  
		  try {  
		    double num = Double.parseDouble(str);  
		  }  
		  catch(NumberFormatException nfe) {  
		    return false;  
		  }  
		  return true;  
		}
}
