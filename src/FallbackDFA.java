import java.util.*;

class Transition {
	String from;
	String Operator;
	String to;
	String output = "";

	@Override
	public String toString() {
		return "[from=" + from + ", Operator=" + Operator + ", to=" + to + ", output=" + output + "]\n";
	}

	public String getFrom() {
		return from;
	}

	public Transition(String from, String operator, String to, String output) {
		super();
		this.from = from;
		Operator = operator;
		this.to = to;
		this.output = output;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public String getOperator() {
		return Operator;
	}

	public String getTo() {
		return to;
	}
}

public class FallbackDFA {
	ArrayList<Transition> DFAstates;
	ArrayList<String> acceptStates;
	Stack<String> stack = new Stack<String>();

	public FallbackDFA(String input) {
		DFAstates = new ArrayList<Transition>();
		acceptStates = new ArrayList<String>();
		String[] hashInput = input.split("#");
		for (String splitString : hashInput[1].split(",")) {
			acceptStates.add(splitString);
		}
		for (String splitString : hashInput[0].split(";")) {
			String[] split = splitString.split(",");
			DFAstates.add(new Transition(split[0], "0", split[1], split[3]));
			DFAstates.add(new Transition(split[0], "1", split[2], split[3]));							
		}

	}

	public Transition nextState(String current, String operator) {
		for (Transition state : DFAstates) {
			if (state.getFrom().equals(current) && state.getOperator().equals(operator))
				return state;
		}
		return null;
	}

	public Stack<String> getStack(int start, int end, String input) {
		Stack<String> result = new Stack<String>();
		//generate new stack with R and L pointers
		input = input.substring(start, end);
		
		if (input.length() == 0)
			return result;
		//input to array of strings
		ArrayList<String> chars = new ArrayList<String>();
		for (char c : input.toCharArray()) {
			chars.add(c + "");
		}

		String current = DFAstates.get(0).getFrom();
		//add initial state
		result.push(getState(current, chars.get(0)).getFrom());
		//add first trans
		result.push(getState(current, chars.get(0)).getTo());

		Transition next = nextState(current, chars.get(0));
		//add all trans
		for (int i = 1; i < chars.size(); i++) {
			current = next.getTo();
			result.push(getState(current, chars.get(i)).getTo());
			next = nextState(current, chars.get(i));

		}
		return result;
	}

	public String run(String input) {
		stack = getStack(0, input.length(), input);
		String output = "";
		String peek = null;
		int start = 0;
		int end = input.length();
		String top = stack.peek();
		//until the start pointer R is not at the end of the tape
		while (start < input.length()) {
		
			//get the new state
			
			while (!stack.isEmpty()) {
				//System.out.println(stack);
				//System.out.println(" start " +start + " end " + end);
				peek = stack.pop();
				//if the popped state is an accept
			
				if (isAccept(peek)) {
					//System.out.println("added "+ getOutput(peek) +" ");

					if (!stack.isEmpty()) {
						//print the action
						output += getOutput(peek);
						//save the last state
						
						//move the R to the L pointer
						start = end;
						//reset L pointer
						end = input.length();
					}
					//reset and get new stack
					top=null;
					stack = getStack(start, end, input);
				}
				//if the popped state is not an accept
				else {
					//if we kept moving the L pointer until
					//it met the start pointer and the stack is empty
					if (end==start && stack.isEmpty()) {
						//print the saved state 
						//System.out.println("added "+ getOutput(before) +" ");
						output+=getOutput(top);
					}
					//else move L pointer until we find an accept
					else 
						end--;					
				}
				if (!stack.isEmpty() && top==null)
					top = stack.peek();
			}
			//System.out.println("----- peak ----- " + before);
			//move the R pointer
			start++;
		}

		// if ((!before.equals(current) && !isAccept(before)) || accepts == 0) {
		// return output + getOutput(current);
		//
		// }
		return output;



	}

	public String getOutput(String input) {
		for (Transition state : DFAstates) {
			if (state.getFrom().equals(input))
				return state.getOutput();
		}
		return "";
	}


	public boolean isAccept(String state) {
		for (String acc : acceptStates) {
			if (state.equals(acc))
				return true;

		}
		return false;
	}

	public Transition getState(String needle, String op) {
		for (Transition state : DFAstates) {
			if (state.getFrom().equals(needle) && state.getOperator().equals(op))
				return state;

		}
		return null;
	}

	public static void main(String[] args) {
		System.out.println("=======================");
		System.out.println("======PUBLIC TESTS=====");
		System.out.println("=======================");
		FallbackDFA FDFA1 = new FallbackDFA("0,1,0,00;1,1,2,01;2,1,3,10;3,1,0,11#3");
		System.out.println(FDFA1.run("0100"));
		System.out.println(FDFA1.run("10011"));
		System.out.println(FDFA1.run("1000011011"));
		System.out.println(FDFA1.run("011001"));
		System.out.println(FDFA1.run("1001111010"));
		System.out.println("=======================");
		FallbackDFA FDFA2 = new FallbackDFA("0,1,3,000;1,2,3,001;2,2,4,010;3,1,4,011;4,2,4,100#2,4");
		System.out.println(FDFA2.run("01110110"));
		System.out.println(FDFA2.run("0101001"));
		System.out.println(FDFA2.run("1010"));
		System.out.println(FDFA2.run("101011001"));
		System.out.println(FDFA2.run("11110"));
		System.out.println("=======================");
		FallbackDFA FDFA3 = new FallbackDFA("0,0,1,00;1,2,1,01;2,0,3,10;3,3,3,11#0,1,2");
		System.out.println(FDFA3.run("1011100"));
		System.out.println("=======================");
		System.out.println("======PRIVATE TESTS====");
		System.out.println("=======================");
		FallbackDFA FDFA4 = new FallbackDFA("0,1,0,A;1,1,2,B;2,1,3,C;3,4,3,D;4,4,4,E#1,3");
		System.out.println(FDFA4.run("01011010"));
		System.out.println(FDFA4.run("10001"));
		System.out.println(FDFA4.run("1010"));
		System.out.println(FDFA4.run("0111"));
		System.out.println(FDFA4.run("111"));
		System.out.println("=======================");
		FallbackDFA FDFA5 = new FallbackDFA("0,0,1,A;1,3,2,B;2,3,2,C;3,4,3,D;4,3,4,E#2,4");
		System.out.println(FDFA5.run("111"));
		System.out.println(FDFA5.run("1010000"));
		System.out.println(FDFA5.run("10100001"));
		System.out.println(FDFA5.run("000110"));
		System.out.println(FDFA5.run("001"));
		System.out.println("=======================");
		FallbackDFA FDFA6 = new FallbackDFA("0,1,2,A;1,2,4,B;2,4,3,C;3,4,4,D;4,5,4,E;5,5,4,F#3,5");
		System.out.println(FDFA6.run("1010"));
		System.out.println(FDFA6.run("00101"));
		System.out.println(FDFA6.run("1"));
		System.out.println(FDFA6.run("001101"));
		System.out.println(FDFA6.run("0010"));
		System.out.println("=======================");
		FallbackDFA FDFA7 = new FallbackDFA("0,1,3,A;1,0,2,B;2,3,4,C;3,4,1,D;4,4,2,E#2,4");
		System.out.println(FDFA7.run("0010"));
		System.out.println(FDFA7.run("00010"));
		System.out.println(FDFA7.run("101"));
		System.out.println(FDFA7.run("00"));
		System.out.println(FDFA7.run("10101"));

	}

}
