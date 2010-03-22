<?php /* Smarty version Smarty3-b8, created on 2010-03-22 20:28:17
         compiled from "./templates/header.tpl" */ ?>
<?php /*%%SmartyHeaderCode:1697276604ba7c4d11aa275-83434463%%*/if(!defined('SMARTY_DIR')) exit('no direct access allowed');
$_smarty_tpl->decodeProperties(array (
  'file_dependency' => 
  array (
    '97c13ae6868bbc459509c9f1b968154acd23eecc' => 
    array (
      0 => './templates/header.tpl',
      1 => 1267803221,
    ),
  ),
  'nocache_hash' => '1697276604ba7c4d11aa275-83434463',
  'function' => 
  array (
  ),
  'has_nocache_code' => true,
)); /*/%%SmartyHeaderCode%%*/?>
<?php if (!is_callable('smarty_function_popup_init')) include 'libs/smarty/plugins/function.popup_init.php';
?><HTML>
<HEAD>
<?php echo smarty_function_popup_init(array('src'=>"/javascripts/overlib.js"),$_smarty_tpl->smarty,$_smarty_tpl);?>

<TITLE><?php echo $_smarty_tpl->getVariable('title')->value;?>
 - <?php echo '/*%%SmartyNocache:1697276604ba7c4d11aa275-83434463%%*/<?php echo $_smarty_tpl->getVariable(\'Name\')->value;?>
/*/%%SmartyNocache:1697276604ba7c4d11aa275-83434463%%*/';?></TITLE>
</HEAD>
<BODY bgcolor="#ffffff">
