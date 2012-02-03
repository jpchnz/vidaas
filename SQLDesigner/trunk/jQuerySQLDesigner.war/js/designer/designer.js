// Number of Tables in Database
var tableCounter = 0;

// Column Counter should be in Table
var columnCounter = 0;

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
    // Type = Primary/Foreign/Ordinary
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
    
    this.keyDescription = '';
    
    // Position in the Table
    this.keyPosition;
    // ID of Key in the Table
    this.keyID;
}



function createAndReturnKey(keyTypeValue, keyNameValue, dataTypeValue, keyLengthValue, keyScaleValue, keyNullValue, keyUniqueValue, keyAutoIncrementValue, keyDescriptionValue, parentTable){
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
    
    //alert("columnDescription: " + keyDescriptionValue);
    this.keyDescription = keyDescriptionValue;
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
        tempInnerHTML = tempInnerHTML + 'ondblclick="modifyColumn(\'' +  tempTable.columns[i].keyID + '\', \'' + tempTable.id + '\')"/> </td></tr>';
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
		
	console.log("Before creating table: " + databaseObject.tables.length);
    var tempID = "tableDiv_" + tableCounter;
    
    newTable.id = tempID;
    newTable.xPosition = xPositionVal;
    newTable.yPosition = yPositionVal;
    newTable.name = tableLabel;
    newTable.description = tableDescription;
    
    var defaultPrimaryKey = new Key();
    defaultPrimaryKey.createAndAddFunction("Primary", "ID", "int4", 0, 0, "not null", "unique", true, "Default Primary Key", this);
    
    var tempInnerHTML = createTableUtility(newTable);
        
    databaseObject.tables[databaseObject.tables.length] = newTable;
    console.log("After creating table: " + databaseObject.tables.length);
    
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
    
    databaseObject.tables[databaseObject.tables.length] = newTable;
    
    tableCounter++;
    
    return tempInnerHTML;
}
function alterTableFunction(updatedTable){
    var tempInnerHTML = createTableUtility(updatedTable);
    return tempInnerHTML;
}

function updateTableFunction(keyTypeValue, keyNameValue, dataTypeValue, keyLengthValue, keyScaleValue, keyNullValue, keyUniqueValue, keyAutoIncrementValue, keyDescriptionValue) {

    var newKey = new Key();
    //alert("columnDescription: " + keyDescriptionValue);
    //                          keyTypeValue, keyNameValue, dataTypeValue, keyLengthValue, keyScaleValue, keyNullValue, keyUniqueValue, keyAutoIncrementValue, parentTable
    newKey.createAndAddFunction(keyTypeValue, keyNameValue, dataTypeValue, keyLengthValue, keyScaleValue, keyNullValue, keyUniqueValue, keyAutoIncrementValue, keyDescriptionValue, this);
    
    var tempInnerHTML = createTableUtility(this);
 
    return tempInnerHTML;
}

