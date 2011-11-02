package csc4509;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;

public class Test {
	public static void main(String[] argv) {
	
		BufferedReader entree = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Entrez un message:");
		do {
			try {
				String tosend = entree.readLine();
			} catch ( EOFException  eof ) {
				System.out.println("Test");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while(true);
	}
}
