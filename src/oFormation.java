import javax.swing.ImageIcon;
import javax.swing.JButton;

/*
 * This class is for the offensive formation
 */

public class oFormation extends JButton {
	
	//Class fields
	int order;
	int type;
	ImageIcon questionMark = new ImageIcon(getClass().getResource("questionmark.jpg"));
	ImageIcon barbs = new ImageIcon(getClass().getResource("barbs.jpg")); //Image for turned on lights
	ImageIcon archers = new ImageIcon(getClass().getResource("archers.jpg")); //Image for turned off lights
	ImageIcon giants = new ImageIcon(getClass().getResource("giants.jpg")); //Image for turned on lights
	ImageIcon wizards = new ImageIcon(getClass().getResource("wizards.jpg")); //Image for turned off lights
	ImageIcon balloons = new ImageIcon(getClass().getResource("balloon.jpg")); //Image for turned on lights
	ImageIcon dragons = new ImageIcon(getClass().getResource("dragons.jpg")); //Image for turned off lights
	Troops troop;
	
	//Constructor class
	public oFormation(int order, int type){
		this.order = order;
		this.type = type;
		setTroop(type);
		instantiate(type);
	}
	
	//Set imageIcon of button
	public void setTroop(int type){
		
		switch(type){
		
		case 0:
			setIcon(questionMark);
			break;
		
		case 1: //Barbarians
			setIcon(barbs);
			break;
			
		case 2: //Archers
			setIcon(archers);
			break;
			
		case 3: //Giants
			setIcon(giants);
			break;
			
		case 4: //Balloons
			setIcon(balloons);
			break;
			
		case 5: //Wizards
			setIcon(wizards);
			break;
			
		case 6: //Dragons
			setIcon(dragons);
			break;
		
		}
		
	}
	
	//Method to instantiate the troop with respective hp and ap
	public void instantiate(int type){
		this.troop = new Troops(type);
	}
	
	

}
