<?php
require('./config/config.php');
require('./classes/WebInfo.php');

$wi = new WebInfo;

print_r($wi->get_imdb('com','tt0947810','title'));
print_r($wi->get_imdb('de','tt0947810','title'));
?>