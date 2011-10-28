// Number of Tables in Database
var tableCounter = 0;

// Column Counter should be in Table
var columnCounter = 0;


function Database(){
    this.name="";
    this.description = "";
    this.tables = new Array();
}

function createDatabaseFunction(nameValue, descriptionValue){
    var tempDatabase = new Database();
    tempDatabase.name = nameValue;
    tempDatabase.description = descriptionValue;
    return tempDatabase;
}


function generateSQLForPostgres() {
    var sql = "";
    
       
    for (var i = 0; i < this.tables.length; i ++){
        var tempTable = this.tables[i];
        var firstTableEntry = true;
        sql = sql + "CREATE TABLE " + tempTable.name + " ( \n";
        for (var j = 0; j < tempTable.primaryKeys.length; j++){
            var tempPK = tempTable.primaryKeys[j];
            if(firstTableEntry == true){
                sql = sql + parseKeyForSQL(tempPK, false) + " PRIMARY KEY";
                firstTableEntry = false;
            } else {
                sql = sql + ",\n" + parseKeyForSQL(tempPK, false) + " PRIMARY KEY";
            }           
        }
        
        for (var k = 0; k < tempTable.columns.length; k++){
            var tempColumns = tempTable.columns[k];
             if(firstTableEntry == true){
                firstTableEntry = false;
             } else {
                sql = sql + ", \n";
             }
            sql = sql + parseKeyForSQL(tempColumns, true) + "  ";
        }
        
        for (var p = 0; p < tempTable.foreignKeys.length; p++){
            var tempFKColumns = tempTable.foreignKeys[p];
             if(firstTableEntry == true){
                firstTableEntry = false;
             } else {
                sql = sql + ", \n";
             }
            sql = sql + parseKeyForSQL(tempFKColumns, true) + "  ";
        }
        
        for(var l = 0; l < tempTable.relations.length; l++){
            var tempRelation = tempTable.relations[l];
            if(tempRelation.orientation == "end"){
                for(var m = 0; m < tempRelation.keysInSecondryTable.length; m++) {
                    if(tempRelation.keysInSecondryTable[m].keyType == "Foreign"){
                        if(firstTableEntry == true){
                            firstTableEntry = false;
                         } else {
                            sql = sql + ", \n";
                         }
                        // FOREIGN KEY(scale_id) REFERENCES colscales(id));
                        sql = sql + "\tFOREIGN KEY(" + tempRelation.keysInSecondryTable[m].keyName + ") References " + tempRelation.mainTable.name + "(" + tempRelation.keysFromMainTable[m].keyName + ")";    
                    }                
                }
            }
        }
        sql = sql + "\n);\n\n";
    }
    return sql;
}

function generateSQLForMySQL() {
}

function generateSQLForOracle() {
}

function generateSQLForSQLServer() {
}

function parseKeyForSQL(tempColumn, ordinaryKey){
    var keySQL = "\t" + tempColumn.keyName + "  ";
    if(tempColumn.dataType == "float4" || tempColumn.dataType == "float8" ){
        keySQL = keySQL + "FLOAT(" + tempColumn.keyLength + ", " + tempColumn.keyScale + ")";
    } else if(tempColumn.dataType == "varchar" || tempColumn.dataType == "char" ){
        keySQL = keySQL + tempColumn.dataType + "(" + tempColumn.keyLength + ")" ;
    } else {
        keySQL = keySQL + tempColumn.dataType;
    }
    //alert(tempColumns.keyNull);
    if(ordinaryKey == true){
        if (tempColumn.keyNull != "NULL"){
            keySQL = keySQL + " " + tempColumn.keyNull;
        }
        if (tempColumn.keyUnique == "UNIQUE"){
            keySQL = keySQL + " UNIQUE";
        }
    }
    return keySQL;   
}

//Database.prototype.createFunction = createDatabase;
Database.prototype.Postgres = generateSQLForPostgres;
Database.prototype.MySQL = generateSQLForMySQL;
Database.prototype.Oracle = generateSQLForOracle;
Database.prototype.SQLServer = generateSQLForSQLServer;

function Relation () {

    // Other Table is with respect to table holding this relation
    var otherTable;
    var line;
    var orientation;
    
    // Need these Variables for Proper Relation
    // There may not be Foreign Keys for Identifying Relations
    // Name needs to be changed ....!
    var mainTable;
    var secondryTable;
    
    
    this.keysFromMainTable = new Array();
    this.keysInSecondryTable = new Array();
}

