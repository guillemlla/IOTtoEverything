<?php

	include("conectar.php");

	$data_json ="";
	$data_json = $_POST["Data"];
	$data = json_decode($data_json);
	if(count($data)!=0){

		foreach ($data->Devices as $device) {

			$statement1 = addslashes('SELECT Device_ID from PAE.Sensor_Data where Device_ID=');
			$statement2 = addslashes(' AND Calendar=');
			$statement = $statement1 . $device->Device_ID . $statement2 . $device->Calendar;

			$connexion = conectar();
			$resp = query($statement,$connexion);
			desconectar($connexion);
			if($resp->num_rows == 0){
				mysqli_free_result($resp);

				$connexion = conectar();
				$statement = "UPDATE PAE.Sensor_Data set Last_Update = '0' WHERE Device_ID = $device->Device_ID ";
				$resp = query($statement,$connexion);
				if (!$resp ) die('Invalid query: ' . mysql_error());
				desconectar($connexion);

				$connexion = conectar();
				$statement = "INSERT INTO PAE.Sensor_Data(Status,Device_ID,Temperature,Humidity,Calendar,Last_Update) VALUES ('1',$device->Device_ID, $device->Temperature,$device->Humidity, $device->Calendar,'1')";
			
				$resp = query($statement,$connexion);

				if (!$resp ) die('Invalid query: ' . mysql_error());
				desconectar($connexion);
			}
		}
	}

?>
