/*
 * InsertRow.java
 *
 * DBMS Implementation
 */

import com.sleepycat.je.*;
import java.io.*;
import java.util.Arrays;

/**
 * A class that represents a row that will be inserted in a table in a
 * relational database.
 *
 * This class contains the code used to marshall the values of the
 * individual columns to a single key-value pair.
 */
public class InsertRow {
    private Table table;           // the table in which the row will be inserted
    private Object[] columnVals;   // the column values to be inserted
    private RowOutput keyBuffer;   // buffer for the marshalled row's key
    private RowOutput valueBuffer; // buffer for the marshalled row's value
    private int[] offsets;         // offsets for header of marshalled row's value
    
    /** Constants for special offsets **/
    /** The field with this offset is a primary key. */
    public static final int IS_PKEY = -1;
    
    /** The field with this offset has a null value. */
    public static final int IS_NULL = -2;
    
    /**
     * Constructs an InsertRow object for a row containing the specified
     * values that is to be inserted in the specified table.
     *
     * @param  table the table
     * @param  values  the column values for the row to be inserted
     */
    public InsertRow(Table table, Object[] values) {
        this.table = table;
        this.columnVals = values;
        this.keyBuffer = new RowOutput();
        this.valueBuffer = new RowOutput();
        
        // Note that we need one more offset than value,
        // so that we can store the offset of the end of the record.
        this.offsets = new int[values.length + 1];
    }
    
    /**
     * Takes the collection of values for this InsertRow
     * and marshalls them into a key/value pair.
     * 
     * (Note: We include a throws clause because this method will use 
     * methods like writeInt() that the RowOutput class inherits from 
     * DataOutputStream, and those methods could in theory throw that 
     * exception. In reality, an IOException should *not* occur in the
     * context of our RowOutput class.)
     */
    public void marshall() throws IOException {

        populateOffsets(offsets);

        keyBuffer.writeBytes();

        /*
         * PS 2: Implement this method. 
         * 
         * Feel free to also add one or more private helper methods
         * to do some of the work (e.g., to fill in the offsets array
         * with the appropriate offsets).
         */
    }

    private Column findPrimaryKey(){
        Column primaryKeyColumn = null;
        for(int i = 0; i < table.numColumns(); i++){
            if(table.getColumn(i).isPrimaryKey()){
                primaryKeyColumn = primaryKeyColumn;
            }
        }
        return primaryKeyColumn;
    }

    private void populateOffsets(int[] offsets) {

        int firstOffset = (offsets.length) * 2;
        offsets[0] = populateFirstOffset(firstOffset);

        for(int i = 1; i < offsets.length; i++){

            Column column = table.getColumn(i);
            int previous = i - 1;

            if(!column.isNotNull()){
                offsets[i] = IS_NULL;
                continue;
            }

            if(column.isPrimaryKey()){
                offsets[i] = IS_PKEY;
                continue;
            }

            if(precededByPrimaryKeyOrNull(previous, column, i)){
                precededByPrimaryKeyOrNull(previous, column, i);
                continue;
            } else {
                if(column.getValType() == 3){
                    offsets[i] = offsets[i - 1] + column.getValue().toString().length();
                } else {
                    offsets[i] = offsets[i - 1] + column.getLength();
                }
            }
        }
    }

    private boolean precededByPrimaryKeyOrNull(int previous, Column column, int i) {
        int firstOffset = (offsets.length) * 2;
        boolean isPrecededByPKorNull = false;

        while(offsets[previous] == IS_PKEY || offsets[previous] == IS_NULL){
            if(previous == 0){
                offsets[i] = firstOffset;
            }
            isPrecededByPKorNull = true;
            previous--;
        }

        if(previous > 0 && (previous != i - 1) && (offsets[previous + 1] == IS_PKEY || offsets[previous + 1] == IS_NULL)){
            if(column.getValType() == 3){
                offsets[i] = offsets[previous] + column.getValue().toString().length();
            } else {
                offsets[i] = offsets[previous] + column.getLength();
            }
            isPrecededByPKorNull = true;
        }
        return isPrecededByPKorNull;
    }

    public int populateFirstOffset(int firstOffset){
        if (table.getColumn(0).isPrimaryKey()) {
            offsets[0] = IS_PKEY;
        } else if (!table.getColumn(0).isNotNull()){
            offsets[0] = IS_NULL;
        } else {
            offsets[0] = firstOffset;
        }
        return offsets[0];
    }

    /**
     * Returns the RowOutput used for the key portion of the marshalled row.
     *
     * @return  the key's RowOutput
     */
    public RowOutput getKeyBuffer() {
        return this.keyBuffer;
    }
    
    /**
     * Returns the RowOutput used for the value portion of the marshalled row.
     *
     * @return  the value's RowOutput
     */
    public RowOutput getValueBuffer() {
        return this.valueBuffer;
    }

    /**
     * Returns a String representation of this InsertRow object.
     *
     * @return  a String for this InsertRow
     */
    public String toString() {
        return "offsets: " + Arrays.toString(this.offsets)
             + "\nkey buffer: " + this.keyBuffer
             + "\nvalue buffer: " + this.valueBuffer;
    }
}
