<?php
	
	include'connection_db.php';

	$connection = connectDB();
	
	session_start();
	
	// Create connection
	//$connection = new mysqli($servername, $username, $password);

	// Check connection
	if ($connection->connect_error) 
	{
		echo("Greška kod uspostave veze sa bazom: " . $conn->connect_error);
	}

	$connection->select_db('efzg_db');
	
	
	
	$user = mysqli_real_escape_string($connection, $_POST['user']);
	$pass = mysqli_real_escape_string($connection, $_POST['pass']);
	
	
	if(strlen($user) && strlen($pass))
	{
		$sql = "SELECT pass FROM users WHERE user = '$user'";
		$result = mysqli_query($connection, $sql);
		
		if(mysqli_num_rows($result) > 0)
        {
			$result_array = $result->fetch_array(MYSQLI_ASSOC);
			//var_dump($result_array);
			$pass_e = $result_array["pass"];
			
			if (password_verify($pass, $pass_e)) {
				echo 'Password is valid!';
				$sql = "SELECT guid FROM users WHERE user = '$user'";
				$result = mysqli_query($connection, $sql);
				
				if(mysqli_num_rows($result) > 0)
				{
					$result_array = $result->fetch_array(MYSQLI_ASSOC);
					$guid = $result_array["guid"];
					
					$_SESSION["guid"] = $guid;
					$_SESSION['username'] = $user;
					
					header('Location: /upload.php');
					
				}
			}
			else
			{
				$_SESSION["error"] = "Korisničko ime i/ili lozinka su netočni.";
				header('Location: ../index.php');
			}
        }
        else
        { 
			$_SESSION["error"] = "Korisničko ime i/ili lozinka su netočni.";
			header('Location: ../index.php');
		}
	}
	else
	{
		$_SESSION["error"] = "Korisničko ime i/ili lozinka su netočni.";
		header('Location: ../index.php');
	}
	
	//echo "Connected successfully";
?>