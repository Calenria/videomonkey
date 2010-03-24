<?php
require('./config/config.php');
require('./classes/WebInfo.php');

$wi = new WebInfo;
$wi->get_imdb_source('tt0947810','title');

print_r($wi->imdb_info);

?>