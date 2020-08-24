package net.patrykczarnik.commands;

public class CommandRunnerETest {

	public static void main(String[] args) {
		System.out.println("Creating command");
		Command command = CommandImpl.of("ls", "-l", "-a");
		System.out.println("Starting command " + command);
		try {
			CommandRunner.execute(command);
			System.out.println("Finished command");
		} catch (Exception e) {
			System.out.println("Execution error: " + e);
			e.printStackTrace();
		}
	}

}
