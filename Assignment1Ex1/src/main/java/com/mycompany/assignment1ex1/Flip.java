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

/**
 *
 * @author francesco
 */
public class Flip extends JButton implements Serializable, PropertyChangeListener{
    
    private int label1, label2; // Labels of the tiles to be flipped.
    
    /**
     * Constructs a new Flip button.
     * 
     * Initializes the button with default text and background color.
     */
    public Flip() {
        super();
        super.setText("FLIP");
        this.setBackground(Color.CYAN);
    }
    
    /**
     * Handles the flip action when the button is clicked.
     * 
     * This method is called when the flip button is clicked. It fires a vetoable change
     * event to notify listeners about the flip action. If the flip is allowed, it swaps
     * the labels of the two tiles.
     * 
     * @param label1 The label of the first tile to be flipped.
     * @param label2 The label of the second tile to be flipped.
     */
    public void clickedFlip(int label1, int label2){
        try{
            this.label1 = label1;
            this.label2 = label2;

            // Fire a vetoable change event to notify listeners about the flip action.
            this.fireVetoableChange("flip", this.label1, this.label2);

            // If is not vetoed, swap the labels of the two tiles.
            int tmp = this.label1;
            this.label1 = this.label2;
            this.label2 = tmp;

        }
        catch(PropertyVetoException e){ // If the flip is vetoed don't do nothing
            //System.out.println("Flip not possible!");
        }
    }

    /**
     * Handles property change events for the Flip button.
     * 
     * This method is called when a property change event is fired. It listens for restart events
     * to update the labels of the two tiles to be flipped with the new permutation provided in the event.
     * 
     * @param pce The property change event.
     */
    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        if(!"restart".equals(pce.getPropertyName())){
            System.out.println("ERROR: get a wrong property event (" + pce.getPropertyName() + ")");
            return;
        }
        
        // If it is a restart event, update the labels of the two tiles to be flipped with the new permutation.
        ArrayList<Integer> permutation = (ArrayList<Integer>) pce.getNewValue();
        this.label1 = permutation.get(0);
        this.label2 = permutation.get(1);
    }
    
}
