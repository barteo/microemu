package javax.microedition.lcdui;

public class Spacer extends Item {
	private int minWidth, minHeight;
	
	public Spacer(int minWidth, int minHeight) {
		super(null);
		setMinimumSize(minWidth, minHeight);
	}

	public void setLabel(String label) {
		throw new IllegalStateException("Spacer items can't have labels");
	}

	public void addCommmand(Command cmd) {
		throw new IllegalStateException("Spacer items can't have commands");
	}
	
	public void setDefaultCommand(Command cmd) {
		throw new IllegalStateException("Spacer items can't have commands");
	}
	
	public void setMinimumSize(int minWidth, int minHeight) {
		if (minWidth < 0 || minHeight < 0)
			throw new IllegalArgumentException();
		
		this.minWidth = minWidth;
		this.minHeight = minHeight;
	}
	
	// Item methods
	int paint(Graphics g) {
		return 0;
	}

	public int getMinimumHeight() {
		return this.minHeight;
	}

	public int getMinimumWidth() {
		return this.minWidth;
	}
}
