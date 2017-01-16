import javax.swing.*;
import java.awt.*;

public class Cell implements Comparable<Cell> {

    public static final Color LABEL_COLOR = Color.LIGHT_GRAY;

    private JTextField textField;
 
    private int cellNum;
    private boolean isLabel;
    private boolean isEditable;
    public Font bold;
    
    public Cell(JTextField t, int i) { 
	textField = t;
	cellNum = i;
	isLabel = true;
	isEditable = false;

	textField.setEditable(false);
	
	if (i / Squirrel.COLS == 0 && i % Squirrel.COLS == 0) {}
	else if (i / Squirrel.COLS == 0) setValue(String.valueOf((char) ('A'+i-1)));
	else if (i % Squirrel.COLS == 0) setValue(i / Squirrel.COLS);
        else isLabel = false;
	
	Font bold = new Font(textField.getFont().getName(), Font.BOLD, textField.getFont().getSize());
	if (isLabel) {
	    textField.setFont(bold);
	    textField.setBackground(LABEL_COLOR);
	}
	dehighlight(); // uniform coloration across OS's
    }

    public void select() {
	textField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
	textField.requestFocus();
    }

    public void unselect() {        
	textField.setBorder(UIManager.getBorder("TextField.border"));
	textField.setEditable(false);
	textField.getCaret().setVisible(false);
	isEditable = false;
    }

    public void highlight() {
        textField.setBackground(new Color(178,215,254));
    }

    public void dehighlight() {
	if (isLabel) {
	    textField.setBackground(LABEL_COLOR);
	} else {
	    textField.setBackground(Color.WHITE);
	}
    }

    public void decorate(String s) {
	if ( s.equals("tableHead")) {
	    textField.setFont( bold );
	}
    }

    public void makeEditable() {
	isEditable = true;
	textField.setEditable(true);
	textField.getCaret().setVisible(true);
    }

    public void clear() {
	if (!isLabel()) {
	    textField.setText("");
	    dehighlight();
	}
    }

    public boolean isEmpty() {
	return textField.getText().equals("");
    }

    public boolean isLabel() {
	return isLabel;
    }

    public boolean isEditable() {
	return isEditable;
    }

    public int getCellNum() {
	return cellNum;
    }

    public JTextField getTextField() {
	return textField;
    }
    
    public int getIntValue() {
	try {
	    String s = textField.getText();
	    if (s.indexOf(".") > 0) {
		s = s.substring( 0, s.indexOf("."));
	    }
	    return Integer.parseInt(textField.getText());
	} catch (NumberFormatException e) { return 0; }
    }

    public double getDoubleValue() {
	try {
	    String s = textField.getText();
	    return Double.parseDouble(textField.getText());
	} catch (NumberFormatException e) { return 0; }
    }

    public String getValue() {
	return textField.getText();
    }
    
    public void setValue(int v) {
	textField.setText(String.valueOf(v));
    }

    public void setValue(double v) {
	textField.setText(String.valueOf(v));
    }

    public void setValue(String v) {
	textField.setText(v);
    }

    public double getX() {
	return textField.getLocationOnScreen().getX();
    }

    public double getY() {
	return textField.getLocationOnScreen().getY();
    }

    public String toString() {
	return "" + (char) ('A' + cellNum % Squirrel.COLS - 1) + (cellNum / Squirrel.COLS);	
    }
    
    public int compareTo(Cell c) {
	return Integer.compare(getIntValue(), c.getIntValue());
    }
    
}
