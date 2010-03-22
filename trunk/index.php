<?php
require('./config/config.php');

$smarty = new Renderer;
$smarty->Init_Smarty();

$smarty->display('index.tpl');
?>