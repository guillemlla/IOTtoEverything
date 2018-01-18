<?php
  function conectar(){
    $server="127.0.0.1";
  	$user="root";
  	$pass="d@tMySqLPa$$";
  	$db="PAE";
  	$connection = mysqli_connect($server, $user, $pass, $db);
  	if (!$connection) {
          die('MySQL ERROR: ' . mysql_error());
  	}
  	mysqli_select_db($connection,$db) or die( 'MySQL ERROR: '. mysql_error() );
  	return $connection;
  }

  function desconectar($connection){
    $connection->close();
  }

  function query($query,$connection){

    $result = mysqli_query($connection,$query) or die('Invalid query: ' . mysqli_error($connection));
    return $result;

  }
?>
