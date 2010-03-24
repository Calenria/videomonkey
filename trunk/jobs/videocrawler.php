<?php

$fileutil =&new FileUtil;

$fileutil->set_wanted_extensions($config['crawler']['extensions']);
$fileutil->readdirs($config['crawler']['media']);
$fileutil->read_mediainfos();

?>