/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Beans/Bean.java to edit this template
 */
package com.mycompany.assignment1ex1;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.Timer;

/**
 *
 * @author francesco
 */
public class EightTile extends JButton implements Serializable, PropertyChangeListener{

    private int position; // The position of the tile on the game board.
    private int label; // The number displayed on the tile.
    private final int holeLabel = 9; // The label of the empty hole tile.
    
    /**
     * Default constructor for EightTile.
     */
    public EightTile(){
        super();
    }
    
    /**
     * Constructs a new EightTile with specified position and label.
     * @param position The position of the tile on the game board.
     * @param label The number displayed on the tile.
     * @throws IllegalArgumentException if position or label is out of range.
     */
    public EightTile(int position, int label){
        if(position <= 0 && position >= 9 || label <= 0 && label >= 9){
            throw new IllegalArgumentException();
        }
        
        this.position = position;
        this.label = label;
        
        // Inizialize the apparence of the tile
        this.changeColor();
        this.changeText();
    }
    
    /**
     * 
     * @return The label of the tile.
     */
    public int getMyLabel(){
        return this.label;
    }
    
    /**
     * 
     * @return The position of the tile.
     */
    public int getMyPosition(){
        return this.position;
    }
    
    /**
     * Updates the label of the tile and its appearance.
     * @param newLabel The new label to set.
     */
    public void updateLabel(int newLabel){
        this.label = newLabel;
        
        //update the apparence of the tile
        changeColor();
        changeText();
    }
    
    /**
     * Change the color of the tile.
     */
    private void changeColor(){
        if(this.label == this.holeLabel){
            this.setBackground(Color.GRAY);
        }
        else{
            if(this.position == this.label){
                this.setBackground(Color.GREEN);
            }
            else{
                this.setBackground(Color.YELLOW);
            }
        }
    }
    
    /**
     * Change the text showed on the tile.
     */
    private void changeText(){
        if (this.label == this.holeLabel)
            this.setText(" ");
        else{
            this.setText(String.valueOf(this.label));
        }
    }
    
    /**
     * Flash a red button for half second.
     */
    private void flashes(){
        this.setBackground(Color.RED);
        new Timer(500, e -> changeColor()).start();
    }
    
    /**
    * Handles the action when the tile is clicked.
    * Attempts to swap the tile with the empty hole.
    * If successful, fires a vetoable change event.
    */
    public void clickedTile() {
        try{
            // Attempt to swap the tile with the empty hole
            this.fireVetoableChange("swap", String.valueOf(this.label), this.holeLabel);
            
            // If no vetoed, update the label
            this.updateLabel(this.holeLabel);
        }
        catch(PropertyVetoException e){ // If swap is impossible, flash the tile to indicate error
            this.flashes();
        }
    }

    /**
    * Handles property change events for the EightTile.
    * 
    * This method is called when a property change event is fired. It checks the property name
    * to determine the type of event and updates the tile's label accordingly. If the event indicates
    * a restart, the tile's label is updated with the new permutation provided in the event.
    * 
    * @param pce The property change event.
    */
    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        
        if(!"restart".equals(pce.getPropertyName())){
            System.out.println("ERROR: get a wrong property event (" + pce.getPropertyName() + ")");
            return;
        }
        
        // If the event indicates a restart, update the tile's label with the new permutation provided in the event.
        ArrayList<Integer> permutation = (ArrayList<Integer>) pce.getNewValue();
        this.updateLabel(permutation.get(this.position-1));
    }
    
}
