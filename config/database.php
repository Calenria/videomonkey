<?php
require_once($config['pfade']['adodb'] . 'adodb-exceptions.inc.php');
require_once($config['pfade']['adodb'] . 'adodb.inc.php');

$DB = NewADOConnection($config['datenbank']['typ']);  
$DB->Connect($config['datenbank']['server'], $config['datenbank']['user'], $config['datenbank']['password'], $config['datenbank']['shema']);
try {
	$DB->Execute("select * from genres");
} catch (exception $e) {
	Var_Dump::display($e);
}

?>