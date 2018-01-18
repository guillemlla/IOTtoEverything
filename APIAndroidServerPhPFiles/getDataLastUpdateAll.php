<?php
	//Include the helpful php files

	include("conectar.php");

	$devicesId=$_REQUEST["deviceId"];
	$ids = explode(",",$devicesId);

	// we use the query.php query function that queries us and returns the data to
	// the output variable $ dataSQL
	$query = "SELECT Device_ID,Temperature,Humidity,Calendar FROM PAE.Sensor_Data WHERE Last_Update = '1'";
	$connexion = conectar();
	$dataSQL = query($query,$connexion);
	desconectar($connexion);

    // If the query is empty, it gives error and ends
    if (!$dataSQL ) {
    die('Invalid query: ' . mysql_error());

	}

    // we create an array that will fill with data
  $myArray = array();


	while($row = $dataSQL->fetch_array(MYSQLI_ASSOC)) {

            if(in_array($row["Device_ID"],$ids)){
							$myArray[] = $row;
						}
    }
  echo json_encode($myArray);



	//release the results of the query
	mysqli_free_result($dataSQL);






?>
