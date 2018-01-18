<?php

	include("conectar.php");

	$data_json ="";
	$data_json = $_POST["Data"];
	$data = json_decode($data_json);

	foreach ($data->Parameters as $parameter) {

	$statement = "UPDATE Device SET Latitude=". $parameter->latitude . ", Longitude=". $parameter->longitude . ", Alarm='" . $parameter->alarm . "', Future_Alarm='" . $parameter->futureAlarm .  "' where ID= ".  $parameter->id;

	echo $statement;

	$connexion = conectar();
	$resp = query($statement,$connexion);
	desconectar($connexion);

	}

	//}

?>
