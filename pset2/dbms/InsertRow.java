/*
 * InsertRow.java
 *
 * DBMS Implementation
 */

import com.sleepycat.je.*;
import java.io.*;
import java.util.Arrays;
import java.util.logging.Logger;


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

    private boolean isFirstOffset = false;
    private int previousVal = 0;
    Logger logger = Logger.getLogger(InsertRow.class.getName());
    
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
    public void marshall() {

        populateOffsets(offsets);

        try {
            writePrimaryKey();
            writeOffsets();
            writeValues();
        } catch (IOException e) {
            logger.warning("Error writing to buffer " + e.getCause());
        }

    }

    /**
     * Writes offsets
     * @throws IOException
     */
    private void writeOffsets() throws IOException {
        for(int i = 0; i < offsets.length; i++){
            valueBuffer.writeShort(offsets[i]);
        }
    }

    /**
     * Writes values based on type
     * @throws IOException
     */
    private void writeValues() throws IOException {
        for(int i = 0; i < this.table.numColumns(); i++) {
            Column column = this.table.getColumn(i);
            int type = column.getType();
            Object value = this.columnVals[i];
            if (value != null && !column.isPrimaryKey()) {
                switch (type) {
                    case Column.VARCHAR:
                        String strValue = (String) value;
                        valueBuffer.writeBytes(strValue);
                        break;
                    case Column.INTEGER:
                        Integer intValue = (Integer) value;
                        valueBuffer.writeInt(intValue.intValue());
                        break;
                    case Column.REAL:
                        Double doubleValue = (Double) value;
                        valueBuffer.writeDouble(doubleValue.doubleValue());
                        break;
                    case Column.CHAR:
                        String charValue = (String) value;
                        valueBuffer.writeChars(charValue);
                        break;
                }
            }
        }
    }

    /**
     * Writes primary key based on type
     * @throws IOException
     */
    private void writePrimaryKey() throws IOException {
        Object key = this.columnVals[findPrimaryKey()];
        int type = this.table.getColumn(findPrimaryKey()).getType();
        switch(type){
            case Column.VARCHAR:
                String strKey = (String) key;
                keyBuffer.writeBytes(strKey);
                break;
            case Column.INTEGER:
                Integer intKey = (Integer) key;
                keyBuffer.writeInt(intKey.intValue());
                break;
            case Column.REAL:
                Double doubleKey = (Double) key;
                keyBuffer.writeDouble(doubleKey.doubleValue());
                break;
            case Column.CHAR:
                String charKey = (String) key;
                keyBuffer.writeChars(charKey);
                break;
        }
    }

    /**
     * Populates offsets. The first and last offsets are special
     * @param offsets
     */
    private void populateOffsets(int[] offsets) {

        int firstOffset = (this.offsets.length * 2);

        if(findPrimaryKey() == 0){
            offsets[0] = IS_PKEY;
        } else if(this.columnVals[0] == null){
            offsets[0] = IS_NULL;
        } else {
            offsets[0] = firstOffset;
        }

        for(int i = 1; i < this.table.numColumns(); i++){
            Column column = this.table.getColumn(i);
            Object columnVal = this.columnVals[i];;

            if(i == findPrimaryKey()){
                offsets[i] = IS_PKEY;
            } else if(this.columnVals[i] == null){
                offsets[i] = IS_NULL;
            } else {
                if(column.getType() == Column.VARCHAR){
                    String strColumn = (String) columnVal;
                    findANonNegativePredecessorAndSetOffset(offsets, i - 1, i, strColumn.length());
                } else{
                    findANonNegativePredecessorAndSetOffset(offsets, i - 1, i, column.getLength());
                }
            }
        }
        populateLastOffset(offsets);
    }

    /**
     * Populates last offset
     * @param offsets
     */
    private void populateLastOffset(int[] offsets) {
        int lastPositiveIndex = offsets.length - 2;
        while(offsets[lastPositiveIndex] < 0){
            lastPositiveIndex--;
        }
        if(this.table.getColumn(lastPositiveIndex).getType() == Column.VARCHAR){
            offsets[offsets.length - 1] = ((String) this.columnVals[lastPositiveIndex]).length() +
                    offsets[lastPositiveIndex];
        } else {
            offsets[offsets.length - 1] = this.table.getColumn(lastPositiveIndex).getLength() +
                    offsets[lastPositiveIndex];
        }
    }

    /**
     * Finds the last positive offset and sets consecutive offsets
     * @param offsets
     * @param predecessor
     * @param index
     * @param value
     */
    private void findANonNegativePredecessorAndSetOffset(int[] offsets, int predecessor, int index, int value) {
        int firstOffset = (this.offsets.length * 2);

        while(predecessor > 0 && offsets[predecessor] < 0){
            predecessor--;
        }

        if(isFirstOffset){
            offsets[index] = offsets[predecessor] + previousVal;
            isFirstOffset = false;
            previousVal = 0;
        } else {
            if(predecessor == 0 && offsets[predecessor] < 0){
                offsets[index] = firstOffset;
                isFirstOffset = true;
                previousVal = value;
            } else {
                offsets[index] = offsets[predecessor] + value;
            }
        }
    }

    /**
     * Finds primary key in the table
     * @return
     */
    private int findPrimaryKey(){
        int primaryKeyIndex = 0;
        for(int i = 0; i < table.numColumns(); i++){
            if(table.getColumn(i).isPrimaryKey()){
                primaryKeyIndex = i;
            }
        }
        return primaryKeyIndex;
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
