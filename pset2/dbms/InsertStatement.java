/*
 * InsertStatement.java
 *
 * DBMS Implementation
 */

import java.util.*;
import java.util.logging.Logger;

import com.sleepycat.je.*;
import com.sleepycat.persist.impl.Store;

/**
 * A class that represents an INSERT statement.
 */
public class InsertStatement extends SQLStatement {

    Logger logger = Logger.getLogger(InsertStatement.class.getName());

    /** 
     * Constructs an InsertStatement object involving the specified table,
     * list of columns (if any), and list of values.  The columns and 
     * their associated values must be specified in the same order.
     * If no list of columns is specified, we will assume that values are 
     * being specified for all columns.
     *
     * @param  t  the table in which the values should be inserted
     * @param  colList  the list of columns for which values are specified
     * @param  valList  the list of values to be inserted
     */
    public InsertStatement(Table t, ArrayList<Column> colList,
                           ArrayList<Object> valList) {
        super(t, colList, valList);
    }
    
    public void execute() throws DatabaseException, DeadlockException {
        try {
            Table table = this.getTable(0);
            if (table.open() != OperationStatus.SUCCESS) {
                throw new Exception();  // error msg was printed in open()
            }
            
            // Preliminary error checking.
            if (this.numColumns() != 0) {
                throw new Exception("INSERT commands with column names " +
                                    "are not supported");
            }
            if (this.numColumnVals() != table.numColumns()) {
                throw new Exception("Must specify a value for each column");
            }
            
            // Make any necessary adjustments (type conversions, 
            // truncations, etc.) to the values to be inserted.
            // This will throw an exception if a value is invalid.
            Object[] adjustedValues = new Object[table.numColumns()];
            for (int i = 0; i < table.numColumns(); i++) {
                Column col = table.getColumn(i);
                adjustedValues[i] = col.adjustValue(this.getColumnVal(i));
            }
            
            // Create an InsertRow object for the row to be inserted,
            // and use that object to marshall the row.
            InsertRow row = new InsertRow(table, adjustedValues);
            row.marshall();

            /**
             * Persists marshalled data to disc
             */
            persistValuesInDB(row, table);

        } catch (Exception e) {
            String errMsg = e.getMessage();
            if (errMsg != null) {
                System.err.println(errMsg + ".");
            }
            System.err.println("Could not insert row.");
        }
    }

    /**
     * Persists key/value pairs to disc
     * @param row
     * @param table
     * @return
     * @throws Exception
     */
    private OperationStatus persistValuesInDB(InsertRow row, Table table) throws Exception {
        Database db = table.getDB();

        RowOutput keyBuffer = row.getKeyBuffer();
        RowOutput valueBuffer = row.getValueBuffer();

        DatabaseEntry key = new DatabaseEntry(keyBuffer.getBufferBytes(), 0, keyBuffer.getBufferLength());
        DatabaseEntry value = new DatabaseEntry(valueBuffer.getBufferBytes(), 0, valueBuffer.getBufferLength());

        OperationStatus operationStatus = db.putNoOverwrite(null, key, value);

        if(operationStatus == OperationStatus.SUCCESS){
            logger.info("Successfully inserted into database");
        } else if(operationStatus == OperationStatus.KEYEXIST){
            throw new Exception("A primary key must have unique value in table " + table.getName());
        } else {
            throw new Exception("Error inserting into database " + table.getDB().getDatabaseName());
        }
        return operationStatus;
    }
}
