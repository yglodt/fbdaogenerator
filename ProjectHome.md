Fbdaogenerator connects to a [Firebird](http://www.firebirdsql.org/) Database, reads the metadata of the Database by querying the system-tables, and generates code with Data-Access-Objects. It has been tested and found to work well with Firebird 1.5, 2.0 and 2.1.

Code is currently being generated for the following programming languages:

  * Java (POJOs plus custom DAO objects)
  * Java (POJOs only, annotated for [hibernate](http://www.hibernate.org))
  * PHP

Python code generation is planned.

See the usage examples for
[Java](http://code.google.com/p/fbdaogenerator/wiki/javaExample) and
[PHP](http://code.google.com/p/fbdaogenerator/wiki/phpExample).