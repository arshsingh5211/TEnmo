package com.techelevator.view;


import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpEntity;

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

	public String promptForUser() {
		return promptForUser(null);
	}

	public String promptForUser(User user) {
		String userString;
		System.out.println("--------------------------------------------");
		System.out.println("Enter ID of user you are sending TE bucks to (0 to cancel):");
		if (user != null) System.out.println(user);
		System.out.println("--------------------------------------------");
		System.out.println("");
		userString = in.nextLine();
		if (user != null) userString = user.getId() + ", " + userString;

		return userString;
	}

	public BigDecimal promptForAmount() {
		BigDecimal amount = new BigDecimal("0.00");
		System.out.println("--------------------------------------------");
		System.out.println("Enter amount: ");
		System.out.println("--------------------------------------------");
		System.out.println("");

		try {
			amount = in.nextBigDecimal();
		} catch (NumberFormatException e) {
			System.out.println("Sorry, that is not a valid amount!");
		}
		return amount;
	}


	public String promptForTransfer() {
		return promptForTransfer(null);
	}

	public String promptForTransfer(Transfer transfer) {
		String transferString;
		System.out.println("--------------------------------------------");
		System.out.println("Enter amount: ");
		if (transfer != null) {
			System.out.println(transfer.toString());
		} else {
			System.out.println("Example: JoeShmoe, 25.00");
		}
		System.out.println("--------------------------------------------");
		System.out.println("");

		transferString = in.nextLine();
		if (transfer != null) {
			transferString = transfer.getTransferId() + "," + transferString;
		}
		return transferString;
	}

	public void printError(String errorMessage) {
		System.err.println(errorMessage);
	}


}
