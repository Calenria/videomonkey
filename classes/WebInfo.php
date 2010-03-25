<?php
require_once($config['pfade']['simplehtmldom'] . 'simple_html_dom.php');

class WebInfo
{
	
	var $imdb_info = array();
	var $imdb_clean_info = array();
	
	function get_imdb($wich,$code,$type) {
		$this->get_imdb_source($wich,$code,$type);
		return $this->imdb_clean_info;
	}
	
	function get_imdb_source($wich,$code,$type)
	{
		$html = file_get_html("http://www.imdb.$wich/$type/$code");
		$this->get_imdb_infos($html,'div[class=info]');
		$this->clear_unused_infos();
		$this->get_imdb_rating();
	}
	
	/**
	 * Infos von der IMDB holen.
	 * Überschriften stehen in divs der Klasse info
	 * Der Content in den inneren divs
	 */
	function get_imdb_infos($html,$tag) {
		foreach ($html->find($tag) as $info) {
			//Key zur zuordnung holen
			$type = $info->find('h5',0)->innertext;
			$type = $type . $info->find('h3',0)->innertext;
			$type = $this->clean_index($type);
			
			$tmp = array();
			
			foreach ($info->find('div') as $infos) {
				$tmp[] = $this->clean($infos->innertext);
			}
			
			if (count($tmp) > 1) {
				$this->imdb_info[$type] = $tmp;
			} else {
				$this->imdb_info[$type] = $tmp[0];
			}
		}
	}
	
	/**
	 * Holt die infos über die Bewertung, Position 4 im Orginal Array
	 */
	function get_imdb_rating() {
		$tmp = $this->clean_index($this->imdb_info['user_rating'][4]);
		$tmp = substr($tmp,0,strrpos($tmp,'/'));
		$this->imdb_clean_info['rating'] = $tmp;
	}
	
	/**
	 * Säubern der Keys
	 */
	function clean_index($text) {
		$text = trim(strip_tags(trim($text)));
		$text = str_replace(':','',$text);
		$text = str_replace(' (WGA)','',$text);
		$text = str_replace(' ','_',$text);
		$text = strtolower($text);
		
		return $this->clean($text);
	}
	
	/**
	 * Säubern der inhalte
	 */
	function clean($text) {
		$text = trim($text);
		
		return $text;
	}
	
	function clear_unused_infos() {
		
	}
	
	
}
?>