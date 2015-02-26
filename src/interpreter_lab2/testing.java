package interpreter_lab2;

import java.io.IOException;

public class testing extends interpreter {

	public testing(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException {
		if (args.length!=1){
			System.out.println("Enter correct number of arguments.");
			System.exit(0);
		}
		else {
			if (args[0].equals("1"))
				main_func("file.txt");
			else if (args[0].equals("2"))
				main_func("file1.txt");
			else if (args[0].equals("3"))
				main_func("file2.txt");
			else if (args[0].equals("4"))
				main_func("file3.txt");
			else if (args[0].equals("5"))
				main_func("file4.txt");
		}
	}
}
