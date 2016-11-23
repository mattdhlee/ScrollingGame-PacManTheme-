
public class PlayerPosition {

	String imageFileName;
	int col;
	int row;
	
	public PlayerPosition(String imageFileName, int row, int col) {
		this.imageFileName = imageFileName;
		this.col = col;
		this.row = row;
	}
	
	public String getImageFileName() {
		return this.imageFileName;
	}
	public int getCol() {
		return this.col;
	}
	public void setCol(int col) {
		this.col = col;
	}
	
	public int getRow() {
		return this.row;
	}
	
	public void setRow(int row) {
		this.row = row;
	}
	
	public Location getLoc() {
		return new Location(this.row, this.col);
	}
	
	
	
	
	
	
}
