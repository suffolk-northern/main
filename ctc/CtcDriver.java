/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myctc;

import java.util.Scanner;

/**
 *
 * @author missm
 */
public class CtcDriver {

	public static void main(String[] args) {
		MyCtc ctc = new MyCtc();
		ctc.showUI();
		Scanner scan = new Scanner(System.in);
		String str = scan.nextLine();
		while (true) {
			ctc.readIn(str);
			str = scan.nextLine();
		}
	}

}
