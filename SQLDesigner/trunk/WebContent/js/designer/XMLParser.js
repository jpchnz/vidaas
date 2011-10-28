// Number of Tables in Database
var tableCounter = 0;

function ForeignKeyRelation(mainTableValue, secondaryTableValue, mainColumnNameValue, secondaryColumnNameValue, fkRelationNameValue){
    this.mainTable = mainTableValue;
    this.secondaryTable = secondaryTableValue;
    this.mainColumnName = mainColumnNameValue;
    this.secondaryColumnName = secondaryColumnNameValue;
    this.fkRelationName = fkRelationNameValue;
}

function XMLParser(){
    this.xmlSQL = "";
    this.databaseObject = null;
    
    this.foreignKeysArrays = new Array();
}

function xmlParserInit(xmlSQLString){
    this.xmlSQL = xmlSQLString;    
}

function createAndReturnDatabaseFunction(){
    var databaseName = $(this.xmlSQL).find("database").attr("name");
    
    this.databaseObject = createDatabaseFunction(databaseName, "");
    var tempDatabaseObject = this.databaseObject;
    var tempForeignKeysArrays = this.foreignKeysArrays;
    //alert(tempDatabaseObject.name);
    var initialXCoordinate = 300;
    var initialYCoordinate = 100;
    var counter = 1;
    var horizontalCounter = 1;
    var tableNames = "";
    $(this.xmlSQL).find("table").each(function(){
        createAddTable(horizontalCounter * 400, initialYCoordinate, this, tempDatabaseObject, tempForeignKeysArrays);  
        if(counter == 10){
            horizontalCounter = 0;
            initialYCoordinate = 800;
        }
        if(counter == 20){
            horizontalCounter = 0;
            initialYCoordinate = 1600;
        }
        if(counter == 30){
            horizontalCounter = 0;
            initialYCoordinate = 2400;
        }
        if(counter == 40){
            horizontalCounter = 0;
            initialYCoordinate = 3200;
        }
        if(counter == 50){
            horizontalCounter = 0;
            initialYCoordinate = 4000;
        }
        if(counter == 60){
            horizontalCounter = 0;
            initialYCoordinate = 4800;
        }
        
        counter++;
        horizontalCounter++;
    });   
    return this.databaseObject;
}

function createAddTable(xCoordinate, yCoordinate, xmlTable, databaseObject, foreignKeysArrays){
    
    var tempInnerHTML;
    var tableName = $(xmlTable).attr("name");
    
    var tableTemp = new Table();
    tempInnerHTML = tableTemp.createAndAddWithoutPKFunction(xCoordinate, yCoordinate, tableName, tableTemp, databaseObject);
       
    var columnKeyType = "Ordinary";
    var columnKeyType = "Ordinary";
    var columnValuesArray;
    $(xmlTable).find("column").each(function(){
        columnValuesArray = createAddColumn(this);
        if(columnValuesArray[0] == "true"){
            columnKeyType = "Primary";
        } else {
            columnKeyType = "Ordinary";
        }
        tempInnerHTML = tableTemp.updateFunction(columnKeyType, columnValuesArray[1], columnValuesArray[2], columnValuesArray[3], 0, columnValuesArray[4], columnValuesArray[5]);
    });
    
    $(xmlTable).find("foreign-key").each(function(){
         var foreignKeyValuesArray = findAndReturnForeignKey(this);
         var localColumnName = foreignKeyValuesArray[0];
         
         for (var i = 0; i < tableTemp.columns.length; i++){
            if(tableTemp.columns[i].keyName == localColumnName) {
                tempInnerHTML = tableTemp.updateColumnFunction(tableTemp.columns[i], 'Foreign');
                //return tableTemp.columns[i];                               
            }
        }
        
        //foreignKeysArrays
        var tempForeignKeyRelation = new ForeignKeyRelation(foreignKeyValuesArray[1], tableTemp.name, foreignKeyValuesArray[2], foreignKeyValuesArray[0], foreignKeyValuesArray[3]);
        foreignKeysArrays[foreignKeysArrays.length] = tempForeignKeyRelation;
    });
    
    $(tempInnerHTML).appendTo($( "#droppable" ));
    tableTemp.height = $("#" + tableTemp.id).height()	
    tableTemp.width = $("#" + tableTemp.id).width();
    //alert(tableName);
}

function createAddColumn(xmlColumn){
    var columnValuesArray = new Array();
    
    var columnName = $(xmlColumn).attr("name");
    var primaryKey = $(xmlColumn).attr("primaryKey");
    var dataType = $(xmlColumn).attr("type");
    var optional = $(xmlColumn).attr("required");
    var size = $(xmlColumn).attr("size");
    var autoIncrement = $(xmlColumn).attr("autoIncrement");
    //var unique = $(xmlColumn).attr("name");
    //var foreignKey = $(xmlColumn).attr("name");
    //alert(columnName + " " + primaryKey + " " +  dataType + " " +  optional + " " +  size + " " +  autoIncrement);
    
    columnValuesArray[0] = primaryKey;
    columnValuesArray[1] = columnName;
    columnValuesArray[2] = dataType.toLowerCase();
    columnValuesArray[3] = size;
    columnValuesArray[4] = optional;
    columnValuesArray[5] = autoIncrement;
    
    return columnValuesArray;
}

function findAndReturnForeignKey(xmlForeignKey){
    var mainTable = $(xmlForeignKey).attr("foreignTable");
    var foreignKeyRelationName = $(xmlForeignKey).attr("name");
    
    var localColumnName = "";
    var referencedColumnName = ""
    
    $(xmlForeignKey).find("reference").each(function(){
       localColumnName = $(this).attr("local");
       referencedColumnName = $(this).attr("foreign");
    });
    
   
    
    var foreignKeyValuesArray = new Array();
    foreignKeyValuesArray[0] = localColumnName;
    foreignKeyValuesArray[1] = mainTable;
    foreignKeyValuesArray[2] = referencedColumnName;
    foreignKeyValuesArray[3] = foreignKeyRelationName;
    
    //alert(mainTable + " " + foreignKeyValuesArray[1] + " : " + localColumnName + "  " + foreignKeyValuesArray[0] + " : " + referencedColumnName + " " + foreignKeyValuesArray[2]);
    
    return foreignKeyValuesArray;
    
}

function createAddRelation(){
}

XMLParser.prototype.init = xmlParserInit;
XMLParser.prototype.getDatabase = createAndReturnDatabaseFunction;
//XMLParser.prototype.createAndAddTable = createAddTable