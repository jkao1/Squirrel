import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class Squirrel extends JFrame {

    public static String OS = System.getProperty("os.name").toLowerCase();
    public static final int ROWS = 30, COLS = 12;

    private static final int WINDOW_WIDTH = 960;
    private static final int WINDOW_HEIGHT = 720;
    private static final int MATH_LABEL_START_LEVEL = 3;
    private static final int SS_START_LEVEL = 1;
    private static int BORDER_GAP;

    private JFrame frame;
    private Container ss;

    private JMenuBar mb;
    private JMenu fileMenu, dataMenu;
    private JMenuItem fileMenu_New, dataMenu_Graph;
    private JLabel count, sum, mean;
    private JTextField cellID, textInput;
	
    private Cell selected;

    private ArrayList<Cell> cells;
    private ArrayList<Cell> highlighted;

    public Squirrel()
    {
	frame = new JFrame("Camel");

	// a few default settings
	this.setTitle("Spreadsheet");
	this.setLocation(100,100);
	this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	this.setResizable(false);

	ss = this.getContentPane();
	osDependentStyles(); // styles based on OS (specifically border gap)
	ss.setLayout(new GridLayout(0,COLS,BORDER_GAP,BORDER_GAP));

	createMenuBar();	
	initializeCells();

	this.pack();
    }
    
    public void osDependentStyles()
    {
	if (OS.indexOf("mac") >= 0) {
	    BORDER_GAP = -6;
	} else {
	    BORDER_GAP = 0;
	}
    }
    
    public void createMenuBar()
    {
	mb = new JMenuBar();

	// creates file menu
	fileMenu = new JMenu("File");
	fileMenu_New = new JMenuItem("New");		
	fileMenu.add(fileMenu_New);

	mb.add(fileMenu);

	// creates data analysis menu
	dataMenu = new JMenu("Data");
	dataMenu_Graph = new JMenuItem("Graph");
	dataMenu_Graph.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { // creates instance GraphInput g
		    GraphInput g = new GraphInput(highlighted);
		    g.setVisible(true);
		}
	    });
	dataMenu.add(dataMenu_Graph);

	mb.add(dataMenu);
	this.setJMenuBar(mb);
    }

    // draws cells
    public  void initializeCells()
    {	
	cells = new ArrayList<Cell>();
	highlighted = new ArrayList<Cell>();

	cellID = new JTextField();
	//ss.add(cellID);
	textInput = new JTextField();
	textInput.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    selected.setValue(textInput.getText()); // makes textInput's value equal to that of the selected cell
		}
	    });
	//ss.add(textInput);

	for (int i = 0; i < ROWS*COLS; i++) {

	    final Cell cell = new Cell(new JTextField(5),i);

	    // sets default select to the first enabled cell
	    if (i == COLS + 1) selected = cell;

	    cell.textField.addMouseListener(new MouseListener()
		{		    
		    public void mousePressed(MouseEvent e) {
			select(cell);
		    }		    
		    public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) { // double click will allow cell to be editable
			    cell.textField.setEditable(true);
			    cell.textField.getCaret().setVisible(true); // shows cursor
			}
		    }		    
		    public void mouseReleased(MouseEvent e) {
			Point p = e.getLocationOnScreen();
			highlightCells(cell.cellNum, releasedCellNum(p));
		    }		    
		    public void mouseEntered(MouseEvent e) {}
		    public void mouseExited(MouseEvent e) {}
		});
	    cell.textField.addKeyListener(new KeyListener()
		{
		    public void keyPressed(KeyEvent e)
		    {
			// up: arrow key up and shift-enter
			if (e.getKeyCode() == KeyEvent.VK_UP || e.getModifiers() == InputEvent.SHIFT_MASK && e.getKeyCode() == KeyEvent.VK_ENTER) {
			    select(cells.get(cell.cellNum - COLS));
			    updateTexts();
			}
			// right: arrow key right and tab
			else if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_TAB) {
			    select(cells.get(cell.cellNum + 1));
			    updateTexts();
			}
			// down: arrow key down and enter
			else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER) {
			    select(cells.get(cell.cellNum + COLS));
			    updateTexts();
			}
			// left: arrow key left and shift-tab
			else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getModifiers() == InputEvent.SHIFT_MASK && e.getKeyCode() == KeyEvent.VK_TAB) {
			    select(cells.get(cell.cellNum - 1));
			    updateTexts();
			}
			// catch independence
			else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {}
			// other characters: types in field
			else {
			    cell.textField.setEditable(true);
			    cell.textField.getCaret().setVisible(true);
			}
		    }
		    public void keyReleased(KeyEvent e) {}
		    public void keyTyped(KeyEvent e) {}
		});

	    ss.add(cell.textField);
	    cells.add(cell);
	}
	
	count = new JLabel("COUNT: ");
	ss.add(count);
	
	sum = new JLabel("SUM: ");
	ss.add(sum);
	
	mean = new JLabel("MEAN: ");
	ss.add(mean);	
    }

    public void select(Cell c) {
	selected.unSelect();
	for (Cell h : highlighted) h.deHighlight();
	highlighted.clear();

	if (c.isLabel && cells.get(c.cellNum+1).isLabel) { // if cell is alphabetical label
	    selected = cells.get(c.cellNum + COLS);
	    selected.select(); // selects cell directly underneath
	    highlightCells(selected.cellNum, c.cellNum+(ROWS)*COLS); // highlights column
	}
	else if (c.isLabel && cells.get(c.cellNum+COLS).isLabel) { // if cell is numerical label
	    selected = cells.get(c.cellNum + 1);
	    selected.select(); // selects cell directly to the right
	    highlightCells(c.cellNum+1, c.cellNum+COLS-1); // highlights row
	} else {
	    c.select();
	    selected = c;
	}
	updateTexts(); // updates COUNT/SUM/MEAN
    }

    public int releasedCellNum(Point p)
    {
	int tfWidth = (int) (cells.get(1).textField.getLocationOnScreen().getX() - cells.get(0).textField.getLocationOnScreen().getX()); // interval width for the while loop
	int tfHeight = (int) (cells.get(12).textField.getLocationOnScreen().getY() - cells.get(0).textField.getLocationOnScreen().getY()); // interval height for the while loop

        int i = 0;
	try {
	    // moves X by an interval of textfield's width until reaches Point p's X
	    while (cells.get(i).textField.getLocationOnScreen().getX() + tfWidth <= p.getX()) {
		i++;
	    }
	    // moves Y by an interval of textfield's height until reaches Point p's Y
	    while (cells.get(i).textField.getLocationOnScreen().getY() + tfHeight <= p.getY()) {
		i += COLS;
	    }
	} catch (IndexOutOfBoundsException e) {
	    // pointer went out of window
	}

	return i;
    }

    public boolean highlightCells(int x, int y) // to do: highlight labels
    {
	if (x == y) return true;
	
	// switches a % COLS and b % COLS to maintain top-left/bottom-right endpoints
	int a = Math.min(x,y);
	int b = Math.max(x,y);
	if (a % COLS > b % COLS) {
	    int sw = a % COLS - b % COLS;
	    a -= sw;
	    b += sw;
	}

	if (b > ROWS * COLS) return false; // off the screen
	
	for (int i = a; i <= b; i++) {
	    if (i % COLS >= a % COLS && i / COLS >= a / COLS && i % COLS <= b % COLS && i / COLS <= b / COLS) {
		cells.get(i).highlight(); 
		highlighted.add(cells.get(i));
	    }
	}

	updateTexts();
	return true;
    }

    // updates COUNT/SUM/MEAN, called in public boolean highlightCells(int x, int y)
    private void updateTexts()
    {
	cellID.setText(selected.toString());
	textInput.setText(selected.textField.getText());

	// math labels sum and mean count, respectively
	int s = 0; 
	int n = 0;

	if (highlighted.size() == 0) {
	    s = selected.getIntValue();
	    n = 1;
	    count.setText("COUNT: " + 1);
	} else {
	    for (Cell c : highlighted) {
		if (c.textField.getText().equals("")) continue;
		s += c.getIntValue(); // this and previous line check for String or empty values and ignores them for the mean count
		n++;
	    }		
	    count.setText("COUNT: " + highlighted.size());
	}
	sum.setText("SUM: " + s);
	mean.setText("MEAN: " + ((double) (s) / n));
    }

    public int[] toLineGraph()
    {

	LineGraph l = new LineGraph(int[])
	    }

    public int[][] toScatterPlot()
    {

    }

    public int[][] toHistogram() 
    {

    }
    

    public static void main(String[] args)
    {
	// just for testing
	if (args.length > 0 && args[0].equals("cmd")) {
	    Squirrel s = new Squirrel();
	    s.setVisible(true);
	    int[][] values = { {1, 9},
			       {0, 5, 10} };
	    s.writeHistogramTable(values,13);
	} else {
	    Squirrel s = new Squirrel();
	    s.setVisible(true);
	}
    }
    
}
