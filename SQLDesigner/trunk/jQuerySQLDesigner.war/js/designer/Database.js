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
function deleteTableFromDatabase(tableID) {
	console.log("Tables in Database: " + this.tables.length);
	var tempTableInDatabase;
	for ( var i = 0; i < this.tables.length; i++) {
		tempTableInDatabase = this.tables[i];
		// alert("tempTableInDatabase.id" + " " + tableID);
		if (tempTableInDatabase.id == tableID) {
			// alert(tempTableInDatabase.id);
			this.tables.splice(i, 1);
		}
	}
	console.log("Tables in Database: " + this.tables.length);
}

function findTableInDatabaseByID(tableID) {
	// alert(tableID);
	var tempTableInDatabase;
	console.log("Tables in Database: " + this.tables.length);
	for ( var i = 0; i < this.tables.length; i++) {
		tempTableInDatabase = this.tables[i];
		// alert("tempTableInDatabase.id" + " " + tableID);
		if (tempTableInDatabase.id == tableID) {
			// alert(tempTableInDatabase.id);
			return tempTableInDatabase;
		}
	}
}

function findTableInDatabaseByName(tableName) {
	// alert(tableName);
	var tempTableInDatabase;
	for ( var i = 0; i < this.tables.length; i++) {
		tempTableInDatabase = this.tables[i];
		// alert("tempTableInDatabase.name" + " " + tableName);
		if (tempTableInDatabase.name.toLowerCase() == tableName.toLowerCase()) {
			// alert(tableName + " " + tempTableInDatabase.id);
			return tempTableInDatabase;
		}
	}
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
Database.prototype.findTableInDatabaseByIDFunction = findTableInDatabaseByID;
Database.prototype.findTableInDatabaseByNameFunction = findTableInDatabaseByName;

Database.prototype.deleteTableFunction = deleteTableFromDatabase;
Database.prototype.Postgres = generateSQLForPostgres;
Database.prototype.MySQL = generateSQLForMySQL;
Database.prototype.Oracle = generateSQLForOracle;
Database.prototype.SQLServer = generateSQLForSQLServer;
