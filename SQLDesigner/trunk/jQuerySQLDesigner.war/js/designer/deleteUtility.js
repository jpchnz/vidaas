function deleteColumn(columnObject, tableObject){
	//alert("Delete Column Utility called");
	
	var tempID = "#" + tableObject.id;
	//var columnID = "#" + columnObject.keyID;
	
	var innerHTML = deleteColumnFromTableFunction(columnObject.keyID, tableObject);

	$(tempID).replaceWith(innerHTML);
	
}

function deleteTable(tableObject, databaseObjectValue){
	var tempID = "#" + tableObject.id;
	
	if(tableObject.relations.length == 0){
		databaseObjectValue.deleteTableFunction(tableObject.id);
		//deleteTableInDatabase(tableObject.id, databaseObjectValue);
		$(tempID).remove();
	} else {
		alert("Table Has Relation/s with other Table/s");
	}
	
}


/*
 *  > > > > > > > >  ONLY CALLED LOCALLY < < < < < < < < < 
 */

function deleteTableInDatabase(tableID, databaseObjectValue) {
	console.log("Tables in Database: " + databaseObjectValue.tables.length);
	var tempTableInDatabase;
	for ( var i = 0; i < databaseObjectValue.tables.length; i++) {
		tempTableInDatabase = databaseObjectValue.tables[i];
		// alert("tempTableInDatabase.id" + " " + tableID);
		if (tempTableInDatabase.id == tableID) {
			// alert(tempTableInDatabase.id);
			databaseObjectValue.tables.splice(i, 1);
		}
	}
	console.log("Tables in Database: " + databaseObjectValue.tables.length);
}