// columnValue is the Column itself
function updateTableColumnFunction(columnValue, keyTypeValue, keyNameValue, dataTypeValue, keyLengthValue, keyScaleValue, keyNullValue, keyUniqueValue, keyAutoIncrementValue, keyDescriptionValue) {
    /*
    var newKey = new Key();
    //                          keyTypeValue, keyNameValue, dataTypeValue, keyLengthValue, keyScaleValue, keyNullValue, keyUniqueValue, keyAutoIncrementValue, parentTable
    newKey.createAndAddFunction(keyTypeValue, keyNameValue, dataTypeValue, keyLengthValue, keyScaleValue,  keyNullValue, keyUniqueValue, keyAutoIncrementValue, this);
    */
    var localColumnValue = columnValue;
    localColumnValue.keyDescription = keyDescriptionValue;
    
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

function updatedTableHTMLFunction(){
	var tempInnerHTML = createTableUtility(this);
	 
  return tempInnerHTML;
}

/*
function deleteColumnFromTableFunction(columnIDValue){
	for (var i = 0; i < this.primaryKeys.length; i++){
        if(this.primaryKeys[i].keyID == columnIDValue) {
            
        	// There is no relation so Primary Key can be deleted
        	if(this.relations.length == 0){
        		this.primaryKeys.splice(i,1);
        	} else {
        		// Test if this table is in any Relation as Main Table
        		// This test will not work for identifying relations
        		// There can be primary key but contributed from other main table
        		var tempRelation;
        		var tempTable;
        		var tempColumn;
        		for(var i = 0; i < this.relations.length; i++){
        			tempRelation = this.relations[i];
        			if(this.id == tempRelation.mainTable.id){
        				tempTable = tempRelation.secondryTable;
        				//alert("Seondary Table ID: " + tempTable.id);
        				for (var j = 0; j < tempRelation.keysInSecondryTable.length; j++){
        					tempColumn = tempRelation.keysInSecondryTable[j];
        					deleteColumnFromTable(tempTable, tempColumn.keyID);
        				}
        			}
        		}
        	}
        }
    }
    
    for (var i = 0; i < this.columns.length; i++){
        if(this.columns[i].keyID == columnIDValue) {
            //alert("Column Key Found");
        	this.columns.splice(i,1);
        }
    }
    
    for (var i = 0; i < this.foreignKeys.length; i++){
        if(this.foreignKeys[i].keyID == columnIDValue) {
            //alert("Foreign Key Found");
        	this.foreignKeys.splice(i,1);
        }
    }
    var tempInnerHTML = createTableUtility(this);
    
    return tempInnerHTML;
}

// This is utility function used by deleteColumnFromTableFunction
function deleteColumnFromTable(parentTable, columnID) {
	for ( var i = 0; i < parentTable.primaryKeys.length; i++) {
		if (parentTable.primaryKeys[i].keyID == columnID) {
			// alert("Primary Key Found");
			parentTable.primaryKeys.splice(i, 1);
		}
	}

	for ( var i = 0; i < parentTable.columns.length; i++) {
		if (parentTable.columns[i].keyID == columnID) {
			//alert("Column Key Found");
			parentTable.columns.splice(i, 1);
		}
	}

	for ( var i = 0; i < parentTable.foreignKeys.length; i++) {
		if (parentTable.foreignKeys[i].keyID == columnID) {
			// alert("Foreign Key Found");
			parentTable.foreignKeys.splice(i, 1);
		}
	}
}
*/

// Function removes the column from one array and add into other array.
// Not sure why function is testing newColumnType == Foreign .. 
// This should be done when relations are created. User can't change the type 
// to Foreign manually.
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

function findColumnInTableByNameFunction(columnName) {
	for ( var i = 0; i < this.primaryKeys.length; i++) {
		if (this.primaryKeys[i].keyName.toLowerCase() == columnName.toLowerCase()) {
			// alert("Primary Key Found");
			return this.primaryKeys[i];
		}
	}

	for ( var i = 0; i < this.columns.length; i++) {
		if (this.columns[i].keyName.toLowerCase() == columnName.toLowerCase()) {
			// alert("Column Key Found");
			return this.columns[i];
		}
	}

	for ( var i = 0; i < this.foreignKeys.length; i++) {
		if (this.foreignKeys[i].keyName.toLowerCase() == columnName.toLowerCase()) {
			// alert("Foreign Key Found");
			return this.foreignKeys[i];
		}
	}

	return null;
}

Table.prototype.createAndAddFunction = createTableFunction;
Table.prototype.createAndAddWithoutPKFunction = createTableWithoutPrimaryKeyFunction;
Table.prototype.updateFunction = updateTableFunction;
Table.prototype.updateColumnFunction = updateTableColumnFunction;
//Table.prototype.deleteColumnFromTable = deleteColumnFromTableFunction;
Table.prototype.alterFunction = alterTableFunction;
Table.prototype.getTableHTMLFunction = updatedTableHTMLFunction;
Table.prototype.findColumnFunction = findColumnInTableByNameFunction;
