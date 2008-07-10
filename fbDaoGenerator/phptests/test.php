<pre>
<style type='text/css'>
* {
	font-size: 10px;
}
</style>
<script type='text/javascript'>

//window.alert("test");

</script>
<?php

include('ar.php');

$dbh = ibase_pconnect("localhost:test", "SYSDBA" ,"masterkey",'ISO8859_1',0,1) or die("<br>Could not connect to database.");
ini_set('magic_quotes_sybase',1);
set_magic_quotes_runtime(0);


$dao = new SimpleTableDAO($dbh);

$test = $dao->getByPK(9);

$new = new SimpleTable();
$test->setStr01("Norge4");
$dao->update($test);
$dao->commit();

/*
 $more = $dao->getAllWithClause("where id > 5");
 print_r($more);
 */
/*
 $t = new SimpleTable();
 $t->setId(8);
 $dao->delete($t);


 $test = new SimpleTable($dbh);
 $test->getByPK(9);


 //$more = $test->getAllWithClause("where id > 6");
 //print_r($more);

 $test->delete();
 */
?>