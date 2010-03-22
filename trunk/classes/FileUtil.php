<?php
class FileUtil
{
	public $files = array();
	public $extension = array();
	
	function set_wanted_extensions($ext) {
		$this->extension = $ext;
	}
	
	function readdirs($path)
	{
		$entrys=scandir($path);
		foreach($entrys as $part)
		{
			if (!is_dir($path.$part) && !preg_match('/^(.|..)$/i', $part)) {
				$info = pathinfo($path.$part);
				if (in_array($info['extension'],$this->extension)) {
					$this->files[] = $info;
				}
			}
			if(is_dir($path.$part) && !preg_match('/^(.|..)$/i', $part, $hit))
			{
				$this->readdirs($path.$part.'/');
			}
		}
	}
}
?>