function Key(){
    // Type = Primary/Foreign/Normal
    this.keyType;
    this.keyName;
    this.dataType;
    
    // For Strings Length
    // For numeric values Scale
    this.keyLength;
    
    // For numerical values digits after decimal
    this.keyScale;
    
    this.keyNull;
    
    // Important for 1-1 relation
    // or 1-M relation
    this.keyUnique;
    
    this.keyAutoIncrement;
    
    // Position in the Table
    this.keyPosition;
    // ID of Key in the Table
    this.keyID;
}



function createAndReturnKey(keyTypeValue, keyNameValue, dataTypeValue, keyLengthValue, keyScaleValue, keyNullValue, keyUniqueValue, keyAutoIncrementValue, parentTable){
    // Type = Primary/Foreign/Normal
    this.keyType = keyTypeValue;
    this.keyName = keyNameValue;
    this.dataType = dataTypeValue;
    
    // For Strings Length
    // For numeric values Scale
    this.keyLength = keyLengthValue;
    
    // For numerical values digits after decimal
    this.keyScale = keyScaleValue;
    
    if (keyNullValue == "null"){
        this.keyNull = "NULL";
    } else {
        this.keyNull = "NOT NULL";
    }
    //this.keyNull = keyNullValue;
    
    //alert(keyUniqueValue);
    // UNIQUE (col3)
    if (keyUniqueValue == "unique"){
        this.keyUnique = "UNIQUE";
    } else {
        this.keyUnique = " ";
    }
    //this.keyUnique = keyUniqueValue;
           
    this.keyAutoIncrement = keyAutoIncrementValue;
    
    parentTable.columnCounter = parentTable.columnCounter +  1;    
    this.keyID = parentTable.id + "_" + parentTable.columnCounter;
    
    if (this.keyType == "Primary") {
        parentTable.primaryKeys[parentTable.primaryKeys.length] = this;
    } else if (this.keyType == "Foreign"){
        parentTable.foreignKeys[parentTable.foreignKeys.length] = this;
    } else if (this.keyType == "Ordinary"){
        parentTable.columns[parentTable.columns.length] = this;
    }
}

Key.prototype.createAndAddFunction = createAndReturnKey;

function Table(){
    //var primaryKey
    this.id = '';
    this.name = '';
    
    this.description = '';
    
    this.primaryKeys = new Array();
    this.foreignKeys = new Array();
    this.columns = new Array();
    
    this.relations = new Array();
    
    this.xPosition = 0;
    this.yPosition = 0;
    
    this.width;
    this.height;
    
    this.state = 'Open';    
    
    this.columnCounter = 0;    
}

