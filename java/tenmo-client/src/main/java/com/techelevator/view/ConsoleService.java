package com.techelevator.view;


import com.techelevator.tenmo.model.Transfers;
import com.techelevator.tenmo.model.User;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Scanner;

public class ConsoleService {

	private PrintWriter out;
	private Scanner in;

	public ConsoleService(InputStream input, OutputStream output) {
		this.out = new PrintWriter(output, true);
		this.in = new Scanner(input);
	}

	public void menuPause() {
		System.out.println("Press 'ENTER' to continue.");
		in.nextLine();
	}

	public Object getChoiceFromOptions(Object[] options) {
		Object choice = null;
		while (choice == null) {
			displayMenuOptions(options);
			choice = getChoiceFromUserInput(options);
		}
		out.println();
		return choice;
	}

	private Object getChoiceFromUserInput(Object[] options) {
		Object choice = null;
		String userInput = in.nextLine();
		try {
			int selectedOption = Integer.valueOf(userInput);
			if (selectedOption > 0 && selectedOption <= options.length) {
				choice = options[selectedOption - 1];
			}
		} catch (NumberFormatException e) {
			// eat the exception, an error message will be displayed below since choice will be null
		}
		if (choice == null) {
			out.println(System.lineSeparator() + "*** " + userInput + " is not a valid option ***" + System.lineSeparator());
		}
		return choice;
	}

	private void displayMenuOptions(Object[] options) {
		out.println();
		for (int i = 0; i < options.length; i++) {
			int optionNum = i + 1;
			out.println(optionNum + ") " + options[i]);
		}
		out.print(System.lineSeparator() + "Please choose an option >>> ");
		out.flush();
	}

	public String getUserInput(String prompt) {
		out.print(prompt+": ");
		out.flush();
		return in.nextLine();
	}

	public long getUserInputReturnLong(String prompt) {
		out.print(prompt+": ");
		out.flush();
		String inputStr = in.nextLine();
		return Long.parseLong(inputStr);
	}

	public Integer getUserInputInteger(String prompt) {
		Integer result = null;
		do {
			out.print(prompt+": ");
			out.flush();
			String userInput = in.nextLine();
			try {
				result = Integer.parseInt(userInput);
			} catch(NumberFormatException e) {
				out.println(System.lineSeparator() + "*** " + userInput + " is not valid ***" + System.lineSeparator());
			}
		} while(result == null);
		return result;
	}

	public void printUsers(User[] users) {
		System.out.println("--------------------------------------------");
		System.out.println("ID\t\t\tUser Name");
		System.out.println("--------------------------------------------");
		for (User user : users) {
			System.out.println(user.getId() + "\t\t\t" + user.getUsername());
		}
	}

	public void printTransferHeaders() {
		System.out.println("----------------------------------------------------");
		System.out.println("ID\t\t\t\t\tFrom/To\t\t\t\t\tAmount");
		System.out.println("----------------------------------------------------");
	}

	public void printError(String errorMessage) {
		System.err.println(errorMessage);
	}


}
