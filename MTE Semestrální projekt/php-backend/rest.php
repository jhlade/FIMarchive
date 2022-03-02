<?php

require("include/messages.inc.php");

header("Accept: application/json; charset=utf-8");
header("Content-Type: application/json;charset=utf-8");

/******************************************************************************/

$post = json_decode(file_get_contents('php://input'));

if (!isset($post->apikey)) {
	header('HTTP/1.0 403 Forbidden', true, 403);
	exit(reply("Chybí API klíč."));
}

/******************************************************************************/

if ($post->apikey != md5("Koci1234")) {
	header('HTTP/1.0 403 Forbidden', true, 403);
	exit(reply("Nesprávný API klíč."));
}

/******************************************************************************/

if (isset($post->type)) {

	/*
	DBS je uzavřená součást systému GC-StyX, (c) 2008 - 2018 Jan Hladěna
	Představuje abstrakční vrstvu pro komunikaci s databází, zde je použita
	nízkoúrovňová funkce pouhého spuštění SQL dotazu a získání dat.
	*/
	require("include/dbs.inc.php");

	// read data
	if ($post->type == "fetch") {
	
		$fetcher = \DBS::getInstance();
		$data = $fetcher->exeSQL("SELECT *, (runtime / avg_precision) AS total FROM score ORDER BY total ASC, runtime DESC, avg_precision DESC LIMIT 10");
	
		$result = Array();
		$data_json = Array();
	
		echo "[";
		if ($data && $fetcher->dbs_rows > 0) {
			foreach ($fetcher->dbs_data as $record) {
				$data_json[] = json_encode(Array("name" => $record["name"],
								  "runtime" => $record["runtime"],
								  "distance" => $record["distance"],
								  "avg" => $record["avg_precision"],
								  "date" => $record["date"]));
			}
		}
		
		echo implode(",", $data_json);
		echo "]";

	
	} else if ($post->type == "create") {
	// create data entry
	
		// sanitize data
		$new_name = strip_tags($post->name);
		$new_runtime = (int) $post->runtime;
		$new_distance = (int) $post->distance;
		$new_avg = (int) $post->avg;

		$writer = \DBS::getInstance();
		$write = $writer->exeSQL("INSERT INTO score (name, runtime, distance, avg_precision, date) VALUES ('" . $new_name . "', '" . $new_runtime . "', '" . $new_distance . "', '" . $new_avg . "', NOW())");
	
		if ($write) {
			exit(reply("Data byla zapsána."));
		} else {
			header('HTTP/1.0 500 Internal server error', true, 500);
			exit(reply("Nepozadřilo se zapsat data."));
		}
	
	} else {
		header('HTTP/1.0 405 Method not allowed', true, 405);
		exit(reply("Neplatná operace."));
	}

} else {
	header('HTTP/1.0 405 Method not allowed', true, 405);
	exit(reply("Nebyla vybrána operace."));
}

/******************************************************************************/

