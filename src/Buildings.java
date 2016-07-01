/**
 * This class is for the Defensive Buildings Object
 * @author Jophi
 *
 */
public class Buildings {
	
	//Class variables
	int hp;
	int ap;
	int type;
	
	//Constructor method
	public Buildings(int type){
		this.type = type;
		initD(type); //Initialize troop
	}
	
	//Method for initializing troop object
	public void initD(int type){
		
		switch(type){
		
		case 0: //Unknown
			this.hp = 0;
			this.ap = 0;
			break;
		
		case 1: //Cannons
			this.hp = 150;
			this.ap = 30;
			break;
			
		case 2: //Archer Towers
			this.hp = 100;
			this.ap = 20;
			break;
			
		case 3: //Mortars
			this.hp = 250;
			this.ap = 50;
			break;
			
		case 4: //Air defense
			this.hp = 100;
			this.ap = 50;
			break;
			
		case 5: //Wizard towers
			this.hp = 200;
			this.ap = 80;
			break;
			
		case 6: //Tesla towers
			this.hp = 200;
			this.ap = 100;
			break;
		
		}
		
	}
	
	//Method to get the type of building
	public String showName(int type){
		String name = null;
		switch(type){
		
		case 0: //Not yet initialized
			name = "Unknown";
			break;
		
		case 1: //Cannons
			name = "Cannons";
			break;
			
		case 2: //Archer Towers
			name = "ArcherTowers";
			break;
			
		case 3: //Mortars
			name = "Mortars";
			break;
			
		case 4: //Air Defense
			name = "AirDefense";
			break;
			
		case 5: //Wizard Towers
			name = "WizardTowers";
			break;
			
		case 6: //Tesla Towers
			name = "TeslaTowers";
			break;
		}
		return name;
	}
}