function deleteColumnFromTableFunction(columnIDValue, tableObject){
	//Stores Line ID to be deleted
	var relationsToBeDeleted = new Array();
	// Iterate Primary Keys Array
	for (var i = 0; i < tableObject.primaryKeys.length; i++){
				// Is ColumnIDValue a Primary Key ...?
        if(tableObject.primaryKeys[i].keyID == columnIDValue) {
        	console.log("Column is Primary Key columnIDValue: " +  columnIDValue);
        	// Should be after updating other tables in relations
        	// but doesn't work once other tables are updated ...!
        	tableObject.primaryKeys.splice(i,1);
        	
        	// There is no relation, Primary Key already deleted
        	// Nothing more to do
        	if(tableObject.relations.length > 0) {
        		console.log("Table has Relations: " +  tableObject.relations.length);
        		// Test if this table is in any Relation as Main Table
        		// This test will not work for identifying relations
        		// There can be primary key but contributed from other main table
        		var tempRelation;
        		var tempTable;
        		var tempColumn;
        		for(var i = 0; i < tableObject.relations.length; i++){
        			tempRelation = tableObject.relations[i];
        			//alert("tempRelation: " + tempRelation.line.id);
        			
        			// Is table the Main Table in Relation ..?
        			if(tableObject.id == tempRelation.mainTable.id){
        				console.log("Table is Main Table in relation");
        			// Delete the line representing the relation
          			svg.remove(tempRelation.line);
        				tempTable = tempRelation.secondryTable;
        				//alert("Seondary Table ID: " + tempTable.id);
        				for (var j = 0; j < tempRelation.keysInSecondryTable.length; j++){
        					tempColumn = tempRelation.keysInSecondryTable[j];
        					console.log("Deleting Column from Secondary Table in relation: " + tempColumn);
        					
        					deleteColumnFromTable(tempTable, tempColumn.keyID);
        				}
        			// Delete the relation from Secondary Table
        				for (var j = 0; j < tempTable.relations.length; j++){
        					var localTempRelation = tempTable.relations[j];
        					console.log("Deleting Relation from Secondary Table: " + localTempRelation.line.id + " : " + tempRelation.line.id);
        					if(localTempRelation.line.id == tempRelation.line.id){
        						tempTable.relations.splice(j,1);
        						break;
        					}
        				}
        				
        				// Need to delete the relations also ..!
        				// deleting the relation from Main Table
        				// Splicing within the loop will not delete remaining elements ...
        				// use slightly different technique 
        				relationsToBeDeleted.push(tempRelation.line.id);
        				      				
        				// Visually update Secondary Table Here ....!
        				var tempTableID = "#" + tempTable.id;
        				$(tempTableID).replaceWith(tempTable.getTableHTMLFunction());
        			} else if(tableObject.id == tempRelation.secondryTable.id){
        				svg.remove(tempRelation.line);
        				tempTable = tempRelation.mainTable;
        				console.log(tempRelation.keysInSecondryTable.length);
        				for (var j = 0; j < tempRelation.keysInSecondryTable.length; j++){
        					tempColumn = tempRelation.keysInSecondryTable[j];
        					console.log(tempColumn.keyID);
        					if(columnIDValue != tempColumn.keyID){
        						deleteColumnFromTable(tableObject, tempColumn.keyID);
        					}
        				}
        				for (var j = 0; j < tempTable.relations.length; j++){
        					var localTempRelation = tempTable.relations[j];
        					console.log("Deleting Relation from Main Table: " + localTempRelation.line.id + " : " + tempRelation.line.id);
        					if(localTempRelation.line.id == tempRelation.line.id){
        						tempTable.relations.splice(j,1);
        						break;
        					}
        				}
        				
        				relationsToBeDeleted.push(tempRelation.line.id);
        				/*
        				var tempTableID = "#" + tempTable.id;
        				$(tempTableID).replaceWith(tempTable.getTableHTMLFunction());
        				*/
        			}
        		}
        		
        		for(var index = 0; index < relationsToBeDeleted.length; index++){
        			deleteRelation(tableObject, relationsToBeDeleted[index]);
        		}
        		relationsToBeDeleted = new Array();
        	}
        	
        }
    }
    
    for (var i = 0; i < tableObject.columns.length; i++){
        if(tableObject.columns[i].keyID == columnIDValue) {
            //alert("Column Key Found");
        	tableObject.columns.splice(i,1);
        }
    }
    
    for (var i = 0; i < tableObject.foreignKeys.length; i++){
        if(tableObject.foreignKeys[i].keyID == columnIDValue) {
            //alert("Foreign Key Found");
        	tableObject.foreignKeys.splice(i,1);
        	var tempRelation;
      		var tempTable;
      		var tempColumn;
      		for(var i = 0; i < tableObject.relations.length; i++){
      			tempRelation = tableObject.relations[i];
      			tempTable = tempRelation.mainTable;
      			
      			console.log( tempRelation.keysInSecondryTable.length);
      			for (var j = 0; j < tempRelation.keysInSecondryTable.length; j++){
    					tempColumn = tempRelation.keysInSecondryTable[j];
    					if(columnIDValue == tempColumn.keyID){
    						for (var j = 0; j < tempRelation.keysInSecondryTable.length; j++){
    							deleteColumnFromTable(tableObject, tempRelation.keysInSecondryTable[j].keyID);
    						}
    						// Deleting Relation from Main Table
    						deleteRelation(tempTable, tempRelation.line.id);
    						
    						//Deleting the line representing the relation
    						svg.remove(tempRelation.line);
    						
    						// Deleting the relation from Secondry Table
    						tableObject.relations.splice(i,1);
    						break;
    					}
      			}
      			
      		}
        }
    }
    var tempInnerHTML = tableObject.getTableHTMLFunction();   
    //alert(tempInnerHTML);
    return tempInnerHTML;
}

function deleteRelation(tableObject, relationLineID){
	console.log(tableObject.relations.length);
	for(var i = 0; i < tableObject.relations.length; i++){
		if(tableObject.relations[i].line.id == relationLineID){
			tableObject.relations.splice(i,1);
			break;
		}
	}
	console.log(tableObject.relations.length);
}

//This is utility function used by deleteColumnFromTableFunction
function deleteColumnFromTable(parentTable, columnID) {
	for ( var i = 0; i < parentTable.primaryKeys.length; i++) {
		if (parentTable.primaryKeys[i].keyID == columnID) {
			//alert("Primary Key Found");
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
			//alert("Foreign Key Found");
			parentTable.foreignKeys.splice(i, 1);
		}
	}
}