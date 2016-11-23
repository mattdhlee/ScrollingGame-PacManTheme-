import java.awt.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.*;


@SuppressWarnings("serial")
public class GameWindow extends JComponent implements KeyListener
{
  /* instance variables */
  private Cell[][] cells;             // info used to paint each cell of the game window
  private Color lineColor;            // inter-cell border color
  private Image background = null;    // background image (or null if none)

  private JFrame frame;               // Java window object

  private int lastKeyPressed;         // retains keyboard signal
  

  /* constructors */

  // no colors, only dimensions of window
  public GameWindow(int numRows, int numCols)
  {
    init(numRows, numCols, null, null);
  }
  
  // can be used to provided a background color (paint all the cells) 
  public GameWindow(int numRows, int numCols, Color bcolor)
  {
    init(numRows, numCols, bcolor, null);
  }
  
  // can be used to adjust the background color (cell) and line color (in between)
  public GameWindow(int numRows, int numCols, Color bcolor, Color lcolor)
  {
    init(numRows, numCols, bcolor, lcolor);
  }
  
  // can be used to provide a background image along with dimensions
  public GameWindow(String imageFileName, int numRows, int numCols)
  {
    loadBackground(imageFileName);
    
    init(numRows, numCols, null, null);
  }

  // common intitialization code for all constructors
  private void init(int numRows, int numCols, Color bcolor, Color lcolor)
  {
    lastKeyPressed = -1;
    
    //if no background color is provided, use the default BLACK 
    if (bcolor == null) bcolor = Color.BLACK;
    
    lineColor = lcolor;
    
    cells = new Cell[numRows][numCols];
    for (int row = 0; row < numRows; row++)
    {
      for (int col = 0; col < numCols; col++)
        cells[row][col] = new Cell(bcolor);
    }
    
    frame = new JFrame("Game Window");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.addKeyListener(this);
    
    int cellSize = Math.max(Math.min(500 / getNumRows(), 500 / getNumCols()), 1);    
    setPreferredSize(new Dimension(cellSize * numCols, cellSize * numRows));
    frame.getContentPane().add(this);
    
    frame.pack();
    frame.setVisible(true);
  }


  
  /* general information methods */

  // returns the number of rows
  public int getNumRows()
  {
    return cells.length;
  }
  
  // returns the number of columns
  public int getNumCols()
  {
    return cells[0].length;
  }

  public boolean isValid(Location loc)
  {
    int row = loc.getRow();
    int col = loc.getCol();
    return 0 <= row && row < getNumRows() && 0 <= col && col < getNumCols();
  }
  


  /* callback functions for KeyListener to respond to user keyboard presses */
  
  public void keyPressed(KeyEvent e)
  {
    lastKeyPressed = e.getKeyCode();
  }
  
  public void keyReleased(KeyEvent e)
  {
    //ignored
  }
  
  public void keyTyped(KeyEvent e)
  {
    //ignored
  }
  
  //returns -1 if no key pressed since last call.
  //otherwise returns the code for the last key pressed.
  public int checkLastKeyPressed()
  {
    int key = lastKeyPressed;
    lastKeyPressed = -1;
    return key;
  }


  /* graphics functions to paint the window */

  // used to calculate the actual pixel size of a cell based on window size
  private int getCellSize()
  {
    int cellWidth = getWidth() / getNumCols();
    int cellHeight = getHeight() / getNumRows();
    return Math.min(cellWidth, cellHeight);
  }

  // converts a Color object to a Java color object
  private static java.awt.Color toJavaColor(Color color)
  {
    return new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue());
  }
  
  // callback graphics function
  public void paintComponent(Graphics g)
  {
    if (background!=null)
      g.drawImage(background, 0, 0,frame.getWidth(), frame.getHeight(), null);
    
    for (int row = 0; row < getNumRows(); row++)
    {
      for (int col = 0; col < getNumCols(); col++)
      {
        Location loc = new Location(row, col);
        Cell cell = cells[loc.getRow()][loc.getCol()];
        
        int cellSize = getCellSize();
        
        int x = col * cellSize;
        int y = row * cellSize; 
        if (background==null) {
          Color color = cell.getColor();
          g.setColor(toJavaColor(color));
          
          g.fillRect(x, y, cellSize, cellSize);
        }
        
        String imageFileName = cell.getImageFileName();
        if (imageFileName != null)
        {
          URL url = getClass().getResource(imageFileName);
          if (url == null)
            System.out.println("File not found:  " + imageFileName);
          else
          {

            Image image = new ImageIcon(url).getImage();
            int width = image.getWidth(null);
            int height = image.getHeight(null);
            if (width > height)
            {
              int drawHeight = cellSize * height / width;
              g.drawImage(image, x, y + (cellSize - drawHeight) / 2, cellSize, drawHeight, null);
            }
            else
            {
              int drawWidth = cellSize * width / height;
              g.drawImage(image, x + (cellSize - drawWidth) / 2, y, drawWidth, cellSize, null);
            }
          }
        }
        
        if (lineColor != null)
        {
          g.setColor(toJavaColor(lineColor));
          g.drawRect(x, y, cellSize, cellSize);
        }
      }    
    }
  }

  // sleeps (used in animation loop)
  public void pause(int milliseconds)
  {
    try
    {
      Thread.sleep(milliseconds);
    }
    catch(Exception e)
    {
      //ignore
    }
  }


  /* methods to manipulate the window state */
  
  // change window title
  public void setTitle(String title)
  {
    frame.setTitle(title);
  }
  
  // change fill color of a cell
  public void setColor(Location loc, Color color)
  {
    if (!isValid(loc))
      throw new RuntimeException("cannot set color of invalid location " + loc + " to color " + color);
    cells[loc.getRow()][loc.getCol()].setColor(color);
    repaint();
  }
  
  // get fill color of a cell
  public Color getColor(Location loc)
  {
    if (!isValid(loc))
      throw new RuntimeException("cannot get color from invalid location " + loc);
    return cells[loc.getRow()][loc.getCol()].getColor();
  }

  // change image of a cell
  public void setImage(Location loc, String imageFileName)
  {
    if (!isValid(loc))
      throw new RuntimeException("cannot set image for invalid location " + loc + " to \"" + imageFileName + "\"");
    cells[loc.getRow()][loc.getCol()].setImageFileName(imageFileName);
    repaint();
  }
  
  // get image assigned to a cell
  public String getImage(Location loc)
  {
    if (!isValid(loc))
      throw new RuntimeException("cannot get image for invalid location " + loc);
    return cells[loc.getRow()][loc.getCol()].getImageFileName();
  }

  // change background image
  public void setBackground(String imageFileName)
  {
    loadBackground(imageFileName);
    repaint();
  }

  // helper function to load image file for background
  private void loadBackground(String imageFileName)
  {
    background = new ImageIcon(getClass().getResource(imageFileName)).getImage();
  }
  
  // change inter-cell border color
  public void setLineColor(Color color)
  {
    lineColor = color;
    repaint();
  } 
  

  /* other useful methods */

  // display a popup message
  public void showMessageDialog(String message)
  {
    JOptionPane.showMessageDialog(this, message);
  }
  
  // ask for input in a popup box
  public String showInputDialog(String message)
  {
    return JOptionPane.showInputDialog(this, message);
  }
}