function createTableUtility(tempTable){

    var tempInnerHTML =  '<div style="position: absolute; top:' + tempTable.yPosition +'px; left:' + tempTable.xPosition + 'px;" class="tableElement" ondblclick ="onMouseClickTable(this)" id="' + tempTable.id + '">';
    tempInnerHTML = tempInnerHTML + '<table class="box-table-a"> <thead><tr><th scope="col">';
    tempInnerHTML = tempInnerHTML + '<img src="images/minus.png" height="18" width="18" ondblclick ="tableToggleClick(this)" align="left"/>&nbsp;&nbsp;' + tempTable.name + '&nbsp; &nbsp; &nbsp;';
    //tempInnerHTML = tempInnerHTML + '<img src="images/Modify.png" height="18" width="18" class="close-icon" ondblclick ="tableToggleClick(this)"/>';
    // escaping single Quote is key .....!
    tempInnerHTML = tempInnerHTML + '<img src="images/customize-icon.png" height="18" width="18" class="close-icon" align="right" ondblclick ="modifyTable(\'' + tempTable.id + '\')"/>';
    
    //tempInnerHTML = tempInnerHTML + '<img src="images/drag1.jpg" height="15" width="15" class="drag-icon" onmouseover="dragIconMouseOver(this)" onmouseout="dragIconMouseOut(this)" align="left"/>';
    tempInnerHTML = tempInnerHTML + '</th></tr></thead><tbody class="primaryRows">';
    
    for (var i = 0; i < tempTable.primaryKeys.length; i++){
        tempInnerHTML = tempInnerHTML + '<tr><td id="' + tempTable.primaryKeys[i].keyID +'">(PK)&nbsp;&nbsp;' + tempTable.primaryKeys[i].keyName;
        tempInnerHTML = tempInnerHTML + '<em > &nbsp;&nbsp;(' + tempTable.primaryKeys[i].dataType + ')&nbsp; </em>';
        tempInnerHTML = tempInnerHTML + '<img src="images/customize-icon.png" height="18" width="18" class="close-icon" align="right"';
        tempInnerHTML = tempInnerHTML + 'ondblclick="modifyColumn(\'' +  tempTable.primaryKeys[i].keyID + '\', \'' + tempTable.id + '\')"/> </td></tr>';
    }
    
    tempInnerHTML = tempInnerHTML + '</tbody><tbody class="ordinaryRows">';
    for (var i = 0; i < tempTable.columns.length; i++){
        tempInnerHTML = tempInnerHTML + '<tr><td id="' + tempTable.columns[i].keyID +'">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' + tempTable.columns[i].keyName;
        tempInnerHTML = tempInnerHTML + '<em > &nbsp;&nbsp;(' + tempTable.columns[i].dataType + ')&nbsp;</em>';
        tempInnerHTML = tempInnerHTML + '<img src="images/customize-icon.png" height="18" width="18" class="close-icon" align="right"';
        tempInnerHTML = tempInnerHTML + 'ondblclick="modifyColumn(\'' +  tempTable.columns[i].keyID + '\', \'' + tempTable.id + '\')"/> </td></tr>'
    }
    
    tempInnerHTML = tempInnerHTML + '</tbody><tbody class="foreignRows">';
    for (var i = 0; i < tempTable.foreignKeys.length; i++){
        tempInnerHTML = tempInnerHTML + '<tr><td id="' + tempTable.foreignKeys[i].keyID +'">(FK)&nbsp;&nbsp;' + tempTable.foreignKeys[i].keyName;
        tempInnerHTML = tempInnerHTML + '<em > &nbsp;&nbsp;(' + tempTable.foreignKeys[i].dataType + ')&nbsp;</em>';
        tempInnerHTML = tempInnerHTML + '<img src="images/customize-icon.png" height="18" width="18" class="close-icon" align="right"';
        tempInnerHTML = tempInnerHTML + 'ondblclick="modifyColumn(\'' +  tempTable.foreignKeys[i].keyID + '\', \'' + tempTable.id + '\')"/> </td></tr>';
    }
    
    tempInnerHTML = tempInnerHTML + '</tbody>';
    //tempInnerHTML = tempInnerHTML + '<thead><tr><th scope="col"><img src="images/close_icon.png" height="15" width="15" class="close-icon" align="right"/></th></tr></thead>';
        
    tempInnerHTML = tempInnerHTML + '</table></div>';
    return tempInnerHTML;
}

function createTableFunction(xPositionVal, yPositionVal, tableLabel, tableDescription, newTable, databaseObject){
    var tempID = "tableDiv_" + tableCounter;
    
    newTable.id = tempID;
    newTable.xPosition = xPositionVal;
    newTable.yPosition = yPositionVal;
    newTable.name = tableLabel;
    newTable.description = tableDescription;
    
    var defaultPrimaryKey = new Key();
    defaultPrimaryKey.createAndAddFunction("Primary", "ID", "int4", 0, 0, "not null", "unique", true, this);
    
    var tempInnerHTML = createTableUtility(newTable);
        
    databaseObject.tables[tableCounter] = newTable;
    
    tableCounter++;
    
    return tempInnerHTML;
}

function createTableWithoutPrimaryKeyFunction(xPositionVal, yPositionVal, tableLabel, newTable, databaseObject){
    var tempID = "tableDiv_" + tableCounter;
    
    newTable.id = tempID;
    newTable.xPosition = xPositionVal;
    newTable.yPosition = yPositionVal;
    newTable.name = tableLabel;
    
    var tempInnerHTML = createTableUtility(newTable);
    
    databaseObject.tables[tableCounter] = newTable;
    
    tableCounter++;
    
    return tempInnerHTML;
}
function alterTableFunction(updatedTable){
    var tempInnerHTML = createTableUtility(updatedTable);
    return tempInnerHTML;
}

