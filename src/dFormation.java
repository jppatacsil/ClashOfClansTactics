import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 * This is the class for the defensive formation which extends a JButton
 * @author Jophi
 *
 */

public class dFormation extends JButton {
	
	int order;
	int type;
	ImageIcon questionMark = new ImageIcon(getClass().getResource("questionmark.jpg"));
	ImageIcon cannons = new ImageIcon(getClass().getResource("cannon.jpg")); //Image for turned on lights
	ImageIcon aTowers = new ImageIcon(getClass().getResource("aTowers.jpg")); //Image for turned off lights
	ImageIcon mortars = new ImageIcon(getClass().getResource("mortars.jpg")); //Image for turned on lights
	ImageIcon airD = new ImageIcon(getClass().getResource("airD.jpg")); //Image for turned off lights
	ImageIcon wTowers = new ImageIcon(getClass().getResource("wTowers.jpg")); //Image for turned on lights
	ImageIcon tTowers = new ImageIcon(getClass().getResource("tTowers.jpg")); //Image for turned off lights
	Buildings build;
	
	//Constructor class
		public dFormation(int order, int type){
			this.order = order;
			this.type = type;
			setD(type);
			instantiate(type);
		}
		
		//Set imageIcon of button
		public void setD(int type){
			
			switch(type){
			
			case 0:
				setIcon(questionMark);
				break;
			
			case 1: //cannons
				setIcon(cannons);
				break;
				
			case 2: //archer towers
				setIcon(aTowers);
				break;
				
			case 3: //mortars
				setIcon(mortars);
				break;
				
			case 4: //air defense
				setIcon(airD);
				break;
				
			case 5: //wizard towers
				setIcon(wTowers);
				break;
				
			case 6: //tesla towers
				setIcon(tTowers);
				break;
			
			}
			
		}
		
		//Method to instantiate the troop with respective hp and ap
		public void instantiate(int type){
			this.build = new Buildings(type);
		}

}
