<?php
	//Include the helpful php files

	include("conectar.php");

	// we use the query.php query function that queries us and returns the data to
	// the output variable $ dataSQL
	$query = "SELECT ID FROM PAE.Device";

	$connexion = conectar();
	$dataSQL = query($query,$connexion);
	desconectar($connexion);

    // If the query is empty, it gives error and ends
    if (!$dataSQL ) {
    die('Invalid query: ' . mysql_error());}
	$finalId = 0;
    // we create an array that will fill with data
	while($row = $dataSQL->fetch_array(MYSQLI_ASSOC)) {
		$row =implode("|",$row);
      if($row>$finalId){
				$finalId = $row;
			}
    }
		$finalId = $finalId +1;

		$connexion = conectar();
		$statement = "INSERT INTO PAE.Device(ID,Latitude,Longitude,Alarm,Future_Alarm) VALUES ('$finalId', 0,0, 0,0) ";
		$resp = query($statement,$connexion);
		if (!$resp ) die('Invalid query: ' . mysql_error());
		desconectar($connexion);


  echo $finalId.':';



	//release the results of the query
	mysqli_free_result($dataSQL);






?>