function updateTableFunction(keyTypeValue, keyNameValue, dataTypeValue, keyLengthValue, keyScaleValue, keyNullValue, keyUniqueValue, keyAutoIncrementValue) {

    var newKey = new Key();
    //                          keyTypeValue, keyNameValue, dataTypeValue, keyLengthValue, keyScaleValue, keyNullValue, keyUniqueValue, keyAutoIncrementValue, parentTable
    newKey.createAndAddFunction(keyTypeValue, keyNameValue, dataTypeValue, keyLengthValue, keyScaleValue, keyNullValue, keyUniqueValue, keyAutoIncrementValue, this);
    
    var tempInnerHTML = createTableUtility(this);
 
    return tempInnerHTML;
}

// columnValue is the Column itself
function updateTableColumnFunction(columnValue, keyTypeValue, keyNameValue, dataTypeValue, keyLengthValue, keyScaleValue, keyNullValue, keyUniqueValue, keyAutoIncrementValue) {
    /*
    var newKey = new Key();
    //                          keyTypeValue, keyNameValue, dataTypeValue, keyLengthValue, keyScaleValue, keyNullValue, keyUniqueValue, keyAutoIncrementValue, parentTable
    newKey.createAndAddFunction(keyTypeValue, keyNameValue, dataTypeValue, keyLengthValue, keyScaleValue,  keyNullValue, keyUniqueValue, keyAutoIncrementValue, this);
    */
    var localColumnValue = columnValue;
    
    if(localColumnValue.keyType == keyTypeValue){
    } else {
        if(localColumnValue.keyType == "Primary"){
            removeColumnFromArray(localColumnValue, this, "Primary", keyTypeValue);
            localColumnValue.keyType = keyTypeValue;            
        } else if(localColumnValue.keyType == "Ordinary"){
            
            removeColumnFromArray(localColumnValue, this, "Ordinary", keyTypeValue);
            localColumnValue.keyType = keyTypeValue;
        }
    } /**/
    
    if(keyNameValue != undefined){
        localColumnValue.keyName = keyNameValue;
    }
    if(dataTypeValue != undefined){
        localColumnValue.dataType = dataTypeValue;
        if(dataTypeValue == "float4" || dataTypeValue == "float8" || dataTypeValue == "varchar" || dataTypeValue == "char"){
            localColumnValue.keyLength = keyLengthValue;
            if(dataTypeValue == "float4" || dataTypeValue == "float8"){
                localColumnValue.keyScale = keyScaleValue;
            }
        }
    }
    
    
    if(keyNullValue != undefined){
        if (keyNullValue == "null"){
            localColumnValue.keyNull = "NULL";
        } else {
            localColumnValue.keyNull = "NOT NULL";
        }
    }
    
    if(keyUniqueValue != undefined){
        if (keyUniqueValue == "unique"){
            localColumnValue.keyUnique = "UNIQUE";
        } else {
            localColumnValue.keyUnique = " ";
        }
    }
    var tempInnerHTML = createTableUtility(this);
 
    return tempInnerHTML;
}

function removeColumnFromArray(columnValue, tableValue, columnType, newcolumnType){
    
    if(columnType == "Primary"){
         for (var i = 0; i < tableValue.primaryKeys.length; i++){
            if(columnValue.keyID ==  tableValue.primaryKeys[i].keyID){
                tableValue.primaryKeys.splice(i,1);
                if(newcolumnType == "Foreign"){
                    tableValue.foreignKeys[tableValue.foreignKeys.length] = columnValue;
                }else {
                    tableValue.columns[tableValue.columns.length] = columnValue;   
                }             
            }
         }        
    }
    if(columnType == "Ordinary"){
         for (var i = 0; i < tableValue.columns.length; i++){
            if(columnValue.keyID ==  tableValue.columns[i].keyID){
                tableValue.columns.splice(i,1);
                if(newcolumnType == "Foreign"){
                    //alert(newcolumnType + "  " +  tableValue.foreignKeys.length);
                    tableValue.foreignKeys[tableValue.foreignKeys.length] = columnValue;
                    //alert(newcolumnType + "  " +  tableValue.foreignKeys.length);
                }else {
                    tableValue.primaryKeys[tableValue.primaryKeys.length] = columnValue;
                }
            }
         }        
    }
}

Table.prototype.createAndAddFunction = createTableFunction;
Table.prototype.createAndAddWithoutPKFunction = createTableWithoutPrimaryKeyFunction;
Table.prototype.updateFunction = updateTableFunction;
Table.prototype.updateColumnFunction = updateTableColumnFunction;

Table.prototype.alterFunction = alterTableFunction;