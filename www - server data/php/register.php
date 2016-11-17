<?php
	$ENABLED = 1; // OBAVEZNO STAVITI NA 0 (NULA) NAKON DODAVANJA KORISNIKA!
	error_reporting(E_ALL);

	if($ENABLED == 1)
	{
		$servername = "localhost";
		$username = "fgsg";
		$password = "gfsgs";
	
		// Create connection
		$connection = new mysqli($servername, $username, $password);

		// Check connection
		if ($connection->connect_error) {
			echo("Greška kod uspostave veze sa bazom: " . $conn->connect_error);
		}

		$connection->select_db('efzg_db');
	
	
	
		$user = mysqli_real_escape_string($connection, $_GET['user']);
		$pass = mysqli_real_escape_string($connection, $_GET['pass']);
	
	
		if(strlen($user) && strlen($pass))
		{
			$guid = md5(uniqid(rand(), true));
			$pass_e = password_hash($pass, PASSWORD_DEFAULT); // KORISTI PHP > 5.5
			
			echo $guid;
			echo $pass;
			echo $pass_e;
			
			$sql = "INSERT INTO users (user, pass, guid) VALUES ('$user', '$pass_e', '$guid')" ;
			
			mysqli_query($connection, $sql) or die ('Greška kod zapisa korisnika. ' . $connection->error);
			echo "Korisnik dodan";

		}
		else
		{
			echo("Korisničko ime i/ili lozinka su neispravni (2).");
		
		}
	}
?>