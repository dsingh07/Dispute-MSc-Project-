package application;

import java.util.Arrays;
import java.util.List;

// Class to store argumentation framework
public class Framework {
	
	private String argument;
	private List<String> attackers;
	
	public Framework(){
		this.setArgument("");
		this.setAttackers(Arrays.asList());
	}
	
	public Framework(String name, List<String> attacks){
		this.setArgument(name);
		this.setAttackers(attacks);
	}

	public String getArgument() {
		return argument;
	}

	public void setArgument(String argument) {
		this.argument = argument;
	}

	public List<String> getAttackers() {
		return attackers;
	}

	public void setAttackers(List<String> attackers) {
		this.attackers = attackers;
	}

}
