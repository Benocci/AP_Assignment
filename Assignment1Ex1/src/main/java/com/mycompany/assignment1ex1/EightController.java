/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.assignment1ex1;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.JLabel;

/**
 *
 * @author francesco
 */
public class EightController extends JLabel implements Serializable, VetoableChangeListener, PropertyChangeListener{
    
    ArrayList<Integer> board = new ArrayList<Integer>(9); // Represents the arrangement of tiles on the board.
    private final int holeLabel = 9; // Label representing the empty space on the board.
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this); // Support for firing property change events.
    
    /**
     * Constructs a new EightController object.
     * 
     * Initializes the game board with tiles in their initial positions.
     */
    public EightController() {
        super();
        this.setText("START");
        
        for (int i = 1; i <= 9; i++) {
            board.add(i);
        }
    }
    
    /**
     * DEBUG function: prints the current state of the game board.
     */
    private void DEBUG_printALL(){
        int i = 0;
        System.out.println("---- PRINT ALL IN CONTROLLER ----");
        for(int et: board){
            System.out.println("EightTile[" + i + "] = pos: " + (i+1) + ", label:" + et);
            i++;
        }
        System.out.println("---- END PRINT ALL ----");
    }
    
    /**
     * Checks if a tile with the given label is adjacent to the hole.
     * 
     * @param label The label of the tile to check.
     * @return True if the tile is adjacent to the hole, false otherwise.
     */
    private boolean isAdjacent(int label){
        int tilePosition = this.board.indexOf(label);
        int holePosition = this.board.indexOf(this.holeLabel);
        
        // Calculate row and column indices of the tile and the hole.
        int tileRow = tilePosition / 3;
        int tileCol = tilePosition % 3;
        int holeRow = holePosition / 3;
        int holeCol = holePosition % 3;

        return Math.abs(tileRow - holeRow) + Math.abs(tileCol - holeCol) == 1;
    }
    
    /**
     * Checks if the old and new labels are the same, indicating an invalid tile swap.
     * 
     * @param oldLabel The old label of the tile.
     * @param newLabel The new label of the tile.
     * @return True if the old and new labels are the same, indicating an invalid swap, false otherwise.
     */
    private boolean isHole(int oldLabel, int newLabel){
        return oldLabel == newLabel;
    }
    
    /**
     * Updates the game board after a successful tile swap.
     * 
     * @param firstLabel The label of the first tile involved in the swap.
     * @param secondLabel The label of the second tile involved in the swap.
     */
    private void updateBoard(int firstLabel, int secondLabel){
        int pos1 = this.board.indexOf(firstLabel);
        int pos2 = this.board.indexOf(secondLabel);
        
        board.set(pos1, secondLabel);
        board.set(pos2, firstLabel);
    }

    /**
     * Handles vetoable change events related to tile swaps and flips.
     * 
     * This method is called when a vetoable change event is fired. It enforces game
     * rules for tile swaps and flips, such as ensuring the swap is valid and updating
     * the game board accordingly.
     * 
     * @param pce The vetoable change event.
     * @throws PropertyVetoException If the tile swap or flip is invalid.
     */
    @Override
    public void vetoableChange(PropertyChangeEvent pce) throws PropertyVetoException {
        // Check the type of property change event:
        if("swap".equals(pce.getPropertyName())){
            int oldLabel = Integer.parseInt((String) pce.getOldValue());
            int newLabel = (int) pce.getNewValue();
            
            // Ensure the tile swap is valid:
            if(this.isHole(oldLabel, newLabel) || !this.isAdjacent(oldLabel)){
                // If not valid, set text to "KO" and throw an exception.
                this.setText("KO");
                
                throw new PropertyVetoException("Invalid swap", pce);
            }
            else{
                // If valid, set text to "OK" and update the game board.
                this.setText("OK");
                
                int labelPosition = this.board.indexOf(newLabel);
            
                if(labelPosition < 0 || labelPosition >= 9){ // Check for out-of-bound error and throw exception if necessary.
                    System.out.println("ERROR: out of bound (element equal to " + labelPosition +")!");
                    throw new PropertyVetoException("Invalid position of the tile", pce);
                }
                
                // Notify listeners about the tile swap and update the game board.
                for(var listener : changeSupport.getPropertyChangeListeners()){
                    listener.propertyChange(new PropertyChangeEvent(changeSupport, pce.getPropertyName(), oldLabel, labelPosition));
                }
                
                this.updateBoard(oldLabel, newLabel);
            }
        }
        else if("flip".equals(pce.getPropertyName())){
            int holePosition = this.board.indexOf(this.holeLabel);
            int label1 = (int) pce.getOldValue();
            int label2 = (int) pce.getNewValue();
            
            // Ensure the flip is valid.
            if(holePosition == 8){
                // If valid, notify listeners about the flip and update the game board.
                for(var listener : changeSupport.getPropertyChangeListeners()){
                    listener.propertyChange(new PropertyChangeEvent(changeSupport, pce.getPropertyName(), label1, label2));
                }
                
                this.updateBoard(label1, label2);
            }
            else{
                // If not valid, throw an exception.
                throw new PropertyVetoException("Invalid flip", pce);
            }
        }
    }
    
    /**
     * Adds a property change listener to the controller.
     * 
     * @param l The property change listener to add.
     */
    public void controllerAddPropertyChangeListener(PropertyChangeListener l) {
        this.changeSupport.addPropertyChangeListener(l);
    }
    
    /**
     * Removes a property change listener from the controller.
     * 
     * @param l The property change listener to remove.
     */
    public void controllerRemovePropertyChangeListener(PropertyChangeListener l) {
        this.changeSupport.removePropertyChangeListener(l);
    }

    /**
     * Handles property change events related to game restarts.
     * 
     * This method is called when a property change event related to game restarts
     * is fired. It updates the game board with the new permutation provided in the event.
     * 
     * @param pce The property change event.
     */
    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        if(!"restart".equals(pce.getPropertyName())){
            System.out.println("ERROR: get a wrong property event (" + pce.getPropertyName() + ")");
            return;
        }
        // If it is a restart event, update the game board with the new permutation.
        this.board = (ArrayList<Integer>) pce.getNewValue();
    }
}
