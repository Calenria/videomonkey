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
		foreach($entrys as $file)
		{
			if (!is_dir($path.$file) && !preg_match('/^(.|..)$/i', $file)) {
				$info = pathinfo($path.$file);
				if (in_array($info['extension'],$this->extension)) {
					$this->files[] = $info;
				}
			}
			if(is_dir($path.$file) && !preg_match('/^(.|..)$/i', $file, $hit))
			{
				$this->readdirs($path.$file.'/');
			}
		}
	}
	
	function read_mediainfos() {
		foreach($this->files as $file)
		{
			$cmd = 'mediainfo -f --Output=XML "' . $file['dirname'] . '/' . $file['basename'] . '"';
			Var_Dump::display(system($cmd));
		}
	}
	
	
}
?>