<?php
	//Include the helpful php files
	include("conectar.php");


	$deviceId=$_REQUEST["deviceId"];

	$statement = 'SELECT ID from PAE.Device where ID =' . $deviceId;

	$connexion = conectar();
	$resp = query($statement,$connexion);
	desconectar($connexion);
	if($resp->num_rows == 0){
		mysqli_free_result($resp);

		$connexion = conectar();
		$statement = "INSERT INTO PAE.Device(ID,Latitude,Longitude,Alarm,Future_Alarm) VALUES ('$deviceId', 0,0, 0,0) ";
		$resp = query($statement,$connexion);
		if (!$resp ) die('Invalid query: ' . mysql_error());
		desconectar($connexion);
		echo 1;
	}else{
		echo 0;
	}





?>
