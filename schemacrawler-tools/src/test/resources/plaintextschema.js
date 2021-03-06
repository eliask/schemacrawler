var printDb = function()
{
  println(database.schemaCrawlerInfo);
  println(database.databaseInfo);
  println(database.jdbcDriverInfo);
  
  var schemas = database.schemas.toArray();
  for ( var i = 0; i < schemas.length; i++)
  {
    println(schemas[i].fullName);
    var tables = database.getTables(schemas[i]).toArray();
    for ( var j = 0; j < tables.length; j++)
    {
      println("o--> " + tables[j].name);
      var columns = tables[j].columns.toArray();
      for ( var k = 0; k < columns.length; k++)
      {
        println("     o--> " + columns[k].name);
      }
    }
  }
};

printDb();
