package at.tyron.vintagecraft.World.Crafting;

import at.tyron.vintagecraft.WorldProperties.EnumWorkableTechnique;

public enum EnumMasonryTechnique implements EnumWorkableTechnique {

	SPLIT (1, "woodchop"),   // Hand Axe
	
	CARVE (2, "woodchisel"),   // Chisel
	
	DRILL (3, "handdrill"),   // Drill
	
	PLANE (4, "woodplane"),   // Hand Plane 
	
	SAW (5, "woodsaw"),     // Hand Saw
	
	JOIN (6, "woodhammer"),	 // Saw, Chisel, Hand Axe, Drill
	
	;
	
	
	String soundName;
	int id;
	static EnumMasonryTechnique[] TECHNIQUESBYID; 

	
	private EnumMasonryTechnique(int id, String soundName) {
		this.soundName = soundName;
		this.id = id;
	}

	@Override
	public int getId() {
		return id;
	}
	
	public String getSoundName() {
		return "vintagecraft:" + soundName;
	}
	
	public static EnumMasonryTechnique fromId(int id) {
		return TECHNIQUESBYID[id];
	}
	
	static {
		TECHNIQUESBYID = new EnumMasonryTechnique[values().length + 1];
		
		EnumMasonryTechnique[] techniques = values();
		
		for (int i = 0; i < values().length; i++) {
			TECHNIQUESBYID[techniques[i].getId()] = techniques[i];
		}
	}

}
