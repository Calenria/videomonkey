<?php
require_once($config['pfade']['simplehtmldom'] . 'simple_html_dom.php');

class WebInfo
{
	
	var $imdb_info = array();
	
	function get_imdb_source($code,$type)
	{
		$html = file_get_html("http://www.imdb.com/$type/$code");
		$this->get_imdb_infos($html,'div[class=info]');
		$this->clear_unused_infos();
		$this->get_imdb_rating();
	}

	function get_imdb_infos($html,$tag) {
		foreach ($html->find($tag) as $info) {
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
		$this->imdb_info['user_rating'] = $tmp;
	}
	
	function clean_index($text) {
		$text = trim(strip_tags(trim($text)));
		$text = str_replace(':','',$text);
		$text = str_replace(' (WGA)','',$text);
		$text = str_replace(' ','_',$text);
		$text = strtolower($text);
		
		return $this->clean($text);
	}
	
	function clean($text) {
		$text = trim($text);
		
		return $text;
	}
	
	function clear_unused_infos() {
		unset($this->imdb_info['moviemeter']);
		unset($this->imdb_info['your_rating']);
		unset($this->imdb_info['contact']);
		unset($this->imdb_info['tagline']);
		unset($this->imdb_info['plot_keywords']);
		
		unset($this->imdb_info['user_reviews']);
		unset($this->imdb_info['mpaa']);
		unset($this->imdb_info['parents_guide']);
		unset($this->imdb_info['sound_mix']);
		
		unset($this->imdb_info['certification']);
		unset($this->imdb_info['goofs']);
		unset($this->imdb_info['movie_connections']);
		unset($this->imdb_info['newsdesk
(601_articles)']);
	}
	
	
}
?>