<pre>
<?php


$dbh = ibase_pconnect("localhost:test", "SYSDBA" ,"masterkey",'ISO8859_1',0,1) or die("<br>Could not connect to database.");
ini_set('magic_quotes_sybase',1);
set_magic_quotes_runtime(0);

$test = new SimpleTable($dbh);
$test->getByPK(6);

print $test->getId();
print $test->getStr01();
print $test->getTestint01();

$more = $test->getAll();
//print_r($more);

$more = $test->getAllWithClause("where id > 6");
print_r($more);

$test->delete();

class SimpleTable {
	private $dbh;
	private $trans;

	private $id;
	public function getId() {
		return $this->id;
	}
	public function setId($id) {
		$this->id = $id;
	}

	private $str01;
	public function getStr01() {
		return $this->str01;
	}
	public function setStr01($str01) {
		$this->str01 = $str01;
	}

	private $testint01;
	public function getTestint01() {
		return $this->testint01;
	}
	public function setTestint01($testint01) {
		$this->testint01 = $testint01;
	}

	public function __construct($dbh) {
		$this->dbh = $dbh;
		$this->trans = ibase_trans($dbh);
	}

	public function getByPK($id) {
		$query = "select id, str01, testint01 from SIMPLE_TABLE where id = ?";
		$sth = ibase_query($this->dbh, $query, $id);
		while ($row = ibase_fetch_row($sth, IBASE_FETCH_BLOBS)) {
			$this->setId($row[0]);
			$this->setStr01($row[1]);
			$this->setTestint01($row[2]);
		}
		ibase_free_result($sth);
	}

	public function getAll() {
		$query = "select id, str01, testint01 from SIMPLE_TABLE";
		$sth = ibase_query($query);
		while ($row = ibase_fetch_row($sth, IBASE_FETCH_BLOBS)) {
			$temp = new SimpleTable(Null);
			$temp->setId($row[0]);
			$temp->setStr01($row[1]);
			$temp->setTestint01($row[2]);
			$result[] = $temp;
		}
		ibase_free_result($sth);
		return $result;
	}

	public function getAllWithClause($where) {
		$query = "select id, str01, testint01 from SIMPLE_TABLE $where";
		$sth = ibase_query($query);
		while ($row = ibase_fetch_row($sth, IBASE_FETCH_BLOBS)) {
			$temp = new SimpleTable(Null);
			$temp->setId($row[0]);
			$temp->setStr01($row[1]);
			$temp->setTestint01($row[2]);
			$result[] = $temp;
		}
		ibase_free_result($sth);
		return $result;
	}

	public function insert() {
		$stmt = "insert into SIMPLE_TABLE (id, str01, testint01) values (? ,?, ?)";
		$sth = ibase_prepare($this->dbh, $stmt);
		$result = ibase_execute($sth, 101, 'a', 100);
	}

	public function update() {
		$stmt = "update SIMPLE_TABLE set id = ?, str01 = ?, testint01 = ? where id = ?";
		$sth = ibase_prepare($this->dbh, $stmt);
		$result = ibase_execute($sth, 102, 'a', 100, 1);
	}

	public function delete() {
		$stmt = "delete from SIMPLE_TABLE where id = ?";
		$sth = ibase_prepare($this->dbh, $stmt);
		$result = ibase_execute($sth, 2);
	}

	public function commit() {
		return ibase_commit($this->trans);
	}

	public function rollback() {
		return ibase_rollback($this->trans);
	}
}

?>