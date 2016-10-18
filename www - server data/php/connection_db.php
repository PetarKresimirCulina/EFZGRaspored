<?php
	header('Content-type: text/plain; charset=utf-8');
        include 'setSession.php';

	function connectDB()
	{
		setSession();
		$servername = 'localhost';
		$username = 'user';
		$password = 'pass';
		
		$connection = new mysqli($servername, $username, $password);
		
		if($connection->connect_error)
		{
			die('Connection failed: ' . $connection->connect_error);
		}
		
		$connection->set_charset('utf8');
		$connection->select_db($_SESSION['database']);
		return $connection;
	}
?>