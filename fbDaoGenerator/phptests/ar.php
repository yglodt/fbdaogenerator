<?php


class SimpleTable {
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
}


class SimpleTableDAO {
	private $dbh;
	private $trans;
	
	protected function getDbh() {
		return $this->dbh;
	}

	protected function setDbh($dbh) {
		$this->dbh = $dbh;
	}
	
	public function __construct($dbh) {
		$this->setDbh($dbh);
		$this->trans = ibase_trans($dbh);
	}

	public function getByPK($id) {
		$query = "select id, str01, testint01 from SIMPLE_TABLE where id = ?";
		$sth = ibase_query($this->getDbh(), $query, $id);
		$temp = new SimpleTable();
		while ($row = ibase_fetch_row($sth, IBASE_FETCH_BLOBS)) {
			$temp->setId($row[0]);
			$temp->setStr01($row[1]);
			$temp->setTestint01($row[2]);
		}
		ibase_free_result($sth);
		return $temp;
	}

	public function getAll() {
		$query = "select id, str01, testint01 from SIMPLE_TABLE";
		$sth = ibase_query($this->getDbh(), $query);
		while ($row = ibase_fetch_row($sth, IBASE_FETCH_BLOBS)) {
			$temp = new SimpleTable();
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
		$sth = ibase_query($this->getDbh(), $query);
		while ($row = ibase_fetch_row($sth, IBASE_FETCH_BLOBS)) {
			$temp = new SimpleTable();
			$temp->setId($row[0]);
			$temp->setStr01($row[1]);
			$temp->setTestint01($row[2]);
			$result[] = $temp;
		}
		ibase_free_result($sth);
		return $result;
	}

	public function insert($o) {
		$stmt = "insert into SIMPLE_TABLE (id, str01, testint01) values (? ,?, ?)";
		$sth = ibase_prepare($this->getDbh(), $stmt);
		$result = ibase_execute($sth, $o->getId(), $o->getStr01(), $o->getTestint01());
		return $result;
	}

	public function update($o) {
		$stmt = "update SIMPLE_TABLE set id = ?, str01 = ?, testint01 = ? where id = ?";
		$sth = ibase_prepare($this->getDbh(), $stmt);
		$result = ibase_execute($sth, $o->getId(), $o->getStr01(), $o->getTestint01(), $o->getId());
		return $result;
	}

	public function delete($o) {
		$stmt = "delete from SIMPLE_TABLE where id = ?";
		$sth = ibase_prepare($this->getDbh(), $stmt);
		$result = ibase_execute($sth, $o->getId());
		return $result;
	}

	public function commit() {
		return ibase_commit($this->trans);
	}

	public function rollback() {
		return ibase_rollback($this->trans);
	}
}

?>