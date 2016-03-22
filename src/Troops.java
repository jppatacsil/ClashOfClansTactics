import javax.swing.ImageIcon;


public class Troops {
	
	int hp;
	int ap;
	int type;
	
	//Constructor method
	public Troops(int type){
		this.type = type;
		initTroop(type); //Initialize troop
	}
	
	//Method for initializing troop object
	public void initTroop(int type){
		
		switch(type){
		
		case 0: //Unknown
			this.hp = 0;
			this.ap = 0;
			break;
		
		case 1: //Barbarians
			this.hp = 150;
			this.ap = 30;
			break;
			
		case 2: //Archers
			this.hp = 100;
			this.ap = 20;
			break;
			
		case 3: //Giants
			this.hp = 250;
			this.ap = 50;
			break;
			
		case 4: //Balloons
			this.hp = 100;
			this.ap = 50;
			break;
			
		case 5: //Wizards
			this.hp = 200;
			this.ap = 80;
			break;
			
		case 6: //Dragons
			this.hp = 200;
			this.ap = 100;
			break;
		
		}
		
	}

	public String showName(int type){
		String name = null;
		switch(type){
		
		case 0: //Not initialized yet
			name = "Unknown";
			break;
		
		case 1: //Barbarians
			name = "Barbarians";
			break;
			
		case 2: //Archers
			name = "Archers";
			break;
			
		case 3: //Giants
			name = "Giants";
			break;
			
		case 4: //Balloons
			name = "Balloons";
			break;
			
		case 5: //Wizards
			name = "Wizards";
			break;
			
		case 6: //Dragons
			name = "Dragons";
			break;
		
		}
		
		return name;
	}

}
