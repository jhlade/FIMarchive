<?php

function reply($message) {
	
	return json_encode(Array("reply" => $message));
	
}